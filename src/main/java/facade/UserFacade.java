package facade;

import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jwt.JWTClaimsSet;
import dto.UserDTO;
import entity.LoginHistory;
import entity.User;
import exceptions.AuthenticationException;
import java.text.ParseException;
import java.util.Date;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import org.mindrot.jbcrypt.BCrypt;
import utils.MailUtil;

public class UserFacade {

    private static EntityManagerFactory emf;
    private static UserFacade instance;

    private UserFacade() {
    }

    public static UserFacade getInstance(EntityManagerFactory factory) {
        if (instance == null) {
            emf = factory;
            instance = new UserFacade();
        }
        return instance;
    }

    private boolean verifyPassword(User user, String password) {
        if (password.isEmpty()) {
            return false;
        }
        String hashedPassword = BCrypt.hashpw(password, user.getSalt());
        return hashedPassword.equals(user.getHashedPassword());
    }

    public User getVeryfiedUser(String email, String password, String ip) throws AuthenticationException {
        EntityManager em = emf.createEntityManager();
        User user;
        try {
            user = em.createNamedQuery("User.findByEmail", User.class).setParameter("email", email).getSingleResult();
            if (user == null) {
                throw new AuthenticationException("Invalid credentials");
            }
            saveLoginAttempt(user, ip);
            if (!verifyPassword(user, password)) {
                throw new AuthenticationException("Invalid credentials");
            }
        } catch (NoResultException e) {
            throw new AuthenticationException("Invalid credentials");
        } finally {
            em.close();
        }
        return user;
    }

    public String sendCode(String email) {
        String code = MailUtil.generateCode();
        MailUtil.sendCode(email, code);
        return code;
    }

    private void saveLoginAttempt(User user, String ip) {
        EntityManager em = emf.createEntityManager();
        LoginHistory loginHistory = new LoginHistory(ip, new Date());
        loginHistory.setUser(user);
        try {
            em.getTransaction().begin();
            em.persist(loginHistory);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

//    public void testExtendedLatinChars() throws Exception {
//
//        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder().claim("fullName", "João").build();
//
//        String json = claimsSet.toJSONObject().toJSONString();
//
//        Base64URL base64URL = Base64URL.encode(json);
//
//        claimsSet = JWTClaimsSet.parse(base64URL.decodeToString());
//
//        System.out.println("João " + claimsSet.getStringClaim("fullName"));
//    }
    public int verifyToken(String token) throws AuthenticationException, ParseException {
        int id = 0;

        Base64URL base64URL = Base64URL.encode(token);
        JWTClaimsSet claimsSet = JWTClaimsSet.parse(base64URL.decodeToString());
        System.out.println("João " + claimsSet.getStringClaim("fullName"));

        if (id == 0) {
            throw new AuthenticationException("Invalid Token");
        }
        return 0;
    }

    public UserDTO getUserInformation(String token) throws AuthenticationException, ParseException {
        EntityManager em = emf.createEntityManager();
        User user;
        int id = verifyToken(token);
        try {
            user = em.createNamedQuery("User.findById", User.class).setParameter("id", id).getSingleResult();
            if (user == null) {
                throw new AuthenticationException("Invalid Token!");
            }
        } catch (NoResultException e) {
            throw new AuthenticationException("Invalid Token!");
        }
        return new UserDTO(user);
    }

}

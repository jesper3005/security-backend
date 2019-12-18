package facade;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.crypto.RSADecrypter;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.SignedJWT;
import dto.UserDTO;
import entity.LoginHistory;
import entity.User;
import exceptions.AuthenticationException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
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

    public int verifyToken(String token) throws AuthenticationException {
        int id = 0;
        // Reread token
        // Retrieve id
        if (id == 0) {
            throw new AuthenticationException("Invalid Token");
        }
        return id;
    }

    public UserDTO getUserInformation(String email, String token) throws AuthenticationException {
        EntityManager em = emf.createEntityManager();
        User user;
        //int id = verifyToken(token);
        try {
            //user = em.createNamedQuery("User.findById", User.class).setParameter("id", id).getSingleResult();
            user = em.createNamedQuery("User.findByEmail", User.class).setParameter("email", email).getSingleResult();
            if (user == null) {
                throw new AuthenticationException("Invalid Token!");
            }
        } catch (NoResultException e) {
            throw new AuthenticationException("Invalid Token!");
        }
        return new UserDTO(user);
    }

    public void deleteUser(String email, String token) throws AuthenticationException {
        EntityManager em = emf.createEntityManager();
        try {
            User user = em.createNamedQuery("User.findByEmail", User.class).setParameter("email", email).getSingleResult();
            em.getTransaction().begin();
            List<LoginHistory> attempts = em.createNamedQuery("LoginHistory.findByUserId", LoginHistory.class).setParameter("user_id", user.getId()).getResultList();
            for (int i = 0; i < attempts.size(); i++) {
                em.remove(attempts.get(i));
            }
            em.remove(user);
            em.getTransaction().commit();
        } catch (NoResultException e) {
            throw new AuthenticationException("Invalid Token!");
        }
    }

    public void sendMailWithAttempts(String email, String token) throws AuthenticationException {
        EntityManager em = emf.createEntityManager();
        try {
            User user = em.createNamedQuery("User.findByEmail", User.class).setParameter("email", email).getSingleResult();
            List<LoginHistory> attempts = em.createNamedQuery("LoginHistory.findByUserId", LoginHistory.class).setParameter("user_id", user.getId()).getResultList();
            MailUtil.sendMail(email, attempts);
        } catch (NoResultException e) {
            throw new AuthenticationException("Invalid Token!");
        }
    }

}

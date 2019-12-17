package facade;

import entity.LoginHistory;
import entity.User;
import exceptions.AuthenticationException;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import org.mindrot.jbcrypt.BCrypt;

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

    public void saveLoginAttempt(User user, String ip) {
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

}

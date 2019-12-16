package facade;

import entity.User;
import exceptions.AuthenticationException;

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

    public User getVeryfiedUser(String email, String password) throws AuthenticationException {
        String errMsg = "Invalid credentials";
        EntityManager em = emf.createEntityManager();
        User user;
        try {
            user = em.createNamedQuery("User.findByEmail", User.class).setParameter("email", email).getSingleResult();
            if (user == null || !verifyPassword(user, password)) {
                throw new AuthenticationException(errMsg);
            }
        } catch (NoResultException e) {
            throw new AuthenticationException(errMsg);
        } finally {
            em.close();
        }
        return user;
    }

}

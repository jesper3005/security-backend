package testutils;

import entity.User;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.mindrot.jbcrypt.BCrypt;

public class TestUtils {

    public static void setupTestUsers(EntityManagerFactory emf) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            //Delete existing users and roles to get a "fresh" database
            em.createQuery("delete from User").executeUpdate();

            String email = "test@test.dk";
            String salt = BCrypt.gensalt();
            String hashedPassword = BCrypt.hashpw("pass1234", salt);
            String phone = "+45 1234 4556";
            String firstName = "Test";
            String lastName = "Testersen";
            int age = 35;
            String gender = "Male";

            User user = new User(email, hashedPassword, salt, phone, firstName, lastName, age, gender);
            em.persist(user);
            System.out.println("Saved test data to database");
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

}

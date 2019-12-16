package unittest;

import entity.User;
import facade.UserFacade;
import exceptions.AuthenticationException;
import javax.persistence.EntityManagerFactory;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import testutils.TestUtils;
import utils.PuSelector;

public class TestUser {

    private static UserFacade facade;

    @BeforeClass
    public static void setUpClass() {
        EntityManagerFactory emf = PuSelector.getEntityManagerFactory("pu_unit_test");
        facade = UserFacade.getInstance(emf);
        TestUtils.setupTestUsers(emf);
    }

    // User Registration
    @Test
    public void testRegistrationPositive() throws IllegalArgumentException {
        assertTrue(false);
    }

    @Test
    public void testRegistrationNegativeInvalidEmail() throws IllegalArgumentException {
        assertTrue(false);
    }

    @Test
    public void testRegistrationNegativeInvalidPassword() throws IllegalArgumentException {
        assertTrue(false);
    }

    // User Login
    @Test
    public void testLoginPositive() throws AuthenticationException {
        User user = facade.getVeryfiedUser("test@test.dk", "pass1234");
        assertNotNull(user);
    }

    @Test(expected = AuthenticationException.class)
    public void testLoginNegativeInvalidPassword() throws AuthenticationException {
        facade.getVeryfiedUser("test@test.dk", "invalidPassword");
    }

    @Test(expected = AuthenticationException.class)
    public void testLoginNegativeInvalidEmail() throws AuthenticationException {
        facade.getVeryfiedUser("invalid@invalid.dk", "pass1234");
    }

    // User Deletion
    @Test
    public void testDeletionPositive() throws IllegalArgumentException {
        assertTrue(false);
    }

    @Test
    public void testDeletionNegative() throws IllegalArgumentException {
        assertTrue(false);
    }

    // User information
    @Test
    public void testInformationPositive() throws IllegalArgumentException {
        assertTrue(false);
    }

    @Test
    public void testInformationNegative() throws IllegalArgumentException {
        assertTrue(false);
    }
}

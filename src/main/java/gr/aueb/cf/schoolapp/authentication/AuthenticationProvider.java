
package gr.aueb.cf.schoolapp.authentication;


import gr.aueb.cf.schoolapp.dao.UserDAOHibernateImpl;
import gr.aueb.cf.schoolapp.dao.exceptions.UserDAOException;
import gr.aueb.cf.schoolapp.dto.UserLoginDTO;
import gr.aueb.cf.schoolapp.model.User;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;


public class AuthenticationProvider {

    private static EntityManagerFactory emf;
    private static EntityManager entityManager;
    private static UserDAOHibernateImpl userDAO;


    private AuthenticationProvider() {}


    static {
        try {
            emf = Persistence.createEntityManagerFactory("myPU");
            entityManager = emf.createEntityManager();
            userDAO = new UserDAOHibernateImpl(entityManager);
        } catch (Exception e) {
            e.printStackTrace();  // Print the error for debugging purposes
            throw new RuntimeException("Error initializing the AuthenticationProvider", e);
        }
    }


    public static boolean authenticate(UserLoginDTO userLoginDTO) {
        // Print input username and password to debug
        System.out.println("Input Username: " + userLoginDTO.getUsername());
        System.out.println("Input Password: " + userLoginDTO.getPassword());

        // Validate authentication
        try {
            boolean isAuthenticated = userDAO.isUserValid(userLoginDTO.getUsername(), userLoginDTO.getPassword());
            System.out.println("Authentication result: " + isAuthenticated);
            return isAuthenticated;
        } catch (UserDAOException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean register(UserLoginDTO userLoginDTO) {
        // Create a new User and register it
        User newUser = new User(userLoginDTO.getUsername(), userLoginDTO.getPassword());
        try {
            return userDAO.registerUser(newUser);
        } catch (UserDAOException e) {
            throw new RuntimeException(e);
        }
    }
}
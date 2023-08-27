package gr.aueb.cf.schoolapp.controller;








import gr.aueb.cf.schoolapp.dao.UserDAOHibernateImpl;
import gr.aueb.cf.schoolapp.dao.exceptions.UserDAOException;
import gr.aueb.cf.schoolapp.dto.UserDTO;
import gr.aueb.cf.schoolapp.service.UserServiceImpl;
import gr.aueb.cf.schoolapp.service.exceptions.UserNotFoundException;


import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@WebServlet("/register")
public class RegisterController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("myPU");
    private EntityManager entityManager = emf.createEntityManager();
    private UserDAOHibernateImpl userDAO = new UserDAOHibernateImpl(entityManager);
    private UserServiceImpl userService = new UserServiceImpl(userDAO);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/school/static/templates/login.jsp").forward(request, response);
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        // Check if the username already exists in the database
        boolean usernameExists = false;
        try {
            usernameExists = userService.isUserValid(username,password);
        } catch (UserNotFoundException e) {
            throw new RuntimeException(e);
        }

        if (usernameExists) {
            request.setAttribute("errorMessage", "Username already exists. Please choose a different username.");
            request.getRequestDispatcher("/school/static/templates/login.jsp").forward(request, response);
            return;
        }

        UserDTO userDTO = new UserDTO(username, password);
        boolean registrationResult = false;

        try {
            registrationResult = userService.registerUser(userDTO);
        } catch (UserNotFoundException | UserDAOException e) {
            throw new RuntimeException(e);
        }

        if (registrationResult) {
            request.setAttribute("successMessage", "Registration successful! You can now log in.");
        } else {
            request.setAttribute("errorMessage", "Registration failed. Please try again.");
        }

        request.getRequestDispatcher("/school/static/templates/login.jsp").forward(request, response);
    }
}

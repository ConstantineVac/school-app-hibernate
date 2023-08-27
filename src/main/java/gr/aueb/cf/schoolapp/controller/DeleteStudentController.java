package gr.aueb.cf.schoolapp.controller;




import gr.aueb.cf.schoolapp.dao.CityDAOHibernateImpl;
import gr.aueb.cf.schoolapp.dao.StudentDAOHibernateImpl;
import gr.aueb.cf.schoolapp.dao.exceptions.CityDAOException;

import gr.aueb.cf.schoolapp.dao.exceptions.StudentDAOException;
import gr.aueb.cf.schoolapp.dto.StudentDeleteDTO;
import gr.aueb.cf.schoolapp.model.City;
import gr.aueb.cf.schoolapp.service.CityServiceImpl;

import gr.aueb.cf.schoolapp.service.StudentServiceImpl;
import gr.aueb.cf.schoolapp.service.exceptions.StudentNotFoundException;


import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@WebServlet("/schoolapp/deleteStudent")
public class DeleteStudentController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("myPU");
    private EntityManager entityManager = emf.createEntityManager();

    private StudentDAOHibernateImpl studentDAO = new StudentDAOHibernateImpl(entityManager);
    private StudentServiceImpl studentService = new StudentServiceImpl(studentDAO);

    private CityDAOHibernateImpl cityDAO = new CityDAOHibernateImpl(entityManager);
    private CityServiceImpl cityService = new CityServiceImpl(cityDAO);

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        String firstname = request.getParameter("firstname");
        String lastname = request.getParameter("lastname");
        String gender = request.getParameter("gender");
        String birthdateStr = request.getParameter("birthdate");
        String cityName = request.getParameter("city");


        // Convert birthdate to java.sql.Date format
        java.sql.Date birthdate = null;
        if (birthdateStr != null && !birthdateStr.isEmpty()) {
            try {
                birthdate = java.sql.Date.valueOf(birthdateStr);
            } catch (IllegalArgumentException e) {
                request.setAttribute("deleteAPIError", true);
                request.setAttribute("message", "Invalid birthdate format. Use 'YYYY-MM-DD'.");
                request.getRequestDispatcher("/school/static/templates/students.jsp")
                        .forward(request, response);
                return;
            }
        }


        City city = null;
        if (cityName != null && !cityName.trim().isEmpty()) {
            try {
                Optional<List<City>> citiesOptional = cityDAO.getByCityName(cityName);
                if (citiesOptional.isPresent() && !citiesOptional.get().isEmpty()) {
                    city = citiesOptional.get().get(0);
                } else {
                    request.setAttribute("deleteAPIError", true);
                    request.setAttribute("message", "City not found.");
                    request.getRequestDispatcher("/school/static/templates/students.jsp")
                            .forward(request, response);
                    return;
                }
            } catch (CityDAOException e) {
                request.setAttribute("deleteAPIError", true);
                request.setAttribute("message", "Failed to retrieve city.");
                request.getRequestDispatcher("/school/static/templates/students.jsp")
                        .forward(request, response);
                return;
            }
        }






        StudentDeleteDTO studentDTO = new StudentDeleteDTO();
        studentDTO.setId(id);
        studentDTO.setFirstname(firstname);
        studentDTO.setLastname(lastname);
        studentDTO.setGender(gender);
        studentDTO.setBirthdate(birthdate);
        studentDTO.setCity(city);  // Assuming you've updated your DTO to store City objects

        try {
            studentService.deleteStudent(id);
            request.setAttribute("studentDTO", studentDTO);
            request.getRequestDispatcher("/school/static/templates/studentDeleted.jsp")
                    .forward(request, response);
        } catch (StudentNotFoundException | StudentDAOException e) {
            request.setAttribute("deleteAPIError", true);
            request.setAttribute("message", e.getMessage());
            request.getRequestDispatcher("/school/static/templates/students.jsp")
                    .forward(request, response);
        }
    }


}

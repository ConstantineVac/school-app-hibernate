package gr.aueb.cf.schoolapp.controller;

import gr.aueb.cf.schoolapp.dao.CityDAOHibernateImpl;
import gr.aueb.cf.schoolapp.dao.StudentDAOHibernateImpl;
import gr.aueb.cf.schoolapp.dao.exceptions.CityDAOException;
import gr.aueb.cf.schoolapp.dao.exceptions.StudentDAOException;
import gr.aueb.cf.schoolapp.dto.StudentUpdateDTO;
import gr.aueb.cf.schoolapp.model.City;
import gr.aueb.cf.schoolapp.model.Student;
import gr.aueb.cf.schoolapp.service.CityServiceImpl;
import gr.aueb.cf.schoolapp.service.StudentServiceImpl;
import gr.aueb.cf.schoolapp.service.exceptions.CityNotFoundException;
import gr.aueb.cf.schoolapp.service.exceptions.StudentNotFoundException;
import gr.aueb.cf.schoolapp.validator.StudentValidator;


import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@WebServlet("/schoolapp/updateStudent")
public class UpdateStudentController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private EntityManagerFactory emf;
    private EntityManager entityManager;

    private StudentDAOHibernateImpl studentDAO;
    private StudentServiceImpl studentService;

    private CityDAOHibernateImpl cityDAO;
    private CityServiceImpl cityService;

    @Override
    public void init() throws ServletException {
        emf = Persistence.createEntityManagerFactory("myPU");
        entityManager = emf.createEntityManager();

        studentDAO = new StudentDAOHibernateImpl(entityManager);
        studentService = new StudentServiceImpl(studentDAO);

        cityDAO = new CityDAOHibernateImpl(entityManager);
        cityService = new CityServiceImpl(cityDAO);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/school/static/templates/studentUpdate.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int id = Integer.parseInt(request.getParameter("id"));
        String firstname = request.getParameter("firstname");
        String lastname = request.getParameter("lastname");
        String gender = request.getParameter("gender");
        String birthdateStr = request.getParameter("birthdate");

        City city;
        try {
            city = getCityFromRequest(request);
        } catch (CityNotFoundException e) {
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/school/static/templates/studentsmenu.jsp")
                    .forward(request, response);
            return;
        }

        java.sql.Date birthdate = null;
        if (birthdateStr != null && !birthdateStr.isEmpty()) {
            try {
                birthdate = java.sql.Date.valueOf(birthdateStr);
            } catch (IllegalArgumentException e) {
                request.setAttribute("error", "Invalid birthdate format. Use 'YYYY-MM-DD'.");
                request.getRequestDispatcher("/school/static/templates/studentsmenu.jsp")
                        .forward(request, response);
                return;
            }
        }

        StudentUpdateDTO newStudentDTO = new StudentUpdateDTO();
        newStudentDTO.setId(id);
        newStudentDTO.setFirstname(firstname);
        newStudentDTO.setLastname(lastname);
        newStudentDTO.setGender(gender);
        newStudentDTO.setBirthdate(birthdate);
        newStudentDTO.setCity(city);

        request.setAttribute("updatedStudent", newStudentDTO);

        try {
            Map<String, String> errors = StudentValidator.validate(newStudentDTO);

            if (!errors.isEmpty()) {
                String errorMessage = errors.entrySet().stream()
                        .map(entry -> entry.getKey() + ": " + entry.getValue())
                        .reduce((msg1, msg2) -> msg1 + " " + msg2)
                        .orElse("");
                request.setAttribute("error", errorMessage);
                request.getRequestDispatcher("/school/static/templates/studentsmenu.jsp")
                        .forward(request, response);
                return;
            }

            Student student = studentService.updateStudent(newStudentDTO);
            request.setAttribute("message", "Student successfully updated!");
            request.setAttribute("updatedStudent", student);
            request.getRequestDispatcher("/school/static/templates/studentUpdated.jsp")
                    .forward(request, response);
        } catch (StudentNotFoundException | StudentDAOException e) {
            request.setAttribute("message", e.getMessage());
            request.getRequestDispatcher("/school/static/templates/studentUpdated.jsp")
                    .forward(request, response);
        }
    }

    private City getCityFromRequest(HttpServletRequest request) throws CityNotFoundException {
        String cityId = request.getParameter("cityId");
        if(cityId != null && !cityId.trim().isEmpty()) {
            try {
                Optional<City> cityOptional = Optional.ofNullable(cityService.getCityById(Integer.parseInt(cityId)));
                if (cityOptional.isPresent()) {
                    return cityOptional.get();
                }
            } catch (NumberFormatException e) {
                throw new CityNotFoundException("Invalid city ID format.");
            } catch (CityDAOException e) {
                throw new CityNotFoundException("City not found for provided ID.");
            }
        }
        throw new CityNotFoundException("City ID not provided or is empty.");
    }

    @Override
    public void destroy() {
        entityManager.close();
        emf.close();
    }
}

package gr.aueb.cf.schoolapp.controller;



import gr.aueb.cf.schoolapp.dao.SpecialtyDAOHibernateImpl;
import gr.aueb.cf.schoolapp.dao.TeacherDAOHibernateImpl;
import gr.aueb.cf.schoolapp.dao.exceptions.SpecialtyDAOException;
import gr.aueb.cf.schoolapp.dto.TeacherInsertDTO;
import gr.aueb.cf.schoolapp.model.Specialty;
import gr.aueb.cf.schoolapp.model.Teacher;
import gr.aueb.cf.schoolapp.service.SpecialtyServiceImpl;
import gr.aueb.cf.schoolapp.service.TeacherServiceImpl;

import gr.aueb.cf.schoolapp.service.exceptions.SpecialtyNotFoundException;
import gr.aueb.cf.schoolapp.validator.TeacherValidator;


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
import java.util.Map;
import java.util.Optional;
@WebServlet("/schoolapp/teacherInsert")
public class InsertTeacherController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private EntityManagerFactory emf;
    private EntityManager entityManager;

    private TeacherDAOHibernateImpl teacherDAO;
    private TeacherServiceImpl teacherService;

    private SpecialtyDAOHibernateImpl specialtyDAO;
    private SpecialtyServiceImpl specialtyService;

    @Override
    public void init() throws ServletException {
        emf = Persistence.createEntityManagerFactory("myPU");
        entityManager = emf.createEntityManager();

        teacherDAO = new TeacherDAOHibernateImpl(entityManager);
        teacherService = new TeacherServiceImpl(teacherDAO);

        specialtyDAO = new SpecialtyDAOHibernateImpl(entityManager);
        specialtyService = new SpecialtyServiceImpl(specialtyDAO);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            List<Specialty> specialties = specialtyService.getAllSpecialties();
            request.setAttribute("specialties", specialties);
            request.getRequestDispatcher("/school/static/templates/teachersmenu.jsp").forward(request, response);
        } catch (Exception e) {
            // Handle the error as appropriate. For now, just forward to the menu
            request.setAttribute("error", "There was an error retrieving the list of specialties.");
            request.getRequestDispatcher("/school/static/templates/teachersmenu.jsp").forward(request, response);
        }
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String firstname = request.getParameter("firstname").trim();
        String lastname = request.getParameter("lastname").trim();

        // Check if any of the required fields are empty
        if (firstname.isEmpty() || lastname.isEmpty() ) {
            request.setAttribute("error", "All fields are required.");
            forwardToTeachersMenu(request, response);
            return;
        }

        Specialty specialty;
        try {
            specialty = getSpecialtyFromRequest(request);
        } catch ( SpecialtyNotFoundException e) {
            request.setAttribute("error",e.getMessage());
            forwardToTeachersMenu(request, response);
            return;
        }

        TeacherInsertDTO teacherInsertDTO = new TeacherInsertDTO();
        teacherInsertDTO.setFirstname(firstname);
        teacherInsertDTO.setLastname(lastname);
        teacherInsertDTO.setSpecialty(specialty);

        Map<String, String> errors = TeacherValidator.validate(teacherInsertDTO);
        if (!errors.isEmpty()) {
            request.setAttribute("error", errors);
            forwardToTeachersMenu(request, response);
            return;
        }

        try {
            Teacher teacher = teacherService.insertTeacher(teacherInsertDTO);
            request.setAttribute("insertedTeacher", teacher);
            request.getRequestDispatcher("/school/static/templates/teacherInserted.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("sqlError", true);
            request.setAttribute("message", e.getMessage());
            forwardToTeachersMenu(request, response);
        }
    }

    private Specialty getSpecialtyFromRequest(HttpServletRequest request) throws SpecialtyNotFoundException {
        String specialtyId = request.getParameter("specialtyId");  // Note the change here from "specialty" to "specialtyId"
        if (specialtyId != null && !specialtyId.trim().isEmpty()) {
            try {
                Optional<Specialty> specialtyOptional = Optional.ofNullable(specialtyService.getSpecialtyById(Integer.parseInt(specialtyId)));
                if (specialtyOptional.isPresent()) {
                    return specialtyOptional.get();
                }
            } catch (NumberFormatException e) {
                throw new SpecialtyNotFoundException("Invalid specialty ID format.");
            } catch (SpecialtyDAOException e) {
                throw new SpecialtyNotFoundException("Specialty not found for provided ID.");
            }
        }
        throw new SpecialtyNotFoundException("Specialty ID not provided or is empty.");
    }
    private void forwardToTeachersMenu(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            List<Specialty> specialties = specialtyService.getAllSpecialties();
            request.setAttribute("specialties", specialties);
            request.getRequestDispatcher("/school/static/templates/teachersmenu.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("error", "There was an error retrieving the list of specialties.");
            request.getRequestDispatcher("/school/static/templates/teachersmenu.jsp").forward(request, response);
        }
    }

    @Override
    public void destroy() {
        entityManager.close();
        emf.close();
    }
}

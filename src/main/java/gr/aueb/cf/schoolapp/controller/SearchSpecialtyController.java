package gr.aueb.cf.schoolapp.controller;



import gr.aueb.cf.schoolapp.dao.SpecialtyDAOHibernateImpl;
import gr.aueb.cf.schoolapp.dao.exceptions.SpecialtyDAOException;
import gr.aueb.cf.schoolapp.model.Specialty;
import gr.aueb.cf.schoolapp.service.SpecialtyServiceImpl;
import gr.aueb.cf.schoolapp.service.exceptions.SpecialtyNotFoundException;

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

@WebServlet("/schoolapp/searchSpecialty")
public class SearchSpecialtyController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("myPU");
    private EntityManager entityManager = emf.createEntityManager();

    private SpecialtyDAOHibernateImpl specialtyDAO = new SpecialtyDAOHibernateImpl(entityManager);
    private SpecialtyServiceImpl specialtyService = new SpecialtyServiceImpl(specialtyDAO);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/schoolapp/menu")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String name = request.getParameter("name").trim();

        try {
            entityManager.clear();
            List<Specialty> specialties = specialtyService.getSpecialtiesBySpecialtyName(name);
            if (specialties.isEmpty()) {
                request.setAttribute("specialtyNotFound", true);
                request.getRequestDispatcher("/school/static/templates/specialtiesmenu.jsp")
                        .forward(request, response);
            }
            request.setAttribute("specialties", specialties);
            request.getRequestDispatcher("/school/static/templates/specialties.jsp").forward(request, response);
        } catch (SpecialtyDAOException | SpecialtyNotFoundException e) {
            String message = e.getMessage();
            request.setAttribute("sqlError", true);
            request.setAttribute("message", message);
            request.getRequestDispatcher("/school/static/templates/specialtiesmenu.jsp").forward(request, response);
        }
    }
}

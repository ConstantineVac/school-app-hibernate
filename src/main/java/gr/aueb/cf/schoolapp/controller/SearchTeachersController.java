package gr.aueb.cf.schoolapp.controller;


import gr.aueb.cf.schoolapp.dao.TeacherDAOHibernateImpl;
import gr.aueb.cf.schoolapp.dao.dbutil.HibernateHelper;
import gr.aueb.cf.schoolapp.dao.exceptions.TeacherDAOException;
import gr.aueb.cf.schoolapp.model.Teacher;
import gr.aueb.cf.schoolapp.service.TeacherServiceImpl;
import gr.aueb.cf.schoolapp.service.exceptions.TeacherNotFoundException;


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

@WebServlet("/schoolapp/searchTeacher")
public class SearchTeachersController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private EntityManagerFactory emf;
    private EntityManager entityManager;

    private TeacherDAOHibernateImpl teacherDAO = new TeacherDAOHibernateImpl(entityManager);
    private TeacherServiceImpl teacherService = new TeacherServiceImpl(teacherDAO);

    @Override
    public void init() throws ServletException {
        emf = Persistence.createEntityManagerFactory("myPU");
        entityManager = emf.createEntityManager();

        teacherDAO = new TeacherDAOHibernateImpl(entityManager);
        teacherService = new TeacherServiceImpl(teacherDAO);


    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher("/schoolapp/menu").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String lastname = request.getParameter("lastname").trim();
        HibernateHelper.clearEntityManager();

        try {
            List<Teacher> teachers = teacherService.getTeachersByLastname(lastname);
            if (teachers.isEmpty()) {
                request.setAttribute("teachersNotFound", true);
                request.getRequestDispatcher("/school/static/templates/teachersmenu.jsp").forward(request, response);
            } else {
                request.setAttribute("teachers", teachers);
                request.getRequestDispatcher("/school/static/templates/teachers.jsp").forward(request, response);
            }
        } catch (TeacherNotFoundException | TeacherDAOException e) {
            String message = e.getMessage();
            request.setAttribute("sqlError", true);
            request.setAttribute("message", message);
            request.getRequestDispatcher("/school/static/templates/teachersmenu.jsp").forward(request, response);
        }
    }

    @Override
    public void destroy() {
        HibernateHelper.closeEntityManager();
        HibernateHelper.closeEMF();
    }
}
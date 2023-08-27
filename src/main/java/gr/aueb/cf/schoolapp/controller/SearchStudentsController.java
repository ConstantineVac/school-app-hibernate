package gr.aueb.cf.schoolapp.controller;




import gr.aueb.cf.schoolapp.dao.StudentDAOHibernateImpl;
import gr.aueb.cf.schoolapp.dao.exceptions.StudentDAOException;
import gr.aueb.cf.schoolapp.model.Student;
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

@WebServlet("/schoolapp/searchStudent")
public class SearchStudentsController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("myPU");
    private EntityManager entityManager = emf.createEntityManager();

    private StudentDAOHibernateImpl studentDAO = new StudentDAOHibernateImpl(entityManager);
    private StudentServiceImpl studentService = new StudentServiceImpl(studentDAO);


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

//        request.setAttribute("isError", false);
//        request.setAttribute("error", "");
//        request.setAttribute("studentsNotFound", false);
        request.getRequestDispatcher("/schoolapp/menu")
                .forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String lastname = request.getParameter("lastname").trim();


        try {
            entityManager.clear();
            List<Student> students = studentService.getStudentsByLastname(lastname);
            if (students.isEmpty()) {
                request.setAttribute("studentsNotFound", true);
                request.getRequestDispatcher("/school/static/templates/studentsmenu.jsp")
                        .forward(request, response);
            } else {
                request.setAttribute("students", students);
                request.getRequestDispatcher("/school/static/templates/students.jsp").forward(request, response);
            }
        } catch (StudentDAOException | StudentNotFoundException e) {
            String message = e.getMessage();
            request.setAttribute("sqlError", true);
            request.setAttribute("message", message);
            request.getRequestDispatcher("/school/static/templates/studentsmenu.jsp").forward(request, response);
        }
    }
}

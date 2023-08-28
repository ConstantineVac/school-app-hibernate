package gr.aueb.cf.schoolapp.controller;

import gr.aueb.cf.schoolapp.dao.MeetingDAOHibernateImpl;
import gr.aueb.cf.schoolapp.dao.StudentDAOHibernateImpl;
import gr.aueb.cf.schoolapp.dao.dbutil.HibernateHelper;
import gr.aueb.cf.schoolapp.dao.exceptions.MeetingDAOException;
import gr.aueb.cf.schoolapp.dao.exceptions.StudentDAOException;
import gr.aueb.cf.schoolapp.model.Meeting;
import gr.aueb.cf.schoolapp.model.Student;
import gr.aueb.cf.schoolapp.service.MeetingServiceImpl;
import gr.aueb.cf.schoolapp.service.StudentServiceImpl;
import gr.aueb.cf.schoolapp.service.exceptions.MeetingNotFoundException;
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

@WebServlet("/schoolapp/deleteStudent")
public class DeleteStudentController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("myPU");
    private EntityManager entityManager = emf.createEntityManager();
    private StudentDAOHibernateImpl studentDAO = new StudentDAOHibernateImpl(entityManager);
    private StudentServiceImpl studentService = new StudentServiceImpl(studentDAO);
    private MeetingDAOHibernateImpl meetingDAO = new MeetingDAOHibernateImpl(entityManager);
    private MeetingServiceImpl meetingService = new MeetingServiceImpl(meetingDAO);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));

        Student studentToDelete = null;
        try {
            studentToDelete = studentService.getStudentById(id);
        } catch (StudentNotFoundException | StudentDAOException e) {
            handleError(request, response, e.getMessage());
            return;
        }

        List<Meeting> studentMeetings = studentToDelete.getMeetings();
        if (studentMeetings != null) {
            try {
                studentMeetings.stream()
                        .forEach(meeting -> {
                            try {
                                meetingService.deleteMeeting(meeting.getId());
                            } catch (MeetingDAOException | MeetingNotFoundException e) {
                                throw new RuntimeException(e);
                            }
                        });
            } catch (RuntimeException e) {
                Throwable cause = e.getCause();
                if (cause instanceof MeetingDAOException || cause instanceof MeetingNotFoundException) {
                    handleError(request, response, cause.getMessage());
                    return;
                }
            }
        }

        try {
            studentService.deleteStudent(id);
            request.setAttribute("studentDTO", studentToDelete);
            request.getRequestDispatcher("/school/static/templates/studentDeleted.jsp").forward(request, response);
        } catch (StudentDAOException | StudentNotFoundException e) {
            handleError(request, response, e.getMessage());
        }
    }

    private void handleError(HttpServletRequest request, HttpServletResponse response, String message)
            throws ServletException, IOException {
        request.setAttribute("deleteAPIError", true);
        request.setAttribute("message", message);
        request.getRequestDispatcher("/school/static/templates/students.jsp").forward(request, response);
    }

    @Override
    public void destroy () {
        HibernateHelper.closeEntityManager();
        HibernateHelper.closeEMF();
    }
}
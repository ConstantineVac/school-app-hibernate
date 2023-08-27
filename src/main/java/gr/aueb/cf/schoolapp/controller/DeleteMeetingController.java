package gr.aueb.cf.schoolapp.controller;


import gr.aueb.cf.schoolapp.dao.MeetingDAOHibernateImpl;
import gr.aueb.cf.schoolapp.dao.StudentDAOHibernateImpl;
import gr.aueb.cf.schoolapp.dao.TeacherDAOHibernateImpl;
import gr.aueb.cf.schoolapp.dao.exceptions.MeetingDAOException;
import gr.aueb.cf.schoolapp.dao.exceptions.StudentDAOException;
import gr.aueb.cf.schoolapp.dao.exceptions.TeacherDAOException;
import gr.aueb.cf.schoolapp.dto.MeetingDeleteDTO;
import gr.aueb.cf.schoolapp.model.Student;
import gr.aueb.cf.schoolapp.model.Teacher;
import gr.aueb.cf.schoolapp.service.MeetingServiceImpl;
import gr.aueb.cf.schoolapp.service.StudentServiceImpl;
import gr.aueb.cf.schoolapp.service.TeacherServiceImpl;
import gr.aueb.cf.schoolapp.service.exceptions.MeetingNotFoundException;

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

@WebServlet("/schoolapp/deleteMeeting")
public class DeleteMeetingController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("myPU");
    private EntityManager entityManager = emf.createEntityManager();
    private MeetingDAOHibernateImpl meetingDAO = new MeetingDAOHibernateImpl(entityManager);
    private MeetingServiceImpl meetingService = new MeetingServiceImpl(meetingDAO);
    private TeacherDAOHibernateImpl teacherDAO = new TeacherDAOHibernateImpl(entityManager);
    private TeacherServiceImpl teacherService = new TeacherServiceImpl(teacherDAO);
    private StudentDAOHibernateImpl studentDAO = new StudentDAOHibernateImpl(entityManager);
    private StudentServiceImpl studentService = new StudentServiceImpl(studentDAO);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        String teacherName = request.getParameter("teacher");
        String studentName = request.getParameter("student");
        String room = request.getParameter("room");
        String meetingDateStr = request.getParameter("meetingDate");

        // Convert meetingDate to java.sql.Date format
        java.sql.Date meetingDate = null;
        if (meetingDateStr != null && !meetingDateStr.isEmpty()) {
            try {
                meetingDate = java.sql.Date.valueOf(meetingDateStr);
            } catch (IllegalArgumentException e) {
                request.setAttribute("deleteAPIError", true);
                request.setAttribute("message", "Invalid meetingDate format. USE 'YYYY-MM-DD'.");
                request.getRequestDispatcher("/school/static/templates/meetings.jsp")
                        .forward(request, response);
                return;
            }
        }




            Student student = null;
            if (studentName!= null && !studentName.trim().isEmpty()) {
                try {
                    Optional<List<Student>> studentsOptional = studentDAO.getByLastname(studentName);

                    if (studentsOptional.isPresent() && !studentsOptional.get().isEmpty()) {
                        student = studentsOptional.get().get(0);
                    } else {
                        request.setAttribute("deleteAPIError", true);
                        request.setAttribute("message", "Student not found.");
                        request.getRequestDispatcher("/school/static/templates/meetings.jsp")
                                .forward(request, response);
                        return;
                    }
                } catch (StudentDAOException e) {
                    request.setAttribute("deleteAPIError", true);
                    request.setAttribute("message", "Failed to retrieve student.");
                    request.getRequestDispatcher("/school/static/templates/meetings.jsp")
                            .forward(request, response);
                    return;
                }
            }

        Teacher teacher = null;
        if (teacherName != null && !teacherName.trim().isEmpty()) {
            try {
                Optional<List<Teacher>> teachersOptional = teacherDAO.getByLastname(teacherName);
                if (teachersOptional.isPresent() && !teachersOptional.get().isEmpty()) {
                    teacher = teachersOptional.get().get(0);
                } else {
                    request.setAttribute("deleteAPIError", true);
                    request.setAttribute("message", "Teacher not found.");
                    request.getRequestDispatcher("/school/static/templates/meetings.jsp")
                            .forward(request, response);
                    return;
                }
            } catch (TeacherDAOException e) {
                request.setAttribute("deleteAPIError", true);
                request.setAttribute("message", "Failed to retrieve city.");
                request.getRequestDispatcher("/school/static/templates/meetings.jsp")
                        .forward(request, response);
                return;


            }

            MeetingDeleteDTO meetingDTO = new MeetingDeleteDTO();
            meetingDTO.setId(id);
            meetingDTO.setTeacher(teacher);
            meetingDTO.setStudent(student);
            meetingDTO.setRoom(room);
            meetingDTO.setMeetingDate(meetingDate);

            try {
                meetingService.deleteMeeting(id);
                request.setAttribute("meetingDTO", meetingDTO);
                request.getRequestDispatcher("/school/static/templates/meetingDeleted.jsp")
                        .forward(request, response);
            } catch (MeetingNotFoundException | MeetingDAOException e) {
                request.setAttribute("deleteAPIError", true);
                request.setAttribute("message", e.getMessage());
                request.getRequestDispatcher("/school/static/templates/meetingDeleted.jsp")
                        .forward(request, response);
            }
        }
    }
}
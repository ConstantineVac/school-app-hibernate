package gr.aueb.cf.schoolapp.controller;


import gr.aueb.cf.schoolapp.dao.MeetingDAOHibernateImpl;
import gr.aueb.cf.schoolapp.dao.StudentDAOHibernateImpl;
import gr.aueb.cf.schoolapp.dao.TeacherDAOHibernateImpl;
import gr.aueb.cf.schoolapp.dao.exceptions.MeetingDAOException;
import gr.aueb.cf.schoolapp.dao.exceptions.StudentDAOException;
import gr.aueb.cf.schoolapp.dao.exceptions.TeacherDAOException;
import gr.aueb.cf.schoolapp.dto.MeetingUpdateDTO;
import gr.aueb.cf.schoolapp.model.Meeting;
import gr.aueb.cf.schoolapp.model.Student;
import gr.aueb.cf.schoolapp.model.Teacher;
import gr.aueb.cf.schoolapp.service.MeetingServiceImpl;
import gr.aueb.cf.schoolapp.service.StudentServiceImpl;
import gr.aueb.cf.schoolapp.service.TeacherServiceImpl;

import gr.aueb.cf.schoolapp.service.exceptions.MeetingNotFoundException;
import gr.aueb.cf.schoolapp.service.exceptions.StudentNotFoundException;
import gr.aueb.cf.schoolapp.service.exceptions.TeacherNotFoundException;
import gr.aueb.cf.schoolapp.validator.MeetingValidator;

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

@WebServlet("/schoolapp/updateMeeting")
public class UpdateMeetingController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("myPU");
    private EntityManager entityManager = emf.createEntityManager();

    private MeetingDAOHibernateImpl meetingDAO = new MeetingDAOHibernateImpl(entityManager);
    private MeetingServiceImpl meetingService = new MeetingServiceImpl(meetingDAO);

    private StudentDAOHibernateImpl studentDAO = new StudentDAOHibernateImpl(entityManager);
    private StudentServiceImpl studentService = new StudentServiceImpl(studentDAO);

    private TeacherDAOHibernateImpl teacherDAO = new TeacherDAOHibernateImpl(entityManager);
    private TeacherServiceImpl teacherService = new TeacherServiceImpl(teacherDAO);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/school/static/templates/meetingUpdate.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Integer id = Integer.parseInt(request.getParameter("id"));
        String room = request.getParameter("room");
        String meetingDateStr = request.getParameter("meetingDate");

        // Validate and convert birthdate to java.sql.Date format
        java.sql.Date meetingDate = null;
        if (meetingDateStr != null && !meetingDateStr.isEmpty()) {
            try {
                meetingDate = java.sql.Date.valueOf(meetingDateStr);
            } catch (IllegalArgumentException e) {
                request.setAttribute("error", "Invalid birthdate format. Use 'YYYY-MM-DD'.");
                request.getRequestDispatcher("/school/static/templates/meetingsmenu.jsp")
                        .forward(request, response);
                return;
            }
        }

        Teacher teacher;
        try {
            teacher = getTeacherFromRequest(request);
        } catch (TeacherNotFoundException e) {
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/school/static/templates/meetingsmenu.jsp")
                    .forward(request, response);
            return;
        }


        Student student;
        try {
            student = getStudentFromRequest(request);
        } catch (StudentNotFoundException e) {
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/school/static/templates/meetingsmenu.jsp")
                    .forward(request, response);
            return;
        }



    MeetingUpdateDTO newMeetingDTO = new MeetingUpdateDTO();
        newMeetingDTO.setId(id);
        newMeetingDTO.setTeacher(teacher);
        newMeetingDTO.setStudent(student);
        newMeetingDTO.setRoom(room);
        newMeetingDTO.setMeetingDate(meetingDate);
        request.setAttribute("updatedMeeting", newMeetingDTO);

        try {
            Map<String, String> errors = MeetingValidator.validate(newMeetingDTO);

            if (!errors.isEmpty()) {
                String errorMessage = errors.entrySet().stream()
                        .map(entry -> entry.getKey() + ": " + entry.getValue())
                        .reduce((msg1, msg2) -> msg1 + " " + msg2)
                        .orElse("");
                request.setAttribute("error", errorMessage);
                request.getRequestDispatcher("/school/static/templates/meetingsmenu.jsp")
                        .forward(request, response);
                return;
            }


            Meeting meeting = meetingService.updateMeeting(newMeetingDTO);
            request.setAttribute("message", "");
            request.setAttribute("meetingUpdated", meeting);
            request.getRequestDispatcher("/school/static/templates/meetingUpdated.jsp")
                    .forward(request, response);
        } catch (MeetingNotFoundException | MeetingDAOException e) {
            String message = e.getMessage();
            request.setAttribute("message", message);
            request.getRequestDispatcher("/school/static/templates/meetingUpdated.jsp")
                    .forward(request, response);
        }


    }
    private Student getStudentFromRequest(HttpServletRequest request) throws StudentNotFoundException {
        String studentId = request.getParameter("studentId");
        if(studentId != null && !studentId.trim().isEmpty()) {
            try {
                Optional<Student> studentOptional = Optional.ofNullable(studentService.getStudentById(Integer.parseInt(studentId)));
                if (studentOptional.isPresent()) {
                    return studentOptional.get();
                }
            } catch (NumberFormatException e) {
                throw new StudentNotFoundException("Invalid student ID format.");
            } catch (StudentDAOException e) {
                throw new StudentNotFoundException("Student not found for provided ID.");
            }
        }
        throw new StudentNotFoundException("Student ID not provided or is empty.");
    }


    private Teacher getTeacherFromRequest(HttpServletRequest request) throws TeacherNotFoundException {
        String teacherId = request.getParameter("teacherId");
        if(teacherId != null && !teacherId.trim().isEmpty()) {
            try {
                Optional<Teacher> teacherOptional = Optional.ofNullable(teacherService.getTeacherById(Integer.parseInt(teacherId)));
                if (teacherOptional.isPresent()) {
                    return teacherOptional.get();
                }
            } catch (NumberFormatException e) {
                throw new TeacherNotFoundException("Invalid teacher ID format.");
            } catch (TeacherDAOException e) {
                throw new TeacherNotFoundException("Teacher not found for provided ID.");
            }
        }
        throw new TeacherNotFoundException("Teacher ID not provided or is empty.");
    }



    @Override
    public void destroy() {
        entityManager.close();
        emf.close();
    }
}

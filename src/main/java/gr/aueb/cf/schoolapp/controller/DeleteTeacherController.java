package gr.aueb.cf.schoolapp.controller;

import gr.aueb.cf.schoolapp.dao.MeetingDAOHibernateImpl;
import gr.aueb.cf.schoolapp.dao.SpecialtyDAOHibernateImpl;
import gr.aueb.cf.schoolapp.dao.TeacherDAOHibernateImpl;
import gr.aueb.cf.schoolapp.dao.dbutil.HibernateHelper;
import gr.aueb.cf.schoolapp.dao.exceptions.MeetingDAOException;
import gr.aueb.cf.schoolapp.dao.exceptions.TeacherDAOException;
import gr.aueb.cf.schoolapp.model.Meeting;
import gr.aueb.cf.schoolapp.model.Teacher;
import gr.aueb.cf.schoolapp.service.MeetingServiceImpl;
import gr.aueb.cf.schoolapp.service.SpecialtyServiceImpl;
import gr.aueb.cf.schoolapp.service.TeacherServiceImpl;
import gr.aueb.cf.schoolapp.service.exceptions.MeetingNotFoundException;
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

@WebServlet("/schoolapp/deleteTeacher")
public class DeleteTeacherController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private EntityManagerFactory emf = Persistence.createEntityManagerFactory("myPU");
	private EntityManager entityManager = emf.createEntityManager();
	private TeacherDAOHibernateImpl teacherDAO = new TeacherDAOHibernateImpl(entityManager);
	private TeacherServiceImpl teacherService = new TeacherServiceImpl(teacherDAO);
	private MeetingDAOHibernateImpl meetingDAO = new MeetingDAOHibernateImpl(entityManager);
	private MeetingServiceImpl meetingService = new MeetingServiceImpl(meetingDAO); // Initialize your MeetingService

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		int id = Integer.parseInt(request.getParameter("id"));
		Teacher teacherToDelete = null;
		try {
			teacherToDelete = teacherService.getTeacherById(id);
		} catch (TeacherNotFoundException | TeacherDAOException e) {
			handleError(request, response, e.getMessage());
			return;
		}

		List<Meeting> teacherMeetings = teacherToDelete.getMeetings();

		if (teacherMeetings != null) {
			try {
				for (Meeting meeting : teacherMeetings) {
					try {
						meetingService.deleteMeeting(meeting.getId());
					} catch (MeetingDAOException | MeetingNotFoundException e) {
						throw new RuntimeException(e);
					}
				}
			} catch (RuntimeException e) {
				Throwable cause = e.getCause();
				if (cause instanceof MeetingDAOException || cause instanceof MeetingNotFoundException) {
					handleError(request, response, cause.getMessage());
					return;
				}
			}
		}

		// Now delete the teacher
		try {
			teacherService.deleteTeacher(id);
		} catch (TeacherDAOException | TeacherNotFoundException e) {
			handleError(request, response, e.getMessage());
			return;
		}

		request.setAttribute("teacherDTO", teacherToDelete);
		request.getRequestDispatcher("/school/static/templates/teacherDeleted.jsp").forward(request, response);
	}

	@Override
	public void destroy() {
		HibernateHelper.closeEntityManager();
		HibernateHelper.closeEMF();
	}

	private void handleError(HttpServletRequest request, HttpServletResponse response, String message)
			throws ServletException, IOException {
		request.setAttribute("deleteAPIError", true);
		request.setAttribute("message", message);
		request.getRequestDispatcher("/school/static/templates/teachers.jsp").forward(request, response);
	}
}
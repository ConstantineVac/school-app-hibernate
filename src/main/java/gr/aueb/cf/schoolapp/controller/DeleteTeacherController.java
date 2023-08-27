package gr.aueb.cf.schoolapp.controller;





import gr.aueb.cf.schoolapp.dao.SpecialtyDAOHibernateImpl;
import gr.aueb.cf.schoolapp.dao.TeacherDAOHibernateImpl;
import gr.aueb.cf.schoolapp.dao.exceptions.SpecialtyDAOException;
import gr.aueb.cf.schoolapp.dao.exceptions.TeacherDAOException;
import gr.aueb.cf.schoolapp.dto.TeacherDeleteDTO;
import gr.aueb.cf.schoolapp.model.Specialty;
import gr.aueb.cf.schoolapp.service.SpecialtyServiceImpl;
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
import java.util.Optional;

@WebServlet("/schoolapp/deleteTeacher")
public class DeleteTeacherController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private EntityManagerFactory emf = Persistence.createEntityManagerFactory("myPU");
	private EntityManager entityManager = emf.createEntityManager();
	private TeacherDAOHibernateImpl teacherDAO = new TeacherDAOHibernateImpl(entityManager);
	private TeacherServiceImpl teacherService = new TeacherServiceImpl(teacherDAO);
	private SpecialtyDAOHibernateImpl specialtyDAO = new SpecialtyDAOHibernateImpl(entityManager);
	private SpecialtyServiceImpl specialtyService = new SpecialtyServiceImpl(specialtyDAO);

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		//response.setContentType("text/html; charset=UTF-8");
		int id = Integer.parseInt(request.getParameter("id"));
		String firstname = request.getParameter("firstname");
		String lastname = request.getParameter("lastname");
		String specialtyName = request.getParameter("specialty");


		Specialty specialty = null;
		if (specialtyName != null && !specialtyName.trim().isEmpty()) {
			try {
				Optional<List<Specialty>> specialtiesOptional = specialtyDAO.getSpecialtyByName(specialtyName);
				if (specialtiesOptional.isPresent() && !specialtiesOptional.get().isEmpty()) {
					specialty = specialtiesOptional.get().get(0);
				} else {
					request.setAttribute("deleteAPIError", true);
					request.setAttribute("message", "Specialty not found.");
					request.getRequestDispatcher("/school/static/templates/teachers.jsp")
							.forward(request, response);
					return;
				}
			} catch (SpecialtyDAOException e) {
				request.setAttribute("deleteAPIError", true);
				request.setAttribute("message", "Failed to retrieve specialty.");
				request.getRequestDispatcher("/school/static/templates/teachers.jsp")
						.forward(request, response);
				return;
			}
		}


		TeacherDeleteDTO teacherDTO = new TeacherDeleteDTO();
		teacherDTO.setId(id);
		teacherDTO.setFirstname(firstname);
		teacherDTO.setLastname(lastname);
		teacherDTO.setSpecialty(specialty);
		try {
			teacherService.deleteTeacher(id);
			request.setAttribute("teacherDTO", teacherDTO);
			request.getRequestDispatcher("/school/static/templates/teacherDeleted.jsp")
					.forward(request, response);
		} catch (TeacherNotFoundException | TeacherDAOException e) {
			request.setAttribute("deleteAPIError", true);
			request.setAttribute("message", e.getMessage());
			request.getRequestDispatcher("/school/static/templates/teachers.jsp")
					.forward(request, response);
		}
	}
}


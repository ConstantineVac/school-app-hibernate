package gr.aueb.cf.schoolapp.controller;


import gr.aueb.cf.schoolapp.dao.SpecialtyDAOHibernateImpl;
import gr.aueb.cf.schoolapp.dao.TeacherDAOHibernateImpl;
import gr.aueb.cf.schoolapp.dao.exceptions.SpecialtyDAOException;
import gr.aueb.cf.schoolapp.dao.exceptions.TeacherDAOException;
import gr.aueb.cf.schoolapp.dto.TeacherUpdateDTO;
import gr.aueb.cf.schoolapp.model.Specialty;
import gr.aueb.cf.schoolapp.model.Teacher;
import gr.aueb.cf.schoolapp.service.SpecialtyServiceImpl;
import gr.aueb.cf.schoolapp.service.TeacherServiceImpl;
import gr.aueb.cf.schoolapp.service.exceptions.SpecialtyNotFoundException;
import gr.aueb.cf.schoolapp.service.exceptions.TeacherNotFoundException;
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
import java.util.Map;
import java.util.Optional;

@WebServlet("/schoolapp/updateTeacher")
public class UpdateTeacherController extends HttpServlet {
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
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.getRequestDispatcher("/school/static/templates/teacherUpdate.jsp")
				.forward(request, response);
	}


	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		int id = Integer.parseInt(request.getParameter("id"));
		String firstname = request.getParameter("firstname");
		String lastname = request.getParameter("lastname");

		Specialty specialty;
		try {
			specialty = getSpecialtyFromRequest(request);
		} catch (SpecialtyNotFoundException e) {
			request.setAttribute("error", e.getMessage());
			request.getRequestDispatcher("/school/static/templates/teachersmenu.jsp")
					.forward(request, response);
			return;
		}

		TeacherUpdateDTO newTeacherDTO = new TeacherUpdateDTO();
		newTeacherDTO.setId(id);
		newTeacherDTO.setFirstname(firstname);
		newTeacherDTO.setLastname(lastname);
		newTeacherDTO.setSpecialty(specialty);  // Set the Specialty object directly
		request.setAttribute("updatedTeacher", newTeacherDTO);

		try {
			Map<String, String> errors = TeacherValidator.validate(newTeacherDTO);

			if (!errors.isEmpty()) {
				String errorMessage = errors.entrySet().stream()
						.map(entry -> entry.getKey() + ": " + entry.getValue())
						.reduce((msg1, msg2) -> msg1 + " " + msg2)
						.orElse("");
				request.setAttribute("error", errorMessage);
				request.getRequestDispatcher("/school/static/templates/teachersmenu.jsp")
						.forward(request, response);
				return;
			}

			Teacher teacher = teacherService.updateTeacher(newTeacherDTO);
			entityManager.clear();  // Clear the Hibernate entity cache

			request.setAttribute("message", "");
			request.setAttribute("teacher", teacher);
			request.getRequestDispatcher("/school/static/templates/teacherUpdated.jsp")
					.forward(request, response);
		} catch (TeacherNotFoundException | TeacherDAOException e) {
			String message = e.getMessage();
			request.setAttribute("message", message);
			request.getRequestDispatcher("/schoolapp/static/templates/teacherUpdated.jsp")
					.forward(request, response);
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




	@Override
	public void destroy() {
		entityManager.close();
		emf.close();
	}
}

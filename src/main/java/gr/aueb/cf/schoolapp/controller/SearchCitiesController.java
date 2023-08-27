package gr.aueb.cf.schoolapp.controller;




import gr.aueb.cf.schoolapp.dao.CityDAOHibernateImpl;
import gr.aueb.cf.schoolapp.dao.exceptions.CityDAOException;
import gr.aueb.cf.schoolapp.model.City;
import gr.aueb.cf.schoolapp.service.CityServiceImpl;
import gr.aueb.cf.schoolapp.service.exceptions.CityNotFoundException;


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

@WebServlet("/schoolapp/searchCity")
public class SearchCitiesController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("myPU");
    private EntityManager entityManager = emf.createEntityManager();

    private CityDAOHibernateImpl cityDAO = new CityDAOHibernateImpl(entityManager);
    private CityServiceImpl cityService = new CityServiceImpl(cityDAO);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

//        request.setAttribute("isError", false);
//        request.setAttribute("error", "");
//        request.setAttribute("teachersNotFound", false);
        request.getRequestDispatcher("/schoolapp/menu")
                .forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //response.setContentType("text/html; charset=UTF-8");
        String name = request.getParameter("name").trim();


        try {
            entityManager.clear();
            List<City> cities = cityService.getCitiesByCityName(name);
            if (cities.isEmpty()) {
                request.setAttribute("citiesNotFound", true);
                request.getRequestDispatcher("/school/static/templates/citiesmenu.jsp")
                        .forward(request, response);
            } else {
                request.setAttribute("cities", cities);
                request.getRequestDispatcher("/school/static/templates/cities.jsp").forward(request, response);
            }
        } catch (CityDAOException | CityNotFoundException e) {
            String message = e.getMessage();
            request.setAttribute("sqlError", true);
            request.setAttribute("message", message);
            request.getRequestDispatcher("/school/static/templates/citiesmenu.jsp").forward(request, response);


        }
    }
}

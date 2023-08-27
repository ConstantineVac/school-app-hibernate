package gr.aueb.cf.schoolapp.controller;




import gr.aueb.cf.schoolapp.dao.MeetingDAOHibernateImpl;
import gr.aueb.cf.schoolapp.dao.dbutil.HibernateHelper;
import gr.aueb.cf.schoolapp.dao.exceptions.MeetingDAOException;
import gr.aueb.cf.schoolapp.model.Meeting;
import gr.aueb.cf.schoolapp.service.MeetingServiceImpl;
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


@WebServlet("/schoolapp/searchMeeting")
public class SearchMeetingController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("myPU");
    private EntityManager entityManager = emf.createEntityManager();

    private MeetingDAOHibernateImpl meetingDAO = new MeetingDAOHibernateImpl(entityManager);
    private MeetingServiceImpl meetingService = new MeetingServiceImpl(meetingDAO);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/schoolapp/menu")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String room = request.getParameter("room").trim();



        try {
            HibernateHelper.clearEntityManager();
            List<Meeting> meetings = meetingService.getMeetingByRoom(room);
            if (meetings.isEmpty()) {
                request.setAttribute("meetingsNotFound", true);
                request.getRequestDispatcher("/school/static/templates/meetingsmenu.jsp")
                        .forward(request, response);
            }
            request.setAttribute("meetings", meetings);
            request.getRequestDispatcher("/school/static/templates/meetings.jsp")
                    .forward(request, response);
        } catch (MeetingDAOException | MeetingNotFoundException e) {
           String message = e.getMessage();
            request.setAttribute("sqlError", true);
            request.setAttribute("message", message);
            request.getRequestDispatcher("/school/static/templates/meetingsmenu.jsp")
                    .forward(request, response);
        }
    }
    @Override
    public void destroy() {
        HibernateHelper.closeEntityManager();
        HibernateHelper.closeEMF();
    }
}

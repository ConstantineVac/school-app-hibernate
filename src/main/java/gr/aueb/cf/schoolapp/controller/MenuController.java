package gr.aueb.cf.schoolapp.controller;



import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@WebServlet("/schoolapp/menu")
public class MenuController extends HttpServlet {

    @Override


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("studentsNotFound", false);
        request.setAttribute("teachersNotFound", false);
        request.setAttribute("citiesNotFound",false);
        request.setAttribute("specialtiesNotFound", false);
        request.setAttribute("meetingsNotFound",false);
        request.setAttribute("isError", false);
        request.setAttribute("error", "");



        request.getRequestDispatcher("/school/static/templates/controlPanel.jsp").forward(request, response);
    }
}


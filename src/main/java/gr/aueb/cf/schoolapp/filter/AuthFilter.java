
package gr.aueb.cf.schoolapp.filter;



import javax.servlet.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;


import java.io.IOException;

public class AuthFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        boolean authenticated = false;

        String requestedURI = req.getRequestURI();
//        if (requestedURI.endsWith(".css")) {
//            chain.doFilter(req, response);
//        } else {
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("JSESSIONID")) {
                    HttpSession session = req.getSession(false);
//                        System.out.println(session.getId());
//                        System.out.println(session.getAttribute("loginName"));
                    authenticated = (session != null) && (session.getAttribute("loginName") != null);
                }
            }
        }
        //  }

        if (authenticated) {
            chain.doFilter(request, response);
        } else {
            res.sendRedirect(req.getContextPath() + "/login");
        }
    }
}

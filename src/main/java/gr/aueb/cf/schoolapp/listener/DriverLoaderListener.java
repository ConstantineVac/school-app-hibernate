package gr.aueb.cf.schoolapp.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class DriverLoaderListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to initialize MySQL driver", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // No cleanup needed for the driver
    }
}

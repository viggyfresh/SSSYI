package listing;

import java.util.logging.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import main.Startup;
import db.DatabaseModule;

// Handles initialization of project on startup of server =)
// Also handles shutdown of project on closing of server
public class ServletListener implements ServletContextListener {

	private static final Logger log = Logger.getLogger(ServletListener.class.getName());

	public ServletListener() {
		// Nothing
	}

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		try {
			Startup.shutdown();
		} 
		catch (Exception ex) {
			log.severe(ex.toString());
			StackTraceElement[] st = ex.getStackTrace();
			for (int i = 0; i < st.length; i++) {
				log.severe(st[i].toString());
			}
			ex.printStackTrace();
		}
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		Startup.init();
	}

}

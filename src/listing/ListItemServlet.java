package listing;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import db.DatabaseModule;
import ebay.EbayServiceModule;

/**
 * Servlet implementation class ListItemServlet
 */
@SuppressWarnings("serial")
public class ListItemServlet extends HttpServlet {
	
	private static final Logger log = Logger.getLogger(ListItemServlet.class.getName());       
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String keyString = request.getParameter("key");
		Long id = Long.parseLong(keyString);
		try {
			Listing l = DatabaseModule.retrieveListing(id);
			String[] results = EbayServiceModule.actuallyListItem(l, id, null);
			String nextURL = "done.jsp?id=" + results[1];
			response.sendRedirect(nextURL);
			return;
		}
		catch (Exception ex) {
			log.severe(ex.toString());
			StackTraceElement[] st = ex.getStackTrace();
			for (int j = 0; j < st.length; j++) {
				log.severe(st[j].toString());
			}
			ex.printStackTrace();
		}
	}

}

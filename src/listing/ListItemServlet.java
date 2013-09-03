package listing;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import db.DatabaseModule;
import ebay.EbayServiceModule;

/*
 * Servlet that actually lists the item to eBay. Makes a call to the actuallyListItem and reviseListing
 * functions in EbayServiceModule, and a database call to retrieve the listing in question.
 */
@SuppressWarnings("serial")
public class ListItemServlet extends HttpServlet {

	private static final Logger log = Logger.getLogger(ListItemServlet.class.getName());       
	/*
	 * Only post requests are honored
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String keyString = request.getParameter("key");
		Long id = Long.parseLong(keyString);
		try {
			Listing l = DatabaseModule.retrieveListing(id);
			String finalized = l.getFinalized();
			String[] results;
			// If we've listed it before...
			if (isLongParseable(finalized)) {
				// Revise the item
				results = EbayServiceModule.reviseItem(l, id, null);
			}
			else {
				// Otherwise list it fresh
				results = EbayServiceModule.actuallyListItem(l, id, null);
			}
			// Take user to final page of flow
			DatabaseModule.updateFinalized(id, results[1]);
			String nextURL = "done.jsp?id=" + results[1] + "&key=" + keyString;
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
	
	// Helper function to check if a string is numeric
	private boolean isLongParseable(String finalized) {
		try {  
			long l = Long.parseLong(finalized);  
		}  
		catch(Exception ex) {  
			return false;  
		}  
		return true;  
	}
}

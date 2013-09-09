package listing;

import java.io.IOException;
import java.util.logging.Logger;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import db.DatabaseModule;
import ebay.EbayServiceModule;
import email.EmailSender;

/*
 * Servlet that actually lists the item to eBay. Makes a call to the actuallyListItem and reviseListing
 * functions in EbayServiceModule, and a database call to retrieve the listing in question.
 */
@SuppressWarnings("serial")
public class ListItemServlet extends HttpServlet {

	private static final Logger log = Logger.getLogger(ListItemServlet.class.getName());    
	private static final String BASE_URL = "http://emailtolisting-16253.phx-os1.stratus.dev.ebay.com:8080/PlainTextSYI/";

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
			if (results[0].equals("Error")) {
				String text = results[1];
				String nextURL = "error.jsp?key=" + keyString + "&text=" + text;
				response.sendRedirect(nextURL);
				return;
			}
			else {
				// Take user to final page of flow
				DatabaseModule.updateFinalized(id, results[1]);
				InternetAddress from = new InternetAddress(l.getEmail());
				String replyBody = "Congratulations! Your item has been listed.\n";
				replyBody += "You can view your item here: http://www.ebay.com/itm/" + results[1] + "\n";
				replyBody += "To revise it, go here: " + BASE_URL + "web/listing.jsp?key=" + keyString;
				String[] content;
				String title = l.getTitle();
				if (title.contains(" ")) content = title.split(" ");
				else {
					content = new String[1];
					if (!title.equals("")) content[0] = title;
					else content[0] = "Listing";
				}
				String subject = "";
				if (content.length == 1) subject = "Your item \"" + content[0] + "\" has been successfully listed!";
				else if (content.length == 2) subject = "Your item \"" + content[0] + " " + content[1] + "\" has been successfully listed!";
				else if (content.length >= 3) subject = "Your item \"" + content[0] + " " + content[1] +  " " + content[2] + "\" has been successfully listed!";
				EmailSender.sendEmail(from, subject, replyBody);
				log.info("Confirmation email sent.");		
				String nextURL = "done.jsp?id=" + results[1] + "&key=" + keyString;
				response.sendRedirect(nextURL);
				return;
			}
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

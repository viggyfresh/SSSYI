package listing;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONStringer;

import db.DatabaseModule;


/* Deals with form input from listing.jsp, stores necessary data in DB, redirects user to 
 * final confirmation page.
 */
@SuppressWarnings("serial")
public class FinalizeServlet extends HttpServlet {

	private static final Logger log = Logger.getLogger(FinalizeServlet.class.getName());

	// Only accepts post requests =)
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
		// If no key string, user has illegally navigated here; tell him/her to get lost
		String keyString = req.getParameter("key");
		if (keyString == null) {
			String nextURL = "finalized.jsp";
			resp.sendRedirect(nextURL);
			return;
		}
		// Otherwise...
		else {
			// Retrieve all other parameters of the entity for future use
			String categoryIndex = req.getParameter("whatCategoryIndex");
			String title = req.getParameter("title");
			if (title == null) title = "";
			String body = req.getParameter("body");
			if (body == null) body = "";
			String price = req.getParameter("price");
			if (price == null) price = "";
			String buyItNow = (String) req.getParameter("buyItNow");
			if (buyItNow == null)
				buyItNow = "";
			String condition = req.getParameter("condition");
			if (condition == null) condition = "";
			String time = req.getParameter("time");
			if (time == null) time = "";
			String category = req.getParameter("category");
			if (category == null) category = "";
			String shippingChoice = req.getParameter(categoryIndex + "shipping");
			if (shippingChoice == null) shippingChoice = "";
			String attribute = req.getParameter("attributes");
			if (attribute == null) attribute = "";
			String captcha = req.getParameter("captcha");
			if (captcha == null) captcha = "";
			String location = req.getParameter("location");
			if (location == null) location = "";
			String handlingTime = req.getParameter("handlingTime");
			if (handlingTime == null) handlingTime = "";
			if (handlingTime.equals("disabled")) handlingTime = "3";
			String returns = req.getParameter("returns");
			if (returns == null) returns = "";
			// Populate the new entry with all fields (title, body, price, condition, etc
			try {
				// Deal with captcha errors
				boolean binadjusted = false;
				String captchaOfficial = (String) req.getParameter("captchaText");
				log.info("User captcha: " + captcha);
				log.info("Actual captcha: " + captchaOfficial);
				if (!captcha.equalsIgnoreCase(captchaOfficial)) {
					String nextURL = "/web/listing.jsp?key=" + new String(keyString) + "&badCaptcha=true";
					RequestDispatcher dispatch = req.getRequestDispatcher(nextURL);
					dispatch.forward(req, resp);
					return;
				}
				// Deal with title and body
				title = StringEscapeUtils.escapeHtml4(title);
				body = StringEscapeUtils.escapeHtml4(body);
				price = price.replaceAll("\\$", "");
				// Deal with price
				double listPrice = 0;
				if (!price.equals("")) {
					listPrice = Double.parseDouble(price);
					BigDecimal bdprice = new BigDecimal(price);
					bdprice = bdprice.setScale(2, BigDecimal.ROUND_HALF_UP);
					price = bdprice.toString();
					if (!buyItNow.equals("")) {
						double BINprice;
						try {
							buyItNow = buyItNow.replaceAll("\\$", "");
							BINprice = Double.parseDouble(buyItNow);
						}
						catch (NumberFormatException e) {
							BINprice = -1;
						}
						if (listPrice * 1.3 > BINprice && BINprice != -1) {
							buyItNow = "";
							binadjusted = true;
						}
						else {
							BigDecimal bdBIN = new BigDecimal(buyItNow);
							bdBIN = bdBIN.setScale(2, BigDecimal.ROUND_HALF_UP);
							buyItNow = bdBIN.toString();
						}
					}
				}
				else {
					price = "";
					buyItNow = "";
				}
				// Deal with IP, specifics
				String IP = req.getRemoteAddr();
				String specifics = "[]";
				log.info("Category index: " + categoryIndex);
				String countString = req.getParameter(categoryIndex + "specCount");
				log.info("Count of required specifics: " + countString);
				if (countString != null) {
					Integer count = Integer.parseInt(countString);
					JSONStringer stringer = new JSONStringer();
					stringer.array();
					for (int i = 0; i < count; i++) {
						stringer.object();
						String currKey = req.getParameter(categoryIndex + "specKey" + i);
						String currVal = req.getParameter(categoryIndex + "specValue" + i);
						stringer.key("key");
						stringer.value(currKey);
						stringer.key("value");
						stringer.value(currVal);
						stringer.endObject();
					}
					stringer.endArray();
					specifics = stringer.toString();
					log.info("Specifics JSON text: " + specifics);
				}
				// Update listing in DB
				DatabaseModule.updateListing(Long.parseLong(keyString), title, body, price, buyItNow, condition, time, category, shippingChoice, attribute, location, handlingTime, returns, specifics, IP);
				// Create the response page, job's done
				String nextURL;
				if (!binadjusted) nextURL = "finalized.jsp?key=" + new String(keyString);
				else nextURL = "finalized.jsp?key=" + new String(keyString) + "&adj=true";
				resp.sendRedirect(nextURL);
				return;
			}
			// On error, crap out
			catch (Exception ex) {
				log.severe(ex.toString());
				StackTraceElement[] st = ex.getStackTrace();
				for (int i = 0; i < st.length; i++) {
					log.severe(st[i].toString());
				}
				ex.printStackTrace();
				String nextURL = "finalized.jsp";
				resp.sendRedirect(nextURL);
				return;
			}
		}
	}
}


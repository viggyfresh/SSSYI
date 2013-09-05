package listing;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JsonReader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.toyz.litetext.FontUtils;

import com.ebay.sdk.ApiException;
import com.ebay.sdk.SdkException;
import com.ebay.soap.eBLBaseComponents.SuggestedCategoryType;

import db.DatabaseModule;
import ebay.EbayServiceModule;
import email.EmailSender;

/*
 * Takes in email, turns it into a listing object
 */

public class EmailToListing {

	private static final Logger log = Logger.getLogger(EmailToListing.class.getName());

	private static final int IMAGE_MAX = 12;

	private static final String regex = "\\$\\s*\\d*[.]?\\d{0,10}";

	private static final String BASE_URL = "http://emailtolisting-16253.phx-os1.stratus.dev.ebay.com:8080/PlainTextSYI/";

	private static final String HARDCODE_URL = "C:/Program Files (x86)/Apache Software Foundation/Tomcat 7.0/webapps/PlainTextSYI/";
	//private static final String HARDCODE_URL = "/Users/vivenkataraman/Documents/PlainTextSYI/PlainTextSYI/war/";

	private static final String IMAGE_URL = "files/images/";

	public static void toListing(Message message) throws IOException {
		// Initialize needed vars
		Message msg = message;
		List<BodyPart> images = new ArrayList<BodyPart>();
		ArrayList<String> types = new ArrayList<String>();
		try {
			// Parse message for images, types, title, and body
			String[] titleAndBody = parseMessage(msg, images, types);
			if (images.size() == 0) {
				sendErrorReply(msg);
				return;
			}
			else {
				// Get count (unique identifier) from database
				long count = DatabaseModule.getCount();
				// Init new listing
				Listing entry = new Listing();
				// Set all fields for listing
				setTextFields(titleAndBody, entry);
				setAndStoreImages(images, types, entry, count);
				setPrice(entry);
				setCategories(entry);
				setCaptcha(entry, count);
				entry.setEmail(msg.getFrom()[0].toString());
				setRequiredSpecifics(entry);

				//System.out.println(sql);
				// Store listing to DB
				DatabaseModule.storeListing(count, entry);
				// Send reply
				formatReplyMessage(count, msg, entry.getFullTitle());
			}
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

	// Sets the required specifics for an entry using getCategorySpecifics based on the given categories
	private static void setRequiredSpecifics(Listing entry) throws ApiException, SdkException, Exception {
		int count = 0;
		for (int i = 0; i < 3; i++) {
			if (!entry.getCategories().get(i).equals("")) count++;
		}
		String[] iKilledTheEmpties = new String[count];
		for (int i = 0; i < count; i++) {
			iKilledTheEmpties[i] = EbayServiceModule.getCategoryIDFromCategory(entry.getCategories().get(i));
		}
		// JSON return format documented in EbayServiceModule
		ArrayList<String> requiredSpecificsArray = EbayServiceModule.getCategorySpecifics(iKilledTheEmpties);
		if (requiredSpecificsArray.size() > 0) {
			entry.setReqSpecifics0(requiredSpecificsArray.get(0));
		}
		if (requiredSpecificsArray.size() > 1) {
			entry.setReqSpecifics1(requiredSpecificsArray.get(1));
		}
		if (requiredSpecificsArray.size() > 2) {
			entry.setReqSpecifics2(requiredSpecificsArray.get(2));
		}
	}

	// Sets listing title, body, and fulltitle
	private static void setTextFields(String[] titleAndBody, Listing entry) {
		String fullTitle = titleAndBody[0];
		entry.setFullTitle(fullTitle);
		log.info("Full title: " + fullTitle);
		String tempTitle = titleAndBody[0];
		int lastWordIndex = -1;
		if (titleAndBody[0].length() >= 81) {
			lastWordIndex = titleAndBody[0].length() - 1;
			while (lastWordIndex > 0) {
				if ((titleAndBody[0].charAt(lastWordIndex) == ' ') && lastWordIndex <= 80) break;
				lastWordIndex--;
			}
			if (lastWordIndex == 0) lastWordIndex = 80;
			tempTitle = titleAndBody[0].substring(0, lastWordIndex);
		}
		String storeable = StringEscapeUtils.escapeHtml4(tempTitle);
		entry.setTitle(storeable);
		log.info("Contracted title: " + storeable);
		if (lastWordIndex != -1) titleAndBody[1] = titleAndBody[1] + " " + fullTitle.substring(lastWordIndex + 1);
		titleAndBody[1] = StringEscapeUtils.escapeHtml4(titleAndBody[1]);
		entry.setBody(titleAndBody[1]);
		log.info("Body: " + titleAndBody[1]);

	}

	// Write the images to the disk, save URLs to the entry
	private static void setAndStoreImages(List<BodyPart> images, ArrayList<String> types, Listing entry, long count) throws IOException, InterruptedException, MessagingException {
		ArrayList<String> imageURLs = new ArrayList<String>();
		for (int i = 0; i < 12; i++) {
			if (i < images.size()) {
				BodyPart curr = images.get(i);
				String mimeType = types.get(i);
				String[] typeSplit = mimeType.split("/");
				String imageURL = "" + count + "_" + i + "." + typeSplit[1].toLowerCase();
				OutputStream out = new BufferedOutputStream(new FileOutputStream(HARDCODE_URL + IMAGE_URL + imageURL));
				IOUtils.copy(curr.getInputStream(), out);
				if (out != null) out.close();
				imageURLs.add(BASE_URL + IMAGE_URL + imageURL);
			}
			else imageURLs.add("");
		}
		entry.setUrls(imageURLs);
		log.info("Images found: " + images.size());
	}

	// Sets price of entry
	private static void setPrice(Listing entry) {
		// Retrieve the price (if gettable), store it too
		String price = "";
		Pattern mine = Pattern.compile(regex);
		Matcher m1 = mine.matcher(entry.getFullTitle());
		Matcher m2 = mine.matcher(entry.getBody());
		if (m1.find()) {
			price = m1.group();
		}
		else if (m2.find()) {
			price = m2.group();
		}
		price = price.replaceAll("\\$", "");
		price = price.trim();
		// Format the price, if it exists, then set the entry's property
		if (!price.equals("")) {
			BigDecimal bdprice = new BigDecimal(price);
			bdprice = bdprice.setScale(2, BigDecimal.ROUND_HALF_UP);
			String tempPrice = bdprice.toString();
			price = tempPrice;
		}
		else {
			price = "";
		}
		entry.setPrice(price);
		log.info("Price: " + price);
	}

	// Sets categories for the listing using getSuggestedCategories call
	private static void setCategories(Listing entry) throws IOException {
		ArrayList<String> categories = new ArrayList<String>();
		ArrayList<String> categoryIDs = new ArrayList<String>();
		SuggestedCategoryType[] suggestedCategories = EbayServiceModule.getSuggestedCategories(entry.getFullTitle());
		for (int i = 0; i < 3; i++) {
			if (i < suggestedCategories.length) {
				SuggestedCategoryType curr = suggestedCategories[i];
				String[] parents = curr.getCategory().getCategoryParentName();
				String output = "";
				for (String s : parents) {
					output += s + " > ";
				}
				output += curr.getCategory().getCategoryName();
				output += " (" + curr.getCategory().getCategoryID() + ")";
				categories.add(output);
				categoryIDs.add(curr.getCategory().getCategoryID());
				log.info("Category " + i + ": " + categories.get(i));
			}
			else {
				categories.add("");
				log.info("Category " + i + " is empty");
			}
		}
		entry.setCategories(categories);
		setShippingOptions(entry, categoryIDs);
		setAttributes(entry, categoryIDs);
	}

	// Sets shipping options, currently uses poseidon (ugh)
	private static void setShippingOptions(Listing entry, ArrayList<String> categoryIDs) {
		ArrayList<Document> shippingPages = new ArrayList<Document>();
		ArrayList<String> shippingOptions = new ArrayList<String>();
		for (int i = 0; i < categoryIDs.size(); i++) {
			if (i < 3) {
				String posURL = "http://labs.ebay.com/poseidon/QueryServletUS?i=" + categoryIDs.get(i) + "&m=0_US";
				log.info("Poseidon URL: " + posURL);
				Document doc;
				try {
					doc = Jsoup.connect(posURL).get();
				}
				catch (IOException ex){
					log.severe("Couldn't connect to Poseidon!");
					log.severe(ex.toString());
					StackTraceElement[] st = ex.getStackTrace();
					for (int j = 0; j < st.length; j++) {
						log.severe(st[j].toString());
					}
					ex.printStackTrace();
					doc = null;
				}
				shippingPages.add(doc);
			}
		}
		//Follow the category links to get the appropriate shipping options
		int count = 0;
		for (int j = 0; j < shippingPages.size(); j++) {
			Document tempDoc = shippingPages.get(j);
			if (tempDoc == null) continue;
			Elements options = tempDoc.select("span");
			ListIterator<Element> iter = options.listIterator();
			for (int i = 0; i < 2; i++) {
				if (!iter.hasNext()) {
					shippingOptions.add(""); 
					count++;
					continue;
				}
				String carrier = "";
				try {
					carrier = iter.next().ownText() + ", " + iter.next().text();
				}
				catch (NoSuchElementException ex) {
					log.info("Bogus shipping option");
					shippingOptions.add("");
					count++;
					continue;
				}
				try {
					if (carrier.startsWith("to")) carrier = iter.next().ownText() + ", " + iter.next().text();
				}
				catch (NoSuchElementException ex) {
					log.info("Bogus shipping option");
					shippingOptions.add("");
					count++;
					continue;
				}
				if (!iter.hasNext()) {
					log.info("Bogus shipping option");
					shippingOptions.add(""); 
					count++;
					continue;
				}
				String maxWeight = "";
				try {
					maxWeight = iter.next().text() + " " + iter.next().text();
				}
				catch (NoSuchElementException ex) {
					log.info("Bogus shipping option");
					shippingOptions.add("");
					count++;
					continue;
				}
				if (maxWeight.startsWith("Cost")) {
					String shippingOption = carrier + "; " + maxWeight + ".";
					shippingOptions.add(StringEscapeUtils.escapeHtml4(shippingOption)); 
					count++;
					continue;
				}
				String dimensions = "";
				try {
					dimensions = iter.next().text();
				}
				catch (NoSuchElementException ex) {
					log.info("Bogus shipping option");
					shippingOptions.add("");
					count++;
					continue;
				}
				if (dimensions.startsWith("Cost")) {
					dimensions += (" " + iter.next().text());
					String shippingOption = carrier + "; " + maxWeight + "; " + dimensions + ".";
					shippingOptions.add(StringEscapeUtils.escapeHtml4(shippingOption)); 
					count++;
					continue;
				}
				try {
					dimensions += (" " + iter.next().text() + " " + iter.next().text() + " " + iter.next().text() + " " + iter.next().text() + " " + iter.next().text());
				}
				catch (NoSuchElementException ex) {
					log.info("Bogus shipping option");
					shippingOptions.add("");
					count++;
					continue;
				}
				try {
					String cost = iter.next().text() + " " + iter.next().text();
					String shippingOption = carrier + "; " + maxWeight + "; " + dimensions + "; " + cost;
					if (i == 1 && iter.hasNext()) {
						shippingOption += (" " + iter.next().text() + " " + iter.next().text() + ".");
					}
					else {
						shippingOption += ".";
					}
					log.info("ShippingOption" + count + ": " + shippingOption);
					shippingOptions.add(StringEscapeUtils.escapeHtml4(shippingOption)); 
					count++;
				}
				catch (NoSuchElementException ex) {
					log.info("Bogus shipping option");
					shippingOptions.add("");
					count++;
					continue;
				}
			}
		}
		if (shippingOptions.size() < 6) {
			for (int k = shippingOptions.size(); k < 6; k++) {
				shippingOptions.add("");
			}
		}
		entry.setShippingOptions(shippingOptions);

	}

	// Sets the attributes using the ebay service URL
	private static void setAttributes (Listing entry, ArrayList<String> categoryIDs) throws UnsupportedEncodingException {
		ArrayList<String> attributes = new ArrayList<String>();
		// Parse for entry.getAttributes() based on the categories retrieved
		for (int i = 0; i < 3; i++) {
			String netAttributes = "";
			String input = URLEncoder.encode(entry.getFullTitle(), "UTF-8");
			input = input.replaceAll("%26", "");
			try {
				String attrURL = "http://svcs.ebay.com/attr/v2/extract/bySiteCategoryAndTitle/json/0/" + categoryIDs.get(i) + "/" + input;
				log.info("Attribute URL: " + attrURL);
				JSONObject json = JsonReader.readJsonFromUrl(attrURL);
				JSONArray test = json.getJSONArray("attributes");
				for (int j = 0; j < test.length(); j++) {
					JSONObject curr = test.getJSONObject(j);
					JSONObject currAttribute = curr.getJSONObject("attribute");
					netAttributes += (currAttribute.get("attributeType") + " : " + currAttribute.get("attributeValue") + "\n");
				}
				log.info("Attribute" + i + ": " + netAttributes);
				attributes.add(StringEscapeUtils.escapeHtml4(netAttributes));
				if (i == 0) entry.setAttribute(StringEscapeUtils.escapeHtml4(netAttributes));
			}
			catch (Exception ex) {
				log.info("Attribute" + i + " is empty");
				attributes.add("");
				if (i == 0) entry.setAttribute("");
			}
		}
		entry.setAttributes(attributes);

	}

	// Constructs and sets a captcha to the entry
	private static void setCaptcha(Listing entry, long count) throws IOException, MessagingException {
		FontUtils font = new FontUtils();
		String[] split;
		if (entry.getFullTitle().contains(" ")) split = entry.getFullTitle().split(" ");
		else {
			split = new String[1];
			if (!entry.getFullTitle().equals("")) split[0] = entry.getFullTitle();
			else split[0] = "listing";
		}
		Random rn = new Random();
		int one;
		int two;
		int three;
		if (split.length != 1) {
			one = rn.nextInt(split.length - 1);
			two = rn.nextInt(split.length - 1);
			three = rn.nextInt(split.length - 1);
		}
		else {
			one = 0;
			two = 0;
			three = 0;
		}
		String captcha = split[one] + " " + split[two] + " " + split[three];
		if (captcha.contains("  ")) captcha = captcha.replaceAll("  ", " eBay ");
		entry.setCaptcha(captcha);
		log.info("Captcha: " + captcha);
		byte[] bmp_data = font.doRender(captcha, "LucidaBright-DemiBold");
		String captchaURL = "" + count + "_captcha.bmp";
		BufferedImage image = ImageIO.read( new ByteArrayInputStream( bmp_data ) );
		ImageIO.write(image, "BMP", new File(HARDCODE_URL + IMAGE_URL + captchaURL));
		entry.setCaptchaImage(BASE_URL + IMAGE_URL + captchaURL);
		setDefaultStates(entry, split);
	}

	// Sets default states (condition, shipping, auction length, buy it now)
	private static void setDefaultStates(Listing entry, String[] split) throws MessagingException {
		// Search for new keyword in the entry's title and body
		for (int i = 0; i < split.length; i++) {
			if (split[i].toLowerCase().contains("new")) {
				if (i + 1 < split.length) {
					if (!split[i + 1].equalsIgnoreCase("york") && !split[i + 1].equalsIgnoreCase("england") && !split[i + 1].equalsIgnoreCase("jersey") && !split[i + 1].equalsIgnoreCase("hampshire") && !split[i + 1].equalsIgnoreCase("mexico") && !split[i + 1].equalsIgnoreCase("delhi") && !split[i + 1].equalsIgnoreCase("era")) {
						entry.setCondition("New");
						log.info("Condition: new");
					}
				}
				else {
					entry.setCondition("New");
					log.info("Condition: new");
				}
			}
			if (split[i].toLowerCase().contains("free")) {
				if (i + 1 < split.length) {
					if (split[i + 1].toLowerCase().contains("ship")){
						entry.setShippingChoice("Free Shipping");
						log.info("Shipping: free shipping");

					}
				}
			}
			if (split[i].toLowerCase().equalsIgnoreCase("ship")) {
				if (i + 1 < split.length) {
					if (split[i + 1].toLowerCase().contains("free")){
						entry.setShippingChoice("Free Shipping");
						log.info("Shipping: free shipping");

					}
				}
			}
			if (split[i].toLowerCase().contains("no")) {
				if (i + 1 < split.length) {
					if (split[i + 1].toLowerCase().contains("ship")) {
						entry.setShippingChoice("No shipping: local pickup only");
						log.info("Shipping: local pickup only");
					}
				}
			}
			if (split[i].toLowerCase().contains("local")) {
				if (i + 1 < split.length) {
					if (split[i + 1].toLowerCase().contains("pickup")) {
						entry.setShippingChoice("No shipping: local pickup only");
						log.info("Shipping: local pickup only");
					}
				}
			}
		}
		if (!entry.getBody().equals("")) {
			String[] bodySplit = entry.getBody().split(" ");
			for (int i = 0; i < bodySplit.length; i++) {
				if (bodySplit[i].toLowerCase().contains("new")) {
					if (i + 1 < bodySplit.length) {
						if (!bodySplit[i + 1].equalsIgnoreCase("york") && !bodySplit[i + 1].equalsIgnoreCase("england") && !bodySplit[i + 1].equalsIgnoreCase("jersey") && !bodySplit[i + 1].equalsIgnoreCase("hampshire") && !bodySplit[i + 1].equalsIgnoreCase("mexico") && !bodySplit[i + 1].equalsIgnoreCase("delhi") && !bodySplit[i + 1].equalsIgnoreCase("era")) {
							entry.setCondition("New");
							log.info("Condition: new");
						}
					}
					else {
						entry.setCondition("New");
						log.info("Condition: new");
					}
				}
				if (bodySplit[i].toLowerCase().contains("free")) {
					if (i + 1 < bodySplit.length) {
						if (bodySplit[i + 1].toLowerCase().contains("ship")){
							entry.setShippingChoice("Free Shipping");
							log.info("Shipping: free shipping");
						}
					}
				}
				if (bodySplit[i].toLowerCase().contains("ship")) {
					if (i + 1 < bodySplit.length) {
						if (bodySplit[i + 1].toLowerCase().contains("free")){
							entry.setShippingChoice("Free Shipping");
							log.info("Shipping: free shipping");
						}
					}
				}
				if (bodySplit[i].toLowerCase().equalsIgnoreCase("no")) {
					if (i + 1 < bodySplit.length) {
						if (bodySplit[i + 1].toLowerCase().contains("ship")) {
							entry.setShippingChoice("No shipping: local pickup only");
							log.info("Shipping: local pickup only");
						}
					}
				}
				if (bodySplit[i].toLowerCase().contains("local")) {
					if (i + 1 < bodySplit.length) {
						if (bodySplit[i + 1].toLowerCase().contains("pickup")) {
							entry.setShippingChoice("No shipping: local pickup only");
							log.info("Shipping: local pickup only");
						}
					}
				}
			}
		}
		entry.setFinalized("false");
		entry.setTime("7");
		entry.setBuyItNow("");
	}


	// Parses message for title, body, and images
	private static String[] parseMessage(Message msg, List<BodyPart> images, ArrayList<String> types) throws InterruptedException {

		String[] titleAndBody = new String[2];
		for (int i = 0; i < 2; i++) {
			titleAndBody[i] = new String("");
		}
		try {
			// Retrieve the subject of the email, and set it to be the title of the listing
			titleAndBody[0] = msg.getSubject();			
			Object content = msg.getContent();
			// If the message has multiple parts...
			if (content.getClass().isAssignableFrom(MimeMultipart.class)) {
				// Parse the content using the parseMultiPart recursive function
				MimeMultipart mmp = (MimeMultipart) content;
				parseMultiPart(mmp, titleAndBody, images, types);
			}
			// If it's only one part, then just string this bad boy and add it to the body text
			else {
				titleAndBody[1] += new String(msg.getContent().toString());
			}
			return titleAndBody;
		}
		catch (Exception m) {
			// On failure, log and return
			log.severe("Error parsing message content.");
			return titleAndBody;
		}
	}

	// Recursively parse a MimeMultipart email
	private static void parseMultiPart(MimeMultipart mmp, String[] titleAndBody, List<BodyPart> images, ArrayList<String> types) throws MessagingException, IOException {
		for (int i = 0; i < mmp.getCount(); i++) {
			// For each body part...
			BodyPart bp = mmp.getBodyPart(i);
			log.info("Content type" + i + ": " + bp.getContentType());
			// If plaintext, append to body
			if (bp.getContentType().toLowerCase().contains("text/plain")) {
				titleAndBody[1] += (String) bp.getContent();
			}
			// If image, get the bytes!
			else if (bp.getContentType().toLowerCase().contains("image")) {
				if (images.size() < IMAGE_MAX) {
					try {
						String temp = bp.getContentType();
						String[] pieces = temp.split(";");
						types.add(pieces[0]);
						images.add(bp);
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
			// If multipart alternative, recursively call parseMultiPart on this object
			else if (bp.getContentType().toLowerCase().contains("multipart")) {
				parseMultiPart((MimeMultipart) bp.getContent(), titleAndBody, images, types);
			}
			// All other types of body parts can safely be ignored (verified through trial and error)
		}
	}

	// Sends reply message
	private static void formatReplyMessage(long key, Message msg, String title) throws UnsupportedEncodingException {
		// Put together the templated message response (with key identifier)
		String replyBody = "Thank you for emailing me, the world's greatest email-to-listing test server!\n";
		replyBody += "I have processed your email and have created an editable listing page for your perusal.\n";
		replyBody += "Please check it out at " + BASE_URL + "web/listing.jsp?key=" + key;
		// Prepare & send the return message to the user
		try {
			Address[] froms = msg.getFrom();
			InternetAddress trueFrom = (InternetAddress) froms[0];
			String[] content;
			if (title.contains(" ")) content = title.split(" ");
			else {
				content = new String[1];
				if (!title.equals("")) content[0] = title;
				else content[0] = "Listing";
			}
			String subject = "";
			if (content.length == 1) subject = "Your listing \"" + content[0] + "\" is ready to be viewed!";
			else if (content.length == 2) subject = "Your listing \"" + content[0] + " " + content[1] + "\" is ready to be viewed!";
			else if (content.length >= 3) subject = "Your listing \"" + content[0] + " " + content[1] +  " " + content[2] + "\" is ready to be viewed!";
			EmailSender.sendEmail(trueFrom, subject, replyBody);
			log.info("Reply email sent.");
		}
		catch (MessagingException m) {
			log.severe("Sending reply email failed");
			m.printStackTrace();
			StackTraceElement[] st = m.getStackTrace();
			for (int j = 0; j < st.length; j++) {
				log.severe(st[j].toString());
			}
		}

	}

	private static void sendErrorReply(Message msg) {
		// Put together the templated message response (with key identifier)
		String replyBody = "Thank you for emailing me, the world's greatest email-to-listing test server!\n";
		replyBody += "I saw your email, but it didn't have any pictures attached!\n";
		replyBody += "If you still want to list this item, please resend the email with at least one photo =)";
		// Prepare & send the return message to the user
		try {
			Address[] froms = msg.getFrom();
			InternetAddress trueFrom = (InternetAddress) froms[0];
			String subject = "Your listing couldn't be created - see details!";
			EmailSender.sendEmail(trueFrom, subject, replyBody);
			log.info("Error reply email sent.");
		}
		catch (MessagingException m) {
			log.severe("Sending error reply email failed");
			m.printStackTrace();
			StackTraceElement[] st = m.getStackTrace();
			for (int j = 0; j < st.length; j++) {
				log.severe(st[j].toString());
			}
		}

	}

}

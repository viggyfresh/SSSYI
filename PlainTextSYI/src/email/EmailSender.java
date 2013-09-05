package email;

import java.util.Properties;
import java.util.logging.Logger;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/*
 * Sends email from your account of choice. Right now configured to gmail; commented out sections
 * work on ebay corp server
 */
public class EmailSender {

	private static String email;
	private static Properties props;
	private static Session session;
	private static final Logger log = Logger.getLogger(EmailSender.class.getName());

	
	public static void init(String incomingEmail) {
		email = incomingEmail;
		props = System.getProperties();
		//props.put("mail.smtp.host", "atom.corp.ebay.com");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class",
				"javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465");
		//session = Session.getInstance(props, null);
		session = Session.getDefaultInstance(props,
				new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication("emailtolisting@gmail.com","EbayOpArch");
					}
				});
		//session.setDebug(true);
	}

	public static void sendEmail(InternetAddress targetAddress, String subject, String response) {	
		try {			
			// Copied from my EmailToListing code
			Message reply = new MimeMessage(session);
			reply.setFrom(new InternetAddress(email));
			reply.addRecipient(Message.RecipientType.TO, targetAddress);
			reply.setSubject(subject);
			reply.setText(response);
			Transport.send(reply);
		} catch (MessagingException ex) {
			log.severe(ex.toString());
			StackTraceElement[] st = ex.getStackTrace();
			for (int i = 0; i < st.length; i++) {
				log.severe(st[i].toString());
			}
			ex.printStackTrace();
		}

	}
	
}
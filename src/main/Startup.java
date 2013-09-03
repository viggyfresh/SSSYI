package main;
import java.sql.SQLException;
import java.util.logging.Logger;

import javax.mail.MessagingException;

import db.DatabaseModule;
import ebay.EbayServiceModule;
import email.EmailReceiver;
import email.EmailSender;

public class Startup {

private static final String email = "emailtolisting@gmail.com";

private static final Logger log = Logger.getLogger(Startup.class.getName());

	/**
	 * @param args
	 * @throws IOException 
	 * @throws AddressException 
	 */
	private static EmailReceiver receiver;

	public static void init() {
		log.info("Starting up background programs...");
		Runnable r = new Runnable() {
			public void run() {
				DatabaseModule.init();
				EbayServiceModule.init();
				EmailSender.init(email);
				String user = "emailtolisting@gmail.com";
				String pwd = "EbayOpArch";
				receiver = new EmailReceiver(user, pwd);
				receiver.ListenForEmails();
			}
		};
		new Thread(r).start();
	}

	public static void shutdown() throws SQLException, MessagingException {
		log.info("Shutting down database connection and email receiver...");
		DatabaseModule.shutdown();
		receiver.shutdown();	
	}
}

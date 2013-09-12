package main;
import java.sql.SQLException;
import java.util.logging.Logger;

import javax.mail.MessagingException;

import db.DatabaseModule;
import ebay.EbayServiceModule;
import email.EmailReceiver;
import email.EmailSender;

/*
 * Startup class. Initializes everything that needs to be, shuts down
 * stuff when needed. You most likely do not need to touch this.
 */

public class Startup {

private static final String email = "emailtolisting@gmail.com";
private static final String pwd = "EbayOpArch";


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
				// Init database, ebayservice, and email modules
				DatabaseModule.init();
				EbayServiceModule.init();
				EmailSender.init(email);
				receiver = new EmailReceiver(email, pwd);
				receiver.ListenForEmails();
			}
		};
		new Thread(r).start();
		log.info("Background programs started...");
	}

	public static void shutdown() throws SQLException, MessagingException {
		// Kill service modules
		log.info("Shutting down database connection and email receiver...");
		DatabaseModule.shutdown();
		receiver.shutdown();	
	}
}

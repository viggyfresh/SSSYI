// Adapted from JavaMail tutorial (specifically the monitor one)
// TODO: Need to add copyright disclaimer at some point (required for use/manipulation of source code)

package email;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.event.MessageCountAdapter;
import javax.mail.event.MessageCountEvent;

import listing.EmailToListing;

import com.sun.mail.imap.IMAPFolder;

/*
 * Receives emails, grabs new messages, and goes through the EmailToListing process.
 * Currently configured to gmail; uncomment stuff to make it work for CORP
 */

public class EmailReceiver {

	private static final Logger log = Logger.getLogger(EmailReceiver.class.getName());

	private String host;
	private String email;
	private String password;
	private Properties props;
	private Session session;
	private ExecutorService executor;
	private Folder inbox;
	private Store store;

	public EmailReceiver(String email, String password) {
		//this.host = "proton.corp.ebay.com";
		this.host = "imap.gmail.com";
		this.email = email;
		this.password = password;
		assignProperties();
		getSession();
		executor = Executors.newFixedThreadPool(8);
	}

	private void assignProperties() {
		props = System.getProperties();
		props.setProperty("mail.store.protocol", "imaps");
	}

	private void getSession() {
		session = Session.getInstance(props, null);
	}

	public void ListenForEmails() {
		try {
			//Store store = session.getStore("imap");
			store = session.getStore("imaps");
			store.connect(host, email, password);
			inbox = store.getFolder("INBOX");
			inbox.open(Folder.READ_WRITE);
			inbox.addMessageCountListener(new MessageCountAdapter() {
				public void messagesAdded(MessageCountEvent e) {
					respondToNewMessages(e);
				}
			});
			// Check mail once in "freq" MILLIseconds
			int freq = 10000;
			for (;;) {
				Thread.sleep(freq);
				inbox.getMessageCount();
			}
		} catch (Exception ex) {
			log.severe(ex.toString());
			StackTraceElement[] st = ex.getStackTrace();
			for (int i = 0; i < st.length; i++) {
				log.severe(st[i].toString());
			}
			ex.printStackTrace();
			ListenForEmails();
		}		

	}

	protected void respondToNewMessages(MessageCountEvent e) {
		// Your response code will go here; currently just marks messages as read, prints them to system out,
		// and responds to the first sender of each received message with an email
		Message[] msgs = e.getMessages();
		log.info("Got " + msgs.length + " new message(s)");
		// Send responses to each of the messages
		for (int i = 0; i < msgs.length; i++) {
			Runnable worker = new WorkerThread(msgs[i]);
			executor.execute(worker);
		}
	}
	
	public void shutdown() throws MessagingException {
		executor.shutdown();
		inbox.close(false);
		store.close();
	}

	public class WorkerThread implements Runnable {

		private Message msg;

		public WorkerThread(Message msg){
			this.msg=msg;
		}

		@Override
		public void run() {
			try {
				log.info("New thread started.");
				EmailToListing.toListing(msg);
			} catch (Exception ex) {
				log.severe(ex.toString());
				StackTraceElement[] st = ex.getStackTrace();
				for (int j = 0; j < st.length; j++) {
					log.severe(st[j].toString());
				}
				ex.printStackTrace();
			}	
		}
	}

}

I. PlainTextSYI system requirements
	1. An OpenStratus box (preferably externally enabled or public facing) with administrator privileges
	2. IBM JRE, which is the eBay suggested JRE anyway
	3. Eclipse installed
	4. Tomcat 7.0 installed
	5. MySQL server installed and ETL_DB schema imported (see section III)
	6. Configuration settings set up for your local box (see section IV)

II. Setting up PlainTextSYI on a fresh machine
	1. Install Tomcat 7.0 as a service
		a. Use the following as the install directory: C:/Program Files (x86)/Apache Software Foundation/Tomcat 7.0
	2. Install MySQL (default install will work)
	3. Configure MySQL (see section III)
	4. Import the project into Eclipse (from the Git repo, just import the PlainTextSYI folder)
	5. Verify configuration settings (see section IV)

III. Set up MySQL and configure schema
	1. Set up database connection protocols
		a. Username: root
		b. Password: root
		c. Host: localhost, port 5555
	2. Import schema: etldb.mwb
		a. This file is checked in with the Git project and is located in the root directory of the project
		b. Using MySQL Workbench, import this model and apply it to your DB
	3. Verify schema 
		a. Ensure that your schema is called "etl_db"
		b. Make sure there are 2 tables: etl_db.etl_count (total count) and etl_db.etl_data (actual listing data)	

IV. Configure your box settings - open up the project in Eclipse, edit the following constants to match your box's setup
	1. EmailToListing.java
		a. private static final String BASE_URL = "http://emailtolisting-16253.phx-os1.stratus.dev.ebay.com:8080/PlainTextSYI/";
				i. Make this match the Tomcat URL (port number, hostname, project name, etc)
				ii. Format: "http://[machine-name]:[Tomcat port]/PlainTextSYI"
		b. private static final String HARDCODE_URL = "C:/Program Files (x86)/Apache Software Foundation/Tomcat 7.0/webapps/PlainTextSYI/";
				i. Make this match the local (on disk) path of the extracted WAR, as unpacked by Tomcat
				ii. Format: "C:/[Tomcat 7.0 install path]/webapps/PlainTextSYI"
		c. private static final String IMAGE_URL = "files/images/";
				i. This is where the images will be saved, you probably shouldn't ever need to change this
	2. Startup.java
		a. private static final String email = "emailtolisting@gmail.com";
		b. private static final String pwd = "EbayOpArch";
			i. Configure these with email address and password for your account of choice
	3. EmailSender.java/EmailReceiver.java
		a. If switching to eBay email address, comment out all current properties, and uncomment the commented out fields
			i. Enable atom.corp.ebay.com and proton.corp.ebay.com, comment out gmail specific stuff. That should do it
	4. EbayServiceModule.java
		a. In method getApiContext(), change any eBay developer credentials as required (shouldn’t be necessary until temp account privileges are granted, and even then ONLY on the actuallyListItem and reviseItem calls)
	5. DatabaseModule.java
		a. private static final String SQL_URL = "jdbc:mysql://emailtolisting-16253.phx-os1.stratus.dev.ebay.com:5555/etl_db";
		b. private static final String USERNAME = "root";
		c. private static final String PASSWORD = "root";
		d. private static final String TABLE = "ETL_DATA";
		e. private static final String COUNT_TABLE = "ETL_COUNT";
			i. All of these fields must match (exactly) the MySQL configuration (port number, hostname, username/password for server, schema name (here etl_db), and table names. If you followed section III exactly, all you will have to modify is the hostname.
			ii. Format: "jdbc:[machine-name]:5555/etl_db"

V. Getting the project to run for the first time
	1. It goes without saying that you need the project files imported into Eclipse
		a. ALSO, make sure all your configuration is done correctly
	2. Export entire thing as a WAR file, put the war in the webapps/ directory in your tomcat install 
		a. Default: "C:/Program Files (x86)/Apache Software Foundation/Tomcat 7.0/webapps/
	3. Start up Tomcat. That should do it

V. Pushing changes to the project (NOTE: if you want your images to persist, follow these directions exactly)
	1. Make whatever changes you want to the project in Eclipse
	2. Stop the Tomcat server 
		a. Can be done through system tray if Tomcat is properly installed as a service
	3. Make a copy of the images folder 
		a. Default: "C:/Program Files (x86)/Apache Software Foundation/Tomcat 7.0/webapps/PlainTextSYI/files/images
	4. Export the project (with changes) as a WAR, back into the webapps directory, replacing the existing WAR file
		a. Default: "C:/Program Files (x86)/Apache Software Foundation/Tomcat 7.0/webapps
	5. Delete the old unpacked PlainTextSYI FOLDER in the webapps directory. This forces Tomcat to unpack the fresh WAR
		a. Default: "C:/Program Files (x86)/Apache Software Foundation/Tomcat 7.0/webapps
	6. Restart Tomcat as a service 
	7. After full restart, copy the images folder from wherever you made the copy back into webapps/PlainTextSYI/files/images
 
VI. A newbie's guide to the project
	1. NOTE: MAKE SURE YOU ARE USING THE RECOMMENDED IBM JRE, or else stuff may get a little screwey
	2. The quick and dirty tour
		a. src – contains all the java files needed for the project
			i. db – contains DatabaseModule, for making any and all database calls to the MySQL server
			ii. ebay – contains EbayServiceModule, for making any and all eBay API service calls
			iii. email – contains EmailSender and EmailReceiver, for sending emails and for attaching a listener to the email account of your choosing
			iv. listing – contains Listing (class declaration for the listing object), EmailToListing (here is where you will probably need to make changes; meat of program’s complexity lies here), FinalizeServlet (takes in form input from listing.jsp, updates database with changes), ListItemServlet (goes ahead and actually lists an item for sale on eBay.com), and ServletListener (used to startup and initialize the entire program when deployed to server)			
			v. main – contains Startup (called by ServletListener. One stop shop for initializing any classes that need initializing)
			vi. org.json – don’t worry about this. JSON libraries are sadly not available in jar format, so I had to import the source manually =P
		b. war – contains web app resources
			i. assets – contains non-bootstrap javascript, css, and the fancybox plugin requirements (i.e. jquery)
			ii. dist – contains bootstrap javascript, css, and other requirements
			iii. files – contains images directory, where once the project is deployed as a WAR, the images will be saved to disk			
			iv. META-INF – contains MANIFEST.MF, don’t mess with it			
			v. WEB-INF – contains web.xml config file (for changing deployment instructions and class references on the webapp side), logging.properties file (sets logging rules), and lib folder with ALL dependency wars
			vi. web – contains 4 webpages (listing.jsp, finalized.jsp, done.jsp, error.jsp) and corresponding stylesheets (theme.css, carousel.css)

VII. Project flow summary
	1. Project is exported as a WAR
	2. Tomcat unpacks the WAR and deploys the project to its server
	3. Internally, Startup.java is run, initializing the EbayServiceModule, DatabaseModule, EmailSender, and EmailReceiver
	4. Email is sent to emailtolisting@gmail.com
		a. Picked up by EmailReceiver
	5. EmailReceiver spawns a new thread for the email and calls the EmailToListing module in this new thread
	6. EmailToListing module begins parsing the message and making the fresh listing object (see above for EmailToListing walkthrough)
		a. Calls DatabaseModule.getCount() to get a fresh count that will be this listing’s unique ID
		b. Extracts the title, body, and images
		c. Calls EbayServiceModule.getSuggestedCategories() to get the categories of the listing
		d. Manually HTML-parses the Poseidon shipping website to get shipping options
		e. Manually makes a HTTP Get call to the attribute parsing service and gets those back
		f. Sets all other fields in the Listing object with its own methods
		g. Calls DatabaseModule.storeListing() to store the listing object to the database
		h. Sends a response email to the user through the EmailSender class
	7. User follows link in the email
		a. Tomcat 7.0 serves up listing.jsp – this page contains a form, prepopulated with whatever listing info is already embedded in the Listing object in the db, and a submit button
		b. On submit, form is validated. If fields are missing or incorrectly filled out (or if captcha is wrong), submit blocked. Otherwise, form data is sent to…
	8. FinalizeServlet, which receives and processes form data
		a. Takes this data and updates the listing through the DatabaseModule.updateListing() call
		b. Serves up finalized.jsp page
	9. User sees finalized.jsp page
		a. Final confirmation dialog, with all the stuff they have filled out so far.
		b. Two options at the bottom of the page
			i.	If they are happy, hit the big green button and submit to eBay.com
			ii.	If unhappy, hit the revise button – takes them back to the listing.jsp page, flow continues from step (vi) above.
	10. User hits submit
		a.	Triggers ListItemServlet
	11. ListItemServlet triggered
		a. If item has been listed before, then make an EbayServiceModule.reviseListing() call with the listing object
		b. Otherwise, make a standard EbayServiceModule.actuallyListItem() call with the listing object.
		c. In either case, there are 2 possible outcomes
			i. The listing succeeds -> user is served done.jsp, which contains a success message and 2 links to follow: 
				. View item, which takes the user to the item page on eBay.com itself
				. Revise item, which takes them back to step (vi) of the flow above
				. User is also sent a confirmation email, which contains the same links as above
			ii.	The listing fails due to an error -> user is served error.jsp
				. This page contains a generic error message, a list of typical pitfalls that we encountered in our testing, and the actual XML response given to us by the eBay servers detailing why the AddItemCall failed.
				. Contains a button that takes them to fix the changes -> again leads to step (vi) of the flow above

VIII. Making basic changes to the project
	1. Typically, cosmetic changes to what the user sees will happen on one of the four webpages: listing.jsp, finalized.jsp, done.jsp, error.jsp. Feel free to change any of the pln text markup here, just don’t mess with the ids and classes and names of elements unless you really know what you are doing. 
	2. For instance, changing Confirmation Phrase to Confirmation phrase is as simple as locating Confirmation Phrase on the screen (near the bottom of the form) and replacing the capital P with a lowercase one. Changing the text of the error message on listing.jsp is as easy as locating the div with id “alertDiv” and modifying the text inside. Changing label for any of the inputs is as easy as finding the label you want to change and modifying the text inside the <label> and </label> tags.
	3. Programmatic changes to the process will usually require modification of EmailToListing.java. This file is broken down into a vast variety of subfunctions. 
		a. To change how the email is parsed, look at the parseMessage and parseMultipart methods.
		b. If you want to change how the title and body of the listing are parsed and stored, go to the setTextFields method. 
		c. To change how images are parsed and stored, go to the setAndStoreImages method. 
		d. To change how price is parsed and stored, go to the setPrice method. 
		e. To change how categories are stored, go to the setCategories method. 
		f. To change how the captcha (confirmation phrase) is generated and stored, go to the setCaptcha method. 
		g. To change how shipping options and attributes are stored, go to the setShippingOptions and setAttributes methods respectively. 
		h. To change how default states are processed (i.e. free/no shipping, new condition, local pickup, etc), go to the setDefaultStates method. 
		i. To change how required specifics are stored, go to the setRequiredSpecifics method. 
		j. To change the formatting or text of the response email, go to the formatReplyMessage method.
		k. If you want to change anything else, see the quick and dirty tour above to locate the file you want to modify.

IX. Known Issues
	1. Cannot list in categories that require an eBay product ID match
		a. Examples include Cell Phones and Smartphones, Computers, etc.
	2. Cannot specify your own category (if the three returned options are not correct)
	3. Cannot add, subtract, or reorder pictures after the email is sent
	4. Can only create an auction-style listing (no fixed-price option)
	5. Only some condition values are possible for some categories
		a. For instance, selling used dog food or "for parts/not working" clothing is illegal
	6. Currently, we only show REQUIRED specifics to list an item
		a. There are a bunch of recommended (but optional) specifics that we could also show
	7. Cannot list an item under multiple categories
		a. On regular site, this is allowed, but has an associated fee for it

X. Todo List
	1. Add a product catalog matching function
		a. If category requires a product ID match, search for one using this function and list the item accordingly
		b. If many choices, present the user with another dropdown in the listing.jsp form to pick the correct product and its corresponding product ID
	2. Replace Poseidon shipping options
		a. Currently, we're going to a website and messily pulling details from the website html
		b. Need to replace and refactor this method
	3. Do better at getting public IP
		a. Currently we only get the local eBay IP -> need one more level of indirection
	4. Dynamically populate item condition select field
		a. Need to make an eBay API call to get the permissible condition values for the specific category
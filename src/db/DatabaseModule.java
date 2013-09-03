package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Logger;

import ebay.EbayServiceModule;
import listing.Listing;

/*
 * Sets up and makes all MySQL DB queries
 */

public class DatabaseModule {
	private static final String SQL_URL = "jdbc:mysql://emailtolisting-16253.phx-os1.stratus.dev.ebay.com:5555/etl_db";
	private static final String USERNAME = "root";
	private static final String PASSWORD = "root";
	public static boolean initialized = false;

	private static final String TABLE = "ETL_DATA";
	private static final String COUNT_TABLE = "ETL_COUNT";

	private static Connection conn;

	private static final Logger log = Logger.getLogger(DatabaseModule.class.getName());

	// Here be test functions
	public static void main(String[] args) {
		init();
		EbayServiceModule.init();
		try {
			Listing l = retrieveListing(41);
			String[] results = EbayServiceModule.actuallyListItem(l, 41, null);
			System.out.println("Listing fees: " + results[0]);
			System.out.println("Listing id: " + results[1]);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Initializes the driver
	public static void init() {
		//String driverName = "oracle.jdbc.driver.OracleDriver"; // for Oracle
		String driverName = "com.mysql.jdbc.Driver"; //for MySql
		try {
			// Load the JDBC driver
			Class.forName(driverName); 
		} 
		catch (ClassNotFoundException ex) {
			log.severe(ex.toString());
			StackTraceElement[] st = ex.getStackTrace();
			for (int i = 0; i < st.length; i++) {
				log.severe(st[i].toString());
			}
			ex.printStackTrace();
		}
		try {
			// Create a connection to the database
			conn = DriverManager.getConnection(SQL_URL, USERNAME, PASSWORD);
		}
		catch (SQLException ex) {
			log.severe(ex.toString());
			StackTraceElement[] st = ex.getStackTrace();
			for (int i = 0; i < st.length; i++) {
				log.severe(st[i].toString());
			}
			ex.printStackTrace();
		}
		initialized = true;
	}

	// Closes the connection, if its been started
	public static void shutdown() throws SQLException {
		if (initialized) conn.close();
	}

	// Takes a listing and throws it into the DB
	public static void storeListing(long count, Listing entry) throws SQLException {
		PreparedStatement stmt = conn.prepareStatement("INSERT INTO " + TABLE + " (id, title, fullTitle, body, price, email, finalized, category, category0, category1, category2, shippingChoice, shippingOption0, shippingOption1, shippingOption2, shippingOption3, shippingOption4, shippingOption5, attribute, attribute0, attribute1, attribute2, url0, url1, url2, url3, url4, url5, url6, url7, url8, url9, url10, url11, item_time, item_condition, buyItNow, captcha, captchaImage, location, handling_time, returns, req_specifics0, req_specifics1, req_specifics2, specifics, ip) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
		stmt.setLong(1, count);
		stmt.setString(2, entry.getTitle());
		stmt.setString(3, entry.getFullTitle());
		stmt.setString(4, entry.getBody());
		stmt.setString(5, entry.getPrice());
		stmt.setString(6, entry.getEmail());
		stmt.setString(7, entry.getFinalized());
		stmt.setString(8, entry.getCategory());
		stmt.setString(9, entry.getCategories().get(0));
		stmt.setString(10, entry.getCategories().get(1));
		stmt.setString(11, entry.getCategories().get(2));
		stmt.setString(12, entry.getShippingChoice());
		stmt.setString(13, entry.getShippingOptions().get(0));
		stmt.setString(14, entry.getShippingOptions().get(1));
		stmt.setString(15, entry.getShippingOptions().get(2));
		stmt.setString(16, entry.getShippingOptions().get(3));
		stmt.setString(17, entry.getShippingOptions().get(4));
		stmt.setString(18, entry.getShippingOptions().get(5));
		stmt.setString(19, entry.getAttribute());
		stmt.setString(20, entry.getAttributes().get(0));
		stmt.setString(21, entry.getAttributes().get(1));
		stmt.setString(22, entry.getAttributes().get(2));
		stmt.setString(23, entry.getUrls().get(0));
		stmt.setString(24, entry.getUrls().get(1));
		stmt.setString(25, entry.getUrls().get(2));
		stmt.setString(26, entry.getUrls().get(3));
		stmt.setString(27, entry.getUrls().get(4));
		stmt.setString(28, entry.getUrls().get(5));
		stmt.setString(29, entry.getUrls().get(6));
		stmt.setString(30, entry.getUrls().get(7));
		stmt.setString(31, entry.getUrls().get(8));
		stmt.setString(32, entry.getUrls().get(9));
		stmt.setString(33, entry.getUrls().get(10));
		stmt.setString(34, entry.getUrls().get(11));
		stmt.setString(35, entry.getTime());
		stmt.setString(36, entry.getCondition());
		stmt.setString(37, entry.getBuyItNow());
		stmt.setString(38, entry.getCaptcha());
		stmt.setString(39, entry.getCaptchaImage());
		stmt.setString(40, entry.getLocation());
		stmt.setString(41, entry.getHandlingTime());
		stmt.setString(42, entry.getReturns());
		stmt.setString(43, entry.getReqSpecifics0());
		stmt.setString(44, entry.getReqSpecifics1());
		stmt.setString(45, entry.getReqSpecifics2());
		stmt.setString(46, entry.getSpecifics());
		stmt.setString(47, entry.getIP());
		log.info("Insert statement: " + stmt.toString());
		try {
			stmt.executeUpdate();
		}
		catch (SQLException ex) {
			log.severe(ex.toString());
			StackTraceElement[] st = ex.getStackTrace();
			for (int i = 0; i < st.length; i++) {
				log.severe(st[i].toString());
			}
			ex.printStackTrace();
			shutdown();
			init();
			storeListing(count, entry);
		}

	}

	// Retrieves a listing from the DB
	public static Listing retrieveListing(long id) throws SQLException {
		Statement stmt = conn.createStatement();
		String query = "SELECT * from " + TABLE + " WHERE id=" + id;
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(query);
		}
		catch (SQLException ex) {
			log.severe(ex.toString());
			StackTraceElement[] st = ex.getStackTrace();
			for (int i = 0; i < st.length; i++) {
				log.severe(st[i].toString());
			}
			ex.printStackTrace();
			shutdown();
			init();
			return retrieveListing(id);
		}
		Listing entry = new Listing();
		rs.first();
		entry.setTitle(rs.getString(2));
		entry.setFullTitle(rs.getString(3));
		entry.setBody(rs.getString(4));
		entry.setPrice(rs.getString(5));
		entry.setEmail(rs.getString(6));
		entry.setFinalized(rs.getString(7));
		entry.setCategory(rs.getString(8));
		ArrayList<String> categories = new ArrayList<String>();
		for (int i = 0; i < 3; i++) {
			categories.add(rs.getString(9 + i));
		}
		entry.setCategories(categories);
		entry.setShippingChoice(rs.getString(12));
		ArrayList<String> shippingOptions = new ArrayList<String>();
		for (int i = 0; i < 6; i++) {
			shippingOptions.add(rs.getString(13 + i));
		}
		entry.setShippingOptions(shippingOptions);
		entry.setAttribute(rs.getString(19));
		ArrayList<String> attributes = new ArrayList<String>();
		for (int i = 0; i < 3; i++) {
			attributes.add(rs.getString(20 + i));
		}
		entry.setAttributes(attributes);
		ArrayList<String> urls = new ArrayList<String>();
		for (int i = 0; i < 12; i++) {
			urls.add(rs.getString(23 + i));
		}
		entry.setUrls(urls);
		entry.setTime(rs.getString(35));
		entry.setCondition(rs.getString(36));
		entry.setBuyItNow(rs.getString(37));
		entry.setCaptcha(rs.getString(38));
		entry.setCaptchaImage(rs.getString(39));
		entry.setLocation(rs.getString(40));
		entry.setHandlingTime(rs.getString(41));
		entry.setReturns(rs.getString(42));
		entry.setReqSpecifics0(rs.getString(43));
		entry.setReqSpecifics1(rs.getString(44));
		entry.setReqSpecifics2(rs.getString(45));
		entry.setSpecifics(rs.getString(46));
		entry.setIP(rs.getString(47));
		log.info("Listing retrieved: " + entry.toString());
		return entry;
	}

	// Updates a listing in the DB
	public static void updateListing(long count, String title, String body,
			String price, String buyItNow, String condition, String time,
			String category, String shippingChoice, String attribute, String location, String handlingTime, String returns, String specifics, String IP ) throws SQLException {
		PreparedStatement stmt = conn.prepareStatement("UPDATE " + TABLE + " SET title = ?, body = ?, price = ?, buyItNow = ?, item_condition = ?, item_time = ?, category = ?, shippingChoice = ?, attribute = ?, location = ?, handling_time = ?, returns = ?, specifics = ?, ip = ? WHERE id=" + count);
		stmt.setString(1, title);
		stmt.setString(2, body);
		stmt.setString(3, price);
		stmt.setString(4, buyItNow);
		stmt.setString(5, condition);
		stmt.setString(6, time);
		stmt.setString(7, category);
		stmt.setString(8, shippingChoice);
		stmt.setString(9, attribute);
		stmt.setString(10, location);
		stmt.setString(11, handlingTime);
		stmt.setString(12, returns);
		stmt.setString(13, specifics);
		stmt.setString(14, IP);
		log.info("Update statement: " + stmt.toString());
		try {
			stmt.executeUpdate();
		}
		catch (SQLException ex) {
			log.severe(ex.toString());
			StackTraceElement[] st = ex.getStackTrace();
			for (int i = 0; i < st.length; i++) {
				log.severe(st[i].toString());
			}
			ex.printStackTrace();
			shutdown();
			init();
			updateListing(count, title, body, price, buyItNow, condition, time, category, shippingChoice, attribute, location, handlingTime, returns, specifics, IP);
		}
	}
	
	// Updates the finalized field in the DB. Finalized will be false OR contain the listing ID of an item that has been listed to eBay
	public static void updateFinalized(long count, String finalized) throws SQLException {
		PreparedStatement stmt = conn.prepareStatement("UPDATE " + TABLE + " SET finalized = ? WHERE id=" + count);
		stmt.setString(1, finalized);
		log.info("Update finalized statement: " + stmt.toString());
		try {
			stmt.executeUpdate();
		}
		catch (SQLException ex) {
			log.severe(ex.toString());
			StackTraceElement[] st = ex.getStackTrace();
			for (int i = 0; i < st.length; i++) {
				log.severe(st[i].toString());
			}
			ex.printStackTrace();
			shutdown();
			init();
			updateFinalized(count, finalized);
		}
	}

	// Gets the count of the DB, which becomes the new item's ID. Only synchronized method in whole app
	synchronized public static long getCount() throws SQLException {
		Statement stmt = conn.createStatement();
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery("SELECT total FROM " + COUNT_TABLE);
		}
		catch (SQLException ex) {
			log.severe(ex.toString());
			StackTraceElement[] st = ex.getStackTrace();
			for (int i = 0; i < st.length; i++) {
				log.severe(st[i].toString());
			}
			ex.printStackTrace();
			shutdown();
			init();
			return getCount();
		}
		rs.first();
		long count = rs.getLong("total");
		count++;
		PreparedStatement stmt2 = conn.prepareStatement("UPDATE " + COUNT_TABLE + " SET total = ?");
		stmt2.setLong(1, count);
		log.info("Count update statement: " + stmt2.toString());
		stmt2.executeUpdate();
		log.info("New count: " + count);
		return count;
	}

}

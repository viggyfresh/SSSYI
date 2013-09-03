<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.*"%>
<%@ page import="java.io.*"%>
<%@ page import="javax.mail.*"%>
<%@ page import="javax.mail.internet.*"%>
<%@ page import="java.util.*"%>
<%@ page import="db.*" %>
<%@ page import="listing.*" %>
<%@ page import="org.json.*" %>

<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="">
    <meta name="author" content="">
    <link rel="shortcut icon" href="../assets/ico/favicon.png">

    <title>Listing Confirmation Page</title>

    <!-- Bootstrap core CSS -->
    <link href="../dist/css/bootstrap.css" rel="stylesheet">
    <!-- Bootstrap theme -->
    <link href="../dist/css/bootstrap-theme.min.css" rel="stylesheet">
    <!-- Custom styles for this template -->
    <link href="theme.css" rel="stylesheet">
    <link href="carousel.css" rel="stylesheet">
	<!-- Fancybox CSS -->
	<link rel="stylesheet" type="text/css" href="../assets/fancybox/jquery.fancybox.css?v=2.1.5" media="screen" />


    <!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
      <script src="../assets/js/html5shiv.js"></script>
      <script src="../assets/js/respond.min.js"></script>
    <![endif]-->
  </head>

  <body>

    <div class="container theme-showcase">

      <!-- Main jumbotron for a primary marketing message or call to action -->
      <div class="jumbotron">
		<img src='http://www.plentymarkets.eu/images/produkte/i17/1735-ebay-tm-rgb.png' width="250" height="100"/>
        <h1>Listing Confirmation Page</h1>
<%
String keyString = request.getParameter("key");
if (keyString == null || keyString.equals("")) {
	out.println("<p>An internal servlet error occurred.</p></div>");
} else {
	if (!DatabaseModule.initialized) DatabaseModule.init();
	// Retrieves ALL information related to the current entry being viewed
	Listing listing = DatabaseModule.retrieveListing(Long.parseLong(keyString));
	String badCaptcha = request.getParameter("badCaptcha");
	if (badCaptcha == null) badCaptcha = "false";
	out.println("<p>Please finalize your listing and hit submit! You will get another chance to confirm the details on the next screen.</p>");
	if (badCaptcha.equals("true")) 	out.println("<p class=\"text-danger\">Your confirmation phrase was incorrect. Please check your spelling and try again!</p></div>");
	else out.println("</div>");
	String title = (String) listing.getTitle();
	if (title == null)
		title = "";
	String price = (String) listing.getPrice();
	if (price == null)
		price = "";
	String buyItNow = (String) listing.getBuyItNow();
	if (buyItNow == null)
		buyItNow = "";
	String category = (String) listing.getCategory();
	if (category == null)
		category = "";
	String body;
	String tempbody = listing.getBody();
	if (tempbody == null)
		body = "";
	else body = tempbody;
	String condition = (String) listing.getCondition();
	if (condition == null)
		condition = "";
	String time = (String) listing.getTime();
	if (time == null)
		time = "";
	String shippingChoice = (String) listing.getShippingChoice();
	if (shippingChoice == null) 
		shippingChoice = "";
	ArrayList<String> urls = listing.getUrls();
	ArrayList<String> categories = listing.getCategories();
	ArrayList<String> options = listing.getShippingOptions();
	String attribute = (String) listing.getAttribute();
	if (attribute == null) 
		attribute = "";
	ArrayList<String> attributeList = listing.getAttributes();
	for (int i = 0; i < attributeList.size(); i++) {
		out.println("<input type=\"hidden\" id=\"attribute" + i + "\" value=\"" + attributeList.get(i) + "\">");
	}
	String captchaURL = (String) listing.getCaptchaImage();
	String captcha = listing.getCaptcha();
	String location = listing.getLocation();
	if (location == null) location = "";
	String handlingTime = listing.getHandlingTime();
	if (handlingTime == null) handlingTime = "";
	String returns = listing.getReturns();
	if (returns == null) returns = "";
	String reqSpecifics0 = listing.getReqSpecifics0();
	if (reqSpecifics0 == null) reqSpecifics0 = "";
	String reqSpecifics1 = listing.getReqSpecifics1();
	if (reqSpecifics1 == null) reqSpecifics1 = "";	
	String reqSpecifics2 = listing.getReqSpecifics2();
	if (reqSpecifics2 == null) reqSpecifics2 = "";
	String specifics = listing.getSpecifics();
	if (specifics == null) specifics = "";
	JSONArray spec0;
	if (reqSpecifics0.equals("[]")) spec0 = null;
	else spec0 = new JSONArray(reqSpecifics0);
	JSONArray spec1;
	if (reqSpecifics1.equals("[]")) spec1 = null;
	else spec1 = new JSONArray(reqSpecifics1);	
	JSONArray spec2;
	if (reqSpecifics2.equals("[]")) spec2 = null;
	else spec2 = new JSONArray(reqSpecifics2);
	JSONArray specificsArray;
	if (specifics.equals("[]")) specificsArray = null;
	else specificsArray = new JSONArray(specifics);
	HashMap<String, String> specificsMap = new HashMap<String, String>();
	if (specificsArray != null) {
		for (int p = 0; p < specificsArray.length(); p++) {
			specificsMap.put(specificsArray.getJSONObject(p).getString("key"), specificsArray.getJSONObject(p).getString("value"));
		}
	}
	%>

	  
	  
	  <div class="page-header"></div>
	  <form name="finalize" action="FinalizeServlet" class="form-horizontal" role="form" method="POST">
	  	<input type="hidden" name="captchaText" value="<%=captcha%>">
		<div class="form-group">
			<label for="body" class="col-md-3 control-label lead"><strong>Uploaded Pictures</strong></label>
			<div class="col-md-9">
				<div id="myCarousel" class="carousel slide">
					<ol class="carousel-indicators">
					<%
						for (int xx = 0; xx < 12; xx++) {
							if (!urls.get(xx).equals("") && xx == 0) {
								out.println("<li data-target=\"#myCarousel\" data-slide-to=\"" + xx + "\" class=\"active\"></li>");	
							}
							else if (!urls.get(xx).equals("")) {
								out.println("<li data-target=\"#myCarousel\" data-slide-to=\"" + xx + "\"></li>");	
							}
						}
					%>
					</ol>
					<div class="carousel-inner">
					<%
						for (int j = 0; j < urls.size() ; j++) {
							if (!urls.get(j).equals("")) {
								if (j == 0) {
					%>
						<div class="item active">
							<a class="fancybox" href="<%=urls.get(j)%>" data-fancybox-group="gallery" title="">
								<img src="<%=urls.get(j)%>" alt=""/>
							</a>
						</div>
			
					<% 
								}
								else { %>
						<div class="item">
							<a class="fancybox" href="<%=urls.get(j)%>" data-fancybox-group="gallery" title="">
								<img src="<%=urls.get(j)%>" alt=""/>
							</a>
						</div>
								
					<% }			
							}
						}
					%>
					</div>			
      			<a class="left carousel-control" href="#myCarousel" data-slide="prev"><span class="glyphicon glyphicon-chevron-left"></span></a>
      			<a class="right carousel-control" href="#myCarousel" data-slide="next"><span class="glyphicon glyphicon-chevron-right"></span></a>
				</div>
			</div>
		</div>
		
		<div class="form-group">
			<label for="title" class="col-md-3 control-label lead"><strong>Listing Title</strong></label>
			<div class="col-md-9">
				<input type="text" class="form-control input-lg" name="title" maxlength="80" value="<%=title%>" placeholder="Listing title">
			</div>
		</div>
		<div class="form-group">
			<label for="price" class="col-md-3 control-label lead"><strong>Starting Price ($)</strong></label>
			<div class="col-md-9">
				<input type="text" class="form-control input-lg" name="price" maxlength="30" value="<%=price%>" placeholder="Starting price">
			</div>
		</div>
		<div class="form-group">
			<label for="buyItNow" class="col-md-3 control-label lead"><strong>Buy It Now Price ($)</strong></label>
			<div class="col-md-9">
				<input type="text" class="form-control input-lg" name="buyItNow" maxlength="30" placeholder="Optional, minimum 30% > starting price" value="<%=buyItNow %>">				
			</div>
		</div>
		<div class="form-group">
			<label for="time" class="col-md-3 control-label lead"><strong>Listing Duration</strong></label>
			<div class="col-md-9">
				<select name="time" class="form-control input-lg">
					<option value="3" <%if (time.equals("3")) out.println("selected");%>>3 days</option>
					<option value="5" <%if (time.equals("5")) out.println("selected");%>>5 days</option>
					<option value="7" <%if (time.equals("7") || (time.equals(""))) out.println("selected");%>>7 days</option>
				</select>
			</div>
		</div>
		<div class="form-group">
			<label for="condition" class="col-md-3 control-label lead"><strong>Item Condition</strong></label>
			<div class="col-md-9">
				<select name="condition" class="form-control input-lg">
					<option value="Used" <%if (condition.equals("Used")) out.println("selected");%>>Used</option>
					<option value="New" <%if (condition.equals("New")) out.println("selected");%>>New</option>
					<option value="New other" <%if (condition.equals("New other")) out.println("selected");%>>New other (see details)</option>
					<option value="Manufacturer refurbished" <%if (condition.equals("Manufacturer refurbished")) out.println("selected");%>>Manufacturer refurbished</option>
					<option value="Seller refurbished" <%if (condition.equals("Seller refurbished")) out.println("selected");%>>Seller refurbished</option>
					<option value="For parts or not working" <%if (condition.equals("For parts or not working")) out.println("selected");%>>For parts or not working</option>
				</select>
			</div>
		</div>
		<div class="form-group">
			<label for="category" class="col-md-3 control-label lead"><strong>Item Category</strong></label>
			<div class="col-md-9 lead">
				<% 
				int categoryIndex = 0;
				for (int i = 0; i < categories.size(); i++) { 
					if (!categories.get(i).equals("")) {
				%>
				<div class="radio">
					<label>
						<input type="radio" name="category" id="<%=i%>" onclick="updateAll('<%=i %>')" value="<%=categories.get(i)%>" <% if ((category.equals("") && i == 0) || categories.get(i).equals(category)) { categoryIndex = i; out.println("checked=\"checked\""); }%>> <%=categories.get(i) %>
					</label>
				</div>
				<% }} %>
			</div>
		</div>
		<div class="form-group">
			<label for="body" class="col-md-3 control-label lead"><strong>Item Details</strong></label>
			<div class="col-md-9">
				<textarea class="form-control input-lg" rows="4" name="body"><%=body %> </textarea>
			</div>
		</div>
		<input type='hidden' name='whatCategoryIndex' id='whatCategoryIndex' value='<%=categoryIndex %>'>
		
		<% if (spec0 != null) { %>	
		<div class="form-group" id='specRow0' <% if (categoryIndex != 0) out.println("style=\"display: none\""); %>>
			<label for="specRow0" class="col-md-3 control-label lead"><strong>Required Specifics</strong></label>
			<div class="col-md-9">				
		<%
			out.println("<input type=\"hidden\" name=\"0specCount\" value=\"" + spec0.length() + "\">");
			for (int i = 0; i < spec0.length(); i++) {
				JSONObject curr = spec0.getJSONObject(i);
				if (curr.get("type").equals("FreeText")) {
					out.println("<input type=\"hidden\" name=\"0specKey" + i + "\" value=\"" + curr.get("specific") + "\">");
					if (specificsMap.containsKey(curr.get("specific"))) {
						out.println("<input class=\"form-control input-lg\" type=\"text\" name=\"0specValue" + i + "\" size=\"50\" maxlength=\"60\" placeholder=\"" + curr.get("specific") + "\" value=\"" + specificsMap.get(curr.get("specific")) + "\">");
					}
					else out.println("<input class=\"form-control input-lg\" type=\"text\" name=\"0specValue" + i + "\" size=\"50\" maxlength=\"60\" placeholder=\"" + curr.get("specific") + "\">");
				}
				else {
					out.println("<input type=\"hidden\" name=\"0specKey" + i + "\" value=\"" + curr.get("specific") + "\">");
					out.println("<select class=\"form-control input-lg\" name=\"0specValue" + i + "\">");
					JSONArray suboptions = curr.getJSONArray("options");
					out.println("<option value=\"disabled\" disabled=\"disabled\">" + curr.get("specific") +  "</option>");
					String valueChoice = null;
					if (specificsMap.containsKey(curr.get("specific"))) {
						valueChoice = specificsMap.get(curr.get("specific"));
					}
					for (int k = 0; k < suboptions.length(); k++) {
						JSONObject subcurr = suboptions.getJSONObject(k);
						if (valueChoice != null && valueChoice.equals(subcurr.get("option" + k))) out.println("<option value=\"" + subcurr.get("option" + k) + "\" selected>" + subcurr.get("option" + k) + "</option>");
						else out.println("<option value=\"" + subcurr.get("option" + k) + "\">" + subcurr.get("option" + k) + "</option>");
					}
					out.println("</select>");
				}
			}
		%>
			</div>
		</div>
		<% } %>
		
		<% if (spec1 != null) { %>	
		<div class="form-group" id='specRow1' <% if (categoryIndex != 1) out.println("style=\"display: none\""); %>>
			<label for="specRow1" class="col-md-3 control-label lead"><strong>Required Specifics</strong></label>
			<div class="col-md-9">				
		<%
			out.println("<input type=\"hidden\" name=\"1specCount\" value=\"" + spec1.length() + "\">");
			for (int i = 0; i < spec1.length(); i++) {
				JSONObject curr = spec1.getJSONObject(i);
				if (curr.get("type").equals("FreeText")) {
					out.println("<input type=\"hidden\" name=\"1specKey" + i + "\" value=\"" + curr.get("specific") + "\">");
					if (specificsMap.containsKey(curr.get("specific"))) {
						out.println("<input class=\"form-control input-lg\" type=\"text\" name=\"1specValue" + i + "\" size=\"50\" maxlength=\"60\" placeholder=\"" + curr.get("specific") + "\" value=\"" + specificsMap.get(curr.get("specific")) + "\">");
					}
					else out.println("<input class=\"form-control input-lg\" type=\"text\" name=\"1specValue" + i + "\" size=\"50\" maxlength=\"60\" placeholder=\"" + curr.get("specific") + "\">");
				}
				else {
					out.println("<input type=\"hidden\" name=\"1specKey" + i + "\" value=\"" + curr.get("specific") + "\">");
					out.println("<select class=\"form-control input-lg\" name=\"1specValue" + i + "\">");
					JSONArray suboptions = curr.getJSONArray("options");
					out.println("<option value=\"disabled\" disabled=\"disabled\">" + curr.get("specific") +  "</option>");
					String valueChoice = null;
					if (specificsMap.containsKey(curr.get("specific"))) {
						valueChoice = specificsMap.get(curr.get("specific"));
					}
					for (int k = 0; k < suboptions.length(); k++) {
						JSONObject subcurr = suboptions.getJSONObject(k);
						if (valueChoice != null && valueChoice.equals(subcurr.get("option" + k))) out.println("<option value=\"" + subcurr.get("option" + k) + "\" selected>" + subcurr.get("option" + k) + "</option>");
						else out.println("<option value=\"" + subcurr.get("option" + k) + "\">" + subcurr.get("option" + k) + "</option>");
					}
					out.println("</select>");
				}
			}
		%>
			</div>
		</div>
		<% } %>
		
		<% if (spec2 != null) { %>	
		<div class="form-group" id='specRow2' <% if (categoryIndex != 2) out.println("style=\"display: none\""); %>>
			<label for="specRow2" class="col-md-3 control-label lead"><strong>Required Specifics</strong></label>
			<div class="col-md-9">				
		<%
			out.println("<input type=\"hidden\" name=\"2specCount\" value=\"" + spec2.length() + "\">");
			for (int i = 0; i < spec2.length(); i++) {
				JSONObject curr = spec2.getJSONObject(i);
				if (curr.get("type").equals("FreeText")) {
					out.println("<input type=\"hidden\" name=\"2specKey" + i + "\" value=\"" + curr.get("specific") + "\">");
					if (specificsMap.containsKey(curr.get("specific"))) {
						out.println("<input class=\"form-control input-lg\" type=\"text\" name=\"2specValue" + i + "\" size=\"50\" maxlength=\"60\" placeholder=\"" + curr.get("specific") + "\" value=\"" + specificsMap.get(curr.get("specific")) + "\">");
					}
					else out.println("<input class=\"form-control input-lg\" type=\"text\" name=\"2specValue" + i + "\" size=\"50\" maxlength=\"60\" placeholder=\"" + curr.get("specific") + "\">");
				}
				else {
					out.println("<input type=\"hidden\" name=\"2specKey" + i + "\" value=\"" + curr.get("specific") + "\">");
					out.println("<select class=\"form-control input-lg\" name=\"2specValue" + i + "\">");
					JSONArray suboptions = curr.getJSONArray("options");
					out.println("<option value=\"disabled\" disabled=\"disabled\">" + curr.get("specific") +  "</option>");
					String valueChoice = null;
					if (specificsMap.containsKey(curr.get("specific"))) {
						valueChoice = specificsMap.get(curr.get("specific"));
					}
					for (int k = 0; k < suboptions.length(); k++) {
						JSONObject subcurr = suboptions.getJSONObject(k);
						if (valueChoice != null && valueChoice.equals(subcurr.get("option" + k))) out.println("<option value=\"" + subcurr.get("option" + k) + "\" selected>" + subcurr.get("option" + k) + "</option>");
						else out.println("<option value=\"" + subcurr.get("option" + k) + "\">" + subcurr.get("option" + k) + "</option>");
					}
					out.println("</select>");
				}
			}
		%>
			</div>
		</div>
		<% } %>
		
		<div class="form-group" id="attributeRow"  <% if (attribute.equals("")) out.println("style=\"display: none\""); %> >
			<label for="attributes" class="col-md-3 control-label lead"><strong>Item Attributes</strong></label>
			<div class="col-md-9">
				<textarea name="attributes" class="form-control input-lg" id="attributes" rows="4" readonly><% if (attribute.equals("")) out.println(attributeList.get(categoryIndex)); else out.println(attribute);%></textarea>
			</div>
		</div>
		
		<div class="form-group" id="shippingRow0" <% if (categoryIndex != 0) out.println("style=\"display: none\""); %>>
			<label for="shipping" class="col-md-3 control-label lead"><strong>Shipping Options</strong></label>
			<div class="col-md-9 lead">
				<div class="radio">
					<label><input type="radio" name="0shipping" id="shipping-1" value="Free Shipping" <%if (shippingChoice.equals("Free Shipping")) out.println("checked=\"checked\"");%>>Free Shipping   <small><em>[Seller pays for shipping]</em></small></label>
				</div>
				<div class="radio" id="shippingDisplay0" <% if (options.get(0).equals("")) out.println("style=\"display: none\"");%>>
					<label>
						<input type="radio" name="0shipping" id="shipping0" value="<%=options.get(0)%>" <%if (shippingChoice.equals("") || options.get(0).equals(shippingChoice)) out.println("checked=\"checked\"");%>>
						<%=options.get(0)%>   <small><em>[Buyer pays for shipping]</em></small>
					</label>
				</div>
				<div class="radio" id="shippingDisplay1" <% if (options.get(1).equals("")) out.println("style=\"display: none\"");%>>
					<label>
						<input type="radio" name="0shipping" id="shipping1" value="<%=options.get(1)%>" <%if (options.get(1).equals(shippingChoice) && !shippingChoice.equals("")) out.println("checked=\"checked\"");%>>
						<%=options.get(1)%>   <small><em>[Buyer pays for shipping]</em></small>
					</label>
				</div>
				<div class="radio">
					<label>
						<input type="radio" name="0shipping" id="shipping2" value="No shipping: local pickup only" <%if (shippingChoice.equals("No shipping: local pickup only")) out.println("checked=\"checked\"");%>>
						No shipping: local pickup only
					</label>
				</div>
			</div>
		</div>
		
		<div class="form-group" id="shippingRow1" <% if (categoryIndex != 1) out.println("style=\"display: none\""); %>>
			<label for="shipping" class="col-md-3 control-label lead"><strong>Shipping Options</strong></label>
			<div class="col-md-9 lead">
				<div class="radio">
					<label><input type="radio" name="1shipping" id="shipping-1" value="Free Shipping" <%if (shippingChoice.equals("Free Shipping")) out.println("checked=\"checked\"");%>>Free Shipping   <small><em>[Seller pays for shipping]</em></small></label>
				</div>
				<div class="radio" id="shippingDisplay0" <% if (options.get(2).equals("")) out.println("style=\"display: none\"");%>>
					<label>
						<input type="radio" name="1shipping" id="shipping0" value="<%=options.get(2)%>" <%if (shippingChoice.equals("") || options.get(2).equals(shippingChoice)) out.println("checked=\"checked\"");%>>
						<%=options.get(2)%>   <small><em>[Buyer pays for shipping]</em></small>
					</label>
				</div>
				<div class="radio" id="shippingDisplay1" <% if (options.get(3).equals("")) out.println("style=\"display: none\"");%>>
					<label>
						<input type="radio" name="1shipping" id="shipping1" value="<%=options.get(1)%>" <%if (options.get(3).equals(shippingChoice) && !shippingChoice.equals("")) out.println("checked=\"checked\"");%>>
						<%=options.get(3)%>   <small><em>[Buyer pays for shipping]</em></small>
					</label>
				</div>
				<div class="radio">
					<label>
						<input type="radio" name="1shipping" id="shipping2" value="No shipping: local pickup only" <%if (shippingChoice.equals("No shipping: local pickup only")) out.println("checked=\"checked\"");%>>
						No shipping: local pickup only
					</label>
				</div>
			</div>
		</div>
		
		<div class="form-group" id="shippingRow2" <% if (categoryIndex != 2) out.println("style=\"display: none\""); %>>
			<label for="shipping" class="col-md-3 control-label lead"><strong>Shipping Options</strong></label>
			<div class="col-md-9 lead">
				<div class="radio">
					<label><input type="radio" name="2shipping" id="shipping-1" value="Free Shipping" <%if (shippingChoice.equals("Free Shipping")) out.println("checked=\"checked\"");%>>Free Shipping   <small><em>[Seller pays for shipping]</em></small></label>
				</div>
				<div class="radio" id="shippingDisplay0" <% if (options.get(4).equals("")) out.println("style=\"display: none\"");%>>
					<label>
						<input type="radio" name="2shipping" id="shipping0" value="<%=options.get(4)%>" <%if (shippingChoice.equals("") || options.get(4).equals(shippingChoice)) out.println("checked=\"checked\"");%>>
						<%=options.get(4)%>   <small><em>[Buyer pays for shipping]</em></small>
					</label>
				</div>
				<div class="radio" id="shippingDisplay1" <% if (options.get(5).equals("")) out.println("style=\"display: none\"");%>>
					<label>
						<input type="radio" name="2shipping" id="shipping1" value="<%=options.get(5)%>" <%if (options.get(5).equals(shippingChoice) && !shippingChoice.equals("")) out.println("checked=\"checked\"");%>>
						<%=options.get(5)%>   <small><em>[Buyer pays for shipping]</em></small>
					</label>
				</div>
				<div class="radio">
					<label>
						<input type="radio" name="2shipping" id="shipping2" value="No shipping: local pickup only" <%if (shippingChoice.equals("No shipping: local pickup only")) out.println("checked=\"checked\"");%>>
						No shipping: local pickup only
					</label>
				</div>
			</div>
		</div>
		
		<div class="form-group">
			<label for="location" class="col-md-3 control-label lead"><strong>Your Location</strong></label>
			<div class="col-md-9">
				<input type="text" class="form-control input-lg" name="location" maxlength="80" value="<%=location%>" placeholder="Required (e.g. 95014 or San Jose, CA)">	
			</div>
		</div>
		
		<div class="form-group">
			<label for="handlingTime" class="col-md-3 control-label lead"><strong>Handling Time</strong></label>
			<div class="col-md-9">
				<select name="handlingTime" class="form-control input-lg">
  					<option value="disabled" disabled="disabled">This is the amount of time you will take to ship the item after it sells.</option>
  					<option value="1" <% if (handlingTime.equals("1")) out.println("selected"); %>>1 business day</option>
  					<option value="2" <% if (handlingTime.equals("2")) out.println("selected"); %>>2 business days</option>
  					<option value="3" <% if (handlingTime.equals("3")) out.println("selected"); %>>3 business days</option>
				</select>
			</div>
		</div>

		<div class="form-group">
			<label for="returns" class="col-md-3 control-label lead"><strong>Return Policy</strong></label>
			<div class="col-md-9">
				<select name="returns" class="form-control input-lg">
  					<option value="yes" <% if (returns.equals("yes")) out.println("selected"); %>>Returns Accepted (30 days)</option>
  					<option value="no" <% if (returns.equals("no")) out.println("selected"); %>>No Returns Accepted</option>		
  				</select>
			</div>
		</div>
		
		<div class="form-group">
			<label for="confirmationPic" class="col-md-3 control-label lead"><strong>Confirmation Phrase</strong></label>
			<div class="col-md-9">
				<img style="-moz-transform: scaleY(-1); -o-transform: scaleY(-1); -webkit-transform: scaleY(-1); transform: scaleY(-1); filter: FlipV; -ms-filter: 'FlipV';" src="<%=captchaURL%>" alt="" width="300px" height="225px"/>
			</div>
		</div>
		
		<div class="form-group">
			<label for="captcha" class="col-md-3 control-label lead"><strong>Enter Confirmation Phrase</strong></label>
			<div class="col-md-9">
				<input type="text" class="form-control input-lg" name="captcha" size="50" maxlength="500" placeholder="Decode above picture (case-insensitive).">
			</div>
		</div>
		
		<input type="hidden" name="key" value="<%=keyString%>">
		
				
		<div class="form-group">
			<div class="col-md-offset-3 col-md-9">
				<button type="submit" class="btn btn-primary btn-lg">Submit</button>
			</div>
		</div>
	</form>
	<%	} %>
    </div> <!-- /container -->


    <!-- Bootstrap core JavaScript
    ================================================== -->
    <!-- Placed at the end of the document so the pages load faster -->
    <script src="../assets/js/jquery.js"></script>
    <script src="../dist/js/bootstrap.min.js"></script>
    <script src="../assets/js/holder.js"></script>
	<script type="text/javascript" src="../assets/fancybox/jquery.fancybox.js?v=2.1.5"></script>
	<script type="text/JavaScript"> 
function updateAll(i)
{
	document.getElementById("shippingRow0").style.display='none';
	document.getElementById("shippingRow1").style.display='none';
	document.getElementById("shippingRow2").style.display='none';
	document.getElementById("shippingRow" + i).style.display='inherit';

	var three = document.getElementById('attribute' + i).value;
	// Add hiding functionality when either of the options is ""
	var attributeField = document.getElementById("attributes");
	attributeField.innerHTML = three;
	if (three == "") document.getElementById("attributeRow").style.display='none';
	else document.getElementById("attributeRow").style.display = 'inherit';
	if (document.getElementById("specRow0") != null) document.getElementById("specRow0").style.display='none';
	if (document.getElementById("specRow1") != null) document.getElementById("specRow1").style.display='none';
	if (document.getElementById("specRow2") != null) document.getElementById("specRow2").style.display='none';
	if (document.getElementById("specRow" + i) != null) document.getElementById("specRow" + i).style.display='inherit';
	document.getElementById("whatCategoryIndex").value = i;
}
</script>
<script type="text/javascript">
		$(document).ready(function() {
			/*
			 *  Simple image gallery. Uses default settings
			 */

			$('.fancybox').fancybox({
				type: 'image'
			});
		});
	</script>
  </body>
</html>
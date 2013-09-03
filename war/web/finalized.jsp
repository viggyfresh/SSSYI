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

    <title>Listing Review</title>

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
        <h1>Listing Finalized</h1>
<%
String keyString = request.getParameter("key");
if (keyString == null || keyString.equals("")) {
	out.println("<p>An internal servlet error occurred.</p></div>");
} else {
	String adj = request.getParameter("adj");
	if (adj == null) adj = "";
	if (!DatabaseModule.initialized) DatabaseModule.init();
	// Retrieves ALL information related to the current entry being viewed
	Listing listing = DatabaseModule.retrieveListing(Long.parseLong(keyString));
	out.println("<p>Your changes have been saved! They are shown below.</p>");
	out.println("<p>To make further changes to your listing, please go <a href=\"listing.jsp?key=" + keyString + "\">here.</a></p>");
	out.println("<p>To submit your listing to eBay, click the submit button. It will be listed under the account 'jamzadeh-seller' for now.</p>");
	if (adj.equals("true")) out.println("<p class=\"text-danger\">Your buy it now price wasn't 30% greater than the starting price! Please go back and fix this if you wish.</p></div>");
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
	String attribute = (String) listing.getAttribute();
	if (attribute == null) 
		attribute = "";
	String location = listing.getLocation();
	if (location == null) location = "";
	String handlingTime = listing.getHandlingTime();
	if (handlingTime == null) handlingTime = "";
	String returns = listing.getReturns();
	if (returns == null) returns = "";
	String specifics = listing.getSpecifics();
	if (specifics == null) specifics = "";
	JSONArray specificsArray;
	if (specifics.equals("[]")) specificsArray = null;
	else specificsArray = new JSONArray(specifics);
	%>

	  
	  
	  <div class="page-header"></div>
	  <form id="actuallyListItem" action="ListItemServlet" class="form-horizontal" role="form" method="POST">
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
				<p class="form-control-static lead"><%=title%></p>
			</div>
		</div>
		<div class="form-group">
			<label for="price" class="col-md-3 control-label lead"><strong>Starting Price ($)</strong></label>
			<div class="col-md-9">
				<p class="form-control-static lead"><%=price%></p>
			</div>
		</div>
		<div class="form-group">
			<label for="buyItNow" class="col-md-3 control-label lead"><strong>Buy It Now Price ($)</strong></label>
			<div class="col-md-9">
				<p class="form-control-static lead"><%=buyItNow %></p>				
			</div>
		</div>
		<div class="form-group">
			<label for="time" class="col-md-3 control-label lead"><strong>Listing Duration</strong></label>
			<div class="col-md-9">
				<p class="form-control-static lead"><%=time %> day(s)</p>				
			</div>
		</div>
		<div class="form-group">
			<label for="condition" class="col-md-3 control-label lead"><strong>Item Condition</strong></label>
			<div class="col-md-9">
				<p class="form-control-static lead"><%=condition %></p>				
			</div>
		</div>
		<div class="form-group">
			<label for="category" class="col-md-3 control-label lead"><strong>Item Category</strong></label>
			<div class="col-md-9">
				<p class="form-control-static lead"><%=category %></p>				
			</div>
		</div>
		<div class="form-group">
			<label for="body" class="col-md-3 control-label lead"><strong>Item Details</strong></label>
			<div class="col-md-9">
				<p class="form-control-static lead"><%=body %></p>				
			</div>
		</div>
		<% if (specificsArray != null) { %>		
		<div class="form-group">
			<label for="specifics" class="col-md-3 control-label lead"><strong>Required Specifics</strong></label>
			<div class="col-md-9">
				<p class="form-control-static lead">
					<% 
					if (specificsArray != null) {
						for (int i = 0; i < specificsArray.length(); i++) {
							JSONObject curr = specificsArray.getJSONObject(i);
							out.println(curr.get("key") + ": " + curr.get("value") + "<br/>");
						}
					}					
					%>
				</p>				
			</div>
		</div>	
		<% } %>
		
		
		
		<div class="form-group" id="attributeRow"  <% if (attribute.equals("")) out.println("style=\"display: none\""); %> >
			<label for="attributes" class="col-md-3 control-label lead"><strong>Item Attributes</strong></label>
			<div class="col-md-9">
				<p class="form-control-static lead"><%=attribute %></p>				
			</div>
		</div>
		
		<div class="form-group">
			<label for="shippingChoice" class="col-md-3 control-label lead"><strong>Shipping Choice</strong></label>
			<div class="col-md-9">
				<p class="form-control-static lead"><%=shippingChoice %></p>				
			</div>
		</div>
		
		
		<div class="form-group">
			<label for="location" class="col-md-3 control-label lead"><strong>Your Location</strong></label>
			<div class="col-md-9">
				<p class="form-control-static lead"><%=location %></p>							
			</div>
		</div>
		
		<div class="form-group">
			<label for="handlingTime" class="col-md-3 control-label lead"><strong>Handling Time</strong></label>
			<div class="col-md-9">
				<p class="form-control-static lead"><%=handlingTime %> business day(s)</p>				
			</div>
		</div>

		<div class="form-group">
			<label for="returns" class="col-md-3 control-label lead"><strong>Return Policy</strong></label>
			<div class="col-md-9">
				<p class="form-control-static lead">
					<%
						if (returns.equals("yes")) out.println("Returns Accepted");
						else out.println("No Returns Accepted");
					%>
				</p>				
			</div>
		</div>
		
		<input type="hidden" name="key" value="<%=keyString%>">
		
				
		<div class="form-group">
			<div class="col-md-offset-3 col-md-9">
				<button type="submit" id="submit_to_ebay" class="btn btn-success btn-lg" data-loading-text="Submitting...">Submit to eBay</button>
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
		$(document).on("click", "#submit_to_ebay", function() {
			var btn = $(this);
			btn.button('loading');
			$('#actuallyListItem').submit();
		});
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
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

    <title>Listing Submitted!</title>

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
        <h1>Listing Submitted to eBay</h1>
		<%
		String id = request.getParameter("id");
		String key = request.getParameter("key");
		out.println("<p>Your listing has been submitted to eBay!</p>");
		out.println("<p>To view or modify it, please follow the links below!</p>");
		out.println("<a class=\"btn btn-primary btn-lg\" href=\"http://www.ebay.com/itm/" + id + "\">View Item</a>");
		out.println("<a class=\"btn btn-info btn-lg\" href=\"listing.jsp?key=" + key + "\">Revise Item</a>");
	
		%>
	   </div>
    </div> <!-- /container -->


    <!-- Bootstrap core JavaScript
    ================================================== -->
    <!-- Placed at the end of the document so the pages load faster -->
    <script src="../assets/js/jquery.js"></script>
    <script src="../dist/js/bootstrap.min.js"></script>
    <script src="../assets/js/holder.js"></script>
  </body>
</html>
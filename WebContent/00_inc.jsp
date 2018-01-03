<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!-- Start: 00_inc.jsp -->
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<jsp:useBean id="ed" scope="session" class="bean.EDbean" />
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-beta.2/css/bootstrap.min.css">
<script src="https://code.jquery.com/jquery-3.2.1.slim.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.12.3/umd/popper.min.js" ></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-beta.2/js/bootstrap.min.js" ></script>
<body background="bg.jpeg" style="background-size: cover">
<span style="font-family: 'Arial Black'; color: white;">
Aktuelles Datum: <jsp:getProperty name="ed" property="datum" /><br/> 
Dein Nutzername: <jsp:getProperty name="ed" property="nutzer" />
</span>
<form action="Controller" method="post">
	<input type="hidden" name="action" value="00_Abmelden" />
	<input type="submit" class="btn btn-m btn-info " value="abmelden" />
</form>
<h1 style="text-align:center; font-family: 'Arial Black'; color: Black;">Fertiliser</h1>
</body>
<!-- End: 00_inc.jsp -->

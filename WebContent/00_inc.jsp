<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!-- Start: 00_inc.jsp -->
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<jsp:useBean id="ed" scope="session" class="bean.EDbean" />
<table width="100%" border="0">
<tr valign="middle">
<th align="left">
Datum: <jsp:getProperty name="ed" property="datum" /><br/> 
Nutzer: <jsp:getProperty name="ed" property="nutzer" />
<form action="Controller" method="post">
	<input type="hidden" name="action" value="00_Abmelden" />
	<input type="submit" value="abmelden" />
</form>
</th>
<th><h2>Fertiliser</h2></th>
<th align="right">
<img src="logo.gif" align="top">
</th>
</tr>
</table>
<!-- End: 00_inc.jsp -->

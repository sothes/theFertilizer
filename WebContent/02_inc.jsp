<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!-- Start: 02_inc.jsp -->
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<jsp:useBean id="ed" scope="session" class="bean.EDbean" />
<center>
Modell - Overview:<br/>
<jsp:getProperty name="ed" property="modelsOverview" />
<br/>
<form action="Controller" method="post">
Add a new Model:<br/>
<jsp:getProperty name="ed" property="modelAddView" />
<input type="hidden" name="action" value="14_addModel">
<input type="submit" value="add"> 
</form>

<!-- End: 02_inc.jsp -->

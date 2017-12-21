<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!-- Start: 02_inc.jsp -->
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<jsp:useBean id="ed" scope="session" class="bean.EDbean" />
<br>
<center>
Modell: <jsp:getProperty name="ed" property="modelName" />
<form action="Controller" method="post">
<input type="hidden" name="action" value="02_selectModel">
<input type="submit" value="zurück"> 
</form>
<br>
<jsp:getProperty name="ed" property="zutatenTableau" /><br/>
<jsp:getProperty name="ed" property="vorhandenDuengerTableau" /><br/>
<jsp:getProperty name="ed" property="benoetigteDuengerTableau" /><br/>
<jsp:getProperty name="ed" property="solutionTableau" />

<form action="Controller" method="post">
<input type="hidden" name="action" value="10_solveModel">
<jsp:getProperty name="ed" property="hiddenModdelId" />
<input type="submit" value="solve"> 
</form>
</center>
<!-- End: 02_inc.jsp -->

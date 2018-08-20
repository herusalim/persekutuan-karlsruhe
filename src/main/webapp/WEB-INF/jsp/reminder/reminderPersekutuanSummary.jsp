<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<head><title>Send Reminder Summary</title></head>
<body>
<c:if test="${empty sentReminders}">
Tidak ada reminder yang dikirimkan!
</c:if>

<c:if test="${not empty sentReminders}">
	<h2>Reminder Terkirim</h2>
	<ul>
	<c:forEach items="${sentReminders}" var="sentReminder">
		<li><c:out value="${sentReminder.orang}" />: Persekutuan tanggal <fmt:formatDate pattern="dd.MM.yyyy" value="${sentReminder.tanggal}" /></li>
	</c:forEach>
	</ul>
</c:if>
</body>
</html>
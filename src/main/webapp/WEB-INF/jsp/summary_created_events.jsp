<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<html>
<head><title>Generate Jadwal Persekutuan</title></head>
<body>
	<h2>Daftar Event yang baru ditambahkan: <c:if test="${empty createdEvents}"><font color="red">[Tidak Ada]</font></c:if></h2>
	<c:set var="events" value="${createdEvents}" scope="request" />
	<jsp:include page="printEventTable.jsp" />
	
	<c:if test="${not empty updatedEvents}">
	<h2>Daftar Event yang diperbaharui (overwritten): </h2>
	<c:set var="events" value="${updatedEvents}" scope="request" />
	<jsp:include page="printEventTable.jsp" />
	</c:if>
	
	<h2>Daftar jadwal yang diabaikan karena event sudah ada: <c:if test="${empty ignoredEvents}"><font color="red">[Tidak Ada]</font></c:if></h2>
	<c:set var="events" value="${ignoredEvents}" scope="request" />
	<jsp:include page="printEventTable.jsp" />
	
</body>
</html>
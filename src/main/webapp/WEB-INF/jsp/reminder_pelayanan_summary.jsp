<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<head><title>Send Reminder Summary</title></head>
<body>
<c:if test="${empty daftarTerkirim}">
Tidak ada jadwal persekutuan yang ada di depan mata!
</c:if>

<c:if test="${not empty daftarTerkirim}">
	<h2>Jadwal persekutuan terdekat</h2>
	<c:forEach items="${daftarTerkirim}" var="terkirim">
		<p/>
		<b>Persekutuan: <fmt:formatDate pattern="dd.MM.yyyy" value="${terkirim.tanggal}" /></b>
		<p/>
		Email terkirim ke: <c:if test="${empty terkirim.daftarPetugas}">[Tidak ada]</c:if>
		<p/>
		<ul>
		<c:forEach items="${terkirim.daftarPetugas}" var="petugas">
			<li><c:out value="${petugas}" /></li>
		</c:forEach>
		</ul>
	</c:forEach>
</c:if>
</body>
</html>
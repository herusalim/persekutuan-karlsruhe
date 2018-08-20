<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<% pageContext.setAttribute("newLineChar", "\n"); %>
	
	<c:if test="${not empty events}">
	<jsp:useBean id="dateStart" class="java.util.Date"/>
	<jsp:useBean id="dateEnd" class="java.util.Date"/>
	<c:set var="dateTimePattern" value="dd.MM.yyyy - HH:mm [z]" />
	<table  border="1" cellpadding="10">
		<tr>
			<th>Mulai</th>
			<th>Selesai</th>
			<th>Acara</th>
			<th>Deskripsi</th>
			<th>Peserta</th>
			<th>Lokasi</th>
		</tr>
		<c:forEach var="event" items="${events}">
		<tr>
			<fmt:timeZone value="${timeZone}">
			<jsp:setProperty name="dateStart" property="time" value="${event.start.dateTime.value}"/>
			<jsp:setProperty name="dateEnd" property="time" value="${event.end.dateTime.value}"/>		
			<td><fmt:formatDate pattern="${dateTimePattern}" value="${dateStart}" /></td>
			<td><fmt:formatDate pattern="${dateTimePattern}" value="${dateEnd}" /></td>
			</fmt:timeZone>
			<td><c:out value="${event.summary}" /></td>
			<td>${fn:replace(event.description, newLineChar, "<br /> ")}</td>
			<td>
			<c:forEach var="attendee" items="${event.attendees}">
				<c:out value="${attendee.displayName}" />
				&lt;<c:out value="${attendee.email}" />&gt;;
			</c:forEach>
			</td>
			<td><c:out value="${event.location}" /></td>
		</tr>
		</c:forEach>
	</table>
	</c:if>
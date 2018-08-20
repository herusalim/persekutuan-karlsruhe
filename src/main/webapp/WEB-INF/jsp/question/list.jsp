<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%
    pageContext.setAttribute("newLineChar", "\n");
%>

<c:set var="title" value="Daftar Pertanyaan" scope="request" />
<jsp:include page="../common/header.jsp" />
<jsp:useBean id="dateTime" class="java.util.Date" />
<c:set var="dateTimePattern" value="dd.MM.yyyy - HH:mm [z]" />

<jsp:useBean id="dateTimeSelesai" class="java.util.Date" />
<c:set var="dateTimePatternSelesai" value="dd.MM.yyyy" />

<a href="/kirimPertanyaan">Kirim Pertanyaan</a>
<h1>Daftar Pertanyaan</h1>

<c:if test="${not empty daftarPertanyaan}">
	<table class="w3-table-all">
		<tr>
			<th>No.</th>
			<th>Tanggal</th>
			<th>Pertanyaan</th>
		</tr>
		<c:set var="count" value="0" scope="page" />
		<c:forEach var="pertanyaan" items="${daftarPertanyaan}">
			<c:if test="${pertanyaan.showPublic}">
				<c:set var="count" value="${count + 1}" scope="page" />
				<tr>
					<td colspan="1"><c:out value="${count}" /></td>
					<fmt:timeZone value="${timeZone}">
						<jsp:setProperty name="dateTime" property="time"
							value="${pertanyaan.timestamp}" />
						<td colspan="1" align="center"><fmt:formatDate
								pattern="${dateTimePattern}" value="${dateTime}" /></td>
					</fmt:timeZone>
					<c:set var="textPertanyaan" value="${pertanyaan.text}" />
					<td colspan="2"><c:if
							test="${pertanyaan.timestampSelesai gt 0}">
							<strike>
						</c:if>${fn:replace(textPertanyaan, newLineChar, "<br>")} <c:if
							test="${pertanyaan.timestampSelesai gt 0}">
							</strike>
							<fmt:timeZone value="${timeZone}">
								<jsp:setProperty name="dateTimeSelesai" property="time"
									value="${pertanyaan.timestampSelesai}" />
								<br />(Sudah dibahas tanggal <fmt:formatDate
									pattern="${dateTimePatternSelesai}" value="${dateTimeSelesai}" />)
						</fmt:timeZone>

						</c:if></td>
				</tr>
			</c:if>
		</c:forEach>
	</table>
</c:if>

<c:if test="${empty daftarPertanyaan}">
	<h2>
		<font color="red">Belum ada pertanyaan untuk ditampilkan!</font>
	</h2>
</c:if>

<jsp:include page="../common/footer.jsp" />
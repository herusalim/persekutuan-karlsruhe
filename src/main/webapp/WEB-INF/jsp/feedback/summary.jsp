<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:set var="title" value="Kirim Feedback" scope="request" />
<jsp:include page="../common/header.jsp" />

<c:if test="${isSuccess}">Feedback berhasil dikirim via Email.</c:if>
<c:if test="${not isSuccess}">Feedback gagal dikirim per email, tapi telah disimpan di database. Hubungi Heru untuk lebih lanjut.</c:if>

<p>
	<a href="/feedback">Kirim Feedback lagi</a>
</p>
<jsp:include page="../common/footer.jsp"/>
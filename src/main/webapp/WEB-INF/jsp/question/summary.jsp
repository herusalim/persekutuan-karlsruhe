<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:set var="title" value="Kirim Pertanyaan" scope="request" />
<jsp:include page="../common/header.jsp" />

<c:if test="${isSuccess}">Pertanyaan berhasil dikirim via Email.</c:if>
<c:if test="${not isSuccess}">Email pertanyaan gagal dikirim, tapi telah disimpan di database.</c:if>

<p>
	<a href="/tampilPertanyaan">Lihat daftar pertanyaan</a>
</p>

<p>
	<a href="/kirimPertanyaan">Tulis pertanyaan lain</a>
</p>
<jsp:include page="../common/footer.jsp"/>
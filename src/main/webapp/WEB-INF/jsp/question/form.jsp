<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:set var="title" value="Kirim Pertanyaan" scope="request" />
<jsp:include page="../common/header.jsp" />

<h1>Masukkan Pertanyaan di sini</h1>
<form action="/kirimPertanyaan" method="POST">
	<label class="w3-label">Nama (optional - tidak akan ditampilkan)</label> 
	<input name="nama" class="w3-input" type="text" value="${nama}" /> <br />
	<textarea required name="pertanyaan" rows="5" cols="40"></textarea>
	<br /> <input type="checkbox" name="tampilkan" value="true" checked>Boleh ditampilkan di daftar pertanyaan<br>
	<button type="submit">Kirim</button>
</form>

<p>
	<a href="/tampilPertanyaan">Lihat daftar pertanyaan</a>
</p>

<jsp:include page="../common/footer.jsp" />
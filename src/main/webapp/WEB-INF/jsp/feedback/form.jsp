<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:set var="title" value="Kirim Feedback" scope="request" />
<jsp:include page="../common/header.jsp" />

<h1>Kirim Feedback</h1>
<form action="/feedback" method="POST">
	<label class="w3-label">Nama (optional)</label> 
	<input name="nama" class="w3-input" type="text" value="${nama}" /> <br />
	<label class="w3-label">Judul (optional)</label> 
	<input name="judul" class="w3-input" type="text" value="${nama}" /> <br />
	<textarea required name="feedback" rows="5" cols="40"></textarea><br />
	<label class="w3-label">Penerima Feedback</label> 
	<select required name="penerima" class="w3-select">
	  <option value="">&lt;Pilih Nama Penerima&gt;</option>
	  <c:forEach items="${daftarPenerima}" var="penerima">
	  	<option value="${penerima}">${penerima}</option>	
	  </c:forEach>
	</select>
<button type="submit" class="w3-button w3-blue">Kirim</button>
</form>

<jsp:include page="../common/footer.jsp" />
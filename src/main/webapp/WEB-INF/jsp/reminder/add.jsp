<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="title" value="Daftar Reminder Persekutuan" scope="request" />
<jsp:include page="../common/header.jsp"/>

<jsp:include page="navi.jsp"/>

<c:set var="nama" value="${param['nama']}"/>
<c:if test="${(empty nama) and (not empty currentUser)}">
<c:set var="nama" value="${currentUser.nickname}"/>
</c:if>

<c:set var="email" value="${param['email']}"/>
<c:if test="${(empty email) and (not empty currentUser)}">
<c:set var="email" value="${currentUser.email}"/>
</c:if>

<h3>Info Pendaftaran</h3>
<c:if test="${empty currentUser}">
<article>Untuk mendaftarkan reminder untuk email kamu, masukkan data di bawah. Setelah selesai, kamu akan menerima link aktivasi untuk mengaktifkan pendaftaran kamu (agar email kamu tidak sembarangan didaftarkan oleh orang lain).</article>
<p/>
<article>Untuk mempermudah proses pendaftaran, proses validasi/aktivasi bisa menggunakan account Google kamu (pendaftaran akan langsung aktif tanpa email aktivasi).</article>
<p/>
<article><a href="${loginURL}">Klik di sini</a> untuk <strong>login</strong> menggunakan account Google kamu.</article>
</c:if>

<c:if test="${not empty currentUser}">
<article>Kamu telah logged-in dengan account Google <strong><i><c:out value="${currentUser.nickname} <${currentUser.email}>" /></i></strong>.</article>
<p/>
<article>Kalau kamu mau mendaftarkan reminder untuk email <strong><i><c:out value="${currentUser.email}" /></i></strong>, pendaftaran kamu akan langsung aktif tanpa memerlukan email aktivasi.</article>
<p/>
<article><a href="${logoutURL}">Klik di sini</a> untuk <strong>keluar (logout)</strong> dari account Google.</article>
</c:if>

<p/>
<form action="" method="POST" class="w3-container">
<input name="nama" required class="w3-input <c:if test="${inputNamaError}">w3-border-red</c:if>" type="text" value="${nama}"/>
<label class="w3-label w3-validate <c:if test="${inputNamaError}">w3-text-red</c:if>">Nama</label>
<br/>
<input name="email" required class="w3-input <c:if test="${inputEmailError}">w3-border-red</c:if>" type="email" value="${email}"/>
<label class="w3-label w3-validate <c:if test="${inputEmailError}">w3-text-red</c:if>"">Email</label>
<p/>
<label <c:if test="${inputSelectionsError}">class="w3-text-red"</c:if>>Pilihan reminder:</label>
<c:forEach begin="1" end="14" var="val">
	<br/>
	<input class="w3-check" name="selections" type="checkbox" value="${val}"/>
	<label>${val} hari sebelumnya (H-${val})</label>
</c:forEach>
<p/>
<input type="submit" class="w3-btn w3-blue" value="Kirim"/>
</form>
<p/>

<jsp:include page="../common/footer.jsp"/>
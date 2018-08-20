<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="title" value="Ubah Reminder Persekutuan" scope="request" />
<jsp:include page="../common/header.jsp"/>

<jsp:include page="navi.jsp"/>

<h3>Info Pengubahan Data</h3>
<c:if test="${empty currentUser}">
<article>Untuk mengubah reminder kamu, masukkan alamat email kamu di bawah ini. Setelah selesai, kamu akan menerima link untuk mengubah data reminder (agar reminder kamu tidak sembarangan diubah oleh orang lain).</article>
<p/>
<article>Untuk mempermudah proses pengubahan data, proses validasi bisa menggunakan account Google kamu (kamu bisa mengubah data kamu tanpa konfirmasi via email).</article>
<p/>
<article><a href="${loginURL}">Klik di sini</a> untuk <strong>login</strong> menggunakan account Google kamu.</article>
</c:if>

<c:if test="${not empty currentUser}">
<article>Kamu telah logged-in dengan account Google <strong><i><c:out value="${currentUser.nickname} <${currentUser.email}>" /></i></strong>.</article>
<p/>
<article>Kalau kamu mau mengubah data reminder untuk email <strong><i><c:out value="${currentUser.email}" /></i></strong>, kamu bisa langsung mengubah data kamu tanpa memerlukan konfirmasi via email.</article>
<p/>
<article><a href="${logoutURL}">Klik di sini</a> untuk <strong>keluar (logout)</strong> dari account Google.</article>
</c:if>

<p/>
<form action="" method="POST" class="w3-container">
<input name="email" required class="w3-input type="email" value="${email}" <c:if test="${not empty register}">disabled</c:if> />
<label class="w3-label w3-validate">Email</label>
<p/>

<input name="kirim" type="submit" class="w3-btn w3-blue" value="Kirim"/>
</form>
<p/>

<jsp:include page="../common/footer.jsp"/>
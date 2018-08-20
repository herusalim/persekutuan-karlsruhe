<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="title" value="Ubah Reminder Persekutuan" scope="request" />
<jsp:include page="../common/header.jsp"/>

<jsp:include page="navi.jsp"/>

<p/>
<form action="" method="POST" class="w3-container">

<h3>Info Pengubahan Data</h3>
<article>Masukkan data yang ingin kamu ubah untuk email <strong><i><c:out value="${register.email}" /></i></strong>.</article>

<p/>

<input name="nama" required class="w3-input <c:if test="${inputNamaError}">w3-border-red</c:if>" type="text" value="${register.nama}"/>
<label class="w3-label w3-validate <c:if test="${inputNamaError}">w3-text-red</c:if>">Nama</label>
<input name="email" required class="w3-input" type="email" value="${register.email}" readonly />
<label class="w3-label w3-validate">Email</label>
<p/>
<label <c:if test="${inputSelectionsError}">class="w3-text-red"</c:if>>Pilihan reminder:</label>
<c:forEach begin="1" end="14" var="val">
	<c:set var="mapKey" value="selection${val}"/>
	<br/>
	<input class="w3-check" name="selections" type="checkbox" value="${val}" <c:if test="${register.reminderListSelections[mapKey]}">checked</c:if>/>
	<label>${val} hari sebelumnya (H-${val})</label>
</c:forEach>
<p/>
<input name="ubah" type="submit" class="w3-btn w3-blue" value="Ubah"/>
<input name="batal" type="submit" class="w3-btn w3-blue" value="Batal" onclick="return confirm('Yakin ingin batal mengubah reminder?')"/>
<input name="hapus" type="submit" class="w3-btn w3-blue" value="Hapus Data" onclick="return confirm('Yakin ingin menghapus semua reminder ini?')"/>

<jsp:include page="../common/footer.jsp"/>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%
    pageContext.setAttribute("newLineChar", "\n");
%>

<c:set var="title" value="Daftar URLs (Admin)" scope="request" />
<jsp:include page="../common/header.jsp" />

<h1>Daftar URL</h1>

<h2>Calendar</h2>
<table class="w3-table-all">
	<tr>
		<td><a href="/calendargen/info">/calendargen/info</a></td>
		<td>Info Generate Kalender</td>
	</tr>
	<tr>
		<td><a href="/calendargen">/calendargen</a></td>
		<td>Generate Kalender</td>
	</tr>
	<tr>
		<td><a href="/admin/tampilPertanyaan">/admin/tampilPertanyaan</a></td>
		<td>Tampilkan Pertanyaan (Admin)</td>
	</tr>
</table>

<h2>Reminder Persekutuan</h2>
<table class="w3-table-all">
	<tr>
		<td><a href="/reminder">/reminder</a></td>
		<td>Home untuk Reminder Persekutuan</td>
	</tr>
	<tr>
		<td><a href="/reminder/daftar">/reminder/daftar</a></td>
		<td>Mendaftar Reminder Persekutuan</td>
	</tr>
	<tr>
		<td><a href="/reminder/ubah">/reminder/ubah</a></td>
		<td>Mengubah Data Reminder Persekutuan</td>
	</tr>
	<tr>
		<td><a href="/reminder/aktivasi">/reminder/aktivasi</a></td>
		<td>Aktivasi Reminder Persekutuan (Parameter "email" dan "key")</td>
	</tr>
</table>

<h2>Pertanyaan</h2>
<table class="w3-table-all">
	<tr>
		<td><a href="/tampilPertanyaan">/tampilPertanyaan</a></td>
		<td>Tampilkan Pertanyaan</td>
	</tr>
	<tr>
		<td><a href="/kirimPertanyaan">/kirimPertanyaan</a></td>
		<td>Kirimkan Pertanyaan</td>
	</tr>
	<tr>
		<td><a href="/admin/tampilPertanyaan">/admin/tampilPertanyaan</a></td>
		<td>Tampilkan Pertanyaan (Admin)</td>
	</tr>
</table>

<h2>Feedback</h2>
<table class="w3-table-all">
	<tr>
		<td><a href="/feedback">/feedback</a></td>
		<td>Kirim feedback</td>
	</tr>
</table>

<h2>Cron Jobs</h2>
<table class="w3-table-all">
	<tr>
		<td><a href="/tasks/calendargen">/tasks/calendargen</a></td>
		<td>Generate Kalender untuk Blog</td>
	</tr>
	<tr>
		<td><a href="/tasks/remindpelayanan">/tasks/remindpelayanan</a></td>
		<td>Reminder Pelayanan</td>
	</tr>
	<tr>
		<td><a href="/tasks/remindpelayanan/ivena">/tasks/remindpelayanan/ivena</a></td>
		<td>Reminder Pelayanan untuk persekutuan Ivena</td>
	</tr>
	<tr>
		<td><a href="/tasks/remindpersekutuan">/tasks/remindpersekutuan</a></td>
		<td>Reminder Acara Persekutuan</td>
	</tr>
	<tr>
		<td><a href="/tasks/removeexpiredreminder">/tasks/removeexpiredreminder</a></td>
		<td>Hapus Reminder Kadaluarsa</td>
	</tr>

</table>
<jsp:include page="../common/footer.jsp" />
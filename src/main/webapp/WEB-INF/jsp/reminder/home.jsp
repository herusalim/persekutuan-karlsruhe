<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="title" value="Info Reminder Persekutuan" scope="request" />
<jsp:include page="../common/header.jsp"/>

<jsp:include page="navi.jsp"/>

<h3>Info Pendaftaran Reminder Persekutuan</h3>
<article>
Di sini, kamu bisa memilih kapan kamu mau menerima jadwal persekutuan, misalnya H-1, atau H-2, dst.<br/>
Untuk menjaga privasi agar email tidak didaftarkan oleh orang yang bukan pemilik email tersebut, ada 2 cara untuk mendaftar, bergantung pada email yang kamu pakai untuk mendaftar:
<br/>
<ol>
	<li>Email Google atau email lain yang terdaftar di akun Google</li>
	<p>Klik link "Login" yang ada di halaman daftar/ubah untuk mengaktifkan akun Google (jika belum). Keuntungannya adalah, tidak diperlukan validasi via Email untuk mendaftar atau mengubah pendaftaran.</p>
	<li>Email bukan Google yang belum terdaftar di akun Google</li>
	<ul>
		<li>Agar email tidak didaftarkan oleh orang yang tidak berhak, kamu akan menerima email konfirmasi untuk mengkonfirmasi kalau kamu adalah pemilik email tersebut. Ini berlaku baik untuk mengaktifkan saat mendaftar ataupun untuk mengubah reminder yang telah didaftarkan.</li>
		<li>Jika kamu tidak menerima email konfirmasi untuk mengaktifkan setelah pendaftaran, kamu bisa meminta email aktivasi untuk dikirimkan sekali lagi dengan mencoba mendaftarkan email tersebut sekali lagi.</li>
	</ul>
</ol>
</article>
<p/>
<h3>Penjelasan Menu:</h3>
<ul>
	<li><strong>Info: </strong>halaman ini</li>
	<li><strong>Daftar: </strong>Untuk mendaftarkan email kamu untuk menerima reminder. Hanya bisa dilakukan satu kali per email</li>
	<li><strong>Ubah: </strong>Untuk <strong>mengubah</strong> daftar reminder yang kamu inginkan, dan untuk <strong>menghapus</strong> daftar reminder kamu</li>
</ul>

<jsp:include page="../common/footer.jsp"/>
<html>
<head><title>Info tentang Generate Kalender</title></head>
<body>

<h1>Info</h1>

<h2>Feature yang ada</h2>
<ol>
<li><h3>Men-generate kalender dari data yang tersedia di google sheets <a target="_blank" href="https://docs.google.com/spreadsheets/d/19YlSZJtEmiqaMMbxLHkrCM8PMsODlHyuZzYCmOeduBo/edit">Jadwal Persekutuan</a>.</h3></li>
	<ul>
		<li><a target="_blank" href="/calendargen">/calendargen</a>: Menampilkan pilihan jadwal yang ada untuk dipilih. Options:</li>
		<ul>
			<li><a target="_blank" href="/calendargen?scope=all">/calendargen?scope=all</a>: Otomatis akan memproses semua jadwal yang ada yang belum memiliki kalender event</li>
			<li><a target="_blank" href="/calendargen?scope=all&overwrite=true">/calendargen?scope=all&amp;overwrite=true</a>: Otomatis akan memproses semua jadwal yang ada. Jika kalender event bersangkutan sudah ada, maka akan diperbaharui (overwritten/updated)</li>
		</ul>
		<li><a target="_blank" href="/tasks/calendargen">/tasks/calendargen</a>: URL yang digunakan oleh Cron job untuk men-generate kalender secara rutin</a>.</li>
		<ul>
			<li>Jadwal yang akan digenerate: Jadwal yang belum ada kalender event nya di Google calendar</li>
			<li>Tidak overwrite. Semua jadwal yang sudah ada kalender event nya akan diabaikan</li>
		</ul>
	</ul>
<li><h3>Mengirimkan reminder pelayanan.</h3></li>
	Reminder akan dikirimkan 7 hari dan 2 hari sebelum acara persekutuan bersangkutan. Sumber data:
	<ul>
		<li><a target="_blank" href="https://docs.google.com/spreadsheets/d/19YlSZJtEmiqaMMbxLHkrCM8PMsODlHyuZzYCmOeduBo/edit">Jadwal Persekutuan</a></li>
		<li><a target="_blank" href="https://docs.google.com/spreadsheets/d/1-sKoLqR3MRd8-dtkAiaOe5fy5FMOsU4zjWoA7kc7LMA/edit">Daftar Email</a></li>
	</ul>
	Cara penggunaan:
	<ul>
		<li><a target="_blank" href="/tasks/remindpelayanan">/tasks/remindpelayanan</a>: Mengirimkan email reminder kepada semua petugas bersangkutan yang belum mendapatkan email reminder. Cron job diatur untuk menjalankan setiap hari</li>
		<li><a target="_blank" href="/tasks/removeexpiredreminder">/tasks/removeexpiredreminder</a>: Menghapuskan register email terkirim yang sudah lalu. Cron job diatur untuk berjalan setiap minggu (hari minggu)</li>
	</ul>
</ol>

</body>
</html>
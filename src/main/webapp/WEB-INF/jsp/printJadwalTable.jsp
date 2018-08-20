<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

	<table border="1" cellpadding="10">
		<tr>
			<c:if test="${printCheckBoxColumn eq true}"><th></th></c:if>
			<th>Tanggal</th>
			<th>Pemimpin Renungan</th>
			<th>MC</th>
			<th>Musik</th>
			<th>Materi Renungan</th>
			<th>Sekolah Minggu</th>
			<th>Masak</th>
			<th>Lokasi</th>
		</tr>
		<c:forEach var="jadwal" items="${daftarJadwal}">
		<tr>
		<fmt:timeZone value="${timeZone}">
			<c:if test="${printCheckBoxColumn eq true}">
				
				<c:set var="jadwalSudahAda" value="false" />
				<c:forEach var="item" items="${existingJadwal}">
				  <c:if test="${item eq jadwal}">
				    <c:set var="jadwalSudahAda" value="true" />
				  </c:if>
				</c:forEach>
				<td align="center"><input type="checkbox" name="jadwal" value="<fmt:formatDate pattern="yyyyMMdd" value="${jadwal.tanggal}" />" <c:if test="${not jadwalSudahAda}">checked</c:if> /><c:if test="${jadwalSudahAda}">[Sudah Ada]</c:if></td>
			</c:if>
			<td><fmt:formatDate pattern="dd.MM.yyyy" value="${jadwal.tanggal}" /></td>
			<td><c:set var="daftarOrang" value="${jadwal.pemimpinRenungan}" scope="request" /><jsp:include page="printDaftarOrang.jsp" /></td>
			<td><c:set var="daftarOrang" value="${jadwal.mc}" scope="request" /><jsp:include page="printDaftarOrang.jsp" /></td>
			<td><c:set var="daftarOrang" value="${jadwal.musik}" scope="request" /><jsp:include page="printDaftarOrang.jsp" /></td>
			<td><c:out value="${jadwal.bahanRenungan}" /></td>
			<td><c:set var="daftarOrang" value="${jadwal.sekolahMinggu}" scope="request" /><jsp:include page="printDaftarOrang.jsp" /></td>
			<td><c:set var="daftarOrang" value="${jadwal.masak}" scope="request" /><jsp:include page="printDaftarOrang.jsp" /></td>
			<td><c:out value="${jadwal.lokasi}" /></td>
		</fmt:timeZone>
		</tr>
		</c:forEach>
	</table>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
<title>Generate Jadwal Persekutuan</title>
<script>
function selectSemuaJadwal() {
  checkboxes = document.getElementsByName('jadwal');
  for(var i=0; i<checkboxes.length; i++) {
    checkboxes[i].checked = "checked";
  }
}

function unselectSemuaJadwal() {
  checkboxes = document.getElementsByName('jadwal');
  for(var i=0; i<checkboxes.length; i++) {
    checkboxes[i].checked = "";
  }
}
</script>
</head>
<body>	

<form action="<c:out value="${pageContext.request.contextPath}"/>" method="POST">
	<c:set var="printCheckBoxColumn" value="true" scope="request" />
	<jsp:include page="printJadwalTable.jsp" />
	<input type="submit" value="Generate" <c:if test="${empty daftarJadwal}">disabled</c:if>/>
	<button type="button" onclick="unselectSemuaJadwal()" <c:if test="${empty daftarJadwal}">disabled</c:if>>Hapus Pilihan</button>
	<button type="button" onclick="selectSemuaJadwal()" <c:if test="${empty daftarJadwal}">disabled</c:if>>Pilih Semua</button>
</form>

</body>
</html>
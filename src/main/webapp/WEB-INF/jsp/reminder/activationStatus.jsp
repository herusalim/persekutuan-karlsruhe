<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="title" value="Aktivasi Reminder" scope="request" />
<jsp:include page="../common/header.jsp"/>

<jsp:include page="navi.jsp"/>

<h1>Aktivasi Reminder untuk email <c:out value="${param['email']}" /></h1>
<jsp:include page="../common/footer.jsp"/>
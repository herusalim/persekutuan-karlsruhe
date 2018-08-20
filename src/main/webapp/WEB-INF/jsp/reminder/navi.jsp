<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="barColor" value="w3-light-blue" />
<c:set var="selectedColor" value="w3-blue" />
<c:set var="currentPath" value="${requestScope['javax.servlet.forward.request_uri']}" />
<c:set var="homeHref" value="/reminder"/>
<c:set var="addHref" value="/reminder/daftar"/>
<c:set var="maintainHref" value="/reminder/ubah"/>

<c:if test="${currentPath eq homeHref}">
<c:set var="homeHref" value="#" />
<c:set var="homeHrefClass" value="${selectedColor}" />
</c:if>

<c:if test="${currentPath eq addHref}">
<c:set var="addHref" value="#" />
<c:set var="addHrefClass" value="${selectedColor}" />
</c:if>

<c:if test="${currentPath eq maintainHref}">
<c:set var="maintainHref" value="#" />
<c:set var="maintainHrefClass" value="${selectedColor}" />
</c:if>

<h2>Reminder Persekutuan</h2>
<ul class="w3-navbar w3-border ${barColor}">
<li><a class="${homeHrefClass}" href="${homeHref}">Info</a></li>
<li><a class="${addHrefClass}" href="${addHref}">Daftar</a></li>
<li><a class="${maintainHrefClass}" href="${maintainHref}">Ubah</a></li>
</ul>

<jsp:include page="../common/messageBar.jsp"/>
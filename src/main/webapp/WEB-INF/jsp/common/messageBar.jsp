<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:if test="${not empty errorMessage}"><div class="w3-container w3-red"><p>Pesan error: ${errorMessage}</p></div></c:if>
<c:if test="${not empty infoMessage}"><div class="w3-container w3-orange w3-text-white"><p><c:out value="${infoMessage}"/></p></div></c:if>
<c:if test="${not empty successMessage}"><div class="w3-container w3-green"><p><c:out value="${successMessage}"/></p></div></c:if>
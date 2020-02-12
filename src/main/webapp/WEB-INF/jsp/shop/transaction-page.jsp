<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%><!DOCTYPE html>
<div class="content">
	<h2>Transaction Page</h2>
	<p>Good ${timeGreeting}, ${loggedUser.displayName}. Have a great day!</p>
	<p>This is the page you perform transactions</p> 
	<div class="row">
		<c:forEach var="menu" items="${menus }">
			<div class="col-sm-3">
				<div class="card" style="width: 100%;">
					<img class="card-img-top" width="100" height="150"
						src="${host}/${contextPath}/${imagePath}/${menu.iconUrl }"
						alt="Card image cap">
					<div class="card-body" style="background-color:${menu.color }; color:${menu.fontColor }">
						<h5 class="card-title">
							 ${menu.name } 
						</h5>
						<a role="button" class="badge badge-primary"
							data-toggle="tooltip" data-placement="bottom"
							title="${menu.description }" href="${menu.url }">Detail</a>
					</div>
				</div>
			</div>
		</c:forEach>
	</div>
	<p></p>
</div>

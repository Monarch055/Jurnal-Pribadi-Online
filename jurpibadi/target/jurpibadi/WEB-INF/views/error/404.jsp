<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="../header.jsp">
    <jsp:param name="title" value="Page Not Found" />
</jsp:include>

<div class="text-center my-5">
    <h1 class="display-1">404</h1>
    <h2>Page Not Found</h2>
    <p class="lead">The page you are looking for does not exist.</p>
    <a href="${pageContext.request.contextPath}/journal" class="btn btn-primary">Go to Home</a>
</div>

<jsp:include page="../footer.jsp" /> 
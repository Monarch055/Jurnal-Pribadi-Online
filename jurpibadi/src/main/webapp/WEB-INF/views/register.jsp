<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="header.jsp">
    <jsp:param name="title" value="Register" />
</jsp:include>

<div class="row justify-content-center">
    <div class="col-md-6">
        <div class="card">
            <div class="card-header">
                <h2 class="text-center">Register</h2>
            </div>
            <div class="card-body">
                <form action="${pageContext.request.contextPath}/register" method="post">
                    <div class="mb-3">
                        <label for="username" class="form-label">Username</label>
                        <input type="text" class="form-control ${not empty usernameError ? 'is-invalid' : ''}" 
                               id="username" name="username" value="${username}" required>
                        <c:if test="${not empty usernameError}">
                            <div class="invalid-feedback">${usernameError}</div>
                        </c:if>
                    </div>
                    <div class="mb-3">
                        <label for="email" class="form-label">Email</label>
                        <input type="email" class="form-control ${not empty emailError ? 'is-invalid' : ''}" 
                               id="email" name="email" value="${email}" required>
                        <c:if test="${not empty emailError}">
                            <div class="invalid-feedback">${emailError}</div>
                        </c:if>
                    </div>
                    <div class="mb-3">
                        <label for="password" class="form-label">Password</label>
                        <input type="password" class="form-control ${not empty passwordError ? 'is-invalid' : ''}" 
                               id="password" name="password" required>
                        <c:if test="${not empty passwordError}">
                            <div class="invalid-feedback">${passwordError}</div>
                        </c:if>
                        <div class="form-text">Password must be at least 6 characters long.</div>
                    </div>
                    <div class="mb-3">
                        <label for="confirmPassword" class="form-label">Confirm Password</label>
                        <input type="password" class="form-control ${not empty confirmPasswordError ? 'is-invalid' : ''}" 
                               id="confirmPassword" name="confirmPassword" required>
                        <c:if test="${not empty confirmPasswordError}">
                            <div class="invalid-feedback">${confirmPasswordError}</div>
                        </c:if>
                    </div>
                    <div class="d-grid gap-2">
                        <button type="submit" class="btn btn-primary">Register</button>
                    </div>
                </form>
            </div>
            <div class="card-footer text-center">
                <p>Already have an account? <a href="${pageContext.request.contextPath}/login">Login</a></p>
            </div>
        </div>
    </div>
</div>

<jsp:include page="footer.jsp" /> 
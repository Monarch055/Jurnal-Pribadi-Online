<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:include page="../header.jsp">
    <jsp:param name="title" value="View Entry" />
</jsp:include>

<div class="d-flex justify-content-between align-items-center mb-4">
    <h1>Journal Entry</h1>
    <div>
        <a href="${pageContext.request.contextPath}/journal" class="btn btn-secondary">Back to List</a>
        <a href="${pageContext.request.contextPath}/journal/edit/${entry.entryId}" class="btn btn-primary">Edit</a>
        
        <form method="post" action="${pageContext.request.contextPath}/journal/delete/${entry.entryId}" style="display:inline;">
            <button type="submit" class="btn btn-danger" 
                onclick="return confirm('Are you sure you want to delete this entry?')">
                Delete
            </button>
        </form>
    </div>
</div>

<div class="card">
    <div class="card-header d-flex justify-content-between align-items-center">
        <h3><c:out value="${entry.entryDate}" /></h3>
        <c:if test="${not empty entry.mood}">
            <span class="badge bg-info fs-6">${entry.mood}</span>
        </c:if>
    </div>
    <div class="card-body">
        <p style="white-space: pre-wrap;"><c:out value="${entry.content}" /></p>
    </div>
    <div class="card-footer text-muted">
        <div class="d-flex justify-content-between">
            <span>Created: 
                <c:out value="${entry.createdAt}" />
            </span>
            <c:if test="${not empty entry.updatedAt}">
                <span>Last Updated: 
                    <c:out value="${entry.updatedAt}" />
                </span>
            </c:if>
        </div>
    </div>
</div>

<jsp:include page="../footer.jsp" /> 
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:include page="../header.jsp">
    <jsp:param name="title" value="My Journal Entries" />
</jsp:include>

<div class="d-flex justify-content-between align-items-center mb-4">
    <h1>My Journal Entries</h1>
    <a href="${pageContext.request.contextPath}/journal/new" class="btn btn-primary">
        <i class="bi bi-plus-circle"></i> New Entry
    </a>
</div>

<div class="card mb-4">
    <div class="card-header">
        <h5>Search Entries</h5>
    </div>
    <div class="card-body">
        <form action="${pageContext.request.contextPath}/journal/search" method="get" class="row g-3">
            <div class="col-md-6">
                <label for="keyword" class="form-label">Search by keyword</label>
                <input type="text" class="form-control" id="keyword" name="keyword" value="${keyword}">
            </div>
            <div class="col-md-3">
                <label for="startDate" class="form-label">Start Date</label>
                <input type="date" class="form-control" id="startDate" name="startDate" value="${startDate}">
            </div>
            <div class="col-md-3">
                <label for="endDate" class="form-label">End Date</label>
                <input type="date" class="form-control" id="endDate" name="endDate" value="${endDate}">
            </div>
            <div class="col-12">
                <button type="submit" class="btn btn-primary">Search</button>
                <a href="${pageContext.request.contextPath}/journal" class="btn btn-secondary">Reset</a>
            </div>
        </form>
    </div>
</div>

<c:choose>
    <c:when test="${empty entries}">
        <div class="alert alert-info">
            <p>No journal entries found. <a href="${pageContext.request.contextPath}/journal/new">Create your first entry</a>.</p>
        </div>
    </c:when>
    <c:otherwise>
        <div class="row row-cols-1 row-cols-md-2 row-cols-lg-3 g-4">
            <c:forEach var="entry" items="${entries}">
                <div class="col">
                    <div class="card h-100">
                        <div class="card-header d-flex justify-content-between align-items-center">
                            <span><c:out value="${entry.entryDate}" /></span>
                            <c:if test="${not empty entry.mood}">
                                <span class="badge bg-info">${entry.mood}</span>
                            </c:if>
                        </div>
                        <div class="card-body">
                            <p class="card-text"><c:out value="${entry.content.length() > 150 ? entry.content.substring(0, 150).concat('...') : entry.content}" /></p>
                        </div>
                        <div class="card-footer d-flex justify-content-between">
                            <a href="${pageContext.request.contextPath}/journal/view/${entry.entryId}" class="btn btn-sm btn-primary">View</a>
                            <div>
                                <a href="${pageContext.request.contextPath}/journal/edit/${entry.entryId}" class="btn btn-sm btn-secondary">Edit</a>
                                <form method="post" action="${pageContext.request.contextPath}/journal/delete/${entry.entryId}" style="display:inline;">
                                    <button type="submit" class="btn btn-sm btn-danger" 
                                            onclick="return confirm('Are you sure you want to delete this entry?')">
                                        Delete
                                    </button>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>
    </c:otherwise>
</c:choose>

<jsp:include page="../footer.jsp" /> 
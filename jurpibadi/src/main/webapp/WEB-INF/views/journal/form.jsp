<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:include page="../header.jsp">
    <jsp:param name="title" value="${empty entry.entryId or entry.entryId == 0 ? 'New Entry' : 'Edit Entry'}" />
</jsp:include>

<div class="card">
    <div class="card-header">
        <h2>${empty entry.entryId or entry.entryId == 0 ? 'Create New Entry' : 'Edit Entry'}</h2>
    </div>
    <div class="card-body">
        <form action="${pageContext.request.contextPath}/journal/${empty entry.entryId or entry.entryId == 0 ? 'new' : 'edit/'.concat(entry.entryId)}" method="post">
            <div class="mb-3">
                <label for="entryDate" class="form-label">Date</label>
                <input type="date" class="form-control ${not empty dateError ? 'is-invalid' : ''}" 
                       id="entryDate" name="entryDate" 
                       value="${not empty entryDate ? entryDate : (not empty entry.entryDate ? entry.entryDate : today)}" required>
                <c:if test="${not empty dateError}">
                    <div class="invalid-feedback">${dateError}</div>
                </c:if>
            </div>
            <div class="mb-3">
                <label for="mood" class="form-label">Mood (Optional)</label>
                <select class="form-select" id="mood" name="mood">
                    <option value="" ${empty entry.mood ? 'selected' : ''}>Select Mood</option>
                    <option value="Happy" ${entry.mood == 'Happy' ? 'selected' : ''}>Happy</option>
                    <option value="Sad" ${entry.mood == 'Sad' ? 'selected' : ''}>Sad</option>
                    <option value="Angry" ${entry.mood == 'Angry' ? 'selected' : ''}>Angry</option>
                    <option value="Excited" ${entry.mood == 'Excited' ? 'selected' : ''}>Excited</option>
                    <option value="Calm" ${entry.mood == 'Calm' ? 'selected' : ''}>Calm</option>
                    <option value="Anxious" ${entry.mood == 'Anxious' ? 'selected' : ''}>Anxious</option>
                    <option value="Tired" ${entry.mood == 'Tired' ? 'selected' : ''}>Tired</option>
                </select>
            </div>
            <div class="mb-3">
                <label for="content" class="form-label">Content</label>
                <textarea class="form-control ${not empty contentError ? 'is-invalid' : ''}" 
                          id="content" name="content" rows="10" required><c:out value="${not empty content ? content : entry.content}" /></textarea>
                <c:if test="${not empty contentError}">
                    <div class="invalid-feedback">${contentError}</div>
                </c:if>
            </div>
            <div class="d-flex justify-content-between">
                <a href="${pageContext.request.contextPath}/journal" class="btn btn-secondary">Cancel</a>
                <button type="submit" class="btn btn-primary">Save</button>
            </div>
        </form>
    </div>
</div>

<jsp:include page="../footer.jsp" /> 
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%--
    users.jsp - Main listing page.
    Displays all users (or search results) in a Bootstrap table.
    "userList" attribute is set by UserServlet (action=list or action=search).
--%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>All Users - User Management System</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="css/style.css">
</head>
<body>

<nav class="navbar navbar-dark navbar-custom">
    <div class="container">
        <a class="navbar-brand mb-0 h1" href="index.jsp">🧑‍💼 User Management System</a>
    </div>
</nav>

<div class="container mt-5">

    <!-- Success / Error banners -->
    <c:if test="${not empty successMessage}">
        <div class="alert alert-success alert-dismissible fade show" role="alert">
                ${successMessage}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>
    <c:if test="${not empty errorMessage}">
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
                ${errorMessage}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>

    <div class="card shadow-sm">
        <div class="card-header bg-primary text-white d-flex justify-content-between align-items-center flex-wrap gap-2">
            <h4 class="mb-0">👥 All Users</h4>
            <a href="UserServlet?action=add" class="btn btn-light btn-sm">➕ Add New User</a>
        </div>

        <div class="card-body">

            <!-- Search bar -->
            <form action="UserServlet" method="get" class="row g-2 mb-4">
                <input type="hidden" name="action" value="search">
                <div class="col-sm-8">
                    <input type="text" class="form-control" name="keyword"
                           placeholder="Search by name or email..."
                           value="${searchKeyword}">
                </div>
                <div class="col-sm-2">
                    <button type="submit" class="btn btn-primary w-100">🔍 Search</button>
                </div>
                <div class="col-sm-2">
                    <a href="UserServlet?action=list" class="btn btn-outline-secondary w-100">Reset</a>
                </div>
            </form>

            <c:if test="${not empty searchKeyword}">
                <p class="text-muted">Showing results for: <strong>${searchKeyword}</strong></p>
            </c:if>

            <div class="table-responsive">
                <table class="table table-hover align-middle">
                    <thead class="table-light">
                        <tr>
                            <th>#</th>
                            <th>Name</th>
                            <th>Email</th>
                            <th>Phone</th>
                            <th>Address</th>
                            <th>Created At</th>
                            <th class="text-center">Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${empty userList}">
                                <tr>
                                    <td colspan="7" class="text-center text-muted py-4">No users found.</td>
                                </tr>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="u" items="${userList}">
                                    <tr>
                                        <td>${u.id}</td>
                                        <td>${u.name}</td>
                                        <td>${u.email}</td>
                                        <td>${u.phone}</td>
                                        <td>${u.address}</td>
                                        <td><fmt:formatDate value="${u.createdAt}" pattern="dd-MMM-yyyy HH:mm"/></td>
                                        <td class="text-center">
                                            <a href="UserServlet?action=edit&id=${u.id}"
                                               class="btn btn-sm btn-outline-primary">Edit</a>
                                            <a href="UserServlet?action=delete&id=${u.id}"
                                               class="btn btn-sm btn-outline-danger"
                                               onclick="return confirm('Delete user \'${u.name}\'? This cannot be undone.');">
                                                Delete
                                            </a>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </tbody>
                </table>
            </div>

        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>

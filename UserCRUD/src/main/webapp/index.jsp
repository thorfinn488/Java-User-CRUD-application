<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--
    index.jsp - Dashboard / Landing page
    Provides navigation buttons to the main features of the app.
--%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>User Management System - Dashboard</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="css/style.css">
</head>
<body>

<nav class="navbar navbar-dark navbar-custom">
    <div class="container">
        <span class="navbar-brand mb-0 h1">🧑‍💼 User Management System</span>
    </div>
</nav>

<div class="container mt-5">
    <div class="text-center mb-5">
        <h1 class="fw-bold">Welcome to the Dashboard</h1>
        <p class="text-muted">Manage application users - add, view, update, delete, and search.</p>
    </div>

    <div class="row g-4 justify-content-center">

        <div class="col-md-4">
            <div class="card shadow-sm h-100 dashboard-card">
                <div class="card-body text-center">
                    <div class="dashboard-icon">👥</div>
                    <h5 class="card-title mt-3">View All Users</h5>
                    <p class="card-text text-muted">Browse, search, edit and delete existing users.</p>
                    <a href="UserServlet?action=list" class="btn btn-primary">View Users</a>
                </div>
            </div>
        </div>

        <div class="col-md-4">
            <div class="card shadow-sm h-100 dashboard-card">
                <div class="card-body text-center">
                    <div class="dashboard-icon">➕</div>
                    <h5 class="card-title mt-3">Add New User</h5>
                    <p class="card-text text-muted">Register a new user into the system.</p>
                    <a href="UserServlet?action=add" class="btn btn-success">Add User</a>
                </div>
            </div>
        </div>

        <div class="col-md-4">
            <div class="card shadow-sm h-100 dashboard-card">
                <div class="card-body text-center">
                    <div class="dashboard-icon">🔍</div>
                    <h5 class="card-title mt-3">Search Users</h5>
                    <p class="card-text text-muted">Quickly find a user by name or email address.</p>
                    <a href="UserServlet?action=list" class="btn btn-outline-secondary">Go to Search</a>
                </div>
            </div>
        </div>

    </div>
</div>

<footer class="text-center text-muted mt-5 mb-3">
    <small>&copy; 2026 User Management System &mdash; Built with Servlets, JSP &amp; JDBC</small>
</footer>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>

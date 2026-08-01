<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%--
    add-user.jsp - Registration form for creating a new user.
    Submits to UserServlet with action=insert (POST).
    On validation failure, the servlet forwards back here with
    "errorMessage" and the previously entered "user" so the form
    can be re-populated instead of forcing the user to retype everything.
--%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Add User - User Management System</title>
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
    <div class="row justify-content-center">
        <div class="col-md-7">

            <div class="card shadow-sm">
                <div class="card-header bg-success text-white">
                    <h4 class="mb-0">➕ Add New User</h4>
                </div>
                <div class="card-body">

                    <!-- Error message banner -->
                    <c:if test="${not empty errorMessage}">
                        <div class="alert alert-danger" role="alert">
                                ${errorMessage}
                        </div>
                    </c:if>

                    <form action="UserServlet" method="post" novalidate>
                        <input type="hidden" name="action" value="insert">

                        <div class="mb-3">
                            <label for="name" class="form-label">Full Name <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" id="name" name="name" required
                                   value="${user.name}" placeholder="e.g. John Doe">
                        </div>

                        <div class="mb-3">
                            <label for="email" class="form-label">Email Address <span class="text-danger">*</span></label>
                            <input type="email" class="form-control" id="email" name="email" required
                                   value="${user.email}" placeholder="e.g. john@example.com">
                        </div>

                        <div class="mb-3">
                            <label for="password" class="form-label">Password <span class="text-danger">*</span></label>
                            <input type="password" class="form-control" id="password" name="password" required
                                   minlength="6" placeholder="At least 6 characters">
                        </div>

                        <div class="mb-3">
                            <label for="phone" class="form-label">Phone Number</label>
                            <input type="text" class="form-control" id="phone" name="phone"
                                   value="${user.phone}" pattern="\d{7,15}" placeholder="Digits only, e.g. 9876543210">
                        </div>

                        <div class="mb-3">
                            <label for="address" class="form-label">Address</label>
                            <textarea class="form-control" id="address" name="address" rows="3"
                                      placeholder="Street, City, State">${user.address}</textarea>
                        </div>

                        <div class="d-flex justify-content-between">
                            <a href="UserServlet?action=list" class="btn btn-outline-secondary">Cancel</a>
                            <button type="submit" class="btn btn-success">Save User</button>
                        </div>
                    </form>

                </div>
            </div>

        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%--
    edit-user.jsp - Pre-filled form for updating an existing user.
    The "user" attribute is set by UserServlet (action=edit) before forwarding here.
    Submits to UserServlet with action=update (POST).
    Password field is left BLANK on purpose - leaving it blank means
    "keep the current password" (handled in UserServlet).
--%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Edit User - User Management System</title>
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
                <div class="card-header bg-primary text-white">
                    <h4 class="mb-0">✏️ Edit User</h4>
                </div>
                <div class="card-body">

                    <c:if test="${not empty errorMessage}">
                        <div class="alert alert-danger" role="alert" data-auto-dismiss>
                                ${errorMessage}
                        </div>
                    </c:if>

                    <form action="UserServlet" method="post" novalidate data-validate>
                        <input type="hidden" name="action" value="update">
                        <input type="hidden" name="id" value="${user.id}">

                        <div class="mb-3">
                            <label for="name" class="form-label">Full Name <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" id="name" name="name" required
                                   value="${user.name}">
                            <div class="invalid-feedback" id="name-feedback">Please enter a name.</div>
                        </div>

                        <div class="mb-3">
                            <label for="email" class="form-label">Email Address <span class="text-danger">*</span></label>
                            <input type="email" class="form-control" id="email" name="email" required
                                   value="${user.email}">
                            <div class="invalid-feedback" id="email-feedback">Please enter a valid email address.</div>
                        </div>

                        <div class="mb-3">
                            <label for="password" class="form-label">Password</label>
                            <input type="password" class="form-control" id="password" name="password"
                                   minlength="6" placeholder="Leave blank to keep current password" data-optional="true">
                            <div class="form-text">Only fill this in if you want to change the password.</div>
                            <div class="invalid-feedback" id="password-feedback">Password must be at least 6 characters.</div>
                        </div>

                        <div class="mb-3">
                            <label for="phone" class="form-label">Phone Number</label>
                            <input type="text" class="form-control" id="phone" name="phone"
                                   value="${user.phone}" pattern="\d{7,15}">
                            <div class="invalid-feedback" id="phone-feedback">Digits only, 7-15 numbers.</div>
                        </div>

                        <div class="mb-3">
                            <label for="address" class="form-label">Address</label>
                            <textarea class="form-control" id="address" name="address" rows="3">${user.address}</textarea>
                        </div>

                        <div class="d-flex justify-content-between">
                            <a href="UserServlet?action=list" class="btn btn-outline-secondary">Cancel</a>
                            <button type="submit" class="btn btn-primary">Update User</button>
                        </div>
                    </form>

                </div>
            </div>

        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="js/app.js"></script>
</body>
</html>

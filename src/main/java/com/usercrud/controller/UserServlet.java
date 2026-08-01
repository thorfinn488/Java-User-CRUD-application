package com.usercrud.controller;

import com.usercrud.dao.UserDAO;
import com.usercrud.model.User;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;

/**
 * UserServlet (Controller)
 *
 * This is the single entry point for all user-related HTTP requests.
 * It follows the "front controller" style using an `action` request
 * parameter to decide what to do, then delegates data work to UserDAO
 * and finally forwards to the correct JSP page (View).
 *
 * Supported actions:
 *   ?action=list    -> show all users              (GET)
 *   ?action=add     -> show the "add user" form     (GET)
 *   ?action=insert  -> save a new user               (POST)
 *   ?action=edit    -> show the "edit user" form     (GET)
 *   ?action=update  -> save changes to a user         (POST)
 *   ?action=delete  -> delete a user                  (GET)
 *   ?action=search  -> search users by name/email      (GET)
 */
@WebServlet("/UserServlet")
public class UserServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    // Simple, standard email format check.
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$");

    // Phone must be digits only, 7 to 15 characters (allows optional blank phone).
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{7,15}$");

    /**
     * Handles all GET requests: list, add (show form), edit (show form),
     * delete, search.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        if (action == null) {
            action = "list"; // default action when no parameter is supplied
        }

        try {
            switch (action) {
                case "add":
                    showAddForm(request, response);
                    break;
                case "edit":
                    showEditForm(request, response);
                    break;
                case "delete":
                    deleteUser(request, response);
                    break;
                case "search":
                    searchUsers(request, response);
                    break;
                case "list":
                default:
                    listUsers(request, response);
                    break;
            }
        } catch (SQLException e) {
            throw new ServletException("Database error while processing action=" + action, e);
        }
    }

    /**
     * Handles all POST requests: insert (create) and update.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        if (action == null) {
            action = "";
        }

        try {
            switch (action) {
                case "insert":
                    insertUser(request, response);
                    break;
                case "update":
                    updateUser(request, response);
                    break;
                default:
                    listUsers(request, response);
                    break;
            }
        } catch (SQLException e) {
            throw new ServletException("Database error while processing action=" + action, e);
        }
    }

    // =====================================================================
    // Action handler methods
    // =====================================================================

    /** action=list : fetches all users and forwards to users.jsp */
    private void listUsers(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {

        List<User> userList = userDAO.getAllUsers();
        request.setAttribute("userList", userList);
        forward(request, response, "users.jsp");
    }

    /** action=add : simply displays the blank add-user form */
    private void showAddForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        forward(request, response, "add-user.jsp");
    }

    /** action=edit : loads the user by id and displays the pre-filled edit form */
    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {

        int id = parseIntSafe(request.getParameter("id"));
        User user = userDAO.getUserById(id);

        if (user == null) {
            request.setAttribute("errorMessage", "User not found.");
            listUsers(request, response);
            return;
        }

        request.setAttribute("user", user);
        forward(request, response, "edit-user.jsp");
    }

    /** action=insert : validates form data and inserts a new user */
    private void insertUser(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {

        // Read form fields
        String name = trim(request.getParameter("name"));
        String email = trim(request.getParameter("email"));
        String password = trim(request.getParameter("password"));
        String phone = trim(request.getParameter("phone"));
        String address = trim(request.getParameter("address"));

        User formUser = new User(name, email, password, phone, address);

        // Validate
        String validationError = validateUser(name, email, password, phone, true);
        if (validationError == null && userDAO.isEmailTaken(email, 0)) {
            validationError = "This email is already registered.";
        }

        if (validationError != null) {
            request.setAttribute("errorMessage", validationError);
            request.setAttribute("user", formUser); // re-populate the form
            forward(request, response, "add-user.jsp");
            return;
        }

        boolean success = userDAO.insertUser(formUser);

        request.setAttribute(success ? "successMessage" : "errorMessage",
                success ? "User added successfully!" : "Failed to add user. Please try again.");

        listUsers(request, response);
    }

    /** action=update : validates form data and updates an existing user */
    private void updateUser(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {

        int id = parseIntSafe(request.getParameter("id"));
        String name = trim(request.getParameter("name"));
        String email = trim(request.getParameter("email"));
        String password = trim(request.getParameter("password")); // may be blank = keep old password
        String phone = trim(request.getParameter("phone"));
        String address = trim(request.getParameter("address"));

        // When updating, password is optional (blank means "don't change it")
        boolean passwordRequired = false;
        String validationError = validateUser(name, email, password, phone, passwordRequired);

        if (validationError == null && userDAO.isEmailTaken(email, id)) {
            validationError = "This email is already registered to another user.";
        }

        User formUser = new User(id, name, email, password, phone, address, null);

        if (validationError != null) {
            request.setAttribute("errorMessage", validationError);
            request.setAttribute("user", formUser);
            forward(request, response, "edit-user.jsp");
            return;
        }

        // If password left blank, keep the existing password from the database
        if (password.isEmpty()) {
            User existing = userDAO.getUserById(id);
            if (existing != null) {
                formUser.setPassword(existing.getPassword());
            }
        }

        boolean success = userDAO.updateUser(formUser);

        request.setAttribute(success ? "successMessage" : "errorMessage",
                success ? "User updated successfully!" : "Failed to update user. Please try again.");

        listUsers(request, response);
    }

    /** action=delete : deletes the user with the given id */
    private void deleteUser(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {

        int id = parseIntSafe(request.getParameter("id"));
        boolean success = userDAO.deleteUser(id);

        request.setAttribute(success ? "successMessage" : "errorMessage",
                success ? "User deleted successfully!" : "Failed to delete user.");

        listUsers(request, response);
    }

    /** action=search : searches by name or email keyword */
    private void searchUsers(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {

        String keyword = trim(request.getParameter("keyword"));

        List<User> results = keyword.isEmpty()
                ? userDAO.getAllUsers()
                : userDAO.searchUsers(keyword);

        request.setAttribute("userList", results);
        request.setAttribute("searchKeyword", keyword);
        forward(request, response, "users.jsp");
    }

    // =====================================================================
    // Helper methods
    // =====================================================================

    /**
     * Validates the core user fields shared by add and edit forms.
     *
     * @param passwordRequired true for "add" (password mandatory),
     *                          false for "edit" (blank = keep existing password)
     * @return an error message describing the FIRST validation failure,
     *         or null if everything is valid
     */
    private String validateUser(String name, String email, String password,
                                 String phone, boolean passwordRequired) {

        if (name == null || name.isEmpty()) {
            return "Name is required.";
        }
        if (email == null || email.isEmpty()) {
            return "Email is required.";
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            return "Please enter a valid email address.";
        }
        if (passwordRequired && (password == null || password.length() < 6)) {
            return "Password must be at least 6 characters long.";
        }
        if (!passwordRequired && password != null && !password.isEmpty() && password.length() < 6) {
            return "Password must be at least 6 characters long.";
        }
        if (phone != null && !phone.isEmpty() && !PHONE_PATTERN.matcher(phone).matches()) {
            return "Phone number must contain only digits (7-15 digits).";
        }
        return null; // all checks passed
    }

    /** Forwards the request/response to a JSP page inside WEB-INF-protected... actually webapp root. */
    private void forward(HttpServletRequest request, HttpServletResponse response, String jspPage)
            throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher(jspPage);
        dispatcher.forward(request, response);
    }

    /** Safely parses an int, returning -1 if parsing fails (instead of throwing). */
    private int parseIntSafe(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException | NullPointerException e) {
            return -1;
        }
    }

    /** Null-safe trim helper. */
    private String trim(String value) {
        return value == null ? "" : value.trim();
    }
}

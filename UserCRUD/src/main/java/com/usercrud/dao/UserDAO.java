package com.usercrud.dao;

import com.usercrud.model.User;
import com.usercrud.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * UserDAO (Data Access Object)
 *
 * This class is the ONLY place in the application that talks
 * directly to the database via JDBC. It isolates all SQL code
 * from the Servlet (Controller) and JSP (View) layers, following
 * the DAO design pattern.
 *
 * Every method:
 *  - Uses PreparedStatement (prevents SQL Injection)
 *  - Uses try-with-resources to auto-close Connection/Statement/ResultSet
 *  - Throws SQLException upward so the Servlet can decide how to
 *    react (log it, show a friendly error message, etc.)
 */
public class UserDAO {

    /**
     * Inserts a new user record into the database.
     *
     * @param user the User object containing form data (id is ignored - auto generated)
     * @return true if exactly one row was inserted
     */
    public boolean insertUser(User user) throws SQLException {
        String sql = "INSERT INTO users (name, email, password, phone, address) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getPhone());
            ps.setString(5, user.getAddress());

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        }
        // Connection, PreparedStatement automatically closed here (try-with-resources)
    }

    /**
     * Retrieves every user in the database, ordered by most recently created first.
     *
     * @return a List of User objects (empty list if the table has no rows)
     */
    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY created_at DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                users.add(mapRowToUser(rs));
            }
        }
        return users;
    }

    /**
     * Retrieves a single user by their primary key.
     *
     * @param id the user's id
     * @return the matching User, or null if no user has that id
     */
    public User getUserById(int id) throws SQLException {
        String sql = "SELECT * FROM users WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToUser(rs);
                }
            }
        }
        return null; // no user found with this id
    }

    /**
     * Updates an existing user's details.
     * Note: password is only updated if a new (non-blank) one is supplied
     * by the calling layer - see UserServlet for that logic.
     *
     * @param user User object with updated fields; user.getId() identifies the row
     * @return true if exactly one row was updated
     */
    public boolean updateUser(User user) throws SQLException {
        String sql = "UPDATE users SET name = ?, email = ?, password = ?, phone = ?, address = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getPhone());
            ps.setString(5, user.getAddress());
            ps.setInt(6, user.getId());

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * Deletes a user by id.
     *
     * @param id the id of the user to delete
     * @return true if exactly one row was deleted
     */
    public boolean deleteUser(int id) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * Searches for users whose name OR email contains the given keyword
     * (case-insensitive, partial match).
     *
     * @param keyword text to search for
     * @return list of matching users (empty list if none match)
     */
    public List<User> searchUsers(String keyword) throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE name LIKE ? OR email LIKE ? ORDER BY created_at DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String likePattern = "%" + keyword + "%";
            ps.setString(1, likePattern);
            ps.setString(2, likePattern);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    users.add(mapRowToUser(rs));
                }
            }
        }
        return users;
    }

    /**
     * Checks whether an email address is already used by ANOTHER user.
     * Used to give a friendly "email already exists" validation message
     * instead of relying only on the database's UNIQUE constraint error.
     *
     * @param email        email to check
     * @param excludeUserId id to exclude from the check (pass 0 when adding a new user)
     * @return true if some other user already has this email
     */
    public boolean isEmailTaken(String email, int excludeUserId) throws SQLException {
        String sql = "SELECT id FROM users WHERE email = ? AND id != ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setInt(2, excludeUserId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next(); // true if a row was found
            }
        }
    }

    /**
     * Helper method that converts the current row of a ResultSet
     * into a User object. Keeps mapping logic in one place instead
     * of repeating it in every query method.
     */
    private User mapRowToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setName(rs.getString("name"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        user.setPhone(rs.getString("phone"));
        user.setAddress(rs.getString("address"));
        user.setCreatedAt(rs.getTimestamp("created_at"));
        return user;
    }
}

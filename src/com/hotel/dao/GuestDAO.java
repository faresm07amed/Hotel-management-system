package com.hotel.dao;

import com.hotel.model.Guest;
import com.hotel.service.DatabaseManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class GuestDAO {
    private final DatabaseManager dbManager;

    public GuestDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }

    public ObservableList<Guest> getAllGuests() {
        ObservableList<Guest> guests = FXCollections.observableArrayList();
        String query = "SELECT * FROM guests ORDER BY id DESC";

        try (Connection conn = dbManager.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Guest guest = new Guest(
                        rs.getInt("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("id_number"),
                        rs.getString("address"));
                guests.add(guest);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return guests;
    }

    public boolean addGuest(Guest guest) {
        String query = "INSERT INTO guests (first_name, last_name, email, phone, id_number, address) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, guest.getFirstName());
            pstmt.setString(2, guest.getLastName());
            pstmt.setString(3, guest.getEmail());
            pstmt.setString(4, guest.getPhone());
            pstmt.setString(5, guest.getIdNumber());
            pstmt.setString(6, guest.getAddress());

            int result = pstmt.executeUpdate();

            if (result > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    guest.setId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateGuest(Guest guest) {
        String query = "UPDATE guests SET first_name=?, last_name=?, email=?, phone=?, " +
                "id_number=?, address=? WHERE id=?";

        try (Connection conn = dbManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, guest.getFirstName());
            pstmt.setString(2, guest.getLastName());
            pstmt.setString(3, guest.getEmail());
            pstmt.setString(4, guest.getPhone());
            pstmt.setString(5, guest.getIdNumber());
            pstmt.setString(6, guest.getAddress());
            pstmt.setInt(7, guest.getId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteGuest(int guestId) {
        String query = "DELETE FROM guests WHERE id=?";

        try (Connection conn = dbManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, guestId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Guest getGuestById(int id) {
        String query = "SELECT * FROM guests WHERE id=?";

        try (Connection conn = dbManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Guest(
                        rs.getInt("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("id_number"),
                        rs.getString("address"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ObservableList<Guest> searchGuests(String searchTerm) {
        ObservableList<Guest> guests = FXCollections.observableArrayList();
        String query = "SELECT * FROM guests WHERE first_name LIKE ? OR last_name LIKE ? OR " +
                "email LIKE ? OR phone LIKE ? OR id_number LIKE ?";

        try (Connection conn = dbManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            String searchPattern = "%" + searchTerm + "%";
            for (int i = 1; i <= 5; i++) {
                pstmt.setString(i, searchPattern);
            }

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Guest guest = new Guest(
                        rs.getInt("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("id_number"),
                        rs.getString("address"));
                guests.add(guest);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return guests;
    }
}

package com.hotel.dao;

import com.hotel.model.Room;
import com.hotel.model.RoomStatus;
import com.hotel.model.RoomType;
import com.hotel.service.DatabaseManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class RoomDAO {
    private final DatabaseManager dbManager;

    public RoomDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }

    public ObservableList<Room> getAllRooms() {
        ObservableList<Room> rooms = FXCollections.observableArrayList();
        String query = "SELECT * FROM rooms ORDER BY room_number";

        try (Connection conn = dbManager.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Room room = new Room(
                        rs.getString("room_number"),
                        RoomType.valueOf(rs.getString("type")),
                        RoomStatus.valueOf(rs.getString("status")),
                        rs.getDouble("price_per_night"),
                        rs.getString("description"),
                        rs.getInt("max_occupancy"));
                rooms.add(room);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rooms;
    }

    public boolean addRoom(Room room) {
        String query = "INSERT INTO rooms (room_number, type, status, price_per_night, description, max_occupancy) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, room.getRoomNumber());
            pstmt.setString(2, room.getType().name());
            pstmt.setString(3, room.getStatus().name());
            pstmt.setDouble(4, room.getPricePerNight());
            pstmt.setString(5, room.getDescription());
            pstmt.setInt(6, room.getMaxOccupancy());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateRoom(Room room) {
        String query = "UPDATE rooms SET type=?, status=?, price_per_night=?, description=?, " +
                "max_occupancy=? WHERE room_number=?";

        try (Connection conn = dbManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, room.getType().name());
            pstmt.setString(2, room.getStatus().name());
            pstmt.setDouble(3, room.getPricePerNight());
            pstmt.setString(4, room.getDescription());
            pstmt.setInt(5, room.getMaxOccupancy());
            pstmt.setString(6, room.getRoomNumber());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteRoom(String roomNumber) {
        String query = "DELETE FROM rooms WHERE room_number=?";

        try (Connection conn = dbManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, roomNumber);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Room getRoomByNumber(String roomNumber) {
        String query = "SELECT * FROM rooms WHERE room_number=?";

        try (Connection conn = dbManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, roomNumber);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Room(
                        rs.getString("room_number"),
                        RoomType.valueOf(rs.getString("type")),
                        RoomStatus.valueOf(rs.getString("status")),
                        rs.getDouble("price_per_night"),
                        rs.getString("description"),
                        rs.getInt("max_occupancy"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateRoomStatus(String roomNumber, RoomStatus newStatus) {
        String query = "UPDATE rooms SET status=? WHERE room_number=?";

        try (Connection conn = dbManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, newStatus.name());
            pstmt.setString(2, roomNumber);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public ObservableList<Room> getRoomsByStatus(RoomStatus status) {
        ObservableList<Room> rooms = FXCollections.observableArrayList();
        String query = "SELECT * FROM rooms WHERE status=? ORDER BY room_number";

        try (Connection conn = dbManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, status.name());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Room room = new Room(
                        rs.getString("room_number"),
                        RoomType.valueOf(rs.getString("type")),
                        RoomStatus.valueOf(rs.getString("status")),
                        rs.getDouble("price_per_night"),
                        rs.getString("description"),
                        rs.getInt("max_occupancy"));
                rooms.add(room);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rooms;
    }

    public ObservableList<Room> getRoomsByType(RoomType type) {
        ObservableList<Room> rooms = FXCollections.observableArrayList();
        String query = "SELECT * FROM rooms WHERE type=? ORDER BY room_number";

        try (Connection conn = dbManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, type.name());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Room room = new Room(
                        rs.getString("room_number"),
                        RoomType.valueOf(rs.getString("type")),
                        RoomStatus.valueOf(rs.getString("status")),
                        rs.getDouble("price_per_night"),
                        rs.getString("description"),
                        rs.getInt("max_occupancy"));
                rooms.add(room);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rooms;
    }
}

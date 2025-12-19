package com.hotel.dao;

import com.hotel.model.Reservation;
import com.hotel.model.ReservationStatus;
import com.hotel.model.Guest;
import com.hotel.model.Room;
import com.hotel.service.DatabaseManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDate;

public class ReservationDAO {
    private final DatabaseManager dbManager;
    private final GuestDAO guestDAO;
    private final RoomDAO roomDAO;

    public ReservationDAO() {
        this.dbManager = DatabaseManager.getInstance();
        this.guestDAO = new GuestDAO();
        this.roomDAO = new RoomDAO();
    }

    public ObservableList<Reservation> getAllReservations() {
        ObservableList<Reservation> reservations = FXCollections.observableArrayList();
        String query = "SELECT * FROM reservations ORDER BY id DESC";

        try (Connection conn = dbManager.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Reservation reservation = createReservationFromResultSet(rs);
                if (reservation != null) {
                    reservations.add(reservation);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reservations;
    }

    public boolean addReservation(Reservation reservation) {
        String query = "INSERT INTO reservations (guest_id, room_number, check_in_date, check_out_date, " +
                "status, total_price, notes) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, reservation.getGuest().getId());
            pstmt.setString(2, reservation.getRoom().getRoomNumber());
            pstmt.setDate(3, Date.valueOf(reservation.getCheckInDate()));
            pstmt.setDate(4, Date.valueOf(reservation.getCheckOutDate()));
            pstmt.setString(5, reservation.getStatus().name());
            pstmt.setDouble(6, reservation.getTotalPrice());
            pstmt.setString(7, reservation.getNotes());

            int result = pstmt.executeUpdate();

            if (result > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    reservation.setId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateReservation(Reservation reservation) {
        String query = "UPDATE reservations SET guest_id=?, room_number=?, check_in_date=?, " +
                "check_out_date=?, status=?, total_price=?, notes=? WHERE id=?";

        try (Connection conn = dbManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, reservation.getGuest().getId());
            pstmt.setString(2, reservation.getRoom().getRoomNumber());
            pstmt.setDate(3, Date.valueOf(reservation.getCheckInDate()));
            pstmt.setDate(4, Date.valueOf(reservation.getCheckOutDate()));
            pstmt.setString(5, reservation.getStatus().name());
            pstmt.setDouble(6, reservation.getTotalPrice());
            pstmt.setString(7, reservation.getNotes());
            pstmt.setInt(8, reservation.getId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteReservation(int reservationId) {
        String query = "DELETE FROM reservations WHERE id=?";

        try (Connection conn = dbManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, reservationId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Reservation getReservationById(int id) {
        String query = "SELECT * FROM reservations WHERE id=?";

        try (Connection conn = dbManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return createReservationFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isRoomAvailable(String roomNumber, LocalDate checkIn, LocalDate checkOut) {
        String query = "SELECT COUNT(*) FROM reservations WHERE room_number=? AND status != 'CANCELLED' " +
                "AND ((check_in_date <= ? AND check_out_date > ?) OR " +
                "(check_in_date < ? AND check_out_date >= ?) OR " +
                "(check_in_date >= ? AND check_out_date <= ?))";

        try (Connection conn = dbManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, roomNumber);
            pstmt.setDate(2, Date.valueOf(checkOut));
            pstmt.setDate(3, Date.valueOf(checkIn));
            pstmt.setDate(4, Date.valueOf(checkOut));
            pstmt.setDate(5, Date.valueOf(checkIn));
            pstmt.setDate(6, Date.valueOf(checkIn));
            pstmt.setDate(7, Date.valueOf(checkOut));

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) == 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public ObservableList<Reservation> getReservationsByStatus(ReservationStatus status) {
        ObservableList<Reservation> reservations = FXCollections.observableArrayList();
        String query = "SELECT * FROM reservations WHERE status=? ORDER BY check_in_date DESC";

        try (Connection conn = dbManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, status.name());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Reservation reservation = createReservationFromResultSet(rs);
                if (reservation != null) {
                    reservations.add(reservation);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reservations;
    }

    public ObservableList<Reservation> getReservationsByRoom(String roomNumber) {
        ObservableList<Reservation> reservations = FXCollections.observableArrayList();
        String query = "SELECT * FROM reservations WHERE room_number=? ORDER BY check_in_date DESC";

        try (Connection conn = dbManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, roomNumber);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Reservation reservation = createReservationFromResultSet(rs);
                if (reservation != null) {
                    reservations.add(reservation);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reservations;
    }

    private Reservation createReservationFromResultSet(ResultSet rs) throws SQLException {
        int guestId = rs.getInt("guest_id");
        String roomNumber = rs.getString("room_number");

        Guest guest = guestDAO.getGuestById(guestId);
        Room room = roomDAO.getRoomByNumber(roomNumber);

        if (guest == null || room == null) {
            return null;
        }

        return new Reservation(
                rs.getInt("id"),
                guest,
                room,
                rs.getDate("check_in_date").toLocalDate(),
                rs.getDate("check_out_date").toLocalDate(),
                ReservationStatus.valueOf(rs.getString("status")),
                rs.getDouble("total_price"),
                rs.getString("notes"));
    }
}

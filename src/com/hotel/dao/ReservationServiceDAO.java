package com.hotel.dao;

import com.hotel.model.ReservationService;
import com.hotel.model.Reservation;
import com.hotel.model.Service;
import com.hotel.service.DatabaseManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDateTime;

public class ReservationServiceDAO {
    private final DatabaseManager dbManager;
    private final ReservationDAO reservationDAO;
    private final ServiceDAO serviceDAO;

    public ReservationServiceDAO() {
        this.dbManager = DatabaseManager.getInstance();
        this.reservationDAO = new ReservationDAO();
        this.serviceDAO = new ServiceDAO();
    }

    public ObservableList<ReservationService> getAllReservationServices() {
        ObservableList<ReservationService> reservationServices = FXCollections.observableArrayList();
        String query = "SELECT * FROM reservation_services ORDER BY id DESC";

        try (Connection conn = dbManager.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                ReservationService rsvc = createReservationServiceFromResultSet(rs);
                if (rsvc != null) {
                    reservationServices.add(rsvc);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reservationServices;
    }

    public boolean addReservationService(ReservationService reservationService) {
        String query = "INSERT INTO reservation_services (reservation_id, service_id, quantity, " +
                "date_requested, status, total_price, notes) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, reservationService.getReservation().getId());
            pstmt.setInt(2, reservationService.getService().getId());
            pstmt.setInt(3, reservationService.getQuantity());
            pstmt.setTimestamp(4, Timestamp.valueOf(reservationService.getDateRequested()));
            pstmt.setString(5, reservationService.getStatus());
            pstmt.setDouble(6, reservationService.getTotalPrice());
            pstmt.setString(7, reservationService.getNotes());

            int result = pstmt.executeUpdate();

            if (result > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    reservationService.setId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateReservationService(ReservationService reservationService) {
        String query = "UPDATE reservation_services SET reservation_id=?, service_id=?, quantity=?, " +
                "date_requested=?, status=?, total_price=?, notes=? WHERE id=?";

        try (Connection conn = dbManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, reservationService.getReservation().getId());
            pstmt.setInt(2, reservationService.getService().getId());
            pstmt.setInt(3, reservationService.getQuantity());
            pstmt.setTimestamp(4, Timestamp.valueOf(reservationService.getDateRequested()));
            pstmt.setString(5, reservationService.getStatus());
            pstmt.setDouble(6, reservationService.getTotalPrice());
            pstmt.setString(7, reservationService.getNotes());
            pstmt.setInt(8, reservationService.getId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteReservationService(int id) {
        String query = "DELETE FROM reservation_services WHERE id=?";

        try (Connection conn = dbManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public ObservableList<ReservationService> getServicesByReservation(int reservationId) {
        ObservableList<ReservationService> reservationServices = FXCollections.observableArrayList();
        String query = "SELECT * FROM reservation_services WHERE reservation_id=?";

        try (Connection conn = dbManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, reservationId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                ReservationService rsvc = createReservationServiceFromResultSet(rs);
                if (rsvc != null) {
                    reservationServices.add(rsvc);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reservationServices;
    }

    public double getTotalServiceCharges(int reservationId) {
        String query = "SELECT SUM(total_price) FROM reservation_services WHERE reservation_id=? " +
                "AND status != 'CANCELLED'";

        try (Connection conn = dbManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, reservationId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    private ReservationService createReservationServiceFromResultSet(ResultSet rs) throws SQLException {
        int reservationId = rs.getInt("reservation_id");
        int serviceId = rs.getInt("service_id");

        Reservation reservation = reservationDAO.getReservationById(reservationId);
        Service service = serviceDAO.getServiceById(serviceId);

        if (reservation == null || service == null) {
            return null;
        }

        Timestamp ts = rs.getTimestamp("date_requested");
        LocalDateTime dateRequested = ts != null ? ts.toLocalDateTime() : LocalDateTime.now();

        return new ReservationService(
                rs.getInt("id"),
                reservation,
                service,
                rs.getInt("quantity"),
                dateRequested,
                rs.getString("status"),
                rs.getDouble("total_price"),
                rs.getString("notes"));
    }
}

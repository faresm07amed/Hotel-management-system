package com.hotel.dao;

import com.hotel.model.Payment;
import com.hotel.model.PaymentMethod;
import com.hotel.model.PaymentStatus;
import com.hotel.model.Reservation;
import com.hotel.service.DatabaseManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDateTime;

public class PaymentDAO {
    private final DatabaseManager dbManager;
    private final ReservationDAO reservationDAO;

    public PaymentDAO() {
        this.dbManager = DatabaseManager.getInstance();
        this.reservationDAO = new ReservationDAO();
    }

    public ObservableList<Payment> getAllPayments() {
        ObservableList<Payment> payments = FXCollections.observableArrayList();
        String query = "SELECT * FROM payments ORDER BY id DESC";

        try (Connection conn = dbManager.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Payment payment = createPaymentFromResultSet(rs);
                if (payment != null) {
                    payments.add(payment);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return payments;
    }

    public boolean addPayment(Payment payment) {
        String query = "INSERT INTO payments (reservation_id, amount, payment_method, payment_date, " +
                "status, transaction_id, notes) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, payment.getReservation().getId());
            pstmt.setDouble(2, payment.getAmount());
            pstmt.setString(3, payment.getPaymentMethod().name());
            pstmt.setTimestamp(4, Timestamp.valueOf(payment.getPaymentDate()));
            pstmt.setString(5, payment.getStatus().name());
            pstmt.setString(6, payment.getTransactionId());
            pstmt.setString(7, payment.getNotes());

            int result = pstmt.executeUpdate();

            if (result > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    payment.setId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updatePayment(Payment payment) {
        String query = "UPDATE payments SET reservation_id=?, amount=?, payment_method=?, " +
                "payment_date=?, status=?, transaction_id=?, notes=? WHERE id=?";

        try (Connection conn = dbManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, payment.getReservation().getId());
            pstmt.setDouble(2, payment.getAmount());
            pstmt.setString(3, payment.getPaymentMethod().name());
            pstmt.setTimestamp(4, Timestamp.valueOf(payment.getPaymentDate()));
            pstmt.setString(5, payment.getStatus().name());
            pstmt.setString(6, payment.getTransactionId());
            pstmt.setString(7, payment.getNotes());
            pstmt.setInt(8, payment.getId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deletePayment(int paymentId) {
        String query = "DELETE FROM payments WHERE id=?";

        try (Connection conn = dbManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, paymentId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public ObservableList<Payment> getPaymentsByReservation(int reservationId) {
        ObservableList<Payment> payments = FXCollections.observableArrayList();
        String query = "SELECT * FROM payments WHERE reservation_id=?";

        try (Connection conn = dbManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, reservationId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Payment payment = createPaymentFromResultSet(rs);
                if (payment != null) {
                    payments.add(payment);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return payments;
    }

    public double getTotalRevenue() {
        String query = "SELECT SUM(amount) FROM payments WHERE status='COMPLETED'";

        try (Connection conn = dbManager.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    private Payment createPaymentFromResultSet(ResultSet rs) throws SQLException {
        int reservationId = rs.getInt("reservation_id");
        Reservation reservation = reservationDAO.getReservationById(reservationId);

        if (reservation == null) {
            return null;
        }

        Timestamp ts = rs.getTimestamp("payment_date");
        LocalDateTime paymentDate = ts != null ? ts.toLocalDateTime() : LocalDateTime.now();

        return new Payment(
                rs.getInt("id"),
                reservation,
                rs.getDouble("amount"),
                PaymentMethod.valueOf(rs.getString("payment_method")),
                paymentDate,
                PaymentStatus.valueOf(rs.getString("status")),
                rs.getString("transaction_id"),
                rs.getString("notes"));
    }
}

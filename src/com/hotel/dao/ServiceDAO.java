package com.hotel.dao;

import com.hotel.model.Service;
import com.hotel.model.ServiceCategory;
import com.hotel.service.DatabaseManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class ServiceDAO {
    private final DatabaseManager dbManager;

    public ServiceDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }

    public ObservableList<Service> getAllServices() {
        ObservableList<Service> services = FXCollections.observableArrayList();
        String query = "SELECT * FROM services ORDER BY category, name";

        try (Connection conn = dbManager.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Service service = new Service(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDouble("price"),
                        ServiceCategory.valueOf(rs.getString("category")),
                        rs.getBoolean("is_active"));
                services.add(service);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return services;
    }

    public ObservableList<Service> getActiveServices() {
        ObservableList<Service> services = FXCollections.observableArrayList();
        String query = "SELECT * FROM services WHERE is_active=TRUE ORDER BY category, name";

        try (Connection conn = dbManager.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Service service = new Service(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDouble("price"),
                        ServiceCategory.valueOf(rs.getString("category")),
                        rs.getBoolean("is_active"));
                services.add(service);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return services;
    }

    public boolean addService(Service service) {
        String query = "INSERT INTO services (name, description, price, category, is_active) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = dbManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, service.getName());
            pstmt.setString(2, service.getDescription());
            pstmt.setDouble(3, service.getPrice());
            pstmt.setString(4, service.getCategory().name());
            pstmt.setBoolean(5, service.isActive());

            int result = pstmt.executeUpdate();

            if (result > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    service.setId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateService(Service service) {
        String query = "UPDATE services SET name=?, description=?, price=?, category=?, is_active=? WHERE id=?";

        try (Connection conn = dbManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, service.getName());
            pstmt.setString(2, service.getDescription());
            pstmt.setDouble(3, service.getPrice());
            pstmt.setString(4, service.getCategory().name());
            pstmt.setBoolean(5, service.isActive());
            pstmt.setInt(6, service.getId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteService(int serviceId) {
        String query = "DELETE FROM services WHERE id=?";

        try (Connection conn = dbManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, serviceId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Service getServiceById(int id) {
        String query = "SELECT * FROM services WHERE id=?";

        try (Connection conn = dbManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Service(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDouble("price"),
                        ServiceCategory.valueOf(rs.getString("category")),
                        rs.getBoolean("is_active"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ObservableList<Service> getServicesByCategory(ServiceCategory category) {
        ObservableList<Service> services = FXCollections.observableArrayList();
        String query = "SELECT * FROM services WHERE category=? ORDER BY name";

        try (Connection conn = dbManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, category.name());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Service service = new Service(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDouble("price"),
                        ServiceCategory.valueOf(rs.getString("category")),
                        rs.getBoolean("is_active"));
                services.add(service);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return services;
    }
}

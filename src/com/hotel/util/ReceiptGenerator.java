package com.hotel.util;

import com.hotel.model.Payment;
import com.hotel.model.Reservation;
import com.hotel.model.ReservationService;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReceiptGenerator {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // Convenience method without services
    public static String generateReceipt(Payment payment) {
        return generatePaymentReceipt(payment, null);
    }

    public static String generatePaymentReceipt(Payment payment, List<ReservationService> services) {
        StringBuilder receipt = new StringBuilder();

        receipt.append("═══════════════════════════════════════════════\n");
        receipt.append("           HOTEL PAYMENT RECEIPT\n");
        receipt.append("═══════════════════════════════════════════════\n\n");

        receipt.append("Payment ID: ").append(payment.getId()).append("\n");
        receipt.append("Date: ").append(payment.getPaymentDate().format(DATE_FORMAT)).append("\n");
        receipt.append("Transaction ID: ").append(payment.getTransactionId()).append("\n");
        receipt.append("Payment Method: ").append(payment.getPaymentMethod()).append("\n\n");

        Reservation reservation = payment.getReservation();
        if (reservation != null) {
            receipt.append("───────────────────────────────────────────────\n");
            receipt.append("RESERVATION DETAILS\n");
            receipt.append("───────────────────────────────────────────────\n");
            receipt.append("Reservation ID: ").append(reservation.getId()).append("\n");
            receipt.append("Guest: ").append(reservation.getGuestName()).append("\n");
            receipt.append("Room: ").append(reservation.getRoomNumber()).append("\n");
            receipt.append("Check-in: ").append(reservation.getCheckInDate()).append("\n");
            receipt.append("Check-out: ").append(reservation.getCheckOutDate()).append("\n");
            receipt.append("Room Charges: $").append(String.format("%.2f", reservation.getTotalPrice())).append("\n");
        }

        if (services != null && !services.isEmpty()) {
            receipt.append("\n───────────────────────────────────────────────\n");
            receipt.append("SERVICES\n");
            receipt.append("───────────────────────────────────────────────\n");

            double servicesTotal = 0.0;
            for (ReservationService service : services) {
                receipt.append(service.getServiceName())
                        .append(" x").append(service.getQuantity())
                        .append(" = $").append(String.format("%.2f", service.getTotalPrice()))
                        .append("\n");
                servicesTotal += service.getTotalPrice();
            }
            receipt.append("Services Total: $").append(String.format("%.2f", servicesTotal)).append("\n");
        }

        receipt.append("\n═══════════════════════════════════════════════\n");
        receipt.append("Amount Paid: $").append(String.format("%.2f", payment.getAmount())).append("\n");
        receipt.append("Status: ").append(payment.getStatus()).append("\n");
        receipt.append("═══════════════════════════════════════════════\n\n");

        receipt.append("        Thank you for choosing our hotel!\n");
        receipt.append("═══════════════════════════════════════════════\n");

        return receipt.toString();
    }

    public static String generateReservationSummary(Reservation reservation, double totalWithServices) {
        StringBuilder summary = new StringBuilder();

        summary.append("═══════════════════════════════════════════════\n");
        summary.append("         RESERVATION SUMMARY\n");
        summary.append("═══════════════════════════════════════════════\n\n");

        summary.append("Reservation ID: ").append(reservation.getId()).append("\n");
        summary.append("Guest: ").append(reservation.getGuestName()).append("\n");
        summary.append("Room: ").append(reservation.getRoomNumber()).append("\n");
        summary.append("Check-in: ").append(reservation.getCheckInDate()).append("\n");
        summary.append("Check-out: ").append(reservation.getCheckOutDate()).append("\n");
        summary.append("Status: ").append(reservation.getStatus()).append("\n\n");

        summary.append("Room Charges: $").append(String.format("%.2f", reservation.getTotalPrice())).append("\n");

        if (totalWithServices > reservation.getTotalPrice()) {
            double serviceCharges = totalWithServices - reservation.getTotalPrice();
            summary.append("Service Charges: $").append(String.format("%.2f", serviceCharges)).append("\n");
        }

        summary.append("\n═══════════════════════════════════════════════\n");
        summary.append("Total Amount: $").append(String.format("%.2f", totalWithServices)).append("\n");
        summary.append("═══════════════════════════════════════════════\n");

        return summary.toString();
    }
}

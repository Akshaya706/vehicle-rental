package com.rental.service;

import com.rental.dto.PaymentRequest;
import com.rental.entity.Booking;
import com.rental.entity.Payment;
import com.rental.repository.BookingRepository;
import com.rental.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Transactional
    public Payment processPayment(PaymentRequest req) {
        Booking booking = bookingRepository.findById(req.getBookingId())
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setPaymentMethod(req.getPaymentMethod());
        payment.setAmount(req.getAmount() != null ? req.getAmount() : booking.getTotalCharge());
        payment.setPaymentStatus("COMPLETED");
        payment.setPaymentDate(LocalDateTime.now());
        payment.setTransactionId("TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        payment = paymentRepository.save(payment);

        if ("APPROVED".equalsIgnoreCase(booking.getBookingStatus())) {
            booking.setBookingStatus("ACTIVE");
            booking.getVehicle().setAvailability("RENTED");
            bookingRepository.save(booking);
        }

        return payment;
    }

    @Transactional
    public Payment processRefund(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        Payment payment = paymentRepository.findByBookingBookingId(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("No payment found for booking ID: " + bookingId));

        payment.setPaymentStatus("REFUNDED");
        booking.setBookingStatus("CANCELLED");
        bookingRepository.save(booking);

        return paymentRepository.save(payment);
    }

    public Map<String, Object> generateInvoice(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        Payment payment = paymentRepository.findByBookingBookingId(bookingId).orElse(null);

        Map<String, Object> invoice = new HashMap<>();
        invoice.put("invoiceNo", "INV-" + booking.getBookingId());
        invoice.put("invoiceDate", LocalDateTime.now().toString());
        invoice.put("customerName", booking.getCustomer().getName());
        invoice.put("customerPhone", booking.getCustomer().getPhone());
        invoice.put("customerLicence", booking.getCustomer().getDrivingLicence());
        invoice.put("vehicleDetails", booking.getVehicle().getBrand() + " " + booking.getVehicle().getModel() + " (" + booking.getVehicle().getRegistrationNo() + ")");
        invoice.put("vehicleType", booking.getVehicle().getVehicleType());
        invoice.put("rentalStartDate", booking.getRentalStartDate().toString());
        invoice.put("rentalEndDate", booking.getRentalEndDate().toString());
        invoice.put("rentalDays", booking.getRentalDays());
        invoice.put("dailyRate", booking.getDailyRate());
        invoice.put("dynamicPricingFactor", booking.getDynamicPricingFactor());
        invoice.put("extraCharges", booking.getExtraCharges());
        invoice.put("totalAmount", booking.getTotalCharge());
        invoice.put("paymentStatus", payment != null ? payment.getPaymentStatus() : "PENDING");
        invoice.put("paymentMethod", payment != null ? payment.getPaymentMethod() : "N/A");
        invoice.put("transactionId", payment != null ? payment.getTransactionId() : "N/A");

        return invoice;
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }
}

package com.rental.service;

import com.rental.entity.*;
import com.rental.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class DataInitializerService implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private DamageReportRepository damageReportRepository;

    @Autowired
    private MaintenanceRepository maintenanceRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() > 0) {
            return; // Already initialized
        }

        // 1. Create Users
        User adminUser = new User("admin", "admin123", "ADMIN", "System Administrator", "+1 800-555-0100");
        userRepository.save(adminUser);

        User managerUser = new User("manager", "manager123", "RENTAL_MANAGER", "Alex Mercer (Rental Manager)", "+1 800-555-0101");
        userRepository.save(managerUser);

        User johnUser = new User("john_doe", "user123", "CUSTOMER", "John Doe", "+1 555-0192");
        userRepository.save(johnUser);

        User sarahUser = new User("sarah_connor", "user123", "CUSTOMER", "Sarah Connor", "+1 555-0188");
        userRepository.save(sarahUser);

        User michaelUser = new User("michael_knight", "user123", "CUSTOMER", "Michael Knight", "+1 555-0177");
        userRepository.save(michaelUser);

        // 2. Create Customers
        Customer johnCustomer = customerRepository.save(new Customer("John Doe", "123 Maple Street, NY", "+1 555-0192", "DL-883920194", johnUser));
        Customer sarahCustomer = customerRepository.save(new Customer("Sarah Connor", "456 Oak Avenue, CA", "+1 555-0188", "DL-992018472", sarahUser));
        Customer michaelCustomer = customerRepository.save(new Customer("Michael Knight", "789 Knight Rider Blvd, TX", "+1 555-0177", "DL-110293847", michaelUser));

        // 3. Create Vehicles
        Vehicle v1 = vehicleRepository.save(new Vehicle("Car", "Toyota", "Camry", "KA-01-AB-1001", 55.0, "AVAILABLE",
                LocalDate.now().minusMonths(2), LocalDate.now().plusMonths(8), LocalDate.now().plusMonths(6), "Petrol", 5, "https://images.unsplash.com/photo-1621007947382-bb3c3994e3fb?w=600&auto=format&fit=crop"));

        Vehicle v2 = vehicleRepository.save(new Vehicle("Car", "Honda", "Civic", "KA-02-CD-2002", 48.0, "BOOKED",
                LocalDate.now().minusMonths(1), LocalDate.now().plusMonths(5), LocalDate.now().plusMonths(4), "Petrol", 5, "https://images.unsplash.com/photo-1606152421802-db97b9c7a11b?w=600&auto=format&fit=crop"));

        Vehicle v3 = vehicleRepository.save(new Vehicle("Car", "BMW", "X5", "KA-03-EF-3003", 130.0, "AVAILABLE",
                LocalDate.now().minusMonths(3), LocalDate.now().plusMonths(10), LocalDate.now().plusMonths(9), "Diesel", 7, "https://images.unsplash.com/photo-1555215695-3004980ad54e?w=600&auto=format&fit=crop"));

        Vehicle v4 = vehicleRepository.save(new Vehicle("Bike", "Yamaha", "YZF R15", "KA-04-GH-4004", 28.0, "AVAILABLE",
                LocalDate.now().minusMonths(4), LocalDate.now().plusMonths(2), LocalDate.now().plusMonths(3), "Petrol", 2, "https://images.unsplash.com/photo-1568772585407-9361f9bf3a87?w=600&auto=format&fit=crop"));

        Vehicle v5 = vehicleRepository.save(new Vehicle("Bike", "Royal Enfield", "Classic 350", "KA-05-IJ-5005", 35.0, "RENTED",
                LocalDate.now().minusMonths(2), LocalDate.now().plusDays(15), LocalDate.now().plusMonths(1), "Petrol", 2, "https://images.unsplash.com/photo-1558981403-c5f9899a28bc?w=600&auto=format&fit=crop"));

        Vehicle v6 = vehicleRepository.save(new Vehicle("Car", "Tesla", "Model 3", "KA-06-KL-6006", 110.0, "MAINTENANCE",
                LocalDate.now().minusWeeks(1), LocalDate.now().plusMonths(11), LocalDate.now().plusMonths(12), "Electric", 5, "https://images.unsplash.com/photo-1560958089-b8a1929cea89?w=600&auto=format&fit=crop"));

        // 4. Create Bookings
        Booking b1 = new Booking(johnCustomer, v5, LocalDateTime.now().minusDays(3), LocalDate.now().minusDays(2), LocalDate.now().plusDays(3), "ACTIVE", 5, 35.0, 1.0, 0.0, 175.0);
        b1 = bookingRepository.save(b1);

        Payment p1 = new Payment(b1, "ONLINE_CARD", 175.0, "COMPLETED", LocalDateTime.now().minusDays(3), "TXN-99812A");
        paymentRepository.save(p1);

        Booking b2 = new Booking(sarahCustomer, v2, LocalDateTime.now().minusDays(1), LocalDate.now().plusDays(1), LocalDate.now().plusDays(4), "APPROVED", 3, 48.0, 1.15, 0.0, 165.6);
        b2 = bookingRepository.save(b2);

        Booking b3 = new Booking(michaelCustomer, v3, LocalDateTime.now().minusDays(10), LocalDate.now().minusDays(9), LocalDate.now().minusDays(4), "COMPLETED", 5, 130.0, 1.10, 50.0, 765.0);
        b3 = bookingRepository.save(b3);

        Payment p3 = new Payment(b3, "UPI", 765.0, "COMPLETED", LocalDateTime.now().minusDays(10), "TXN-77341B");
        paymentRepository.save(p3);

        // 5. Create Damage Report
        DamageReport dr = new DamageReport(b3, "Minor scratch on rear bumper", 120.0, LocalDate.now().minusDays(4), "Alex Mercer", 24500, "Full");
        damageReportRepository.save(dr);

        // 6. Create Maintenance Record
        Maintenance m1 = new Maintenance(v6, LocalDate.now().minusWeeks(1), LocalDate.now().plusDays(4), "IN_PROGRESS", "Regular battery check & tire rotation");
        maintenanceRepository.save(m1);

        // 7. Create Reviews
        reviewRepository.save(new Review(v3, michaelCustomer, 5, "Fantastic luxury SUV! Smooth drive and great condition.", LocalDate.now().minusDays(3)));
        reviewRepository.save(new Review(v1, johnCustomer, 4, "Reliable sedan, very economic fuel consumption.", LocalDate.now().minusDays(15)));

        System.out.println(">>> Vehicle Rental Management System sample data successfully loaded! <<<");
    }
}

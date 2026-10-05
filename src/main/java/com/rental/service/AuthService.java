package com.rental.service;

import com.rental.dto.LoginRequest;
import com.rental.dto.LoginResponse;
import com.rental.dto.RegisterCustomerRequest;
import com.rental.entity.Customer;
import com.rental.entity.User;
import com.rental.repository.CustomerRepository;
import com.rental.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerRepository customerRepository;

    public LoginResponse login(LoginRequest request) {
        Optional<User> userOpt = userRepository.findByUsername(request.getUsername());
        if (userOpt.isEmpty()) {
            return new LoginResponse("Invalid Credentials", false, null, null, null, null, null);
        }

        User user = userOpt.get();
        if (!user.getPassword().equals(request.getPassword())) {
            return new LoginResponse("Invalid Credentials", false, null, null, null, null, null);
        }

        Long customerId = null;
        if ("CUSTOMER".equalsIgnoreCase(user.getRole())) {
            Optional<Customer> custOpt = customerRepository.findByUser(user);
            if (custOpt.isPresent()) {
                customerId = custOpt.get().getCustomerId();
            }
        }

        return new LoginResponse("Login Successful", true, user.getUserId(), user.getUsername(), user.getRole(), user.getName(), customerId);
    }

    @Transactional
    public LoginResponse registerCustomer(RegisterCustomerRequest req) {
        if (userRepository.existsByUsername(req.getUsername())) {
            return new LoginResponse("Username already exists", false, null, null, null, null, null);
        }

        if (req.getDrivingLicence() == null || req.getDrivingLicence().trim().isEmpty()) {
            return new LoginResponse("Driving licence is required", false, null, null, null, null, null);
        }

        User user = new User(req.getUsername(), req.getPassword(), "CUSTOMER", req.getName(), req.getPhone());
        user = userRepository.save(user);

        Customer customer = new Customer(req.getName(), req.getAddress(), req.getPhone(), req.getDrivingLicence(), user);
        customer = customerRepository.save(customer);

        return new LoginResponse("Customer registered successfully", true, user.getUserId(), user.getUsername(), user.getRole(), user.getName(), customer.getCustomerId());
    }

    public boolean changePassword(Long userId, String oldPassword, String newPassword) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.getPassword().equals(oldPassword)) {
                user.setPassword(newPassword);
                userRepository.save(user);
                return true;
            }
        }
        return false;
    }
}

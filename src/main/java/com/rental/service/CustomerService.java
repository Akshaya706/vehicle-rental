package com.rental.service;

import com.rental.entity.Customer;
import com.rental.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Optional<Customer> getCustomerById(Long customerId) {
        return customerRepository.findById(customerId);
    }

    public Customer updateCustomer(Long customerId, Customer details) {
        Customer existing = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with ID: " + customerId));
        existing.setName(details.getName());
        existing.setAddress(details.getAddress());
        existing.setPhone(details.getPhone());
        existing.setDrivingLicence(details.getDrivingLicence());
        return customerRepository.save(existing);
    }

    public void deleteCustomer(Long customerId) {
        customerRepository.deleteById(customerId);
    }

    public boolean validateDrivingLicence(String drivingLicence) {
        if (drivingLicence == null || drivingLicence.trim().length() < 5) {
            return false;
        }
        return true;
    }
}

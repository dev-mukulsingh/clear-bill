package com.clearbillsolutions.service;

import com.clearbillsolutions.dto.CustomerDTO;
import com.clearbillsolutions.entity.Customer;
import com.clearbillsolutions.exception.BadRequestException;
import com.clearbillsolutions.exception.ResourceNotFoundException;
import com.clearbillsolutions.repository.CustomerRepository;
import com.clearbillsolutions.util.GstUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Transactional(readOnly = true)
    public List<CustomerDTO> getAllCustomers() {
        return customerRepository.findAllByOrderByNameAsc()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CustomerDTO> searchCustomers(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllCustomers();
        }
        return customerRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(query.trim(), query.trim())
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CustomerDTO getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        return toDTO(customer);
    }

    @Transactional
    public CustomerDTO createCustomer(CustomerDTO dto) {
        String cleanGstin = null;
        if (dto.getGstin() != null && !dto.getGstin().trim().isEmpty()) {
            cleanGstin = dto.getGstin().trim().toUpperCase();
            if (!GstUtil.isValidGstin(cleanGstin)) {
                throw new BadRequestException("Invalid GSTIN format. Customer GSTIN must be a 15-character valid code (e.g., 27AABCS1429B1ZB).");
            }
        }

        String cleanEmail = dto.getEmail() != null ? dto.getEmail().trim().toLowerCase() : "";
        Customer customer = Customer.builder()
                .name(dto.getName() != null ? dto.getName().trim() : "")
                .email(cleanEmail)
                .phone(dto.getPhone())
                .address(dto.getAddress())
                .city(dto.getCity())
                .state(dto.getState())
                .postalCode(dto.getPostalCode())
                .country(dto.getCountry())
                .gstin(cleanGstin)
                .build();
        Customer saved = customerRepository.save(customer);
        return toDTO(saved);
    }

    @Transactional
    public CustomerDTO updateCustomer(Long id, CustomerDTO dto) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));

        String cleanGstin = null;
        if (dto.getGstin() != null && !dto.getGstin().trim().isEmpty()) {
            cleanGstin = dto.getGstin().trim().toUpperCase();
            if (!GstUtil.isValidGstin(cleanGstin)) {
                throw new BadRequestException("Invalid GSTIN format. Customer GSTIN must be a 15-character valid code (e.g., 27AABCS1429B1ZB).");
            }
        }

        customer.setName(dto.getName() != null ? dto.getName().trim() : "");
        customer.setEmail(dto.getEmail() != null ? dto.getEmail().trim().toLowerCase() : "");
        customer.setPhone(dto.getPhone());
        customer.setAddress(dto.getAddress());
        customer.setCity(dto.getCity());
        customer.setState(dto.getState());
        customer.setPostalCode(dto.getPostalCode());
        customer.setCountry(dto.getCountry());
        customer.setGstin(cleanGstin);

        Customer updated = customerRepository.save(customer);
        return toDTO(updated);
    }

    @Transactional
    public void deleteCustomer(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Customer not found with id: " + id);
        }
        customerRepository.deleteById(id);
    }

    public CustomerDTO toDTO(Customer customer) {
        return CustomerDTO.builder()
                .id(customer.getId())
                .name(customer.getName())
                .email(customer.getEmail())
                .phone(customer.getPhone())
                .address(customer.getAddress())
                .city(customer.getCity())
                .state(customer.getState())
                .postalCode(customer.getPostalCode())
                .country(customer.getCountry())
                .gstin(customer.getGstin())
                .createdAt(customer.getCreatedAt())
                .updatedAt(customer.getUpdatedAt())
                .build();
    }
}

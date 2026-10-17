package com.clearbillsolutions.service;

import com.clearbillsolutions.dto.ProductDTO;
import com.clearbillsolutions.entity.Product;
import com.clearbillsolutions.exception.ResourceNotFoundException;
import com.clearbillsolutions.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public List<ProductDTO> getAllProducts() {
        return productRepository.findAllByOrderByNameAsc()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductDTO> searchProducts(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllProducts();
        }
        return productRepository.findByNameContainingIgnoreCase(query.trim())
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return toDTO(product);
    }

    @Transactional
    public ProductDTO createProduct(ProductDTO dto) {
        Product product = Product.builder()
                .name(dto.getName().trim())
                .description(dto.getDescription())
                .hsnCode(dto.getHsnCode() != null ? dto.getHsnCode().trim() : null)
                .unitPrice(dto.getUnitPrice())
                .taxRate(dto.getTaxRate())
                .build();
        Product saved = productRepository.save(product);
        return toDTO(saved);
    }

    @Transactional
    public ProductDTO updateProduct(Long id, ProductDTO dto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        product.setName(dto.getName().trim());
        product.setDescription(dto.getDescription());
        product.setHsnCode(dto.getHsnCode() != null ? dto.getHsnCode().trim() : null);
        product.setUnitPrice(dto.getUnitPrice());
        product.setTaxRate(dto.getTaxRate());

        Product updated = productRepository.save(product);
        return toDTO(updated);
    }

    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    public ProductDTO toDTO(Product product) {
        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .hsnCode(product.getHsnCode())
                .unitPrice(product.getUnitPrice())
                .taxRate(product.getTaxRate())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}

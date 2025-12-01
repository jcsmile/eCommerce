package com.ecommerce.productservice.dto;

import com.ecommerce.productservice.domain.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.*;

/**
 * Data Transfer Object for Product.
 * <p>
 * Business rules:
 * 1. Used to transfer product data between layers
 * 2. Contains product details for API responses
 * 3. Supports conversion from entity
 *
 * @author JackyChen
 * @since 2025-04-01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
    private Long id;
    private String name;
    private String description;
    private String category;
    private Double price;
    private Integer stock;
    private String imageUrl;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public Double getPrice() {
        return price;
    }
    public void setPrice(Double price) {
        this.price = price;
    }
    public Integer getStock() {
        return stock;
    }
    public void setStock(Integer stock) {
        this.stock = stock;
    }
    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    /**
     * Converts a Product entity to ProductDto.
     * <p>
     * Business rules:
     * 1. Maps all fields from entity to DTO
     *
     * @param p the Product entity
     * @return the ProductDto
     * @author JackyChen
     * @since 2025-04-01
     */
    public static ProductDto fromEntity(Product p) {
        ProductDto dto = new ProductDto();
        dto.id = p.getId();
        dto.name = p.getName();
        dto.description = p.getDescription();
        dto.category = p.getCategory();
        dto.price = p.getPrice();
        dto.stock = p.getStock();
        dto.imageUrl = p.getImageUrl();
        return dto;
    }
}

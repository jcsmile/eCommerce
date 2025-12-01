package com.ecommerce.productservice.domain;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Represents a product entity in the e-commerce system.
 * <p>
 * Business rules:
 * 1. Mapped to the "products" table in the database
 * 2. Contains product details like name, description, price, stock
 * 3. Supports CRUD operations via repository
 *
 * @author JackyChen
 * @since 2025-04-01
 */
@Table("products")
public class Product {
    /**
     * Default constructor.
     */
    public Product() {}

    /**
     * Constructor with all fields.
     *
     * @param id the product ID
     * @param name the product name
     * @param description the product description
     * @param category the product category
     * @param price the product price
     * @param stock the product stock quantity
     * @param imageUrl the product image URL
     */
    public Product(Long id, String name, String description, String category, Double price, Integer stock, String imageUrl) {
        //this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.price = price;
        this.stock = stock;
        this.imageUrl = imageUrl;
    }

    /** The unique identifier of the product. */
    @Id private Long id;
    /** The name of the product. */
    private String name;
    /** The description of the product. */
    private String description;
    /** The category of the product. */
    private String category;
    /** The price of the product. */
    private Double price;
    /** The stock quantity of the product. */
    private Integer stock;
    /** The image URL of the product. */
    private String imageUrl;

    /**
     * Gets the product ID.
     *
     * @return the product ID
     */
    public Long getId() { return id; }

    /**
     * Sets the product ID.
     *
     * @param id the product ID to set
     */
    public void setId(Long id) { this.id = id; }

    /**
     * Gets the product name.
     *
     * @return the product name
     */
    public String getName() { return name; }

    /**
     * Sets the product name.
     *
     * @param name the product name to set
     */
    public void setName(String name) { this.name = name; }

    /**
     * Gets the product description.
     *
     * @return the product description
     */
    public String getDescription() { return description; }

    /**
     * Sets the product description.
     *
     * @param description the product description to set
     */
    public void setDescription(String description) { this.description = description; }

    /**
     * Gets the product category.
     *
     * @return the product category
     */
    public String getCategory() { return category; }

    /**
     * Sets the product category.
     *
     * @param category the product category to set
     */
    public void setCategory(String category) { this.category = category; }

    /**
     * Gets the product price.
     *
     * @return the product price
     */
    public Double getPrice() { return price; }

    /**
     * Sets the product price.
     *
     * @param price the product price to set
     */
    public void setPrice(Double price) { this.price = price; }

    /**
     * Gets the product stock quantity.
     *
     * @return the product stock
     */
    public Integer getStock() { return stock; }

    /**
     * Sets the product stock quantity.
     *
     * @param stock the product stock to set
     */
    public void setStock(Integer stock) { this.stock = stock; }

    /**
     * Gets the product image URL.
     *
     * @return the product image URL
     */
    public String getImageUrl() { return imageUrl; }

    /**
     * Sets the product image URL.
     *
     * @param imageUrl the product image URL to set
     */
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }


}
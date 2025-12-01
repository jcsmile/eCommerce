package com.ecommerce.productservice.event;

/**
 * Event class for product stock updates.
 * <p>
 * Business rules:
 * 1. Used for Kafka messaging
 * 2. Contains product ID, new stock, and action
 *
 * @author JackyChen
 * @since 2025-04-01
 */
public class ProductStockEvent {
    /** The product ID. */
    private Long productId;
    /** The new stock quantity. */
    private Integer newStock;
    /** The action type (e.g. "CREATE", "UPDATE", "DELETE", "SOLD"). */
    private String action;

    /**
     * Default constructor.
     */
    public ProductStockEvent() {}

    /**
     * Constructor with all fields.
     *
     * @param productId the product ID
     * @param newStock the new stock quantity
     * @param action the action type
     */
    public ProductStockEvent(Long productId, Integer newStock, String action) {
        this.productId = productId;
        this.newStock = newStock;
        this.action = action;
    }

    public Long getProductId() {
        return productId;
    }
    public void setProductId(Long productId) {
        this.productId = productId;
    }
    public Integer getNewStock() {
        return newStock;
    }
    public void setNewStock(Integer newStock) {
        this.newStock = newStock;
    }
    public String getAction() {
        return action;
    }
    public void setAction(String action) {
        this.action = action;
    }
}

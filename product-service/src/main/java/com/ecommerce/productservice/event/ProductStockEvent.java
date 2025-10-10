package com.ecommerce.productservice.event;


public class ProductStockEvent {
    private Long productId;
    private Integer newStock;
    private String action;  // e.g. "CREATE", "UPDATE", "DELETE", "SOLD"

    public ProductStockEvent() {}
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

package de.ait.g_67_shop.dto.position;

import de.ait.g_67_shop.dto.product.ProductDto;

public class PositionDto {

    private Long id;
    private ProductDto product;
    private int quantity;

    public PositionDto() {
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public ProductDto getProduct() {
        return product;
    }

    public void setProduct(ProductDto product) {
        this.product = product;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return String.format("Position DTO: id - %d, product - %s, quantity - %d", id, product, quantity);
    }
}

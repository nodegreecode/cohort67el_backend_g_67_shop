package de.ait.g_67_shop.dto.product;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Product DTO for sending to Client")
public class ProductDto {

    @Schema(description = "Product unique identifier", example = "888")
    private Long id;

    private String title;

    private BigDecimal price;

    private String imageUrl;

    public ProductDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public String toString() {
        return String.format("Product DTO: id - %d, title - %s, price - %.2f", id, title, price);
    }
}

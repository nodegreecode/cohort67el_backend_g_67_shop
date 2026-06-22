package de.ait.g_67_shop.dto.customer;

import de.ait.g_67_shop.dto.cart.CartDto;

public class CustomerDto {
    private Long id;
    private String name;
    private CartDto cart;
    private String avatarUrl;

    public CustomerDto() {
    }

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

    public CartDto getCart() {
        return cart;
    }

    public void setCart(CartDto cart) {
        this.cart = cart;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    @Override
    public String toString() {
        return String.format("Customer DTO: id - %d, name - %s", id, name);
    }
}

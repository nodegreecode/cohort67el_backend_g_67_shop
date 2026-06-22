package de.ait.g_67_shop.dto.cart;

import de.ait.g_67_shop.dto.position.PositionDto;

import java.util.Set;

public class CartDto {

    private Long id;
    private Set<PositionDto> positions;

    public CartDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<PositionDto> getPositions() {
        return positions;
    }

    public void setPositions(Set<PositionDto> positions) {
        this.positions = positions;
    }

    @Override
    public String toString() {
        return String.format("Cart DTO: id - %d, positions - %s", id, positions);
    }
}

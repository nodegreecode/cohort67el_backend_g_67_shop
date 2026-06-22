package de.ait.g_67_shop.dto.position;

public class PositionUpdateDto {

    private int quantity;

    public PositionUpdateDto() {

    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return  String.format("Position Update DTO: quantity - %d", quantity);
    }
}

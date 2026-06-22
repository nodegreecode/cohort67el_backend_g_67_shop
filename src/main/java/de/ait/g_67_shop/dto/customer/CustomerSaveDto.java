package de.ait.g_67_shop.dto.customer;

public class CustomerSaveDto {
    private String name;

    public CustomerSaveDto() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return String.format("Customer save DTO:   name - %s", name);
    }
}

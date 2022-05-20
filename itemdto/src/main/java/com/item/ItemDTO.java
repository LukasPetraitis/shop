package com.item;

public class ItemDTO {

    private int id;
    private String price;
    private String name;
    private String description;
    private int amountInStorage;

    public ItemDTO() {
    }

    public ItemDTO(String price, String name, String description, int amountInStorage) {
        this.price = price;
        this.name = name;
        this.description = description;
        this.amountInStorage = amountInStorage;
    }

    public ItemDTO(int id, String price, String name, String description, int amountInStorage) {
        this.id = id;
        this.price = price;
        this.name = name;
        this.description = description;
        this.amountInStorage = amountInStorage;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
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

    public int getAmountInStorage() {
        return amountInStorage;
    }

    public void setAmountInStorage(int amountInStorage) {
        this.amountInStorage = amountInStorage;
    }
}

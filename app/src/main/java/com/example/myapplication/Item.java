package com.example.myapplication;

public class Item {

    private String id;
    private String name;
    private String description;
    private double price;
    private String ownerUid;
    private long createdAt;

    public Item() {
        // Required by Firestore.
    }

    public Item(
            String name,
            String description,
            double price,
            String ownerUid,
            long createdAt
    ) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.ownerUid = ownerUid;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name == null ? "" : name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description == null ? "" : description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getOwnerUid() {
        return ownerUid == null ? "" : ownerUid;
    }

    public void setOwnerUid(String ownerUid) {
        this.ownerUid = ownerUid;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}
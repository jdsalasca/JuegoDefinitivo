package com.juegodefinitivo.autobook.domain;

public record InventoryItem(String id, String name, String description, ItemType type, int power) {
}

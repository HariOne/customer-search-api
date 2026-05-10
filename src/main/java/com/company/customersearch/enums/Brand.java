package com.company.customersearch.enums;

import com.company.customersearch.exception.InvalidBrandException;

public enum Brand {
    ZOOMCAR("Zoomcar"),
    REVV("Revv"),
    MYCHOIZE("MyChoize"),
    MYLES("Myles");

    private final String displayName;

    Brand(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Brand fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new InvalidBrandException("Brand cannot be null or empty");
        }

        for (Brand brand : Brand.values()) {
            if (brand.displayName.equalsIgnoreCase(value.trim())) {
                return brand;
            }
        }

        throw new InvalidBrandException(
                String.format("Invalid brand: %s. Allowed brands are: Zoomcar, Revv, MyChoize, Myles", value));
    }
}

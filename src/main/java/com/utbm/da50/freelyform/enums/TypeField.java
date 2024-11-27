package com.utbm.da50.freelyform.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum TypeField {

    TEXT("text"),
    NUMBER("number"),
    DATE("date"),
    MULTIPLE_CHOICE("multiple_choice"),
    GEOLOCATION("geolocation");

    private final String type;

    TypeField(final String type) {
        this.type = type;
    }

    @JsonValue
    public String getType() {
        return type;
    }

    @JsonCreator
    public static TypeField fromString(String type) {
        for (TypeField field : TypeField.values()) {
            if (field.type.equalsIgnoreCase(type)) {
                return field;
            }
        }
        throw new IllegalArgumentException("Unknown type: " + type);
    }

}

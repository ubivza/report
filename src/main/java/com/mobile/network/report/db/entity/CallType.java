package com.mobile.network.report.db.entity;

import lombok.Getter;

public enum CallType {
    INCOMING("02"),
    OUTCOMING("01");

    @Getter
    private final String type;

    CallType(String type) {
        this.type = type;
    }
}

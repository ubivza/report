package com.mobile.network.report.db.entity.converter;

import com.mobile.network.report.db.entity.CallType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class CallTypeConverter implements AttributeConverter<CallType, String> {

    @Override
    public String convertToDatabaseColumn(CallType callType) {
        return callType == null ? null : callType.getType();
    }

    @Override
    public CallType convertToEntityAttribute(String type) {
        if (type == null) {
            return null;
        }
        for (CallType callType : CallType.values()) {
            if (callType.getType().equals(type)) {
                return callType;
            }
        }
        throw new IllegalArgumentException("Unknown CallType type: " + type);
    }
}

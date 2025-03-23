package com.mobile.network.report.model.outer;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CDRReportFileStatus {
    public static final String IN_PROGRESS = "In progress";
    public static final String SUCCESS = "Successfully";
    public static final String CANNOT_GENERATE_FILE = "Server failed to generate file for report.";
    public static final String UNEXPECTED_FAIL = "Unexpected fail";
}

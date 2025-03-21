package com.mobile.network.report.model.outer;

import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CDRFileReportDto {
    private UUID requestId;
}

package com.mobile.network.report.facade;

import com.mobile.network.report.model.inner.CDRRecordDto;
import com.mobile.network.report.service.api.CDRRecordGeneratorService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Фасад имитирующий работу коммутатора, создает рандомное количество CDR записей и сохраняет их в бд
 */
@Component
@RequiredArgsConstructor
public class CommutatorWorkFacade {

    private final CDRRecordGeneratorService service;

    public void generateAndSaveRecords() {
        List<CDRRecordDto> recordDtos = service.generateCDRRecords(2024);
        service.saveRecords(recordDtos);
    }
}

package com.mobile.network.report.service.impl;

import com.mobile.network.report.db.entity.CallType;
import com.mobile.network.report.model.inner.CDRRecordDto;
import com.mobile.network.report.model.outer.UDRReportDto;
import com.mobile.network.report.model.outer.UDRReportDto.CallDuration;
import com.mobile.network.report.service.api.UDRReportFormatterService;
import com.mobile.network.report.utils.DateTimeUtils;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import org.springframework.stereotype.Service;

/**
 * Класс ответчает за формирование UDR отчетов
 */
@Service
public class UDRReportFormatterServiceImpl implements UDRReportFormatterService {

    /**
     * Метод собирает CDR записи из базы в единый UDR отчет по запрошенному пользователю
     * @param phoneNumber номер телефона пользователя
     * @param records список записей в базе по его номеру где он выступает либо инициатором либо принимающим
     * @return сформированный UDR отчет
     */
    @Override
    public UDRReportDto buildUDRResponse(String phoneNumber, List<CDRRecordDto> records) {
        CallDurations callDurations = new CallDurations();

        for (CDRRecordDto record : records) {
            Duration callDuration = DateTimeUtils.calculateCallDuration(record.getCallStartTime(), record.getCallEndTime());

            if (callDuration == null) {
                continue;
            }

            if (record.getCallType() == CallType.INCOMING) {
                callDurations.addIncomingDuration(callDuration);
            } else {
                callDurations.addOutcomingDuration(callDuration);
            }
        }

        CallDuration incomingCallsTime = new CallDuration();
        incomingCallsTime.setTotalTime(callDurations.getIncomingDuration());

        CallDuration outcomingCallsTime = new CallDuration();
        outcomingCallsTime.setTotalTime(callDurations.getOutcomingDuration());

        return UDRReportDto.builder()
            .msisdn(phoneNumber)
            .incomingCall(incomingCallsTime)
            .outcomingCall(outcomingCallsTime)
            .build();
    }

    /**
     * Метод формирует сводный отчет по всем полдьзователям за определенный период
     * @param records записи из базы уже выбранные по периоду
     * @return список UDR отчетов по всем пользователям
     */
    @Override
    public List<UDRReportDto> buildAllUDRResponse(List<CDRRecordDto> records) {
        Map<String, CallDurations> durationsForNumbers = new HashMap<>();

        for (CDRRecordDto record : records) {
            Duration callDuration = DateTimeUtils.calculateCallDuration(record.getCallStartTime(), record.getCallEndTime());

            if (callDuration == null) {
                continue;
            }

            if (record.getCallType() == CallType.INCOMING) {
                String receiverPhoneNumber = record.getReceiverPhoneNumber();
                durationsForNumbers.computeIfAbsent(receiverPhoneNumber, x -> new CallDurations());
                durationsForNumbers.get(receiverPhoneNumber).addIncomingDuration(callDuration);
            } else {
                String callerPhoneNumber = record.getCallerPhoneNumber();
                durationsForNumbers.computeIfAbsent(callerPhoneNumber, x -> new CallDurations());
                durationsForNumbers.get(callerPhoneNumber).addOutcomingDuration(callDuration);
            }
        }

        return durationsForNumbers.entrySet().stream()
            .map(entry -> {
                String msisdn = entry.getKey();
                CallDurations callDurations = entry.getValue();

                CallDuration incomingCallsTime = new CallDuration();
                incomingCallsTime.setTotalTime(callDurations.getIncomingDuration());

                CallDuration outcomingCallsTime = new CallDuration();
                outcomingCallsTime.setTotalTime(callDurations.getOutcomingDuration());

                return UDRReportDto.builder()
                    .msisdn(msisdn)
                    .incomingCall(incomingCallsTime)
                    .outcomingCall(outcomingCallsTime)
                    .build();
            }).toList();
    }

    /**
     * Вспомогательный класс для подсчета времени разговора пользователей
     */
    @Getter
    private static class CallDurations {
        private Duration incomingDuration = Duration.ZERO;
        private Duration outcomingDuration = Duration.ZERO;

        public void addIncomingDuration(Duration duration) {
            this.incomingDuration = this.incomingDuration.plus(duration);
        }

        public void addOutcomingDuration(Duration duration) {
            this.outcomingDuration = this.outcomingDuration.plus(duration);
        }
    }
}

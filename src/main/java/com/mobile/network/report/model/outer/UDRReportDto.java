package com.mobile.network.report.model.outer;

import java.time.Duration;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UDRReportDto {
    private String msisdn;
    private CallDuration incomingCall;
    private CallDuration outcomingCall;

    @Getter
    public static class CallDuration {
        private String totalTime;

        public void setTotalTime(Duration duration) {
            if (duration == null) {
                this.totalTime = null;
                return;
            }
            long hours = duration.toHours();
            long minutes = duration.toMinutesPart();
            long seconds = duration.toSecondsPart();
            this.totalTime = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        }
    }
}

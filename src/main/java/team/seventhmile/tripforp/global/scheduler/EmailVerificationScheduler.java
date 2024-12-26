package team.seventhmile.tripforp.global.scheduler;

import java.time.LocalDateTime;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import team.seventhmile.tripforp.domain.user.dto.VerificationCode;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailVerificationScheduler {

    private final Map<String, VerificationCode> verificationCodes;

    @Scheduled(cron = "0 0 2 * * *")
    public void cleanExpiredCodes() {
        LocalDateTime now = LocalDateTime.now();

        int initialSize = verificationCodes.size();

        try {
            verificationCodes.entrySet()
                .removeIf(entry -> entry.getValue().getExp().isBefore(now));

            int removedCount = initialSize - verificationCodes.size();
            log.info("만료된 인증 코드 정리 완료. 삭제된 코드 수: {}", removedCount);
        } catch (Exception e) {
            log.error("인증 코드 정리 중 오류 발생", e);
        }
    }
}

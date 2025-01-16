package team.seventhmile.tripforp.global.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import team.seventhmile.tripforp.global.common.RateLimitInterceptor;

@Component
@RequiredArgsConstructor
@Slf4j
public class BucketScheduler {

    private final RateLimitInterceptor rateLimitInterceptor;

    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    private void cleanBuckets() {
        try {
            int initialSize = rateLimitInterceptor.count();
            rateLimitInterceptor.resetBucket();
            int removedCount = initialSize - rateLimitInterceptor.count();
            log.info("버킷 초기화 완료. 초기화된 버킷 수: {}", removedCount);
        } catch (Exception e) {
            log.error("버켓 초기화 중 오류 발생", e);
        }
    }

}

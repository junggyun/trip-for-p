package team.seventhmile.tripforp.global.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import team.seventhmile.tripforp.domain.refresh.repository.RefreshRepository;
import team.seventhmile.tripforp.global.jwt.JwtUtil;

@Component
@RequiredArgsConstructor
@Slf4j
public class RefreshScheduler {

    private final RefreshRepository refreshRepository;
    private final JwtUtil jwtUtil;

    @Scheduled(cron = "0 0 2 * * *", zone = "Asia/Seoul")
    public void cleanExpiredCodes() {
        long initialSize = refreshRepository.count();

        try {
            refreshRepository.findAll()
                .removeIf(refresh -> jwtUtil.isExpired(refresh.getToken()));

            long removedCount = initialSize - refreshRepository.count();;
            log.info("만료된 토큰 정리 완료. 삭제된 토큰 수: {}", removedCount);
        } catch (Exception e) {
            log.error("토큰 정리 중 오류 발생", e);
        }
    }

}

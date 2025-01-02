package team.seventhmile.tripforp.global.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableCaching
@Configuration
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        List<CaffeineCache> caches = new ArrayList<>();
        caches.add(buildRegionCache());
        caches.add(buildEmailCodeCache());

        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(caches);
        return cacheManager;
    }

    private CaffeineCache buildRegionCache() {
        return new CaffeineCache("regions",
            Caffeine.newBuilder()
                .maximumSize(300)
                .build());
    }

    private CaffeineCache buildEmailCodeCache() {
        return new CaffeineCache("emailCodes",
            Caffeine.newBuilder()
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .recordStats()
                .build());
    }
}

package team.seventhmile.tripforp.domain.spot.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import team.seventhmile.tripforp.domain.place.dto.GetPlaceCountResponse;
import team.seventhmile.tripforp.domain.place.dto.QGetPlaceCountResponse;
import team.seventhmile.tripforp.domain.place.entity.QPlace;
import team.seventhmile.tripforp.domain.spot.entity.QSpot;

@RequiredArgsConstructor
@Repository
public class SpotRepositoryImpl implements SpotRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QSpot qSpot = QSpot.spot;
    private final QPlace qPlace = QPlace.place;


    @Override
    @Cacheable(value = "places", key = "'popular'")
    public List<GetPlaceCountResponse> getPlaceCount(Pageable pageable) {
        return queryFactory
            .select(new QGetPlaceCountResponse(
                qPlace.id,
                qPlace.mapPlaceId,
                qSpot.count().as("count")
            ))
            .from(qSpot)
            .groupBy(qSpot.place.id)
            .orderBy(qSpot.count().desc(), qSpot.place.id.asc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();
    }
}

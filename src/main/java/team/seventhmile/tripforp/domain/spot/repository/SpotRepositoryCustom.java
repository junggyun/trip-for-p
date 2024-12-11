package team.seventhmile.tripforp.domain.spot.repository;

import java.util.List;
import org.springframework.data.domain.Pageable;
import team.seventhmile.tripforp.domain.place.dto.GetPlaceCountResponse;

public interface SpotRepositoryCustom {
    List<GetPlaceCountResponse> getPlaceCount(Pageable pageable);

}

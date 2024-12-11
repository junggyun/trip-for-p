package team.seventhmile.tripforp.domain.course.dto;

import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.seventhmile.tripforp.domain.spot.dto.UpdateSpotRequest;

@NoArgsConstructor
@Getter
public class UpdateCourseRequest {

    private LocalDate startDate;
    private LocalDate endDate;
    private String title;
    private String area;
    private List<UpdateSpotRequest> spots;

    @Builder
    public UpdateCourseRequest(LocalDate startDate, LocalDate endDate, String title, String area,
        List<UpdateSpotRequest> spots) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.title = title;
        this.area = area;
        this.spots = spots;
    }
}

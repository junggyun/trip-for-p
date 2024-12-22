package team.seventhmile.tripforp.domain.course.dto;

import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.seventhmile.tripforp.domain.course.entity.Course;
import team.seventhmile.tripforp.domain.spot.dto.CourseGetItemDto;

@NoArgsConstructor
@Getter
public class GetCourseResponse {

    private Long id;
    private String writer;
    private String title;
    private String province;
    private String city;
    private LocalDate startDate;
    private LocalDate endDate;
    private int views;
    private int likeCount;
    private List<CourseGetItemDto> spots;
    private ZonedDateTime createdAt;

    @QueryProjection
    public GetCourseResponse(Course course, int likeCount) {
        this.id = course.getId();
        this.writer = course.getCreator().getNickname();
        this.title = course.getTitle();
        this.province = course.getRegion().getProvince().getName();
        this.city = course.getRegion().getCity();
        this.startDate = course.getStartDate();
        this.endDate = course.getEndDate();
        this.views = course.getViews();
        this.likeCount = likeCount;
        this.spots = course.getSpots().stream()
            .map(CourseGetItemDto::new)
            .toList();
        this.createdAt = course.getCreatedAt();
    }
}

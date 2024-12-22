package team.seventhmile.tripforp.domain.course.dto;

import com.querydsl.core.annotations.QueryProjection;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.seventhmile.tripforp.domain.course.entity.Course;

@NoArgsConstructor
@Getter
public class GetCourseListResponse {

    private Long id;
    private String writer;
    private String title;
    private String province;
    private String city;
    private long duration;
    private ZonedDateTime createdAt;
    private int views;
    private long likes;

    @QueryProjection
    public GetCourseListResponse(Course course, long likes) {
        this.id = course.getId();
        this.writer = course.getCreator().getNickname();
        this.title = course.getTitle();
        this.province = course.getRegion().getProvince().getName();
        this.city = course.getRegion().getCity();
        this.duration = ChronoUnit.DAYS.between(course.getStartDate(), course.getEndDate());
        this.createdAt = course.getCreatedAt();
        this.views = course.getViews();
        this.likes = likes;
    }
}

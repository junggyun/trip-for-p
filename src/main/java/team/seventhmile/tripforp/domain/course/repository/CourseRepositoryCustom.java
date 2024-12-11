package team.seventhmile.tripforp.domain.course.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import team.seventhmile.tripforp.domain.course.dto.GetCourseListResponse;
import team.seventhmile.tripforp.domain.course.entity.Course;

public interface CourseRepositoryCustom {

    Page<GetCourseListResponse> getCourses(String title, Pageable pageable);

    Page<GetCourseListResponse> getMyCourses(String email, Pageable pageable);

    Course findCourse(Long id);

}

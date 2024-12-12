package team.seventhmile.tripforp.domain.course.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.seventhmile.tripforp.domain.course.dto.CreateCourseRequest;
import team.seventhmile.tripforp.domain.course.dto.CreateCourseResponse;
import team.seventhmile.tripforp.domain.course.dto.GetCourseListResponse;
import team.seventhmile.tripforp.domain.course.dto.GetCourseResponse;
import team.seventhmile.tripforp.domain.course.dto.GetPopularCourseResponse;
import team.seventhmile.tripforp.domain.course.dto.UpdateCourseRequest;
import team.seventhmile.tripforp.domain.course.dto.UpdateCourseResponse;
import team.seventhmile.tripforp.domain.course.entity.Course;
import team.seventhmile.tripforp.domain.course.repository.CourseRepository;
import team.seventhmile.tripforp.domain.courseLike.repository.CourseLikeRepository;
import team.seventhmile.tripforp.domain.spot.dto.CreateSpotRequest;
import team.seventhmile.tripforp.domain.spot.service.SpotService;
import team.seventhmile.tripforp.domain.user.entity.User;
import team.seventhmile.tripforp.domain.user.repository.UserRepository;
import team.seventhmile.tripforp.domain.user.service.CustomUserDetails;
import team.seventhmile.tripforp.global.exception.ResourceNotFoundException;
import team.seventhmile.tripforp.global.exception.UnauthorizedAccessException;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final SpotService spotService;
    private final CourseLikeRepository courseLikeRepository;
    private final UserRepository userRepository;

    @Transactional
    public CreateCourseResponse createCourse(CreateCourseRequest request, CustomUserDetails user) {

        User findUser = userRepository.findByEmail(user.getUsername())
            .orElseThrow(() -> new ResourceNotFoundException(User.class));

        Course course = Course.builder()
            .user(findUser)
            .startDate(request.getStartDate())
            .endDate(request.getEndDate())
            .title(request.getTitle())
            .build();
        courseRepository.save(course);

        for (CreateSpotRequest SpotRequest : request.getSpots()) {
            spotService.createSpot(course, SpotRequest);
        }
        return new CreateCourseResponse(course.getId());
    }

    @Transactional
    public UpdateCourseResponse updateCourse(Long id, UpdateCourseRequest request, UserDetails user) {

        Course course = courseRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(Course.class, id));

        checkUpdateAuthorization(user, course);

        course.updateCourse(request);

        spotService.manageSpots(course, request.getSpots());

        return new UpdateCourseResponse(id);
    }

    @Transactional
    public void deleteCourse(Long id, UserDetails user) {
        Course course = courseRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(Course.class, id));

        checkDeleteAuthorization(user, course);

        courseRepository.delete(course);
    }

    @Transactional
    public GetCourseResponse getCourseById(Long id) {

        Course course = courseRepository.findCourse(id);
        if (course == null) {
            throw new ResourceNotFoundException(Course.class, id);
        }
        int likeCount = courseLikeRepository.countByCourseId(id);
        course.increaseViews();

        return new GetCourseResponse(course, likeCount);
    }

    public Page<GetCourseListResponse> getCourseList(String title, Pageable pageable) {
        return courseRepository.getCourses(title, pageable);
    }

    public Page<GetCourseListResponse> getMyCourseList(UserDetails user, Pageable pageable) {
        return courseRepository.getMyCourses(user.getUsername(), pageable);
    }

    /**
     * 수정, 삭제 시 작성자 본인이 맞는지 검증
     */
    private void checkUpdateAuthorization(UserDetails user, Course course) {
        if (!user.getUsername().equals(course.getCreator().getEmail())) {
            System.out.println(user.getAuthorities());
            throw new UnauthorizedAccessException(Course.class);
        }
    }

    private void checkDeleteAuthorization(UserDetails user, Course course) {
        if (!user.getUsername().equals(course.getCreator().getEmail()) && !user.getAuthorities().contains("ROLE_ADMIN")) {
            throw new UnauthorizedAccessException(Course.class);
        }
    }

//    public List<GetPopularCourseResponse> getPopularCourseList() {
//        return courseRepository.findPopularCourses(PageRequest.of(0, 6));
//    }
}

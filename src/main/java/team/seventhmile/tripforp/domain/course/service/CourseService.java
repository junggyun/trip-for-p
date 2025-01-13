package team.seventhmile.tripforp.domain.course.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.seventhmile.tripforp.domain.course.dto.CreateCourseRequest;
import team.seventhmile.tripforp.domain.course.dto.CreateCourseResponse;
import team.seventhmile.tripforp.domain.course.dto.GetCourseListResponse;
import team.seventhmile.tripforp.domain.course.dto.GetCourseResponse;
import team.seventhmile.tripforp.domain.course.dto.UpdateCourseRequest;
import team.seventhmile.tripforp.domain.course.dto.UpdateCourseResponse;
import team.seventhmile.tripforp.domain.course.entity.Course;
import team.seventhmile.tripforp.domain.course.repository.CourseRepository;
import team.seventhmile.tripforp.domain.courseLike.repository.CourseLikeRepository;
import team.seventhmile.tripforp.domain.region.entity.Province;
import team.seventhmile.tripforp.domain.region.entity.Region;
import team.seventhmile.tripforp.domain.region.repository.RegionRepository;
import team.seventhmile.tripforp.domain.spot.dto.CreateSpotRequest;
import team.seventhmile.tripforp.domain.spot.service.SpotService;
import team.seventhmile.tripforp.domain.user.entity.User;
import team.seventhmile.tripforp.domain.user.repository.UserRepository;
import team.seventhmile.tripforp.domain.user.service.CustomUserDetails;
import team.seventhmile.tripforp.global.common.PageResponse;
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
    private final RegionRepository regionRepository;

    @Transactional
    public CreateCourseResponse createCourse(CreateCourseRequest request, CustomUserDetails user) {

        User findUser = userRepository.findByEmail(user.getUsername())
            .orElseThrow(() -> new ResourceNotFoundException(User.class));

        Region region = regionRepository.findByProvinceAndCity(
                Province.findByName(request.getProvince()),
                request.getCity())
            .orElseThrow(() -> new ResourceNotFoundException(Region.class));

        Course course = Course.builder()
            .user(findUser)
            .startDate(request.getStartDate())
            .endDate(request.getEndDate())
            .region(region)
            .title(request.getTitle())
            .build();
        courseRepository.save(course);

        for (CreateSpotRequest SpotRequest : request.getSpots()) {
            spotService.createSpot(course, SpotRequest);
        }
        return new CreateCourseResponse(course.getId());
    }

    @Transactional
    public UpdateCourseResponse updateCourse(Long id, UpdateCourseRequest request,
        UserDetails user) {

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
    public GetCourseResponse getCourse(Long id) {

        Course course = courseRepository.findCourse(id);
        if (course == null) {
            throw new ResourceNotFoundException(Course.class, id);
        }
        long likeCount = courseLikeRepository.countByCourseId(id);
        course.increaseViews();

        return new GetCourseResponse(course, likeCount);
//        return courseRepository.getCourse(id);
    }

    public PageResponse<GetCourseListResponse> getCourseList(String keyword, Pageable pageable) {
        return new PageResponse<>(courseRepository.getCourses(keyword, pageable));
    }

    public PageResponse<GetCourseListResponse> getMyCourseList(UserDetails user, Pageable pageable) {
        return new PageResponse<>(courseRepository.getMyCourses(user.getUsername(), pageable));
    }

    private void checkUpdateAuthorization(UserDetails user, Course course) {
        if (!user.getUsername().equals(course.getCreator().getEmail())) {
            System.out.println(user.getAuthorities());
            throw new UnauthorizedAccessException(Course.class);
        }
    }

    private void checkDeleteAuthorization(UserDetails user, Course course) {
        if (!user.getUsername().equals(course.getCreator().getEmail()) && !user.getAuthorities()
            .contains("ROLE_ADMIN")) {
            throw new UnauthorizedAccessException(Course.class);
        }
    }
}

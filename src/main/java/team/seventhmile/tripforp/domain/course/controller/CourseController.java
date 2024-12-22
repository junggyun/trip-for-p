package team.seventhmile.tripforp.domain.course.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import team.seventhmile.tripforp.domain.course.dto.CreateCourseRequest;
import team.seventhmile.tripforp.domain.course.dto.CreateCourseResponse;
import team.seventhmile.tripforp.domain.course.dto.GetCourseListResponse;
import team.seventhmile.tripforp.domain.course.dto.GetCourseResponse;
import team.seventhmile.tripforp.domain.course.dto.UpdateCourseRequest;
import team.seventhmile.tripforp.domain.course.dto.UpdateCourseResponse;
import team.seventhmile.tripforp.domain.course.service.CourseService;
import team.seventhmile.tripforp.domain.place.dto.GetPlaceCountResponse;
import team.seventhmile.tripforp.domain.spot.service.SpotService;
import team.seventhmile.tripforp.domain.user.service.CustomUserDetails;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;
    private final SpotService spotService;

    /**
     * 여행 코스를 등록합니다.
     *
     * @param request 여행 코스 생성에 필요한 세부 정보가 포함된 요청 본문
     * @param user 여행 코스를 생성하는 인증된 사용자
     * @return 생성된 여행 코스의 id를 포함한 ResponseEntity
     */
    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public ResponseEntity<CreateCourseResponse> createCourse(
        @Valid @RequestBody CreateCourseRequest request,
        @AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(courseService.createCourse(request, user));
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/{id}")
    public ResponseEntity<UpdateCourseResponse> updateCourse(
        @PathVariable("id") Long id,
        @RequestBody UpdateCourseRequest request,
        @AuthenticationPrincipal UserDetails user
    ) {
        return ResponseEntity.ok(courseService.updateCourse(id, request, user));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(
        @PathVariable("id") Long id,
        @AuthenticationPrincipal UserDetails user
    ) {
        courseService.deleteCourse(id, user);
        return ResponseEntity
            .status(HttpStatus.NO_CONTENT)
            .build();
    }

    @GetMapping
    public ResponseEntity<Page<GetCourseListResponse>> getCourseList(
        @RequestParam(value = "keyword", required = false) String keyword,
        Pageable pageable
    ) {
        return ResponseEntity.ok(courseService.getCourseList(keyword, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetCourseResponse> getCourseDetail(
        @PathVariable("id") Long id
    ) {
        GetCourseResponse CourseDto = courseService.getCourseById(id);
        return ResponseEntity.ok(CourseDto);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/me")
    public ResponseEntity<Page<GetCourseListResponse>> getMyCourseList(
        @AuthenticationPrincipal UserDetails user,
        Pageable pageable) {
        return ResponseEntity.ok(courseService.getMyCourseList(user, pageable));
    }

    @GetMapping("/popular-places")
    public ResponseEntity<List<GetPlaceCountResponse>> getPopularPlaces() {
        return ResponseEntity.ok(spotService.getPlaceCount());
    }

//    @GetMapping("/popular-Courses")
//    public ResponseEntity<List<GetPopularCourseResponse>> getPopularCourses() {
//        return ResponseEntity.ok(courseService.getPopularCourseList());
//    }
}

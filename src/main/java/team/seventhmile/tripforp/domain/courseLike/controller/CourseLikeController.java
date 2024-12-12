package team.seventhmile.tripforp.domain.courseLike.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import team.seventhmile.tripforp.domain.course.dto.GetCourseListResponse;
import team.seventhmile.tripforp.domain.courseLike.dto.CourseLikeDto;
import team.seventhmile.tripforp.domain.courseLike.dto.CourseLikeResponseDto;
import team.seventhmile.tripforp.domain.courseLike.service.CourseLikeService;

@RestController
@RequestMapping("/api/course-likes")
public class CourseLikeController {

  private final CourseLikeService courseLikeService;

  public CourseLikeController(CourseLikeService courseLikeService) {
    this.courseLikeService = courseLikeService;
  }

  // 좋아요 또는 좋아요 취소를 처리하는 엔드포인트
  @PostMapping("/courses/{id}")
  public ResponseEntity<CourseLikeResponseDto> toggleLikeCourse(
      @AuthenticationPrincipal UserDetails userDetails,
      @PathVariable(name = "id") Long id) {

    // userDetails에서 이메일을 추출하여 서비스에 전달
    CourseLikeResponseDto responseDto = courseLikeService.toggleLikeCourse(userDetails.getUsername(),
        id);
    return new ResponseEntity<>(responseDto, HttpStatus.OK);
  }

  //[마이페이지]내가 좋아요한 글 목록 조회
  @PreAuthorize("hasRole('USER')")
  @GetMapping("/me")
  public ResponseEntity<Page<GetCourseListResponse>> getMyFavCourseList(
      @AuthenticationPrincipal UserDetails user,
      Pageable pageable) {
    return ResponseEntity.ok(courseLikeService.getMyFavCourseList(user, pageable));
  }

  @PreAuthorize("hasRole('USER')")
  @GetMapping("/check")
  public ResponseEntity<Boolean> checkCourseLike(
      @AuthenticationPrincipal UserDetails user,
      @RequestParam(name = "courseId") Long courseId
  ) {
    return ResponseEntity.ok(courseLikeService.checkCourseLike(user.getUsername(), courseId));
  }
}

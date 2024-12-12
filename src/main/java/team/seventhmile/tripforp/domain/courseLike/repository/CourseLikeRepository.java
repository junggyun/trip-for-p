package team.seventhmile.tripforp.domain.courseLike.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import team.seventhmile.tripforp.domain.course.entity.Course;
import team.seventhmile.tripforp.domain.courseLike.entity.CourseLike;

@Repository
public interface CourseLikeRepository extends JpaRepository<CourseLike, Long> {

    CourseLike findByUserIdAndCourseId(Long userId, Long courseId);

    int countByCourseId(Long courseId); // 좋아요 개수 계산 메서드

    // [마이페이지]내가 좋아요한 여행코스 게시글 목록 조회
    @Query("SELECT pl.course FROM CourseLike pl WHERE pl.user.email = :email")
    Page<Course> findCoursesByUserEmail(@Param("email") String email, Pageable pageable);

    Boolean existsCourseLikeByUserIdAndCourseId(Long userId, Long courseId);
}

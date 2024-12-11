package team.seventhmile.tripforp.domain.course.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import team.seventhmile.tripforp.domain.course.dto.GetCourseListResponse;
import team.seventhmile.tripforp.domain.course.dto.QGetCourseListResponse;
import team.seventhmile.tripforp.domain.course.entity.Course;
import team.seventhmile.tripforp.domain.course.entity.QCourse;
import team.seventhmile.tripforp.domain.courseLike.entity.QCourseLike;
import team.seventhmile.tripforp.domain.spot.entity.QSpot;

@RequiredArgsConstructor
@Repository
public class CourseRepositoryImpl implements CourseRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QCourse qCourse = QCourse.course;
    private final QSpot qSpot = QSpot.spot;
    private final QCourseLike qCourseLike = QCourseLike.courseLike;

    @Override
    public Page<GetCourseListResponse> getCourses(String title, Pageable pageable) {
        List<GetCourseListResponse> courses = queryFactory
            .select(new QGetCourseListResponse(
                qCourse,
                JPAExpressions
                    .select(qCourseLike.count())
                    .from(qCourseLike)
                    .where(qCourseLike.course.eq(qCourse))
            ))
            .from(qCourse)
            .where(containsTitle(title))
            .leftJoin(qCourse.creator).fetchJoin()
            .orderBy(qCourse.createdAt.desc())
            .limit(pageable.getPageSize())
            .offset(pageable.getOffset())
            .fetch();

        JPAQuery<Long> count = queryFactory
            .select(qCourse.count())
            .from(qCourse)
            .where(containsTitle(title));

        return PageableExecutionUtils.getPage(courses, pageable, count::fetchOne);
    }

    @Override
    public Page<GetCourseListResponse> getMyCourses(String email, Pageable pageable) {
        List<GetCourseListResponse> courses = queryFactory
            .select(new QGetCourseListResponse(
                qCourse,
                JPAExpressions
                    .select(qCourseLike.count())
                    .from(qCourseLike)
                    .where(qCourseLike.course.eq(qCourse))
            ))
            .from(qCourse)
            .where(qCourse.creator.email.eq(email))
            .leftJoin(qCourse.creator).fetchJoin()
            .leftJoin(qCourse.courseLikes)
            .orderBy(qCourse.createdAt.desc())
            .limit(pageable.getPageSize())
            .offset(pageable.getOffset())
            .fetch();

        JPAQuery<Long> count = queryFactory
            .select(qCourse.count())
            .from(qCourse)
            .where(qCourse.creator.email.eq(email));

        return PageableExecutionUtils.getPage(courses, pageable, count::fetchOne);
    }

    @Override
    public Course findCourse(Long id) {
        return queryFactory
            .selectFrom(qCourse)
            .leftJoin(qCourse.creator).fetchJoin()
            .leftJoin(qCourse.spots, qSpot).fetchJoin()
            .leftJoin(qSpot.place).fetchJoin()
            .where(qCourse.id.eq(id))
            .fetchOne();
    }

    private BooleanExpression containsTitle(String title) {
        if (title == null || title.isEmpty()) {
            return null;
        }
        return qCourse.title.contains(title);
    }
}

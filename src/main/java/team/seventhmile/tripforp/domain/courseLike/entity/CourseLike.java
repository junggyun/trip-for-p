package team.seventhmile.tripforp.domain.courseLike.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import team.seventhmile.tripforp.domain.course.entity.Course;
import team.seventhmile.tripforp.domain.user.entity.User;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "course_likes",
    uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "course_id"}))
public class CourseLike {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id; // 좋아요 엔티티의 고유 ID

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user; // 좋아요한 사용자에 대한 외래 키

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "course_id", nullable = false)
  private Course course; // 좋아요된 여행 코스에 대한 외래 키
}

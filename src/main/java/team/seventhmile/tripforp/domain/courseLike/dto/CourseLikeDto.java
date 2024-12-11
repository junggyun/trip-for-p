package team.seventhmile.tripforp.domain.courseLike.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import team.seventhmile.tripforp.domain.user.entity.User;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseLikeDto {
    private Long id; // 좋아요의 ID
    private User userId; // 좋아요한 사용자의 ID
    private Long courseId; // 좋아요된 여행 코스의 ID
}

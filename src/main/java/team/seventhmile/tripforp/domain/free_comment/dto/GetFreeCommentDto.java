package team.seventhmile.tripforp.domain.free_comment.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import team.seventhmile.tripforp.domain.free_comment.entity.FreeComment;

@NoArgsConstructor
@Getter
public class GetFreeCommentDto {

    private Long id;
    private String author;
    private String content;

    public GetFreeCommentDto(FreeComment freeComment) {
        this.id = freeComment.getId();
        this.author = freeComment.getAuthor().getNickname();
        this.content = freeComment.getContent();
    }
}
package team.seventhmile.tripforp.domain.review_comment.controller;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.bind.annotation.RestController;
import team.seventhmile.tripforp.domain.review_comment.dto.ReviewCommentDto;
import team.seventhmile.tripforp.domain.review_comment.service.ReviewCommentService;
import team.seventhmile.tripforp.domain.review_post.entity.ReviewPost;
import team.seventhmile.tripforp.domain.review_post.service.ReviewPostService;
import team.seventhmile.tripforp.domain.user.entity.User;

@RestController
@RequestMapping("/api/review-posts/{postId}/comments")
public class ReviewCommentController {

	private final ReviewCommentService reviewCommentService;
	private final ReviewPostService reviewPostService;

	@Autowired
	public ReviewCommentController(ReviewCommentService reviewCommentService,
		ReviewPostService reviewPostService) {
		this.reviewCommentService = reviewCommentService;
		this.reviewPostService = reviewPostService;
	}

	@GetMapping
	public ResponseEntity<List<ReviewCommentDto>> getReviewComments(@PathVariable Long postId) {
		ReviewPost reviewPost = reviewPostService.getReviewPostEntity(postId);
		List<ReviewCommentDto> reviewComments = reviewCommentService.getCommentsByPost(reviewPost);
		return ResponseEntity.ok(reviewComments);
	}

	@PreAuthorize("hasRole('USER')")
	@PostMapping
	public ResponseEntity<ReviewCommentDto> createReviewComment(
		@PathVariable Long postId,
		@Valid @RequestBody ReviewCommentDto commentDto,
		@AuthenticationPrincipal UserDetails user) {
		ReviewPost reviewPost = reviewPostService.getReviewPostEntity(postId);
		ReviewCommentDto createdComment = reviewCommentService.createComment(reviewPost, commentDto,
			user);
		return ResponseEntity.status(HttpStatus.CREATED).body(createdComment);
	}

	@PreAuthorize("hasRole('USER')")
	@PutMapping("/{commentId}")
	public ResponseEntity<ReviewCommentDto> updateReviewComment(
		@PathVariable Long postId,
		@PathVariable Long commentId,
		@Valid @RequestBody ReviewCommentDto commentDto,
		@AuthenticationPrincipal UserDetails user) {
		ReviewCommentDto updatedComment = reviewCommentService.updateComment(commentId, commentDto,
			user);
		return ResponseEntity.ok(updatedComment);
	}

	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	@DeleteMapping("/{commentId}")
	public ResponseEntity<Void> deleteReviewComment(
		@PathVariable Long postId,
		@PathVariable Long commentId,
		@AuthenticationPrincipal UserDetails user) {
		reviewCommentService.deleteComment(commentId, user);
		return ResponseEntity.noContent().build();
	}

}

package org.kdcoder.redditclone.controller;

import java.util.List;

import org.kdcoder.redditclone.dto.CommentDto;
import org.kdcoder.redditclone.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/comments/")
@AllArgsConstructor
public class CommentController {

	private final CommentService commentService;
	
	@PostMapping
	public ResponseEntity<Void> createPost(@RequestBody CommentDto commentDto) {
		commentService.createPost(commentDto);
		return new ResponseEntity<>(HttpStatus.CREATED);
	}
	
	@GetMapping("/by-post/{postId}")
    public ResponseEntity<List<CommentDto>> getAllCommentsForPost(@PathVariable Long postId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(commentService.getAllComment(postId));
    }
	
	@GetMapping("/by-user/{username}")
	public ResponseEntity<List<CommentDto>> getAllCommentsForUser(@PathVariable String username) {
		return ResponseEntity.status(HttpStatus.OK).body(commentService.getAllCommentForUser(username));
	}
}

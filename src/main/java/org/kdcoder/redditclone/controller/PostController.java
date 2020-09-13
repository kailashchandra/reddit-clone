package org.kdcoder.redditclone.controller;

import java.util.List;

import org.kdcoder.redditclone.dto.PostRequest;
import org.kdcoder.redditclone.dto.PostResponse;
import org.kdcoder.redditclone.service.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/posts/")
@AllArgsConstructor
public class PostController {

	private final PostService postService;
	
	@PostMapping
	public ResponseEntity<Void> createPost(@RequestBody PostRequest postRequest) {
		postService.createPost(postRequest);
		return new ResponseEntity<Void>(HttpStatus.CREATED);
	}
	
	@GetMapping("/")
	public ResponseEntity<List<PostResponse>> getAllPost() {
		return ResponseEntity.status(HttpStatus.OK).body(postService.getAllPost());
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<PostResponse> getPost(@PathVariable Long id) {
		return ResponseEntity.status(HttpStatus.OK).body(postService.getPost(id));
	}
	
	@GetMapping("/by-subreddit/{id}")
	public ResponseEntity<List<PostResponse>> getPostBySubreddit(@PathVariable Long id) {
		return ResponseEntity.status(HttpStatus.OK).body(postService.getPostBySubreddit(id));
	}
	
	@GetMapping("/by-user/{name}")
	public ResponseEntity<List<PostResponse>> getPostByUsername(@PathVariable String name) {
		return ResponseEntity.status(HttpStatus.OK).body(postService.getPostByUsername(name));
	}
}

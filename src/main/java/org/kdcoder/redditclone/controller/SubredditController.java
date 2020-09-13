package org.kdcoder.redditclone.controller;

import java.util.List;

import javax.websocket.server.PathParam;

import org.kdcoder.redditclone.dto.SubredditDto;
import org.kdcoder.redditclone.service.SubredditService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/subreddit")
@AllArgsConstructor
@Slf4j
public class SubredditController {
	
	private final SubredditService subRedditService;
	
	@PostMapping
	public ResponseEntity<SubredditDto> create(@RequestBody SubredditDto subredditDto) {
		return ResponseEntity.status(HttpStatus.CREATED).body(subRedditService.create(subredditDto));
	}
	
	@GetMapping
	public ResponseEntity<List<SubredditDto>> getAll() {
		return ResponseEntity.status(HttpStatus.OK).body(subRedditService.getAll());
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<SubredditDto> getSubreddit(@PathVariable Long id) {
		return ResponseEntity.status(HttpStatus.OK).body(subRedditService.getSubreddit(id));
	}
}

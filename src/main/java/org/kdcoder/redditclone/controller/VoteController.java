package org.kdcoder.redditclone.controller;

import org.kdcoder.redditclone.dto.VoteDto;
import org.kdcoder.redditclone.service.VoteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/votes/")
@AllArgsConstructor
public class VoteController {

	private final VoteService voteService;
	
	@PostMapping
	public ResponseEntity<Void> createVote(@RequestBody VoteDto voteDto) {
		voteService.createVote(voteDto);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}

package org.kdcoder.redditclone.service;

import java.util.Optional;

import org.kdcoder.redditclone.dto.VoteDto;
import org.kdcoder.redditclone.exception.SpringRedditException;
import org.kdcoder.redditclone.model.Post;
import org.kdcoder.redditclone.model.Vote;
import org.kdcoder.redditclone.model.VoteType;
import org.kdcoder.redditclone.repository.PostRepository;
import org.kdcoder.redditclone.repository.VoteRepository;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class VoteService {
	
	private final PostRepository postRepository;
	private final VoteRepository voteRepository;
	private final AuthService authService;
	
	public void createVote(VoteDto dto) {
		Post post = postRepository.findById(dto.getPostId()).orElseThrow(() -> new SpringRedditException("Post not found for id : "+dto.getPostId()));
		Optional<Vote> voteOptional = voteRepository.findTopByPostAndUserOrderByVoteIdDesc(post, authService.getCurrentUser());
		if(voteOptional.isPresent() && voteOptional.get().getVoteType().equals(dto.getVoteType())) {
			throw new SpringRedditException("You have already "+dto.getVoteType()+ "'d for this post");
		}
		if(VoteType.UPVOTE.equals(dto.getVoteType())) {
			post.setVoteCount(post.getVoteCount() + 1);
		}
		if(VoteType.DOWNVOTE.equals(dto.getVoteType())) {
			post.setVoteCount(post.getVoteCount() - 1);
		}
		voteRepository.save(mapToVote(dto, post));
		postRepository.save(post);
	}

	private Vote mapToVote(VoteDto dto, Post post) {
		return Vote.builder()
				.voteType(dto.getVoteType())
				.post(post)
				.user(authService.getCurrentUser())
				.build();
	}
}

package org.kdcoder.redditclone.mapper;

import java.util.List;
import java.util.Optional;

import org.kdcoder.redditclone.dto.PostRequest;
import org.kdcoder.redditclone.dto.PostResponse;
import org.kdcoder.redditclone.model.Comment;
import org.kdcoder.redditclone.model.Post;
import org.kdcoder.redditclone.model.Subreddit;
import org.kdcoder.redditclone.model.User;
import org.kdcoder.redditclone.model.Vote;
import org.kdcoder.redditclone.model.VoteType;
import org.kdcoder.redditclone.repository.CommentRepository;
import org.kdcoder.redditclone.repository.VoteRepository;
import org.kdcoder.redditclone.service.AuthService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.marlonlom.utilities.timeago.TimeAgo;

@Mapper(componentModel = "spring")
public abstract class PostMapper {
	
	@Autowired
	private CommentRepository commentRepository;
	@Autowired
	private VoteRepository voteRepository;
	@Autowired
	private AuthService authService;
	
	@Mapping(target = "createdDate", expression = "java(java.time.Instant.now())")
	@Mapping(target = "description", source = "postRequest.description")
	@Mapping(target = "subreddit", source = "subreddit")
	@Mapping(target = "user", source = "user")
	@Mapping(target = "voteCount", constant = "0")
	public abstract Post map(PostRequest postRequest, Subreddit subreddit, User user);
	
	@Mapping(target = "id", source = "postId")
	@Mapping(target = "subredditName", source = "subreddit.name")
	@Mapping(target = "userName", source = "user.username")
	@Mapping(target = "commentCount", expression = "java(commentCount(post))")
	@Mapping(target = "duration", expression = "java(getDuration(post))")
	@Mapping(target = "upVote", expression = "java(isPostUpVoted(post))")
	@Mapping(target = "downVote", expression = "java(isPostDownVoted(post))")
	public abstract PostResponse mapToDto(Post post);
	
	Integer commentCount(Post post) { 
		List<Comment> comments = commentRepository.findByPost(post);
		if(comments != null) return comments.size();
		return 0;
	}
	
	String getDuration(Post post) {
		if(post != null) {
			return TimeAgo.using(post.getCreatedDate().toEpochMilli());
		}
		return null;
	}
	
	boolean isPostUpVoted(Post post) { return checkVoteType(post, VoteType.UPVOTE);}
	
	boolean isPostDownVoted(Post post) { return checkVoteType(post, VoteType.DOWNVOTE);}
	
	private boolean checkVoteType(Post post, VoteType voteType) {
		if(authService.isLoggedIn()) {
			Optional<Vote> voteForPostByUser = voteRepository.findTopByPostAndUserOrderByVoteIdDesc(post, authService.getCurrentUser());
			return voteForPostByUser.filter(vote -> vote.getVoteType().equals(voteType)).isPresent();
		}
		return false;
	}
}

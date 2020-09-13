package org.kdcoder.redditclone.service;

import java.util.List;
import java.util.stream.Collectors;

import org.kdcoder.redditclone.dto.CommentDto;
import org.kdcoder.redditclone.exception.SpringRedditException;
import org.kdcoder.redditclone.mapper.CommentMapper;
import org.kdcoder.redditclone.model.Comment;
import org.kdcoder.redditclone.model.NotificationEmail;
import org.kdcoder.redditclone.model.Post;
import org.kdcoder.redditclone.model.User;
import org.kdcoder.redditclone.repository.CommentRepository;
import org.kdcoder.redditclone.repository.PostRepository;
import org.kdcoder.redditclone.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CommentService {

	private final PostRepository postRepository;
	private final AuthService authService;
	private final CommentMapper commentMapper;
	private final CommentRepository commentRepository;
	private final MailContentBuilder contentBuilder;
	private final MailService mailService;
	private final UserRepository userRepository;
	
	@Transactional
	public void createPost(CommentDto commentDto) {
		Post post = postRepository.findById(commentDto.getPostId()).orElseThrow(() ->
					new SpringRedditException("Post not found for id : "+commentDto.getPostId()));
		Comment comment = commentMapper.map(commentDto, post, authService.getCurrentUser());
		commentRepository.save(comment);
		
//		String message = contentBuilder.build(post.getUser().getUsername()+" posted a comment on your post "+post.getUrl());
		sendCommentNotification(post.getUser().getUsername()+" posted a comment on your post "+post.getUrl(), post.getUser());
	}

	private void sendCommentNotification(String message, User user) {
		mailService.sendMail(new NotificationEmail(user.getUsername() + " commented on your post", user.getEmail(), message));
		
	}

	public List<CommentDto> getAllComment(Long id) {
		Post post = postRepository.findById(id).orElseThrow(() -> 
			new SpringRedditException("Post not found for id : "+id));
		List<Comment> comments = commentRepository.findByPost(post);
		return comments.stream().map(commentMapper::mapToDto).collect(Collectors.toList());
	}

	public List<CommentDto> getAllCommentForUser(String username) {
		User user = userRepository.findByUsername(username).orElseThrow(() -> 
					new SpringRedditException("User not found for username : "+username));
		return commentRepository.findAllByUser(user).stream().map(commentMapper::mapToDto).collect(Collectors.toList());
	}
}

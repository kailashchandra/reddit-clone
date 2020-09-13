package org.kdcoder.redditclone.service;

import java.util.List;
import java.util.stream.Collectors;

import org.kdcoder.redditclone.dto.PostRequest;
import org.kdcoder.redditclone.dto.PostResponse;
import org.kdcoder.redditclone.exception.SpringRedditException;
import org.kdcoder.redditclone.mapper.PostMapper;
import org.kdcoder.redditclone.model.Post;
import org.kdcoder.redditclone.model.Subreddit;
import org.kdcoder.redditclone.model.User;
import org.kdcoder.redditclone.repository.PostRepository;
import org.kdcoder.redditclone.repository.SubredditRepository;
import org.kdcoder.redditclone.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
@Transactional
public class PostService {
	
	private final PostRepository postRepository;
	private final SubredditRepository subredditRepository;
	private final PostMapper postMapper;
	private final AuthService authService;
	private final UserRepository userRepository;

	public Post createPost(PostRequest postRequest) {
		Subreddit subreddit = subredditRepository.findByName(postRequest.getSubredditName()).orElseThrow(() ->
					new SpringRedditException("Exception occured while getting subreddit for name : "+postRequest.getSubredditName()));
		User currentUser = authService.getCurrentUser();
		return postRepository.save(postMapper.map(postRequest, subreddit, currentUser));
	}

	@Transactional(readOnly = true)
	public List<PostResponse> getAllPost() {
		return postRepository.findAll().stream()
						.map(postMapper::mapToDto)
						.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public PostResponse getPost(Long id) {
		Post post = postRepository.findById(id).orElseThrow(() -> 
				new SpringRedditException("post not found with id :"+id));
		return postMapper.mapToDto(post);
	}

	@Transactional(readOnly = true)
	public List<PostResponse> getPostBySubreddit(Long id) {
		Subreddit subreddit = subredditRepository.findById(id).orElseThrow(() ->
					new SpringRedditException("Subreddit not found for Id : "+id));
		List<Post> posts = postRepository.findAllBySubreddit(subreddit);
		return posts.stream().map(postMapper::mapToDto).collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public List<PostResponse> getPostByUsername(String name) {
		User user = userRepository.findByUsername(name).orElseThrow(() ->
					new UsernameNotFoundException(name));
		List<Post> posts = postRepository.findByUser(user);
		return posts.stream().map(postMapper::mapToDto).collect(Collectors.toList());
	}
	
}

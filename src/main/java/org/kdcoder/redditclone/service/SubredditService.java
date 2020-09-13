package org.kdcoder.redditclone.service;

import java.util.List;
import java.util.stream.Collectors;

import org.kdcoder.redditclone.dto.SubredditDto;
import org.kdcoder.redditclone.exception.SpringRedditException;
import org.kdcoder.redditclone.mapper.SubredditMapper;
import org.kdcoder.redditclone.model.Subreddit;
import org.kdcoder.redditclone.repository.SubredditRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class SubredditService {
	
	private final SubredditRepository subredditRepository;
	private final SubredditMapper subredditMapper;
	
	@Transactional
	public SubredditDto create(SubredditDto subredditDto) {
		Subreddit subreddit = subredditRepository.save(subredditMapper.mapDtoToSubreddit(subredditDto));
		subredditDto.setId(subreddit.getId());
		return subredditDto;
	}

	/*private Subreddit mapDtoToSubrddit(SubredditDto subredditDto) {
		return Subreddit.builder().name(subredditDto.getName())
								.description(subredditDto.getDescription())
								.build();
	}*/

	@Transactional(readOnly = true)
	public List<SubredditDto> getAll() {
		return subredditRepository.findAll().stream().map(subredditMapper::mapSubredditToDto).collect(Collectors.toList());
	}
	
	/*private SubredditDto mapToDto(Subreddit subreddit) {
		return SubredditDto.builder().name(subreddit.getName())
				.id(subreddit.getId())
				.numberOfPost(subreddit.getPosts().size())
				.build();
	}*/

	public SubredditDto getSubreddit(Long id) {
		Subreddit subreddit = subredditRepository.findById(id).orElseThrow(() -> new SpringRedditException("Exception while fetching subreddit for id : "+id));
		return subredditMapper.mapSubredditToDto(subreddit);
	}
}

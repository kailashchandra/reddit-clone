package org.kdcoder.redditclone.mapper;

import java.util.List;

import org.kdcoder.redditclone.dto.SubredditDto;
import org.kdcoder.redditclone.model.Post;
import org.kdcoder.redditclone.model.Subreddit;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SubredditMapper {

    @Mapping(target = "numberOfPost", expression = "java(mapPost(subreddit.getPosts()))")
    SubredditDto mapSubredditToDto(Subreddit subreddit);

    default Integer mapPost(List<Post> numberOfPost) {
        return numberOfPost.size();
    }

    @InheritInverseConfiguration
    @Mapping(target = "posts", ignore = true)
    Subreddit mapDtoToSubreddit(SubredditDto subredditDto);
}

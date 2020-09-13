package org.kdcoder.redditclone.mapper;

import java.util.List;

import org.kdcoder.redditclone.dto.CommentDto;
import org.kdcoder.redditclone.model.Comment;
import org.kdcoder.redditclone.model.Post;
import org.kdcoder.redditclone.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "text", source = "commentDto.text")
	@Mapping(target = "createdDate", expression = "java(java.time.Instant.now())")
	@Mapping(target = "post", source = "post")
	Comment map(CommentDto commentDto, Post post, User user);
	
	@Mapping(target = "postId", expression = "java(mapPost(comment.getPost()))")
	@Mapping(target = "userName", expression = "java(comment.getUser().getUsername())")
	CommentDto mapToDto(Comment comment);
	
	default Long mapPost(Post post) {
        if(post != null) return post.getPostId();
        return null;
    }
	
}

package org.kdcoder.redditclone.repository;

import java.util.List;

import org.kdcoder.redditclone.model.Comment;
import org.kdcoder.redditclone.model.Post;
import org.kdcoder.redditclone.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPost(Post post);

    List<Comment> findAllByUser(User user);
}
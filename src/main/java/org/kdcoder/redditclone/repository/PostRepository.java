package org.kdcoder.redditclone.repository;

import java.util.List;

import org.kdcoder.redditclone.model.Post;
import org.kdcoder.redditclone.model.Subreddit;
import org.kdcoder.redditclone.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>{

	List<Post> findAllBySubreddit(Subreddit subreddit);
	
	List<Post> findByUser(User user);
}

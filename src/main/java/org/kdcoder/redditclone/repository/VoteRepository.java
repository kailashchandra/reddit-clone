package org.kdcoder.redditclone.repository;

import java.util.Optional;

import org.kdcoder.redditclone.model.Post;
import org.kdcoder.redditclone.model.User;
import org.kdcoder.redditclone.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long>{
	Optional<Vote> findTopByPostAndUserOrderByVoteIdDesc(Post post, User currentUser);
}

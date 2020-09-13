package org.kdcoder.redditclone.model;

import java.util.Arrays;

import org.kdcoder.redditclone.exception.SpringRedditException;

public enum VoteType {
	UPVOTE(1), DOWNVOTE(-1);
	private int direction;
	
	private VoteType(int direction) {}
	
	public static VoteType lookup(Integer direction) {
		return Arrays.stream(VoteType.values())
			.filter(value -> value.getDirection().equals(direction))
			.findAny()
			.orElseThrow(() -> new SpringRedditException("Vote not found"));
	}

	private Object getDirection() {
		return direction;
	}
}

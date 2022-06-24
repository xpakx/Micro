package io.github.xpakx.micro2.follows;

import io.github.xpakx.micro2.follows.dto.FollowedResponse;
import io.github.xpakx.micro2.follows.error.AlreadyFollowedException;
import io.github.xpakx.micro2.tag.TagRepository;
import io.github.xpakx.micro2.tag.error.TagNotFoundException;
import io.github.xpakx.micro2.user.UserRepository;
import io.github.xpakx.micro2.user.error.UserNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FollowService {
    public final FollowsRepository followsRepository;
    public final UserRepository userRepository;
    public final TagRepository tagRepository;

    public void followUser(String byUsername, String username) {
        UserFollows follows = followsRepository.findWithUsers(byUsername).orElse(getNewFollowObject(byUsername));
        if(follows.getUsers().stream().anyMatch((u) -> u.getUsername().equals(username))) {
            throw new AlreadyFollowedException("You already follow this user!");
        }
        follows.getUsers().add(userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new));
        followsRepository.save(follows);
    }

    public void followTag(String byUsername, String tagName) {
        UserFollows follows = followsRepository.findWIthTags(byUsername).orElse(getNewFollowObject(byUsername));
        if(follows.getTags().stream().anyMatch((u) -> u.getName().equals(tagName))) {
            throw new AlreadyFollowedException("You already follow this tag!");
        }
        follows.getTags().add(tagRepository.findByName(tagName).orElseThrow(TagNotFoundException::new));
        followsRepository.save(follows);
    }

    @Transactional
    public void unfollowUser(String byUsername, String username) {
        UserFollows follows = followsRepository.findWithUsers(byUsername).orElse(getNewFollowObject(byUsername));
        follows.setUsers(
                follows.getUsers().stream()
                        .filter((u) -> !u.getUsername().equals(username))
                        .collect(Collectors.toSet())
        );

        followsRepository.save(follows);
    }

    @Transactional
    public void unfollowTag(String byUsername, String tagName) {
        UserFollows follows = followsRepository.findWIthTags(byUsername).orElse(getNewFollowObject(byUsername));
        follows.setTags(
                follows.getTags().stream()
                        .filter((t) -> !t.getName().equals(tagName))
                        .collect(Collectors.toSet())
        );

        followsRepository.save(follows);
    }

    private UserFollows getNewFollowObject(String username) {
        UserFollows follows = new UserFollows();
        follows.setUser(userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new));
        follows.setTags(new HashSet<>());
        follows.setUsers(new HashSet<>());
        return follows;
    }

    public FollowedResponse isTagFollowed(String username, String tagName) {
        return new FollowedResponse(followsRepository.existsByTagsNameAndUserUsername(tagName, username));
    }

    public FollowedResponse isUserFollowed(String username, String user) {
        return new FollowedResponse(followsRepository.existsByUsersUsernameAndUserUsername(user, username));
    }
}

package io.github.xpakx.micro2.post;

import io.github.xpakx.micro2.comment.CommentRepository;
import io.github.xpakx.micro2.comment.dto.CommentDetails;
import io.github.xpakx.micro2.post.dto.PostDetails;
import io.github.xpakx.micro2.post.dto.PostDto;
import io.github.xpakx.micro2.post.dto.PostRequest;
import io.github.xpakx.micro2.post.dto.PostWithComments;
import io.github.xpakx.micro2.post.error.PostNotFoundException;
import io.github.xpakx.micro2.post.error.PostTooOldToEditException;
import io.github.xpakx.micro2.tag.TagService;
import io.github.xpakx.micro2.user.UserRepository;
import io.github.xpakx.micro2.user.error.UserNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final TagService tagService;

    public PostDto addPost(PostRequest request, String username) {
        Post newPost = new Post();
        newPost.setContent(request.getMessage());
        newPost.setUser(userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Not such user!"))
        );
        newPost.setEdited(false);
        newPost.setCreatedAt(LocalDateTime.now());
        newPost.setLikeCount(0);
        newPost.setDislikeCount(0);
        newPost.setTags(tagService.addTags(request.getMessage()));
        return PostDto.fromPost(postRepository.save(newPost));
    }

    @Transactional
    public void deletePost(Long id, String username) {
        Post toDelete = postRepository.findByIdAndUserUsername(id, username)
                .orElseThrow(PostNotFoundException::new);
        postRepository.delete(toDelete); //TODO: maybe weak deletion? not sure
    }

    public PostDto updatePost(PostRequest request, Long postId, String username) {
        Post toUpdate = postRepository.findByIdAndUserUsername(postId, username)
                .orElseThrow(PostNotFoundException::new);
        if(toUpdate.getCreatedAt().isBefore(LocalDateTime.now().minusHours(24))) {
            throw new PostTooOldToEditException();
        }
        toUpdate.setContent(request.getMessage());
        toUpdate.setEdited(true);
        toUpdate.setTags(tagService.addTags(request.getMessage()));
        return PostDto.fromPost(postRepository.save(toUpdate));
    }

    public Page<PostDetails> getPosts(Integer page) {
        return postRepository.findAllBy(
                PageRequest.of(page, 20, Sort.by("createdAt").descending())
        );
    }

    public Page<PostDetails> getPostsByUsername(Integer page, String username) {
        return postRepository.getAllByUserUsername(
                username,
                PageRequest.of(page, 20, Sort.by("createdAt").descending())
        );
    }

    public Page<PostWithComments> getPostsByTagName(Integer page, String tag) {
        Page<PostDetails> posts = postRepository.findAllByTagsName(
                tag,
                PageRequest.of(page, 20, Sort.by("createdAt").descending())
        );
        return composePostListAndComments(
                posts,
                commentRepository.getCommentMapForPostIds(posts.stream().map(PostDetails::getId).collect(Collectors.toList()))
        );
    }

    public PostWithComments getSinglePostWithComments(Long postId) {
        return PostWithComments.of(
            postRepository.findProjectedById(postId)
                    .orElseThrow(PostNotFoundException::new),
            commentRepository.getAllByPostId(
                    postId,
                    PageRequest.of(0, 20, Sort.by("createdAt").descending()))
        );
    }

    public PostDetails getSinglePost(Long postId) {
        return postRepository.findProjectedById(postId)
                        .orElseThrow(PostNotFoundException::new);
    }

    public Page<PostWithComments> getHotPosts(Integer page) {
        Page<PostDetails> posts = postRepository.findAllByCreatedAtAfter(
                LocalDateTime.now().minusHours(24),
                PageRequest.of(page, 20, Sort.by("likeCount").descending())
        );
        return composePostListAndComments(
                posts,
                commentRepository.getCommentMapForPostIds(posts.stream().map(PostDetails::getId).collect(Collectors.toList()))
        );
    }

    public Page<PostWithComments> getActivePosts(Integer page) {
        Page<PostDetails> posts = postRepository.getPostsWithMostResponsesAfterDate(
                LocalDateTime.now().minusHours(24),
                PageRequest.of(page, 20)
        );
        return composePostListAndComments(
                posts,
                commentRepository.getCommentMapForPostIds(posts.stream().map(PostDetails::getId).collect(Collectors.toList()))
        );
    }

    public Page<PostWithComments> getFavoritePosts(Integer page, String username) {
        Page<PostDetails> posts = postRepository.findAllByFavoriteUserUsername(
                username,
                PageRequest.of(page, 20)
        );
        return composePostListAndComments(
                posts,
                commentRepository.getCommentMapForPostIds(posts.stream().map(PostDetails::getId).collect(Collectors.toList()))
        );
    }

    private Page<PostWithComments> composePostListAndComments(Page<PostDetails> posts, Map<Long, Page<CommentDetails>> commentMap) {
        List<PostWithComments> result = posts.stream()
                .map(
                        (p) -> PostWithComments.of(p, commentMap.getOrDefault(p.getId(), Page.empty()))
                ).collect(Collectors.toList());
        return new PageImpl<PostWithComments>(result, posts.getPageable(), posts.getTotalElements());
    }
}

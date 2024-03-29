package io.github.xpakx.micro2.post;

import io.github.xpakx.micro2.comment.CommentRepository;
import io.github.xpakx.micro2.comment.dto.CommentDetails;
import io.github.xpakx.micro2.comment.dto.CommentUserInfo;
import io.github.xpakx.micro2.comment.dto.CommentWithUserData;
import io.github.xpakx.micro2.mention.MentionService;
import io.github.xpakx.micro2.post.dto.*;
import io.github.xpakx.micro2.post.error.PostNotFoundException;
import io.github.xpakx.micro2.post.error.PostTooOldToEditException;
import io.github.xpakx.micro2.tag.TagService;
import io.github.xpakx.micro2.user.SettingsService;
import io.github.xpakx.micro2.user.UserAccount;
import io.github.xpakx.micro2.user.UserRepository;
import io.github.xpakx.micro2.user.error.FileSaveException;
import io.github.xpakx.micro2.user.error.UserNotFoundException;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final TagService tagService;
    private final MentionService mentionService;

    private final Path root = Paths.get("attachments");
    Logger logger = LoggerFactory.getLogger(SettingsService.class);

    @PostConstruct
    public void init() {
        try {
            if(!Files.exists(root)) {
                Files.createDirectory(root);
            } else if(!Files.isDirectory(root)) {
                logger.error("Cannot create directory for file upload!");
            }
        } catch (IOException e) {
            logger.error("Cannot create directory for file upload!");
        }
    }

    public PostDto addPost(PostRequest request, String username) {
        Post newPost = new Post();
        newPost.setContent(request.getMessage());
        UserAccount user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Not such user!"));
        newPost.setUser(user);
        newPost.setEdited(false);
        newPost.setDeleted(false);
        newPost.setCreatedAt(LocalDateTime.now());
        newPost.setLikeCount(0);
        newPost.setDislikeCount(0);
        newPost.setTags(tagService.addTags(request.getMessage()));
        newPost.setMentions(mentionService.addMentions(request.getMessage(), user, newPost));
        if(request.getEncodedAttachment() != null && request.getEncodedAttachment().length() > 0) {
            byte[] imageByte = Base64.decodeBase64(
                    request.getEncodedAttachment().contains(",") ? request.getEncodedAttachment().split(",")[1] : request.getEncodedAttachment()
            );
            try {
                String filename = username + "_" + LocalDateTime.now();
                Files.deleteIfExists(this.root.resolve(filename));
                ByteArrayInputStream imageStream = new ByteArrayInputStream(imageByte);
                Files.copy(imageStream, this.root.resolve(filename));
                imageStream.close();
                newPost.setAttachmentUrl(filename);
            } catch (IOException ex) {
                throw new FileSaveException();
            }
        }
        return PostDto.fromPost(postRepository.save(newPost));
    }

    public ByteArrayResource getAttachment(String name) throws IOException {
        return new ByteArrayResource(Files.readAllBytes(Paths.get(
                "./attachments/"+name
        )));
    }

    @Transactional
    public void deletePost(Long id, String username) {
        Post toDelete = postRepository.findByIdAndUserUsername(id, username)
                .orElseThrow(PostNotFoundException::new);
        toDelete.setDeleted(true);
        postRepository.save(toDelete);
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

    public Page<PostWithComments> getPosts(Integer page) {
        Page<PostDetails> posts = postRepository.findAllByDeletedIsFalse(
                PageRequest.of(page, 20, Sort.by("createdAt").descending())
        );
        return composePostListAndComments(
                posts,
                commentRepository.getCommentMapForPostIds(posts.stream().map(PostDetails::getId).collect(Collectors.toList()))
        );
    }

    public Page<PostWithComments> getPostsAuth(Integer page, String username) {
        Page<PostDetails> posts = postRepository.findAllByDeletedIsFalse(
                PageRequest.of(page, 20, Sort.by("createdAt").descending())
        );
        return preparePostWithCommentsPage(username, posts);
    }

    private Page<PostWithComments> preparePostWithCommentsPage(String username, Page<PostDetails> posts) {
        List<Long> ids = posts.stream()
                .map(PostDetails::getId)
                .collect(Collectors.toList());
        Map<Long, Page<CommentWithUserData>> comments = commentRepository.getCommentMapForPostIds(ids);
        List<Long> commentIds = comments.values().stream()
                .map(Slice::getContent)
                .flatMap(Collection::stream)
                .distinct()
                .map((c) -> c.getComment().getId())
                .collect(Collectors.toList());
        Map<Long, CommentUserInfo> userInfoMap = commentRepository.getUserInfoMapForCommentIds(commentIds, username);
        comments.replaceAll((k, v) -> transformComments(comments.get(k), userInfoMap));
        return composePostListAndComments(
                posts,
                comments,
                postRepository.getUserInfoMapForPostIds(ids, username)
        );
    }

    public Page<PostWithComments> getPostsByUsername(Integer page, String username) {
        Page<PostDetails> posts = postRepository.getAllByUserUsernameAndDeletedIsFalse(
                username,
                PageRequest.of(page, 20, Sort.by("createdAt").descending())
        );
        return composePostListAndComments(
                posts,
                commentRepository.getCommentMapForPostIds(posts.stream().map(PostDetails::getId).collect(Collectors.toList()))
        );
    }

    public Page<PostWithComments> getPostsByUsernameAuth(Integer page, String user, String username) {
        Page<PostDetails> posts = postRepository.getAllByUserUsernameAndDeletedIsFalse(
                user,
                PageRequest.of(page, 20, Sort.by("createdAt").descending())
        );
        return preparePostWithCommentsPage(username, posts);
    }

    public Page<PostWithComments> getPostsByTagName(Integer page, String tag) {
        Page<PostDetails> posts = postRepository.findAllByTagsNameAndDeletedIsFalse(
                tag,
                PageRequest.of(page, 20, Sort.by("createdAt").descending())
        );
        return composePostListAndComments(
                posts,
                commentRepository.getCommentMapForPostIds(posts.stream().map(PostDetails::getId).collect(Collectors.toList()))
        );
    }

    public Page<PostWithComments> getPostsByTagNameAuth(Integer page, String tag, String username) {
        Page<PostDetails> posts = postRepository.findAllByTagsNameAndDeletedIsFalse(
                tag,
                PageRequest.of(page, 20, Sort.by("createdAt").descending())
        );
        return preparePostWithCommentsPage(username, posts);
    }

    public PostWithComments getSinglePostWithComments(Long postId) {
        return PostWithComments.of(
            postRepository.findProjectedById(postId)
                    .orElseThrow(PostNotFoundException::new),
            transformComments(commentRepository.getAllByPostId(
                    postId,
                    PageRequest.of(0, 20, Sort.by("createdAt").descending())))
        );
    }

    public PostWithComments getSinglePostWithCommentsAuth(Long postId, String username) {
        PostDetails post = postRepository.findProjectedById(postId)
                .orElseThrow(PostNotFoundException::new);
        Page<CommentDetails> comments = commentRepository.getAllByPostId(
                postId,
                PageRequest.of(0, 20, Sort.by("createdAt").descending()));

        Map<Long, CommentUserInfo> userInfoMap = commentRepository.getUserInfoMapForCommentIds(
                comments.stream().map(CommentDetails::getId).collect(Collectors.toList()),
                username
        );
        return PostWithComments.of(
                post,
                transformCommentsSingle(comments, userInfoMap),
                postRepository.getUserInfoForPostId(postId, username).orElse(new PostUserInfo())
        );
    }

    public PostDetails getSinglePost(Long postId) {
        return postRepository.findProjectedById(postId)
                        .orElseThrow(PostNotFoundException::new);
    }

    public Page<PostWithComments> getHotPosts(Integer page) {
        Page<PostDetails> posts = postRepository.findAllByCreatedAtAfterAndDeletedIsFalse(
                LocalDateTime.now().minusHours(24),
                PageRequest.of(page, 20, Sort.by("likeCount").descending())
        );
        return composePostListAndComments(
                posts,
                commentRepository.getCommentMapForPostIds(posts.stream().map(PostDetails::getId).collect(Collectors.toList()))
        );
    }

    public Page<PostWithComments> getHotPostsAuth(Integer page, String username) {
        Page<PostDetails> posts = postRepository.findAllByCreatedAtAfterAndDeletedIsFalse(
                LocalDateTime.now().minusHours(24),
                PageRequest.of(page, 20, Sort.by("likeCount").descending())
        );
        return preparePostWithCommentsPage(username, posts);
    }

    public List<PostDetails> getRandomHotPosts() {
        return postRepository.get2RandomHotPosts(LocalDateTime.now().minusHours(24));
    }

    public List<PostDetails> getRandomHotPostsAuth(String username) {
        return postRepository.get2RandomHotPosts(LocalDateTime.now().minusHours(24));
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

    public Page<PostWithComments> getActivePostsAuth(Integer page, String username) {
        Page<PostDetails> posts = postRepository.getPostsWithMostResponsesAfterDate(
                LocalDateTime.now().minusHours(24),
                PageRequest.of(page, 20)
        );
        return preparePostWithCommentsPage(username, posts);
    }

    public Page<PostWithComments> getFavoritePosts(Integer page, String username) {
        Page<PostDetails> posts = postRepository.findAllByFavoriteUserUsernameAndDeletedIsFalse(
                username,
                PageRequest.of(page, 20, Sort.by("createdAt").descending())
        );
        return preparePostWithCommentsPage(username, posts);
    }

    private Page<PostWithComments> composePostListAndComments(Page<PostDetails> posts, Map<Long, Page<CommentWithUserData>> commentMap) {
        List<PostWithComments> result = posts.stream()
                .map(
                        (p) -> PostWithComments.of(p, commentMap.getOrDefault(p.getId(), Page.empty()))
                ).collect(Collectors.toList());
        return new PageImpl<PostWithComments>(result, posts.getPageable(), posts.getTotalElements());
    }

    private Page<PostWithComments> composePostListAndComments(Page<PostDetails> posts, Map<Long, Page<CommentWithUserData>> commentMap, Map<Long, PostUserInfo> userInfoMap) {
        List<PostWithComments> result = posts.stream()
                .map(
                        (p) -> PostWithComments.of(p, commentMap.getOrDefault(p.getId(), Page.empty()), userInfoMap.get(p.getId()))
                ).collect(Collectors.toList());
        return new PageImpl<PostWithComments>(result, posts.getPageable(), posts.getTotalElements());
    }

    private Page<CommentWithUserData> transformComments(Page<CommentDetails> comments) {
        List<CommentWithUserData> result = comments.stream()
                .map(CommentWithUserData::of).collect(Collectors.toList());
        return new PageImpl<CommentWithUserData>(result, comments.getPageable(), comments.getTotalElements());
    }

    private Page<CommentWithUserData> transformCommentsSingle(Page<CommentDetails> comments, Map<Long, CommentUserInfo> userInfoMap) {
        return  transformComments(transformComments(comments), userInfoMap);
    }

    private Page<CommentWithUserData> transformComments(Page<CommentWithUserData> comments, Map<Long, CommentUserInfo> userInfoMap) {
        comments.getContent()
                .forEach((c) -> {
                            CommentUserInfo info = userInfoMap.get(c.getComment().getId());
                            if(info != null) {
                                c.setLiked(info.isLiked());
                                c.setDisliked(info.isDisliked());
                            }
                        }
                );
        return comments;
    }

    public Page<PostWithComments> getPostsByFollowedUsers(Integer page, String username) {
        Page<PostDetails> posts = postRepository.findAllByFollowedUsers(
                username,
                PageRequest.of(page, 20)
        );
        return preparePostWithCommentsPage(username, posts);
    }

    public Page<PostWithComments> getPostsFromFollowedTags(Integer page, String username) {
        Page<PostDetails> posts = postRepository.findAllFromFollowedTags(
                username,
                PageRequest.of(page, 20)
        );
        return preparePostWithCommentsPage(username, posts);
    }

    public Page<PostDetails> searchPosts(String search, Integer page) {
        return postRepository.findByContentIsContainingIgnoreCaseAndDeletedIsFalse(search, PageRequest.of(page, 20));
    }
}

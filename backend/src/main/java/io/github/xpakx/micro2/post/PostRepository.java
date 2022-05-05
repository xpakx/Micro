package io.github.xpakx.micro2.post;

import io.github.xpakx.micro2.post.dto.PostDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostRepository extends PagingAndSortingRepository<Post, Long> {
    Optional<Post> findByIdAndUserUsername(Long id, String username);
    Page<PostDetails> findAllBy(Pageable pageable);
    Page<PostDetails> getAllByUserUsername(String username, Pageable pageable);
}
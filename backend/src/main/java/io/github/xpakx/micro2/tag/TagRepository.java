package io.github.xpakx.micro2.tag;

import io.github.xpakx.micro2.tag.dto.TagDetails;
import io.github.xpakx.micro2.tag.dto.TagName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByName(String name);

    @Query(value = "SELECT * FROM tag GROUP BY tag.id " +
            "ORDER BY (SELECT count(post_id) FROM post LEFT JOIN post_tag ON post.id = post_id " +
            "WHERE tag_id = tag.id AND post.created_at > :date) LIMIT 10",
            nativeQuery = true)
    List<TagDetails> getTopTagsAfterDate(LocalDateTime date);
    List<TagName> findFirst10ByNameStartsWith(@Param("name") String name);
}

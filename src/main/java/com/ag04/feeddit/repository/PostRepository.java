package com.ag04.feeddit.repository;

import com.ag04.feeddit.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Spring Data JPA repository for the Post entity.
 */
@SuppressWarnings("unused")
public interface PostRepository extends JpaRepository<Post,Long> {
    List<Post> findAllByAuthorName(String name);
    List<Post> findAllByAuthorID(Long id);
}

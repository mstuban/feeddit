package com.ag04.feeddit.repository;

import com.ag04.feeddit.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Spring Data JPA repository for the Post entity.
 */
@SuppressWarnings("unused")
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByAuthorNameOrderByIdDesc(String name);

    List<Post> findAllByAuthorIDOrderByIdDesc(Long id);

/*
    List<Post> findAllByIdArray(List<Long> idArray);
*/

    List<Post> findAllByAuthorIDAndId(Long id, List<Long> idArray);

    List<Post> findAllByAuthorID(Long id);
}

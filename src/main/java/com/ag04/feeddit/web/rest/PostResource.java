package com.ag04.feeddit.web.rest;

import com.ag04.feeddit.domain.Post;
import com.ag04.feeddit.domain.User;
import com.ag04.feeddit.repository.PostRepository;
import com.ag04.feeddit.repository.UserRepository;
import com.ag04.feeddit.web.rest.util.HeaderUtil;
import com.codahale.metrics.annotation.Timed;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

/**
 * REST controller for managing Post.
 */
@RestController
@RequestMapping("/api")
public class PostResource {

    private final Logger log = LoggerFactory.getLogger(PostResource.class);

    private static final String ENTITY_NAME = "post";

    private final PostRepository postRepository;

    private final UserRepository userRepository;

    PostResource(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    /**
     * POST  /posts : Create a new post.
     *
     * @param post the post to create
     * @return the ResponseEntity with status 201 (Created) and with body the new post, or with status 400 (Bad Request) if the post has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("currentUser/posts")
    @Timed
    public ResponseEntity<Post> createPost(@RequestBody Post post) throws URISyntaxException {
        log.debug("REST request to save Post : {}", post);
        if (post.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new post cannot already have an ID")).body(null);
        }
        Post result = postRepository.save(post);
        return ResponseEntity.created(new URI("/api/currentUser/posts/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }


    /**
     * PUT  /posts : Updates an existing post.
     *
     * @param post the post to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated post,
     * or with status 400 (Bad Request) if the post is not valid,
     * or with status 500 (Internal Server Error) if the post couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/posts")
    @Timed
    public ResponseEntity<Post> updatePost(@RequestBody Post post) throws URISyntaxException {
        log.debug("REST request to update Post : {}", post);
        if (post.getId() == null) {
            return createPost(post);
        }


        Post result = postRepository.save(post);


        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, post.getId().toString()))
            .body(result);
    }

    @PutMapping("/posts/{id}/upVote")
    @Timed
    public ResponseEntity<Post> upVotePost(@PathVariable Long id) throws URISyntaxException {

        Post post = postRepository.findOne(id);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findOneByLogin(authentication.getName()).get();
        HashSet<Long> upvotedPostsIds = user.getUpvotedPostsIds();

        System.out.println("upvotedPostsIds: " + upvotedPostsIds);

        boolean postWasUpvoted = false;

        if (upvotedPostsIds == null) {
            post.setNumberOfUpvotes(post.getNumberOfUpvotes() + 1);
            upvotedPostsIds.add(post.getId());
            user.setUpvotedPostsIds(upvotedPostsIds);
            userRepository.save(user);
            Post result = postRepository.save(post);

            return new ResponseEntity<>(HttpStatus.OK);

        }

        for (int i = 0; i < upvotedPostsIds.size(); i++) {
            if (upvotedPostsIds.contains(post.getId())) {
                postWasUpvoted = true;
            }
        }

        if (postWasUpvoted) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } else {
            post.setNumberOfUpvotes(post.getNumberOfUpvotes() + 1);
            HashSet<Long> newUpvotedPostsIds = upvotedPostsIds;
            newUpvotedPostsIds.add(post.getId());
            user.setUpvotedPostsIds(newUpvotedPostsIds);
            userRepository.save(user);
            Post result = postRepository.save(post);

            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    @PutMapping("/posts/{id}/downVote")
    @Timed
    public ResponseEntity<Post> downVotePost(@PathVariable Long id) throws URISyntaxException {

        Post post = postRepository.findOne(id);

        post.setNumberOfUpvotes(post.getNumberOfUpvotes() - 1);

        Post result = postRepository.save(post);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, post.getId().toString()))
            .body(result);
    }


    /**
     * GET  /posts : get all the posts.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of posts in body
     */

    @GetMapping("currentUser/posts")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @Timed
    public List<Post> getAllUserPosts() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return postRepository.findAllByAuthorName(authentication.getName());
    }

    @GetMapping("currentUser/upVotes")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @Timed
    public HashSet getUserUpvoteIds() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findOneByLogin(authentication.getName()).get();
        return user.getUpvotedPostsIds();
    }

    @GetMapping("currentUser/downVotes")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @Timed
    public HashSet getUserdownVoteIds() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findOneByLogin(authentication.getName()).get();
        return user.getDownvotedPostsIds();
    }


    @GetMapping("currentUser/posts/{id}")
    @Timed
    public ResponseEntity<Post> getCurrentUserPostById(@PathVariable Long id) {
        log.debug("REST request to get Post : {}", id);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Post post = postRepository.findOne(id);

        if (post != null && (authentication.getName().equals(post.getAuthorName()) || authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")))) {
            return ResponseUtil.wrapOrNotFound(Optional.of(post));
        }

        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }


    /**
     * GET  /posts : get all the posts.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of posts in body
     */
    @GetMapping("/posts")
    @Timed
    public List<Post> getAllPosts() {
        log.debug("REST request to get all Posts");
        List<Post> posts = postRepository.findAll();
        return posts;
    }

    /**
     * GET  /posts/:id : get the "id" post.
     *
     * @param id the id of the post to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the post, or with status 404 (Not Found)
     */
    @GetMapping("/posts/{id}")
    @Timed
    public ResponseEntity<Post> getPost(@PathVariable Long id) {
        log.debug("REST request to get Post : {}", id);
        Post post = postRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(post));
    }

    @DeleteMapping("currentUser/posts/{idArray}")
    @Timed
    public ResponseEntity<Void> deleteUserPostsById(@PathVariable List<Long> idArray) {

        System.out.println(idArray);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        List<Post> posts = postRepository.findAll();

        for (Post post : posts) {
            for (Long postId : idArray) {
                if (Objects.equals(post.getId(), postId)) {
                    Post postToBeDeleted = postRepository.findOne(postId);
                    if (postToBeDeleted != null && (authentication.getName().equals(post.getAuthorName()) || authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")))) {
                        postRepository.delete(postId);
                    }
                }
            }
        }


        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, idArray.toString())).build();
    }

}

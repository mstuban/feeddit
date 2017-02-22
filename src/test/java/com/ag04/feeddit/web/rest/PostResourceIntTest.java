package com.ag04.feeddit.web.rest;

import com.ag04.feeddit.FeedditApp;

import com.ag04.feeddit.domain.Post;
import com.ag04.feeddit.repository.PostRepository;
import com.ag04.feeddit.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the PostResource REST controller.
 *
 * @see PostResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = FeedditApp.class)
public class PostResourceIntTest {

    private static final LocalDate DEFAULT_SUBMIT_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_SUBMIT_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final Long DEFAULT_AUTHOR_ID = 1L;
    private static final Long UPDATED_AUTHOR_ID = 2L;

    private static final String DEFAULT_HEADLINE = "AAAAAAAAAA";
    private static final String UPDATED_HEADLINE = "BBBBBBBBBB";

    private static final String DEFAULT_POST_URL = "AAAAAAAAAA";
    private static final String UPDATED_POST_URL = "BBBBBBBBBB";

    private static final String DEFAULT_AUTHOR_NAME = "AAAAAAAAAA";
    private static final String UPDATED_AUTHOR_NAME = "BBBBBBBBBB";

    private static final Integer DEFAULT_NUMBER_OF_UPVOTES = 1;
    private static final Integer UPDATED_NUMBER_OF_UPVOTES = 2;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restPostMockMvc;

    private Post post;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
            PostResource postResource = new PostResource(postRepository);
        this.restPostMockMvc = MockMvcBuilders.standaloneSetup(postResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Post createEntity(EntityManager em) {
        Post post = new Post()
                .submitDate(DEFAULT_SUBMIT_DATE)
                .authorID(DEFAULT_AUTHOR_ID)
                .headline(DEFAULT_HEADLINE)
                .postURL(DEFAULT_POST_URL)
                .authorName(DEFAULT_AUTHOR_NAME)
                .numberOfUpvotes(DEFAULT_NUMBER_OF_UPVOTES);
        return post;
    }

    @Before
    public void initTest() {
        post = createEntity(em);
    }

    @Test
    @Transactional
    public void createPost() throws Exception {
        int databaseSizeBeforeCreate = postRepository.findAll().size();

        // Create the Post

        restPostMockMvc.perform(post("/api/posts")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(post)))
            .andExpect(status().isCreated());

        // Validate the Post in the database
        List<Post> postList = postRepository.findAll();
        assertThat(postList).hasSize(databaseSizeBeforeCreate + 1);
        Post testPost = postList.get(postList.size() - 1);
        assertThat(testPost.getSubmitDate()).isEqualTo(DEFAULT_SUBMIT_DATE);
        assertThat(testPost.getAuthorID()).isEqualTo(DEFAULT_AUTHOR_ID);
        assertThat(testPost.getHeadline()).isEqualTo(DEFAULT_HEADLINE);
        assertThat(testPost.getPostURL()).isEqualTo(DEFAULT_POST_URL);
        assertThat(testPost.getAuthorName()).isEqualTo(DEFAULT_AUTHOR_NAME);
        assertThat(testPost.getNumberOfUpvotes()).isEqualTo(DEFAULT_NUMBER_OF_UPVOTES);
    }

    @Test
    @Transactional
    public void createPostWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = postRepository.findAll().size();

        // Create the Post with an existing ID
        Post existingPost = new Post();
        existingPost.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restPostMockMvc.perform(post("/api/posts")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(existingPost)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<Post> postList = postRepository.findAll();
        assertThat(postList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllPosts() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList
        restPostMockMvc.perform(get("/api/posts?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(post.getId().intValue())))
            .andExpect(jsonPath("$.[*].submitDate").value(hasItem(DEFAULT_SUBMIT_DATE.toString())))
            .andExpect(jsonPath("$.[*].authorID").value(hasItem(DEFAULT_AUTHOR_ID.intValue())))
            .andExpect(jsonPath("$.[*].headline").value(hasItem(DEFAULT_HEADLINE.toString())))
            .andExpect(jsonPath("$.[*].postURL").value(hasItem(DEFAULT_POST_URL.toString())))
            .andExpect(jsonPath("$.[*].authorName").value(hasItem(DEFAULT_AUTHOR_NAME.toString())))
            .andExpect(jsonPath("$.[*].numberOfUpvotes").value(hasItem(DEFAULT_NUMBER_OF_UPVOTES)));
    }

    @Test
    @Transactional
    public void getPost() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get the post
        restPostMockMvc.perform(get("/api/posts/{id}", post.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(post.getId().intValue()))
            .andExpect(jsonPath("$.submitDate").value(DEFAULT_SUBMIT_DATE.toString()))
            .andExpect(jsonPath("$.authorID").value(DEFAULT_AUTHOR_ID.intValue()))
            .andExpect(jsonPath("$.headline").value(DEFAULT_HEADLINE.toString()))
            .andExpect(jsonPath("$.postURL").value(DEFAULT_POST_URL.toString()))
            .andExpect(jsonPath("$.authorName").value(DEFAULT_AUTHOR_NAME.toString()))
            .andExpect(jsonPath("$.numberOfUpvotes").value(DEFAULT_NUMBER_OF_UPVOTES));
    }

    @Test
    @Transactional
    public void getNonExistingPost() throws Exception {
        // Get the post
        restPostMockMvc.perform(get("/api/posts/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePost() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);
        int databaseSizeBeforeUpdate = postRepository.findAll().size();

        // Update the post
        Post updatedPost = postRepository.findOne(post.getId());
        updatedPost
                .submitDate(UPDATED_SUBMIT_DATE)
                .authorID(UPDATED_AUTHOR_ID)
                .headline(UPDATED_HEADLINE)
                .postURL(UPDATED_POST_URL)
                .authorName(UPDATED_AUTHOR_NAME)
                .numberOfUpvotes(UPDATED_NUMBER_OF_UPVOTES);

        restPostMockMvc.perform(put("/api/posts")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedPost)))
            .andExpect(status().isOk());

        // Validate the Post in the database
        List<Post> postList = postRepository.findAll();
        assertThat(postList).hasSize(databaseSizeBeforeUpdate);
        Post testPost = postList.get(postList.size() - 1);
        assertThat(testPost.getSubmitDate()).isEqualTo(UPDATED_SUBMIT_DATE);
        assertThat(testPost.getAuthorID()).isEqualTo(UPDATED_AUTHOR_ID);
        assertThat(testPost.getHeadline()).isEqualTo(UPDATED_HEADLINE);
        assertThat(testPost.getPostURL()).isEqualTo(UPDATED_POST_URL);
        assertThat(testPost.getAuthorName()).isEqualTo(UPDATED_AUTHOR_NAME);
        assertThat(testPost.getNumberOfUpvotes()).isEqualTo(UPDATED_NUMBER_OF_UPVOTES);
    }

    @Test
    @Transactional
    public void updateNonExistingPost() throws Exception {
        int databaseSizeBeforeUpdate = postRepository.findAll().size();

        // Create the Post

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restPostMockMvc.perform(put("/api/posts")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(post)))
            .andExpect(status().isCreated());

        // Validate the Post in the database
        List<Post> postList = postRepository.findAll();
        assertThat(postList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deletePost() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);
        int databaseSizeBeforeDelete = postRepository.findAll().size();

        // Get the post
        restPostMockMvc.perform(delete("/api/posts/{id}", post.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Post> postList = postRepository.findAll();
        assertThat(postList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Post.class);
    }
}

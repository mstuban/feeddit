package com.ag04.feeddit.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A Post.
 */
@Entity
@Table(name = "post")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Post implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "post_sequence", sequenceName = "post_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "post_sequence")
    private Long id;

    @Column(name = "submit_date")
    private LocalDate submitDate;

    @Column(name = "author_id")
    private Long authorID;

    @Column(name = "headline")
    private String headline;

    @Column(name = "post_url")
    private String postURL;

    @Column(name = "author_name")
    private String authorName;

    @Column(name = "number_of_upvotes")
    private Integer numberOfUpvotes;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getSubmitDate() {
        return submitDate;
    }

    public Post submitDate(LocalDate submitDate) {
        this.submitDate = submitDate;
        return this;
    }

    public void setSubmitDate(LocalDate submitDate) {
        this.submitDate = submitDate;
    }

    public Long getAuthorID() {
        return authorID;
    }

    public Post authorID(Long authorID) {
        this.authorID = authorID;
        return this;
    }

    public void setAuthorID(Long authorID) {
        this.authorID = authorID;
    }

    public String getHeadline() {
        return headline;
    }

    public Post headline(String headline) {
        this.headline = headline;
        return this;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getPostURL() {
        return postURL;
    }

    public Post postURL(String postURL) {
        this.postURL = postURL;
        return this;
    }

    public void setPostURL(String postURL) {
        this.postURL = postURL;
    }

    public String getAuthorName() {
        return authorName;
    }

    public Post authorName(String authorName) {
        this.authorName = authorName;
        return this;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public Integer getNumberOfUpvotes() {
        return numberOfUpvotes;
    }

    public Post numberOfUpvotes(Integer numberOfUpvotes) {
        this.numberOfUpvotes = numberOfUpvotes;
        return this;
    }

    public void setNumberOfUpvotes(Integer numberOfUpvotes) {
        this.numberOfUpvotes = numberOfUpvotes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Post post = (Post) o;
        if (post.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, post.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Post{" +
            "id=" + id +
            ", submitDate='" + submitDate + "'" +
            ", authorID='" + authorID + "'" +
            ", headline='" + headline + "'" +
            ", postURL='" + postURL + "'" +
            ", authorName='" + authorName + "'" +
            ", numberOfUpvotes='" + numberOfUpvotes + "'" +
            '}';
    }
}

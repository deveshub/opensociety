package com.demo.opensociety.domain;

import com.demo.opensociety.domain.enumeration.NoticeType;
import java.io.Serializable;
import java.time.Instant;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * A Notice.
 */
@Document(collection = "notice")
public class Notice implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @NotNull
    @Field("title")
    private String title;

    @NotNull
    @Field("body")
    private String body;

    @NotNull
    @Field("publish_date")
    private Instant publishDate;

    @Field("notice_type")
    private NoticeType noticeType;

    @NotNull
    @Field("user_id")
    private Long userId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public Notice id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public Notice title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return this.body;
    }

    public Notice body(String body) {
        this.setBody(body);
        return this;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Instant getPublishDate() {
        return this.publishDate;
    }

    public Notice publishDate(Instant publishDate) {
        this.setPublishDate(publishDate);
        return this;
    }

    public void setPublishDate(Instant publishDate) {
        this.publishDate = publishDate;
    }

    public NoticeType getNoticeType() {
        return this.noticeType;
    }

    public Notice noticeType(NoticeType noticeType) {
        this.setNoticeType(noticeType);
        return this;
    }

    public void setNoticeType(NoticeType noticeType) {
        this.noticeType = noticeType;
    }

    public Long getUserId() {
        return this.userId;
    }

    public Notice userId(Long userId) {
        this.setUserId(userId);
        return this;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Notice)) {
            return false;
        }
        return id != null && id.equals(((Notice) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Notice{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", body='" + getBody() + "'" +
            ", publishDate='" + getPublishDate() + "'" +
            ", noticeType='" + getNoticeType() + "'" +
            ", userId=" + getUserId() +
            "}";
    }
}

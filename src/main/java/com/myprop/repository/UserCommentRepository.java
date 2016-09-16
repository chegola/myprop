package com.myprop.repository;

import com.myprop.domain.UserComment;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for the UserComment entity.
 */
@SuppressWarnings("unused")
public interface UserCommentRepository extends JpaRepository<UserComment,Long> {

    @Query("select userComment from UserComment userComment where userComment.user.login = ?#{principal.username}")
    List<UserComment> findByUserIsCurrentUser();

    @Query("select userComment from UserComment userComment where userComment.announcement.id = :id")
    List<UserComment> findByAnnouncementId(@Param("id") Long id);

}

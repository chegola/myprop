package com.myprop.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.myprop.domain.UserComment;
import com.myprop.repository.UserCommentRepository;
import com.myprop.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing UserComment.
 */
@RestController
@RequestMapping("/api")
public class UserCommentResource {

    private final Logger log = LoggerFactory.getLogger(UserCommentResource.class);

    @Inject
    private UserCommentRepository userCommentRepository;

    /**
     * POST  /user-comments : Create a new userComment.
     *
     * @param userComment the userComment to create
     * @return the ResponseEntity with status 201 (Created) and with body the new userComment, or with status 400 (Bad Request) if the userComment has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/user-comments",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<UserComment> createUserComment(@Valid @RequestBody UserComment userComment) throws URISyntaxException {
        log.debug("REST request to save UserComment : {}", userComment);
        if (userComment.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("userComment", "idexists", "A new userComment cannot already have an ID")).body(null);
        }
        UserComment result = userCommentRepository.save(userComment);
        return ResponseEntity.created(new URI("/api/user-comments/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("userComment", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /user-comments : Updates an existing userComment.
     *
     * @param userComment the userComment to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated userComment,
     * or with status 400 (Bad Request) if the userComment is not valid,
     * or with status 500 (Internal Server Error) if the userComment couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/user-comments",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<UserComment> updateUserComment(@Valid @RequestBody UserComment userComment) throws URISyntaxException {
        log.debug("REST request to update UserComment : {}", userComment);
        if (userComment.getId() == null) {
            return createUserComment(userComment);
        }
        UserComment result = userCommentRepository.save(userComment);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("userComment", userComment.getId().toString()))
            .body(result);
    }

    /**
     * GET  /user-comments : get all the userComments.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of userComments in body
     */
    @RequestMapping(value = "/user-comments",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<UserComment> getAllUserComments() {
        log.debug("REST request to get all UserComments");
        List<UserComment> userComments = userCommentRepository.findAll();
        return userComments;
    }

    /**
     * GET  /user-comments/query-by-announcement/{id} : get all the userComments.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of userComments in body
     */
    @RequestMapping(value = "/user-comments/query-by-announcement/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<UserComment> getUserCommentsByAnnouncement(@PathVariable Long id) {
        log.debug("REST request to get all UserComments by Announcement");
        List<UserComment> userComments = userCommentRepository.findByAnnouncementId(id);
        return userComments;
    }


    /**
     * GET  /user-comments/:id : get the "id" userComment.
     *
     * @param id the id of the userComment to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the userComment, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/user-comments/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<UserComment> getUserComment(@PathVariable Long id) {
        log.debug("REST request to get UserComment : {}", id);
        UserComment userComment = userCommentRepository.findOne(id);
        return Optional.ofNullable(userComment)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /user-comments/:id : delete the "id" userComment.
     *
     * @param id the id of the userComment to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/user-comments/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteUserComment(@PathVariable Long id) {
        log.debug("REST request to delete UserComment : {}", id);
        userCommentRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("userComment", id.toString())).build();
    }

}

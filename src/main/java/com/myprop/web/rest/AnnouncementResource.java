package com.myprop.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.linecorp.bot.client.LineMessagingServiceBuilder;
import com.linecorp.bot.model.PushMessage;
import com.linecorp.bot.model.action.URIAction;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TemplateMessage;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.message.template.ButtonsTemplate;
import com.linecorp.bot.model.response.BotApiResponse;
import com.linecorp.bot.spring.boot.LineBotProperties;
import com.myprop.domain.Announcement;
import com.myprop.domain.Line;
import com.myprop.domain.User;
import com.myprop.repository.LineRepository;
import com.myprop.repository.UserRepository;
import com.myprop.service.AnnouncementService;
import com.myprop.service.MailService;
import com.myprop.web.rest.util.HeaderUtil;
import com.myprop.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import retrofit2.Response;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.lang.reflect.Array;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Announcement.
 */
@RestController
@RequestMapping("/api")
public class AnnouncementResource {

    private final Logger log = LoggerFactory.getLogger(AnnouncementResource.class);

    @Inject
    private AnnouncementService announcementService;

    @Inject
    private MailService mailService;

    @Inject
    private UserRepository userRepository;

    @Inject
    private LineRepository lineRepository;

    @Inject
    private LineBotProperties lineBotProperties;

    /**
     * POST  /announcements : Create a new announcement.
     *
     * @param announcement the announcement to create
     * @return the ResponseEntity with status 201 (Created) and with body the new announcement, or with status 400 (Bad Request) if the announcement has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/announcements",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Announcement> createAnnouncement(@Valid @RequestBody Announcement announcement, HttpServletRequest request) throws URISyntaxException {
        log.debug("REST request to save Announcement : {}", announcement);
        if (announcement.getId() != null) {
            return ResponseEntity.badRequest().
                headers(HeaderUtil.createFailureAlert("announcement", "id exists", "A new announcement cannot already have an ID")).body(null);
        }

        Announcement result = announcementService.save(announcement);

        if (result.getSendEmail()) {
            sendAnnouncementEmail(result);
        }

        if (result.getSendLine()) {
            sendLineMessage(result, request);
        }

        return ResponseEntity.created(new URI("/api/announcements/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("announcement", result.getId().toString()))
            .body(result);
    }

    private void sendLineMessage(Announcement announcement, HttpServletRequest request)  {
        log.debug("Send LINE message: " + announcement.getSubject());

        String baseUrl = request.getScheme() + // "http"
            "://" +                                // "://"
            request.getServerName() +              // "myhost"
            request.getContextPath().concat("/#/announcement/" + announcement.getId());

        TextMessage textMessage = new TextMessage(announcement.getSubject() + "\n" + baseUrl);

        ButtonsTemplate buttonTemplate = new ButtonsTemplate(
            "https://i.imgsafe.org/60dd163537.png",
            "ข่าวสารจากนิติบุคลลพฤกษ์ลดาฯ",
            announcement.getSubject(),
            Arrays.asList(new URIAction("ดูรายละเอียด", baseUrl)
            ));

        TemplateMessage templateMessage = new TemplateMessage("ข่าวสารจากนิติบุคคลพฤกษ์ลดาฯ", buttonTemplate);

        List<Message> messages = new ArrayList<Message>();
        messages.add(templateMessage);
        //messages.add(textMessage);

        //List<Line> lines = lineRepository.findAllBySourceType("GroupSource");
        List<Line> lines = lineRepository.findAll();
        for (Line l : lines) {
            //PushMessage pushMessage = new PushMessage(l.getSourceId(), textMessage);
            PushMessage pushMessage = new PushMessage(l.getSourceId(), messages);
            try {
                Response<BotApiResponse> response = LineMessagingServiceBuilder
                    .create(lineBotProperties.getChannelToken())
                    .build()
                    .pushMessage(pushMessage)
                    .execute();
                log.debug(response.code() + " " + response.message());
            } catch (Exception e) {
                log.debug("Exception occure :" + e.getMessage());
            }

        }
    }

    private String createUri(String url) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(url).build().toString();
    }

    private void sendAnnouncementEmail(Announcement announcement){
        List<User> users = userRepository.findAllBySubscribed(true);
        for (User user : users) {
            mailService.sendAnnouncemenceEmail(user, announcement.getSubject(), announcement.getId());
        }
    }
    /**
     * PUT  /announcements : Updates an existing announcement.
     *
     * @param announcement the announcement to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated announcement,
     * or with status 400 (Bad Request) if the announcement is not valid,
     * or with status 500 (Internal Server Error) if the announcement couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/announcements",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Announcement> updateAnnouncement(@Valid @RequestBody Announcement announcement, HttpServletRequest request) throws URISyntaxException {
        log.debug("REST request to update Announcement : {}", announcement);
        if (announcement.getId() == null) {
            return createAnnouncement(announcement, request);
        }
        Announcement result = announcementService.save(announcement);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("announcement", announcement.getId().toString()))
            .body(result);
    }

    /**
     * GET  /announcements : get all the announcements.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of announcements in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/announcements",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Announcement>> getAllAnnouncements(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Announcements");
        Page<Announcement> page = announcementService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/announcements");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /announcements/:id : get the "id" announcement.
     *
     * @param id the id of the announcement to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the announcement, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/announcements/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Announcement> getAnnouncement(@PathVariable Long id) {
        log.debug("REST request to get Announcement : {}", id);
        Announcement announcement = announcementService.findOne(id);
        return Optional.ofNullable(announcement)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /announcements/:id : delete the "id" announcement.
     *
     * @param id the id of the announcement to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/announcements/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteAnnouncement(@PathVariable Long id) {
        log.debug("REST request to delete Announcement : {}", id);
        announcementService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("announcement", id.toString())).build();
    }

}

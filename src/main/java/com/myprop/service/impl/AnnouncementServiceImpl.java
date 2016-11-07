package com.myprop.service.impl;

import com.linecorp.bot.client.LineMessagingServiceBuilder;
import com.linecorp.bot.model.PushMessage;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.response.BotApiResponse;
import com.myprop.service.AnnouncementService;
import com.myprop.domain.Announcement;
import com.myprop.repository.AnnouncementRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import retrofit2.Response;

import javax.inject.Inject;
import java.util.List;

/**
 * Service Implementation for managing Announcement.
 */
@Service
@Transactional
public class AnnouncementServiceImpl implements AnnouncementService{

    private final Logger log = LoggerFactory.getLogger(AnnouncementServiceImpl.class);

    @Inject
    private AnnouncementRepository announcementRepository;

    /**
     * Save a announcement.
     *
     * @param announcement the entity to save
     * @return the persisted entity
     */
    public Announcement save(Announcement announcement) {
        log.debug("Request to save Announcement : {}", announcement);
        Announcement result = announcementRepository.save(announcement);

        try {
            this.pushTextMessage(result.getSubject());
        } catch (Exception e) {
            log.debug("push message to LINE" + e.getMessage());
        }


        return result;
    }



    private void pushTextMessage(String text) throws Exception{
        log.debug("send text message:");
        TextMessage textMessage = new TextMessage(text);
        PushMessage pushMessage = new PushMessage("C7a6e87b46b03a61531e574979d97b3ab", textMessage);

        Response<BotApiResponse> response = LineMessagingServiceBuilder
                .create("FkQLYskmNpPlm6VMwwcaJW8/zvajBTgx/dTkl0REcOVG6P/VhTA0refze5F2lFFbRz+UpghUZNmEa52ufewHoS8dNBQ3Ij2lu+yImbkHWSsBstKH0jlRIWXmkNWkchv9Wh2TgTgff77UGkWXG7U3WAdB04t89/1O/w1cDnyilFU=")
                .build()
                .pushMessage(pushMessage)
                .execute();
        log.debug(response.code() + " " + response.message());
    }


    /*
    @EventMapping
    private void handleTextMessageEvent(MessageEvent<TextMessageContent> event) throws Exception {
        log.debug("event: " + event);
        new TextMessage()
        new ReplyMessage(event.getReplyToken(), singletonList())
        final BotApiResponse apiResponse = lineMessageService
            .replyMessage(new ReplyMessage(event.getReplyToken(),
                singletonList(new TextMessage(event.getMessage().getText()))))
            .execute().body();
    }
    */
    /**
     *  Get all the announcements.
     *
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Announcement> findAll(Pageable pageable) {
        log.debug("Request to get all Announcements");
        Page<Announcement> result = announcementRepository.findAll(pageable);
        return result;
    }

    /**
     *  Get one announcement by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true)
    public Announcement findOne(Long id) {
        log.debug("Request to get Announcement : {}", id);
        Announcement announcement = announcementRepository.findOne(id);
        return announcement;
    }

    /**
     *  Delete the  announcement by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Announcement : {}", id);
        announcementRepository.delete(id);
    }
}

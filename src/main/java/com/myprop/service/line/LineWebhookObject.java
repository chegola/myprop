package com.myprop.service.line;

import com.linecorp.bot.client.LineMessagingService;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.event.*;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.event.source.GroupSource;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.profile.UserProfileResponse;
import com.linecorp.bot.model.response.BotApiResponse;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;
import com.myprop.domain.Announcement;
import com.myprop.domain.Line;
import com.myprop.repository.AnnouncementRepository;
import com.myprop.repository.LineRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.*;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import lombok.NonNull;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import retrofit2.Response;


/**
 * Created by che on 7/11/2559.
**/
@LineMessageHandler
public class LineWebhookObject {
    private static final String GROUP_SOURCE = "GroupSource";
    private static final String USER_SOURCE = "UserSource";

    private final Logger log = LoggerFactory.getLogger(LineWebhookObject.class);

    @Inject
    private LineMessagingService lineMessagingService;

    @Inject
    private LineRepository lineRepository;

    @Inject
    private AnnouncementRepository announcementRepository;

    @Inject
    private MessageSource messageSource;

    @EventMapping
    public void defaultMessageEvent(Event event) {
        log.info("default message event: " + event);
    }

    @EventMapping
    public void handleTextMessageEvent(MessageEvent<TextMessageContent> event) throws IOException {
        TextMessageContent message = event.getMessage();
        handleTextContent(event.getReplyToken(), event, message);

    }

    private void handleTextContent(String replyToken, Event event, TextMessageContent content)
        throws IOException {
        String text = content.getText();
        log.info("Got text message from {}: {}", replyToken, text);
        switch (text) {
            case "ข่าวสาร": {
                Page<Announcement> announcements = announcementRepository.findLastThree(new PageRequest(0, 3));
                StringBuilder sb = new StringBuilder();
                int i = 1;
                for (Announcement a : announcements) {
                    sb.append(i).append(".");
                    sb.append(a.getSubject()).append("\n");
                    sb.append("https://phrueklada.herokuapp.com/#/announcement/").append(a.getId()).append("\n\n");
                    i += 1;
                }
                this.replyText(replyToken, sb.toString());
                break;
            }
            case "ค่าส่วนกลาง" :{
                StringBuilder sb = new StringBuilder(messageSource.getMessage("line.public.account", null, Locale.ENGLISH));
                this.replyText(replyToken, sb.toString());
                break;
            }

            /*default: {
                StringBuilder sb = new StringBuilder(messageSource.getMessage("line.default.message", null, Locale.ENGLISH));
                this.replyText(replyToken, sb.toString());
            }*/

        }
    }
    @EventMapping
    public void handleJoinEvent(JoinEvent event) {
        // String replyToken = event.getReplyToken();
        // this.replyText(replyToken, "Joined " + event.getSource());
        log.info("Joined " + event.getSource());
        GroupSource groupSource = (GroupSource) event.getSource();
        String displayName = "";  // Group can't get group name
        addLineInfo(GROUP_SOURCE, groupSource.getGroupId(), Boolean.TRUE,
            event.getTimestamp().atZone(ZoneId.systemDefault()).toLocalDate(), displayName);
    }


    @EventMapping
    public void handleLeaveEvent(LeaveEvent event) {
        log.info("Leave event occur: {}", event);
        GroupSource groupSource = (GroupSource) event.getSource();
        lineRepository.findBySourceId(groupSource.getGroupId()).ifPresent(u -> {
            lineRepository.delete(u);
            log.debug("Deleted LINE : {}", u);
        });
    }

    @EventMapping
    public void handleUnfollowEvent(UnfollowEvent event) {
        log.info("Unfollow event occur: {}", event.getSource());
        lineRepository.findBySourceId(event.getSource().getUserId()).ifPresent(u -> {
            lineRepository.delete(u);
            log.debug("Deleted LINE : {}", u);
        });
    }

    @EventMapping
    public void handleFollowEvent(FollowEvent event) {
        //String replyToken = event.getReplyToken();
        //this.replyText(replyToken, "Got followed event");
        log.info("Got followed event " + event.getSource());
        String displayName;
        try {
            displayName = getLineProfile(event.getSource().getUserId());

        } catch (Exception e) {
            displayName = "";
        }
        addLineInfo(USER_SOURCE, event.getSource().getUserId(), Boolean.TRUE,
            event.getTimestamp().atZone(ZoneId.systemDefault()).toLocalDate(), displayName);
    }


    private void addLineInfo(String sourceType, String sourceId, Boolean active, LocalDate localDate, String displayName) {
        Line line = new Line();
        line.setSourceType(sourceType);
        line.setSourceId(sourceId);
        line.setActive(active);
        line.setTimestamp(localDate);
        line.setDisplayName(displayName);
        lineRepository.save(line);
        log.info("LINE account added: {}", line);
    }


    private void replyText(@NonNull String replyToken, @NonNull String message) {
        if (replyToken.isEmpty()) {
            throw new IllegalArgumentException("replyToken must not be empty");
        }
        if (message.length() > 1000) {
            message = message.substring(0, 1000 - 2) + "";
        }
        this.reply(replyToken, new TextMessage(message));
    }

    private void reply(@NonNull String replyToken, @NonNull Message message) {
        reply(replyToken, Collections.singletonList(message));
    }

    private void reply(@NonNull String replyToken, @NonNull List<Message> messages) {
        try {
            Response<BotApiResponse> apiResponse = lineMessagingService
                .replyMessage(new ReplyMessage(replyToken, messages))
                .execute();
            log.info("Sent messages: {}", apiResponse);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Get LINE display name during overnight.
     * <p>
     * This is scheduled to get fired everyday, at 01:00 (am).
     * </p>
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void getDisplayName() {
        lineRepository.findAllByDisplayName("").ifPresent(u -> {
            try {
                u.setDisplayName(getLineProfile(u.getSourceId()));
                lineRepository.save(u);
            } catch (IOException ex) {
                log.debug(ex.getMessage());
            }
        });
    }

    private String getLineProfile(String userId) throws IOException {
        log.info("LINE getProfile..." );
        Response<UserProfileResponse> response = lineMessagingService.getProfile(userId).execute();
        if (response.isSuccessful()) {
            UserProfileResponse profiles = response.body();
            log.info("Got displayName :" + profiles.getDisplayName());
            return profiles.getDisplayName();
        } else
            return "";
    }
}



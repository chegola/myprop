package com.myprop.service.line;

import com.linecorp.bot.client.LineMessagingService;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.event.*;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.event.source.GroupSource;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.response.BotApiResponse;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;
import com.myprop.domain.Line;
import com.myprop.repository.LineRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.*;
import java.util.Collections;
import java.util.List;

import lombok.NonNull;
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

    @EventMapping
    public void defaultMessageEvent(Event event) {
        log.info("default message event: " + event);
    }

    @EventMapping
    public void handleTextMessageEvent(MessageEvent<TextMessageContent> event) throws IOException {
        TextMessageContent message = event.getMessage();
    }

    @EventMapping
    public void handleJoinEvent(JoinEvent event) {
        // String replyToken = event.getReplyToken();
        // this.replyText(replyToken, "Joined " + event.getSource());
        log.info("Joined " + event.getSource());
        GroupSource groupSource = (GroupSource) event.getSource();
        addLineInfo(GROUP_SOURCE, groupSource.getGroupId(), Boolean.TRUE,
            event.getTimestamp().atZone(ZoneId.systemDefault()).toLocalDate());
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
        addLineInfo(USER_SOURCE, event.getSource().getUserId(), Boolean.TRUE,
            event.getTimestamp().atZone(ZoneId.systemDefault()).toLocalDate());
    }


    private void addLineInfo(String sourceType, String sourceId, Boolean active, LocalDate localDate) {
        Line line = new Line();
        line.setSourceType(sourceType);
        line.setSourceId(sourceId);
        line.setActive(active);
        line.setTimestamp(localDate);
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

}



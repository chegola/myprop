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

import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
                    sb.append("https://phrueklada.herokuapp.com/#/announcement/").append(a.getId()).append("\n");
                    i += 1;
                }
                this.replyText(replyToken, sb.toString());
                break;
            }
            case "ค่าส่วนกลาง" :{
                StringBuilder sb = new StringBuilder();
                sb.append("-ปี 2555 อัตรา 22 บาท/ตรว. ธ.กรุงเทพ เลขที่บัญชี 022-7-07907-6").append("\n\n");
                sb.append("-ปี 2556 อัตรา 30 บาท/ตรว. ธ.กสิกรไทย เลขที่บัญชี 572-2-28493-5").append("\n\n");
                sb.append("-ปี 2557-59 อัตรา 25บาท/ตรว. ธ.กสิกรไทย เลขที่บัญชี 572-2-28493-5").append("\n\n");
                sb.append("เมื่อชำระแล้วกรุณานำใบนำฝาก (pay-in slip) หรือสำเนามาติดต่อขอรับใบเสร็ขรับเงินได้ที่นำนักงานนิติบุคคลฯ หรือแจ้งทาง LINE ID: jangjang. (มีจุดลงท้าย) ").append("\n");
                sb.append("กรุณาระบุเลขที่บ้านของท่านไว้ในใบนำฝาก เพื่อความสะดวกรวดเร็วในการออกใบเสร็จรับเงิน. หากมีข้อสงสัยกรุณาติดต่อสำนักงานนิติบุคคลฯ").append("\n\n");
                sb.append("ด้วยความนับถืออย่างสูง").append("\n");

                this.replyText(replyToken, sb.toString());
                break;
            }

        }
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



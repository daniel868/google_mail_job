package org.example.google_mail_job;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.BatchDeleteMessagesRequest;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.google_mail_job.entities.JobExecution;
import org.example.google_mail_job.service.email.EmailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

@SpringBootTest
class GoogleMailJobApplicationTests {

    private static final Logger logger = LogManager.getLogger(GoogleMailJobApplicationTests.class);
    @Autowired
    private Gmail gmailService;
    @Autowired
    private EmailService emailService;

    @Test
    void contextLoads() {

    }

    @Test
    public void when_readFromGmail_returnNotNull() throws Exception {
        Gmail.Users.Messages.List buildQueryList = gmailService.
                users().
                messages()
                .list("me")
                .setLabelIds(asList("CATEGORY_SOCIAL"));

        ListMessagesResponse response = buildQueryList.execute();
        Assert.notNull(response.getMessages(), "Message list is null");
        logger.info(response.getMessages());
    }

    @Test
    public void when_readFromGmailAndDelete_returnSuccess() throws Exception {
        Gmail.Users.Messages.List buildQueryList = gmailService.
                users().
                messages()
                .list("me")
                .setLabelIds(asList("CATEGORY_SOCIAL"));
        ListMessagesResponse response = buildQueryList.execute();
        if (response.getMessages() == null || response.getMessages().isEmpty()) {
            logger.info("Empty messages");
            return;
        }
        List<String> idsToDelete = response.getMessages().stream()
                .map(Message::getId)
                .collect(Collectors.toList());
        BatchDeleteMessagesRequest request = new BatchDeleteMessagesRequest();
        request.setIds(idsToDelete);
        Gmail.Users.Messages.BatchDelete batchDeleteRequest = gmailService.users().messages().batchDelete("me", request);
        batchDeleteRequest.execute();
    }

    @Test
    public void when_deleteBatchEmails_returnSuccess() throws Exception {
        emailService.deleteBatchEmails(new ArrayList<>(), new JobExecution());
    }

}

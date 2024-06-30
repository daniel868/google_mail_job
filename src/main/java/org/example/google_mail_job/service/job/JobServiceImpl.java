package org.example.google_mail_job.service.job;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.google_mail_job.config.AppConstants;
import org.example.google_mail_job.config.GmailCategory;
import org.example.google_mail_job.entities.JobExecution;
import org.example.google_mail_job.event.InsertEmailEvent;
import org.example.google_mail_job.service.email.EmailServiceImpl;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class JobServiceImpl implements JobService {
    private static final Logger logger = LogManager.getLogger(EmailServiceImpl.class);

    private final Gmail gmailService;
    private final ApplicationEventPublisher eventPublisher;

    public JobServiceImpl(Gmail gmail, ApplicationEventPublisher eventPublisher) {
        this.gmailService = gmail;
        this.eventPublisher = eventPublisher;
    }

    public void fetchEmailsByCategory(GmailCategory category, JobExecution jobExecution) {
        logger.debug("start processing emails batch by category: {}", category);
        long start = System.currentTimeMillis();

        try {
            Gmail.Users.Messages.List listRequest = gmailService.users().
                    messages().
                    list(AppConstants.GMAIL_USER).
                    setLabelIds(Collections.singletonList(category.getValue()));

            ListMessagesResponse response = listRequest.execute();

            while (response.getMessages() != null) {

                publishNewInsertEmailEvent(jobExecution, response.getMessages());

                String nextPageToken = response.getNextPageToken();

                if (nextPageToken == null) {
                    break;
                }

                listRequest = gmailService.users().
                        messages().
                        list(AppConstants.GMAIL_USER).
                        setLabelIds(Collections.singletonList(category.getValue()))
                        .setPageToken(nextPageToken);
                response = listRequest.execute();

            }

        } catch (Exception e) {
            logger.error("Error retrieving emails batch by category: {}", category);
        }


        logger.debug("finishing processing emails batch by category: {} in {} seconds", category, ((System.currentTimeMillis() - start) / 1000));
    }

    private void publishNewInsertEmailEvent(JobExecution jobExecution, List<Message> messages) {
        InsertEmailEvent event = new InsertEmailEvent(this, messages, jobExecution);
        eventPublisher.publishEvent(event);
    }

}

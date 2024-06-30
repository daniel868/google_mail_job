package org.example.google_mail_job.listener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.google_mail_job.event.InsertEmailEvent;
import org.example.google_mail_job.service.email.EmailService;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

@Service
public class InsertEmailEventListener implements ApplicationListener<InsertEmailEvent> {
    private static final Logger logger = LogManager.getLogger(InsertEmailEventListener.class);

    private final EmailService emailService;
    private final TransactionTemplate transactionTemplate;

    public InsertEmailEventListener(EmailService emailService, TransactionTemplate transactionTemplate) {
        this.emailService = emailService;
        this.transactionTemplate = transactionTemplate;
    }

    @Override
    @Async
    public void onApplicationEvent(InsertEmailEvent event) {
        long startTime = System.currentTimeMillis();
        logger.info("Insert email event received with size: {}", event.getPayload().size());
        transactionTemplate.executeWithoutResult((status) -> {
            emailService.insertBatchEmails(event.getPayload(), event.getCurrentJobExecution());
        });
        logger.info("Insert email event completed in {} s", (System.currentTimeMillis() - startTime) / 1000);
    }
}

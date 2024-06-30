package org.example.google_mail_job.job;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.google_mail_job.config.GmailCategory;
import org.example.google_mail_job.entities.Email;
import org.example.google_mail_job.entities.EmailStatus;
import org.example.google_mail_job.entities.JobExecution;
import org.example.google_mail_job.repository.JobExecutionRepository;
import org.example.google_mail_job.service.email.EmailService;
import org.example.google_mail_job.service.job.JobService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
public class DeleteEmailJob {
    private final Logger logger = LogManager.getLogger(DeleteEmailJob.class);

    @Value("${enable.delete.job}")
    private boolean isActiveJob;

    private final JobService jobService;
    private final EmailService emailService;
    private final JobExecutionRepository jobExecutionRepository;
    private final TransactionTemplate transactionTemplate;

    public DeleteEmailJob(JobService jobService, EmailService emailService, JobExecutionRepository jobExecutionRepository, TransactionTemplate transactionTemplate) {
        this.jobService = jobService;
        this.emailService = emailService;
        this.jobExecutionRepository = jobExecutionRepository;
        this.transactionTemplate = transactionTemplate;
    }

    @Scheduled(fixedRate = 100_000)
    public void onJobExecution() {
        /*

        1 - saved asynchronous batch emails (with event publisher) for each category into database
        2 - read in batch from database where emails == INSERTED
        3 - delete in batch and update in db
         */
        logger.debug("Job execution started");
        if (!isActiveJob) {
            logger.debug("Job execution is disabled");
            return;
        }

        JobExecution jobExecution = transactionTemplate.execute((status) -> initJobExecution());

        /*
            step 1 - saved asynchronous batch emails (with event publisher) for each category into database
         */
        Arrays.stream(GmailCategory.values()).forEach(gmailCategory -> {
            logger.debug("Started execution for category: {}", gmailCategory);
            jobService.fetchEmailsByCategory(gmailCategory, jobExecution);
        });

        /*
            step 2 - read in batch from database where emails == INSERTED
            step 3 - delete in batch and update in db
         */
        Boolean isRunning = true;
        while (Boolean.TRUE.equals(isRunning)) {
            isRunning = transactionTemplate.execute((status) -> {
                List<Email> batchEmails = emailService.fetchEmailBatchByStatus(EmailStatus.INSERTED, 50);
                logger.debug("Found emails batch with size: {}", batchEmails.size());
                if (batchEmails.isEmpty()) {
                    logger.info("No batch emails found; Exiting from transaction");
                    return false;
                }
                logger.debug("Prepare to delete batch emails with size: {}", batchEmails.size());
                emailService.deleteBatchEmails(batchEmails, jobExecution);
                logger.debug("Deleted batch emails with size: {}", batchEmails.size());
                return true;
            });
        }

        if (jobExecution != null) {
            transactionTemplate.executeWithoutResult((status) -> finishJobExecution(jobExecution));
        }
        logger.debug("Job execution finished");
    }

    private JobExecution initJobExecution() {
        JobExecution jobExecution = new JobExecution();
        jobExecution.setCreatedDate(new Date());
        return jobExecutionRepository.save(jobExecution);
    }

    private void finishJobExecution(JobExecution jobExecution) {
        long jobDurationInSeconds = (System.currentTimeMillis() - jobExecution.getCreatedDate().getTime()) / 1000;
        jobExecution.setUpdatedDate(new Date());
        jobExecution.setDuration(jobDurationInSeconds);
        jobExecutionRepository.save(jobExecution);
    }
}

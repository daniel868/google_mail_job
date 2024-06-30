package org.example.google_mail_job.service.email;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.BatchDeleteMessagesRequest;
import com.google.api.services.gmail.model.Message;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.google_mail_job.config.AppConstants;
import org.example.google_mail_job.entities.Email;
import org.example.google_mail_job.entities.EmailStatus;
import org.example.google_mail_job.entities.JobExecution;
import org.example.google_mail_job.repository.EmailRepository;
import org.example.google_mail_job.repository.JobExecutionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class EmailServiceImpl implements EmailService {
    private static final Logger logger = LogManager.getLogger(EmailServiceImpl.class);

    private final Gmail gmailService;
    private final EmailRepository emailRepository;
    private final JobExecutionRepository jobExecutionRepository;
    private final EntityManagerFactory entityManagerFactory;

    public EmailServiceImpl(Gmail gmailService, EmailRepository emailRepository, JobExecutionRepository jobExecutionRepository, EntityManagerFactory entityManagerFactory) {
        this.gmailService = gmailService;
        this.emailRepository = emailRepository;
        this.jobExecutionRepository = jobExecutionRepository;
        this.entityManagerFactory = entityManagerFactory;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteBatchEmails(List<Email> emailsToBeDeleted, JobExecution jobExecution) {
        List<String> batchIdsToDelete = emailsToBeDeleted.stream()
                .map(Email::getEmailId)
                .toList();

        logger.info("Deleting batch emails: {}", batchIdsToDelete);

        BatchDeleteMessagesRequest request = new BatchDeleteMessagesRequest();
        request.setIds(batchIdsToDelete);

        try {
            Gmail.Users.Messages.BatchDelete batchDelete = gmailService.users().messages().batchDelete(AppConstants.GMAIL_USER, request);
            batchDelete.execute();
        } catch (Exception e) {
            logger.error("Error deleting batch emails: {}", batchIdsToDelete);
            throw new RuntimeException(e);
        }

        emailsToBeDeleted.forEach(email -> {
            email.setStatus(EmailStatus.DELETED);
            email.setUpdatedDate(new Date());
            emailRepository.save(email);
            synchronized (jobExecution) {
                int totalEmailDeleted = jobExecution.getTotalEmailDeleted();
                totalEmailDeleted++;
                jobExecution.setTotalEmailDeleted(totalEmailDeleted);
            }
        });

        jobExecutionRepository.save(jobExecution);
        logger.info("Successfully deleted emails: {} with job execution context: {}", batchIdsToDelete, jobExecution);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void insertBatchEmails(List<Message> emailsToBeInserted,
                                  JobExecution currentJobExecution) {
        logger.debug("Inserting batch emails: {} with currentJob: {}", emailsToBeInserted, currentJobExecution);

        emailsToBeInserted.forEach(message -> {
            Email email = new Email();
            email.setEmailId(message.getId());
            email.setStatus(EmailStatus.INSERTED);
            email.setCreatedDate(new Date());
            Email savedEmail = emailRepository.save(email);
            currentJobExecution.addNewEmail(savedEmail);
        });

        logger.debug("Finishing inserting batch emails: {} with currentJob: {}", emailsToBeInserted, currentJobExecution);
    }

    public List<Email> fetchEmailBatchByStatus(EmailStatus status, int bachSize) {
        EntityManager em = entityManagerFactory.createEntityManager();
        TypedQuery<Email> batchQuery = em.createNamedQuery("findAllEmailsByStatus", Email.class);
        batchQuery.setMaxResults(bachSize);
        batchQuery.setParameter("status", status);
        List<Email> response = batchQuery.getResultList();
        em.close();
        return response;
    }
}

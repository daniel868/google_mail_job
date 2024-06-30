package org.example.google_mail_job.service.email;

import com.google.api.services.gmail.model.Message;
import org.example.google_mail_job.config.GmailCategory;
import org.example.google_mail_job.entities.Email;
import org.example.google_mail_job.entities.EmailStatus;
import org.example.google_mail_job.entities.JobExecution;

import java.util.List;

public interface EmailService {
    void deleteBatchEmails(List<Email> emailsToBeDeleted, JobExecution jobExecution);

    void insertBatchEmails(List<Message> emailsToBeInserted, JobExecution currentJobExecution);

    List<Email> fetchEmailBatchByStatus(EmailStatus status, int bachSize);
}

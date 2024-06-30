package org.example.google_mail_job.service.job;

import org.example.google_mail_job.config.GmailCategory;
import org.example.google_mail_job.entities.JobExecution;

public interface JobService {
    void fetchEmailsByCategory(GmailCategory category, JobExecution jobExecution);
}

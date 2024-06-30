package org.example.google_mail_job.repository;

import org.example.google_mail_job.entities.JobExecution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobExecutionRepository extends JpaRepository<JobExecution, Long> {

}

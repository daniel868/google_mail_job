package org.example.google_mail_job.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "gm_job_execution")
@Getter
@Setter
public class JobExecution {

    @Id
    @SequenceGenerator(name = "job_id_generator", allocationSize = 1, sequenceName = "job_id_generator")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "job_id_generator")
    private Long id;

    @Column(name = "created_date")
    private Date createdDate;

    @Column(name = "updated_date")
    private Date updatedDate;

    @Column(name = "duration")
    private Long duration;

    @Column(name = "total_email_deleted")
    private int totalEmailDeleted;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_execution_id")
    private Set<Email> emails;


    public void addNewEmail(Email email) {
        if (emails == null) {
            emails = new HashSet<>();
        }
        emails.add(email);
    }
}

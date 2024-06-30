package org.example.google_mail_job.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "gm_email")
@NamedQueries(value = {
        @NamedQuery(
                name = "findAllEmailsByStatus",
                query = "select email from Email email where email.status =: status"
        )
})
public class Email {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "email_id_generator")
    @SequenceGenerator(name = "email_id_generator", sequenceName = "email_id_generator", allocationSize = 1)
    private Long id;

    @Column(name = "email_id", nullable = false)
    private String emailId;

    @Column(name = "created_date")
    private Date createdDate;

    @Column(name = "updated_date")
    private Date updatedDate;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private EmailStatus status;

}

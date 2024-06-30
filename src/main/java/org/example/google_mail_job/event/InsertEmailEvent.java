package org.example.google_mail_job.event;

import com.google.api.services.gmail.model.Message;
import lombok.Getter;
import org.example.google_mail_job.entities.JobExecution;
import org.springframework.context.ApplicationEvent;

import java.util.List;

@Getter
public class InsertEmailEvent extends ApplicationEvent {
    private final List<Message> payload;
    private final JobExecution currentJobExecution;

    public InsertEmailEvent(Object source, List<Message> payload, JobExecution currentJobExecution) {
        super(source);
        this.payload = payload;
        this.currentJobExecution = currentJobExecution;
    }

}

package org.example.google_mail_job.listener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class ApplicationStartedEventListener implements ApplicationListener<ContextRefreshedEvent> {
    private final Logger logger = LogManager.getLogger(ApplicationStartedEventListener.class);

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        logger.debug("StartupApplicationListener - Application initialize");
    }
}

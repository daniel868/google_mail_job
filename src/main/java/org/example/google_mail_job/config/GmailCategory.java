package org.example.google_mail_job.config;

import lombok.Getter;

@Getter
public enum GmailCategory {
    CATEGORY_FORUMS("CATEGORY_FORUMS"),
    CATEGORY_UPDATES("CATEGORY_UPDATES"),
    CATEGORY_PROMOTIONS("CATEGORY_PROMOTIONS"),
    CATEGORY_SOCIAL("CATEGORY_SOCIAL"),
    TRASH("TRASH"),
    DRAFT("DRAFT"),
    SPAM("SPAM"),
    CHAT("CHAT");
    private final String value;

    GmailCategory(String value) {
        this.value = value;
    }

}

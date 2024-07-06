package org.example.google_mail_job.config;

import com.google.api.services.gmail.GmailScopes;

import java.util.Arrays;
import java.util.List;

public final class AppConstants {
    public static final String APPLICATION_NAME = "google_mail_job";
    public static final List<String> SCOPES = Arrays.asList(
            GmailScopes.MAIL_GOOGLE_COM,
            GmailScopes.GMAIL_LABELS,
            GmailScopes.GMAIL_READONLY,
            GmailScopes.GMAIL_MODIFY);
    public static final String TOKENS_DIRECTORY_PATH = "tokens";
    public static final String GMAIL_USER = "me";
    public static final String HEADER_ARG = "-D";
    public static final String TYPE_ARG = "-Dtype";
    public static final String CLIENT_ID_ARG = "-Dclient_id";
    public static final String CLIENT_SECRET_ARG = "-Dclient_secret";
    public static final String REFRESH_TOKEN_ARG = "-Drefresh_token";
    public static final int CREDENTIALS_ARGS_SIZE = 4;

}

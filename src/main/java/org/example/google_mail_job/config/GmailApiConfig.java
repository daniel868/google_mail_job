package org.example.google_mail_job.config;

import ch.qos.logback.core.joran.sanity.Pair;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.Gmail;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

@Configuration
public class GmailApiConfig {

    private static final Logger logger = LogManager.getLogger(GmailApiConfig.class);


    @Bean
    public JsonFactory jacksonFactory() {
        return GsonFactory.getDefaultInstance();
    }

    @Bean
    public NetHttpTransport httpTransport() throws IOException, GeneralSecurityException {
        return GoogleNetHttpTransport.newTrustedTransport();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public Credential userGoogleCredential(ObjectMapper objectMapper) {
        try {
            Map<String, String> credentials = new HashMap<>();
            RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
            runtimeMXBean.getInputArguments()
                    .stream()
                    .filter(filterVmArgs)
                    .map(vmArg -> vmArg.replace(AppConstants.HEADER_ARG, ""))
                    .forEach(vmArgs -> {
                        String[] keyPair = vmArgs.split("=");
                        credentials.put(keyPair[0], keyPair[1]);
                    });

            logger.info("Retrieved credentials: {}", credentials);

            if (credentials.size()!=AppConstants.CREDENTIALS_ARGS_SIZE){
                throw new IllegalArgumentException("Invalid credentials arguments number");
            }

            byte[] credentialsAsBytes = objectMapper.writeValueAsBytes(credentials);

            return GoogleCredential.fromStream(
                    new ByteArrayInputStream(credentialsAsBytes)
            );
        } catch (Exception e) {
            logger.error("Exception on reading credentials json:", e);
            throw new RuntimeException(e);
        }
    }

    private final Predicate<String> filterVmArgs = vmArg -> vmArg.startsWith(AppConstants.TYPE_ARG) ||
            vmArg.startsWith(AppConstants.CLIENT_ID_ARG) ||
            vmArg.startsWith(AppConstants.CLIENT_SECRET_ARG) ||
            vmArg.startsWith(AppConstants.REFRESH_TOKEN_ARG);


    @Bean
    public Gmail gmailService(NetHttpTransport transport,
                              Credential credential,
                              JsonFactory jsonFactory) {
        return new Gmail.Builder(transport, jsonFactory, credential)
                .setApplicationName(AppConstants.APPLICATION_NAME)
                .build();
    }
}

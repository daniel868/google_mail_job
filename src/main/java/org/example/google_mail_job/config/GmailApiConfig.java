package org.example.google_mail_job.config;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;

@Configuration
public class GmailApiConfig {

    private static final Logger logger = LogManager.getLogger(GmailApiConfig.class);


    @Value("${google.secrets.path}")
    private Resource googleSecretsPathResource;

    @Bean
    public JsonFactory jacksonFactory() {
        return GsonFactory.getDefaultInstance();
    }

    @Bean
    public NetHttpTransport httpTransport() throws IOException, GeneralSecurityException {
        return GoogleNetHttpTransport.newTrustedTransport();
    }

    @Bean
    public Credential googleCredential(JsonFactory jsonFactory,
                                       NetHttpTransport transport) throws IOException {
        try (InputStream in = googleSecretsPathResource.getInputStream()) {
            GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(jsonFactory, new InputStreamReader(in));
            var flow = new GoogleAuthorizationCodeFlow.Builder(
                    transport, jsonFactory, clientSecrets, AppConstants.SCOPES)
                    .setDataStoreFactory(new FileDataStoreFactory(new File(AppConstants.TOKENS_DIRECTORY_PATH)))
                    .setAccessType("offline")
                    .build();


            LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
            //returns an authorized Credential object.
            return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
        } catch (IOException e) {
            logger.error("Exception on reading credentials json:", e);
            throw e;
        } catch (Exception e) {
            logger.error("Error:", e);
            throw new RuntimeException(e);
        }
    }

    @Bean
    public Gmail gmailService(NetHttpTransport transport,
                              Credential credential,
                              JsonFactory jsonFactory) {
        return new Gmail.Builder(transport, jsonFactory, credential)
                .setApplicationName(AppConstants.APPLICATION_NAME)
                .build();
    }
}

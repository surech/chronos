package ch.surech.chronos.chronosanalyser.service;

import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.microsoft.graph.authentication.TokenCredentialAuthProvider;
import com.microsoft.graph.models.User;
import com.microsoft.graph.options.QueryOption;
import com.microsoft.graph.requests.EventCollectionPage;
import com.microsoft.graph.requests.GraphServiceClient;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AuthentificationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthentificationService.class);

    @Value("${microsoft.graph.auth.clientId}")
    private String clientId;

    @Value("${microsoft.graph.auth.clientSecret}")
    private String clientSecret;

    @Value("${microsoft.graph.auth.authorityHost}")
    private String authorityHost;

    @Value("${microsoft.graph.auth.tenantId}")
    private String tenantId;

    @Value("${microsoft.graph.auth.scopes}")
    private String[] appScopes;

    public void makeConnection(){
        LOGGER.info("ClientID: {}", clientId);
        LOGGER.info("Client Secret: {}", clientSecret);
        LOGGER.info("Authority Host: {}", authorityHost);
        LOGGER.info("Tenant ID: {}", tenantId);
        LOGGER.info("Scopes: {}", appScopes);

        final ClientSecretCredential clientSecretCredential = new ClientSecretCredentialBuilder()
            .clientId(clientId)
            .clientSecret(clientSecret)
            .authorityHost(authorityHost)
            .tenantId(tenantId)
            .build();

        final TokenCredentialAuthProvider tokenCredentialAuthProvider = new TokenCredentialAuthProvider(List.of(appScopes), clientSecretCredential);

        final GraphServiceClient graphClient =
            GraphServiceClient
                .builder()
                .authenticationProvider(tokenCredentialAuthProvider)
                .buildClient();

        User me = graphClient.users("stefan.urech@sbb.ch").buildRequest().get();
        LOGGER.info(me.displayName);
    }
}


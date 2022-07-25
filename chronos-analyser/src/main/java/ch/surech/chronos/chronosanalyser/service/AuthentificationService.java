package ch.surech.chronos.chronosanalyser.service;

import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.microsoft.graph.authentication.TokenCredentialAuthProvider;
import com.microsoft.graph.models.User;
import com.microsoft.graph.requests.GraphServiceClient;
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
        this.LOGGER.info("ClientID: {}", clientId);
        this.LOGGER.info("Client Secret: {}", clientSecret);
        this.LOGGER.info("Authority Host: {}", authorityHost);
        this.LOGGER.info("Tenant ID: {}", tenantId);
        this.LOGGER.info("Scopes: {}", appScopes);

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

        final User me = graphClient.me().buildRequest().get();
        LOGGER.info(me.displayName);
    }
}


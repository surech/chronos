package ch.surech.chronos.chronosanalyser.service;

import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.microsoft.graph.authentication.TokenCredentialAuthProvider;
import com.microsoft.graph.models.User;
import com.microsoft.graph.requests.GraphServiceClient;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AuthentificationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthentificationService.class);

    public void makeConnection(){
        final ClientSecretCredential clientSecretCredential = new ClientSecretCredentialBuilder()
            .clientId("xx")
            .clientSecret("xx")
            .authorityHost("xx")
            .tenantId("xx")
            .build();

        final TokenCredentialAuthProvider tokenCredentialAuthProvider = new TokenCredentialAuthProvider(List.of("xx"), clientSecretCredential);

        final GraphServiceClient graphClient =
            GraphServiceClient
                .builder()
                .authenticationProvider(tokenCredentialAuthProvider)
                .buildClient();

        final User me = graphClient.me().buildRequest().get();
        LOGGER.info(me.displayName);
    }
}


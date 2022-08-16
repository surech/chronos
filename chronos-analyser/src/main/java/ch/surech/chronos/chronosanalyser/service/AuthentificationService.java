package ch.surech.chronos.chronosanalyser.service;

import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.microsoft.aad.msal4j.ClaimsRequest;
import com.microsoft.aad.msal4j.DeviceCode;
import com.microsoft.aad.msal4j.DeviceCodeFlowParameters;
import com.microsoft.aad.msal4j.IAuthenticationResult;
import com.microsoft.aad.msal4j.PublicClientApplication;
import com.microsoft.graph.authentication.TokenCredentialAuthProvider;
import com.microsoft.graph.models.User;
import com.microsoft.graph.options.QueryOption;
import com.microsoft.graph.requests.EventCollectionPage;
import com.microsoft.graph.requests.GraphServiceClient;
import java.net.MalformedURLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AuthentificationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthentificationService.class);

    @Value("${microsoft.graph.auth.clientId}")
    private String clientId;

    @Value("${microsoft.graph.auth.authorityHost}")
    private String authorityHost;

    @Value("${microsoft.graph.auth.scopes}")
    private String[] appScopes;

    @Value("${microsoft.graph.auth.authToken:}")
    private String preSetAuthToken;

    private IAuthenticationResult authenticationResult;

    public void signIn(){
        // Are we allready signed in?
        if(authenticationResult != null || StringUtils.isNotEmpty(preSetAuthToken)){
            LOGGER.info("Already logged in, nothing to do here");
            return;
        }

        PublicClientApplication app;
        try {
            // Build the MSAL application object with
            // app ID and authority
            app = PublicClientApplication.builder(clientId)
                .authority(authorityHost)
                .build();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        // Create consumer to receive the DeviceCode object
        // This method gets executed during the flow and provides
        // the URL the user logs into and the device code to enter
        Consumer<DeviceCode> deviceCodeConsumer = (DeviceCode deviceCode) -> {
            // Print the login information to the console
            LOGGER.info(deviceCode.message());
        };

        // Request a token, passing the requested permission scopes
        this.authenticationResult = app.acquireToken(
            DeviceCodeFlowParameters
                .builder(Set.of(appScopes), deviceCodeConsumer)
                .build()
        ).exceptionally(ex -> {
            LOGGER.warn("Unable to authenticate - " + ex.getMessage());
            throw new RuntimeException("Unable to authenticate", ex);
        }).join();
    }

    public String getAccessToken(){
        return StringUtils.isNotEmpty(preSetAuthToken) ? preSetAuthToken : authenticationResult.accessToken();
    }
}


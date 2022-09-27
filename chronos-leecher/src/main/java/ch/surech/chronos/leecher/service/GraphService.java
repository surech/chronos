package ch.surech.chronos.leecher.service;

import com.azure.core.credential.TokenCredential;
import com.microsoft.graph.authentication.IAuthenticationProvider;
import com.microsoft.graph.authentication.TokenCredentialAuthProvider;
import com.microsoft.graph.requests.GraphServiceClient;
import okhttp3.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GraphService {

    private GraphServiceClient<Request> graphClient;

    @Autowired
    private AuthentificationService authentificationService;

    public GraphServiceClient getGraphClient(){
        if(graphClient == null){
            // Build graph client
            PresetTokenAuthenticationProvider tokenProvider = new PresetTokenAuthenticationProvider(authentificationService.getAccessToken());
            this.graphClient = GraphServiceClient.builder().authenticationProvider(tokenProvider).buildClient();
        }

        return graphClient;
    }
}

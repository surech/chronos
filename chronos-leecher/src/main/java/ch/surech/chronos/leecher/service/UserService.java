package ch.surech.chronos.leecher.service;

import com.microsoft.graph.models.User;
import com.microsoft.graph.requests.GraphServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private GraphService graphService;

    public User getUser(){
        User user = graphService.getGraphClient()
            .me()
            .buildRequest()
            .get();
        return user;
    }


    public User getUser(String principalName){
        GraphServiceClient graphClient = graphService.getGraphClient();

        return graphClient.users(principalName).buildRequest().get();
    }
}

package ch.surech.chronos.leecher.service;

import com.microsoft.graph.httpcore.HttpClients;
import com.microsoft.graph.requests.GraphServiceClient;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.Duration;

@Service
public class GraphService {

    private GraphServiceClient<Request> graphClient;

    private Bucket msGraphBucket;

    private RateLimitInterceptor rateLimitInterceptor;

    @Autowired
    private AuthentificationService authentificationService;

    @PostConstruct
    private void init(){
        // Refill every minute
        Refill refill = Refill.intervally(55, Duration.ofMinutes(1));

        // Allow 60 calls per minute
        Bandwidth bandwidth = Bandwidth.classic(55, refill);

        // Build the bucket
        this.msGraphBucket = Bucket.builder().addLimit(bandwidth).build();

        // Create Interceptor
        this.rateLimitInterceptor = new RateLimitInterceptor(msGraphBucket);
    }

    public GraphServiceClient getGraphClient(){
        if(graphClient == null){
            // Build graph client
            PresetTokenAuthenticationProvider tokenProvider = new PresetTokenAuthenticationProvider(authentificationService.getAccessToken());

            OkHttpClient httpClient = HttpClients.createDefault(tokenProvider).newBuilder().addInterceptor(rateLimitInterceptor).connectTimeout(30, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).build();

            this.graphClient = GraphServiceClient.builder().authenticationProvider(tokenProvider).httpClient(httpClient).buildClient();
        }

        return graphClient;
    }
}

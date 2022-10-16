package ch.surech.chronos.leecher.service;

import io.github.bucket4j.Bucket;
import okhttp3.Interceptor;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.SocketTimeoutException;

public class RateLimitInterceptor implements Interceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(RateLimitInterceptor.class);

    private static final int DEFAULT_RETRIES = 3;

    private final Bucket bucket;

    private int callCounter = 0;

    public RateLimitInterceptor(Bucket bucket) {
        this.bucket = bucket;
    }

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        return intercept(chain, DEFAULT_RETRIES);
    }

    @NotNull
    public Response intercept(@NotNull Chain chain, int retries) throws IOException {

        while (!bucket.tryConsume(1)) {
            try {
                LOGGER.info("Call limit reached. Waiting for a free slot...");
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                LOGGER.warn("Error while sleeping: {}", e);
            }
        }
        // Run request
        try {
            LOGGER.info("[{}] Calling URL over HTTP: {}", callCounter++, chain.request().url());
            return chain.proceed(chain.request());
        } catch(SocketTimeoutException e){
            // Should we retry?
            if(retries > 0) {
                // Wait a moment, and then try again
                LOGGER.warn("Got an SocketTimeoutException. Wait a moment, and try again");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ex) {
                    LOGGER.warn("Error while sleeping", ex);
                }
                LOGGER.info("Try again...");
                return intercept(chain, retries - 1);
            } else {
                // No retries, rethrow exeption
                throw e;
            }
        }
    }
}

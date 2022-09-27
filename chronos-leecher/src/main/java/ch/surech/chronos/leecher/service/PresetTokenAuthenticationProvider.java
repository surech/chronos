package ch.surech.chronos.leecher.service;

import com.microsoft.graph.authentication.IAuthenticationProvider;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import org.jetbrains.annotations.NotNull;

public class PresetTokenAuthenticationProvider implements IAuthenticationProvider {

    private final String token;

    public PresetTokenAuthenticationProvider(String token) {
        this.token = token;
    }

    @NotNull
    @Override
    public CompletableFuture<String> getAuthorizationTokenAsync(@NotNull URL url) {
        return CompletableFuture.completedFuture(token);
    }
}

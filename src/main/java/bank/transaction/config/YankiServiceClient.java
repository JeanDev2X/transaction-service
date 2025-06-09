package bank.transaction.config;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import bank.transaction.entity.Wallet;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class YankiServiceClient {
	
	private final WebClient.Builder webClientBuilder;

    private static final String YANKI_SERVICE_URL = "http://localhost:8027/api/wallets";

    public Mono<Wallet> getWalletByPhoneNumber(String phoneNumber) {
        return webClientBuilder.build()
                .get()
                .uri(YANKI_SERVICE_URL + "/" + phoneNumber)
                .retrieve()
                .bodyToMono(Wallet.class);
    }

    public Mono<Wallet> updateWallet(Wallet wallet) {
        return webClientBuilder.build()
                .put()
                .uri(YANKI_SERVICE_URL + "/" + wallet.getPhoneNumber())
                .bodyValue(wallet)
                .retrieve()
                .bodyToMono(Wallet.class);
    }

}

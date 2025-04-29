package bank.transaction.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import bank.transaction.dto.AccountResponse;
import bank.transaction.dto.DebitCardDTO;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AccountServiceClient {
	
	private final WebClient.Builder webClientBuilder;

    private static final String ACCOUNT_SERVICE_URL = "http://localhost:8021/accounts";
    private static final String DEBIT_CARD_SERVICE_URL = "http://localhost:8021/debit-cards";

    public Mono<DebitCardDTO> getDebitCardByNumber(String debitCardNumber) {
        return webClientBuilder.build()
                .get()
                .uri(DEBIT_CARD_SERVICE_URL + "/{cardNumber}", debitCardNumber)
                .retrieve()
                .bodyToMono(DebitCardDTO.class);
    }

    public Mono<AccountResponse> getAccountByNumber(String accountNumber) {
        return webClientBuilder.build()
                .get()
                .uri(ACCOUNT_SERVICE_URL + "/by-account-number/{accountNumber}", accountNumber)
                .retrieve()
                .bodyToMono(AccountResponse.class);
    }

    public Mono<AccountResponse> updateAccount(AccountResponse account) {
        return webClientBuilder.build()
                .put()
                .uri(ACCOUNT_SERVICE_URL + "/{id}", account.getId())
                .bodyValue(account)
                .retrieve()
                .bodyToMono(AccountResponse.class);
    }
	
}

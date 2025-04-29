package bank.transaction.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import bank.transaction.entity.Wallet;
import reactor.core.publisher.Mono;

public interface WalletRepository extends ReactiveMongoRepository<Wallet, String>{
	Mono<Wallet> findByPhoneNumber(String phoneNumber);
}

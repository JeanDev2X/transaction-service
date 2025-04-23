package bank.transaction.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import bank.transaction.entity.TransactionConfigEntity;

public interface TransactionConfigRepository extends ReactiveMongoRepository<TransactionConfigEntity, String>{

}

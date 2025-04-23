package bank.transaction.service;

import bank.transaction.dto.TransactionConfigDTO;
import reactor.core.publisher.Mono;

public interface TransactionConfigCacheService {
	
	Mono<TransactionConfigDTO> getConfig();
    Mono<Void> updateConfig(TransactionConfigDTO config);
	
}

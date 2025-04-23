package bank.transaction.service.impl;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Service;

import bank.transaction.dto.TransactionConfigDTO;
import bank.transaction.entity.TransactionConfigEntity;
import bank.transaction.repository.TransactionConfigRepository;
import bank.transaction.service.TransactionConfigCacheService;
import reactor.core.publisher.Mono;

@Service
public class TransactionConfigCacheServiceImpl implements TransactionConfigCacheService{
	
	private static final String CONFIG_KEY = "transaction-config";

    @Autowired
    private TransactionConfigRepository configRepository;

    @Autowired
    private ReactiveRedisOperations<String, TransactionConfigEntity> redisOperations;

    @Override
    public Mono<TransactionConfigDTO> getConfig() {
        return redisOperations.opsForValue().get(CONFIG_KEY)
                .switchIfEmpty(
                    configRepository.findById("default")
                        .switchIfEmpty(
                            Mono.defer(() -> {
                                TransactionConfigEntity defaultConfig = TransactionConfigEntity.builder()
                                        .id("default")
                                        .maxFreeTransactions(5)
                                        .commissionAmount(new BigDecimal("1.50"))
                                        .build();
                                return configRepository.save(defaultConfig);
                            })
                        )
                        .flatMap(config -> {
                            redisOperations.opsForValue().set(CONFIG_KEY, config).subscribe();
                            return Mono.just(config);
                        })
                )
                .map(config -> new TransactionConfigDTO(config.getMaxFreeTransactions(), config.getCommissionAmount()));
    }

    @Override
    public Mono<Void> updateConfig(TransactionConfigDTO configDto) {
        TransactionConfigEntity entity = TransactionConfigEntity.builder()
                .id("default")
                .maxFreeTransactions(configDto.getMaxFreeTransactions())
                .commissionAmount(configDto.getCommissionAmount())
                .build();

        return configRepository.save(entity)
                .doOnNext(saved -> redisOperations.opsForValue().set(CONFIG_KEY, saved).subscribe())
                .then();
    }
}

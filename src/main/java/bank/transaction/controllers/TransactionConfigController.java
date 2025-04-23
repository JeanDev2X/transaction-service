package bank.transaction.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import bank.transaction.dto.TransactionConfigDTO;
import bank.transaction.service.TransactionConfigCacheService;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/config")
@RequiredArgsConstructor
public class TransactionConfigController {
	
	private final TransactionConfigCacheService configService;

    @GetMapping
    public Mono<TransactionConfigDTO> getConfig() {
        return configService.getConfig();
    }
    
    @PostMapping
    public Mono<Void> saveConfig(@RequestBody TransactionConfigDTO config) {
        return configService.updateConfig(config);
    }

    @PutMapping
    public Mono<Void> updateConfig(@RequestBody TransactionConfigDTO configDto) {
        return configService.updateConfig(configDto);
    }

}

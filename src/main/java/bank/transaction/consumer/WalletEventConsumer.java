package bank.transaction.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import bank.transaction.event.WalletEvent;
import bank.transaction.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class WalletEventConsumer {
	
	 private final ObjectMapper objectMapper = new ObjectMapper(); // Jackson para deserializar manualmente
	 private final TransactionService transactionService;

	    @KafkaListener(topics = "wallet-events", groupId = "transaction-service")
	    public void consume(String message) {
	        try {
	            WalletEvent walletEvent = objectMapper.readValue(message, WalletEvent.class);
	            log.info("Evento recibido en transaction-service: {}", walletEvent);

	            switch (walletEvent.getEventType()) {
	                case "WALLET_CREATED":
	                    log.info("Wallet created: {}", walletEvent.getWallet());
	                    break;
	                case "MONEY_SENT":
	                    log.info("Money sent from {} to {} amount {}", walletEvent.getFromPhoneNumber(), walletEvent.getToPhoneNumber(), walletEvent.getAmount());
	                    break;
	                case "MONEY_RECEIVED":
	                    log.info("Money received by {} amount {}", walletEvent.getToPhoneNumber(), walletEvent.getAmount());
	                    break;
	                case "LOAD_FROM_CARD":
	                    log.info("Money loaded from debit card {} to phone number {} amount {}", walletEvent.getDebitCardNumber(), walletEvent.getFromPhoneNumber(), walletEvent.getAmount());
	                    //registrar la transacción
	                    transactionService.processLoadFromCard(walletEvent);
	                    break;
	                default:
	                    log.warn("Unknown event type: {}", walletEvent.getEventType());
	            }
	        } catch (Exception e) {
	            log.error("Error deserializando el evento WalletEvent", e);
	        }
	    }

}

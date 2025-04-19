package bank.transaction.entity;

public enum TransactionType {
	DEPOSIT,
    WITHDRAWAL,
    PAYMENT,
    TRANSFER,
    DEBIT_CARD_PAYMENT,
    CREDIT_CARD_PAYMENT,
    CREDIT_PAYMENT // <-- nuevo tipo de transacción
}

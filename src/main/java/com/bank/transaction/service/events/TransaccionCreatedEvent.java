package com.bank.transaction.service.events;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class TransaccionCreatedEvent extends Event<Transaccion> {

}

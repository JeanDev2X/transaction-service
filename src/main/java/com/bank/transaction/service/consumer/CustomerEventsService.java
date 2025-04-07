//package com.bank.transaction.service.consumer;
//
//import lombok.extern.slf4j.Slf4j;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.stereotype.Component;
//
//import com.bank.transaction.service.entity.OperacionCuentaBanco;
//import com.bank.transaction.service.entity.TipoOperacionBanco;
//import com.bank.transaction.service.events.Event;
//import com.bank.transaction.service.events.Operacion;
//import com.bank.transaction.service.events.OperacionCreatedEvent;
//import com.bank.transaction.service.events.Orden;
//import com.bank.transaction.service.events.TransaccionCreatedEvent;
//import com.bank.transaction.service.events.Usuario;
//import com.bank.transaction.service.service.OperacionBancoService;
//
//@Slf4j
//@Component
//public class CustomerEventsService {
//
//	public static final String BANCO = "BCP";
//
//	@Autowired
//	private OperacionBancoService productoService;
//
//	@KafkaListener(topics = "${topic.operacion.name:operacion}", containerFactory = "kafkaListenerContainerFactory", groupId = "grupo1")
//	public void operacion(Event<?> event) {
//		if (event instanceof OperacionCreatedEvent) {
//			System.out.println("desde yanki");
//			handleOperacionCreatedEvent((OperacionCreatedEvent) event);
//		}
//	}
//
//	private void handleOperacionCreatedEvent(OperacionCreatedEvent event) {
//		log.info("Received Customer created event with Id={}, data={}", event.getId(), event.getData().toString());
//
//		OperacionCuentaBanco op = createOperacionCuentaBanco(event);
//		productoService.envioYanki(op);
//
//		System.out.println("Desde yanki");
//	}
//
//	private OperacionCuentaBanco createOperacionCuentaBanco(OperacionCreatedEvent event) {
//
//		OperacionCreatedEvent dataEve = (OperacionCreatedEvent) event;
//		Operacion data = dataEve.getData();
//
//		OperacionCuentaBanco op = new OperacionCuentaBanco();
//		TipoOperacionBanco tipoOp = new TipoOperacionBanco("1", data.getTipoOperacion());
//
//		op.setDni(data.getDni());
//		op.setCuenta_origen(data.getNumeroCelularOrigen());
//		op.setCuenta_destino(data.getNumeroCelularDestino());
//		op.setMontoPago(data.getMontoPago());
//		op.setTipoOperacion(tipoOp);
//		op.setCodigo_bancario_origen(BANCO);
//		op.setCodigo_bancario_destino(BANCO);
//
//		return op;
//	}
//
//	@KafkaListener(topics = "${topic.transaccion.name:transaccions}", containerFactory = "kafkaListenerContainerFactory", groupId = "grupo1")
//	public void transaccionBootcoin(Event<?> event) {		
//		log.info("EVENTO: {}", event);
//		
//		if (event instanceof TransaccionCreatedEvent) {
//	        TransaccionCreatedEvent transaccionCreatedEvent = (TransaccionCreatedEvent) event;
//	        log.info("TransaccionCreatedEvent with Id={}, data={}", transaccionCreatedEvent.getId(),
//	                transaccionCreatedEvent.getData());
//
//	        OperacionCuentaBanco op = createOperacionCuentaBanco(transaccionCreatedEvent);
//	        productoService.envioBoitcoin(op);
//	        log.info("Desde yanki");
//	    }
//		
//	}
//	
//	private OperacionCuentaBanco createOperacionCuentaBanco(TransaccionCreatedEvent transaccionCreatedEvent) {
//	    Orden orden = transaccionCreatedEvent.getData().getOrden();
//	    Usuario usuario = orden.getUsuario();
//
//	    OperacionCuentaBanco op = new OperacionCuentaBanco();
//	    TipoOperacionBanco tipoOp = new TipoOperacionBanco("4", "CuentaCuenta");
//	    op.setDni(usuario.getDni());
//	    op.setMontoPago(transaccionCreatedEvent.getData().getMontoPago());
//	    op.setTipoOperacion(tipoOp);
//	    op.setCodigo_bancario_origen(BANCO);
//	    op.setCodigo_bancario_destino(BANCO);
//
//	    if ("VENTA".equalsIgnoreCase(orden.getTipoOperacion())) {
//	        op.setCuenta_origen(transaccionCreatedEvent.getData().getDestino());
//	        op.setCuenta_destino(transaccionCreatedEvent.getData().getOrigen());
//	    } else if ("COMPRA".equalsIgnoreCase(orden.getTipoOperacion())) {
//	        op.setCuenta_origen(transaccionCreatedEvent.getData().getOrigen());
//	        op.setCuenta_destino(transaccionCreatedEvent.getData().getDestino());
//	    }
//
//	    return op;
//	}
//
//}

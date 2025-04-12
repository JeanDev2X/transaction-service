package com.bank.transaction.service;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import reactor.core.publisher.Flux;

@SpringBootApplication
public class TransactionApplication {
	
//	implements CommandLineRunner
	
//	@Autowired
//	private OperacionBancoService operacionService;
//
//	@Autowired
//	private TipoOperacionBancoService tipoOperacionService;
//	
//	@Autowired
//	private ReactiveMongoTemplate mongoTemplate;
//	
//	private static final Logger log = LoggerFactory.getLogger(TransactionApplication.class);
	
	public static void main(String[] args) {
		SpringApplication.run(TransactionApplication.class, args);
	}

//	@Override
//	public void run(String... args) throws Exception {
//		
//		mongoTemplate.dropCollection("Operaciones").subscribe();
//		mongoTemplate.dropCollection("TipoProducto").subscribe();
//		
//		TipoOperacionBanco deposito = new TipoOperacionBanco("1","Deposito");
//		TipoOperacionBanco retiro = new TipoOperacionBanco("2","Retiro");
//		TipoOperacionBanco cuentaCredito = new TipoOperacionBanco("3","CuentaCredito");
//		TipoOperacionBanco cuentaCuenta = new TipoOperacionBanco("4","CuentaCuenta");
//		
//		Flux.just(deposito,retiro,cuentaCredito,cuentaCuenta)
//		.flatMap(tipoOperacionService::saveTipoProducto)
//		.doOnNext(c -> {
//			log.info("Tipo de producto creado: " +  c.getDescripcion() + ", Id: " + c.getId());
//		}).thenMany(					
//				Flux.just(
//						//return serviceCredito.saveProducto(procredito);
//						new OperacionCuentaBanco("47305710","900001","", new Date(),deposito,1000.00,0.0,"ahorro","bcp","bcp"),
//						new OperacionCuentaBanco("47305710","900001","", new Date(),deposito,50.00,0.0,"ahorro","bcp","bcp"),
//						new OperacionCuentaBanco("47305710","900001","", new Date(),deposito,50.00,2.0,"ahorro","bcp","bcp"),
//						
//						new OperacionCuentaBanco("47305711","900003","", new Date(),retiro,2000.00,0.0,"corriente","bcp","bcp"),						
//						new OperacionCuentaBanco("47305711","900044","", new Date(),retiro,2000.00,0.0,"corriente","bcp","bcp"),
//						new OperacionCuentaBanco("47305712","900005","", new Date(),retiro,2000.00,0.0,"plazoFijo","bcp","bcp"),
//						
//						new OperacionCuentaBanco("47305713","900006","450004", new Date(),cuentaCredito,50.00,0.0,"ahorroVip","bcp","bcp"),
//						new OperacionCuentaBanco("47305711","900003","900044", new Date(),cuentaCuenta,100.00,0.0,"corriente","bcp","bcp"),
//						new OperacionCuentaBanco("47305713","900006","450004", new Date(),cuentaCredito,10.00,0.0,"ahorroVip","bcp","bcp"),
//						new OperacionCuentaBanco("47305711","900003","900044", new Date(),cuentaCuenta,10.00,0.0,"corriente","bcp","bcp")
//						
//						)					
//					.flatMap(operacion -> {
//						return operacionService.saveOperacion(operacion);
//					})					
//				).subscribe(operacion -> log.info("Insert: " + operacion.getCuenta_destino() 
//					+ " " + operacion.getCuenta_origen() + " " + operacion.getMontoPago()));
//		
//	}

}

package com.bank.transaction.service.service.impl;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bank.transaction.service.client.ProductoBancoClient;
import com.bank.transaction.service.client.ProductoBancoCreditoClient;
import com.bank.transaction.service.dto.CuentaBanco;
import com.bank.transaction.service.dto.CuentaYanki;
import com.bank.transaction.service.dto.TipoProducto;
import com.bank.transaction.service.entity.OperacionCuentaBanco;
import com.bank.transaction.service.entity.TipoOperacionBanco;
import com.bank.transaction.service.exception.RequestException;
import com.bank.transaction.service.repository.OperacionBancoDao;
import com.bank.transaction.service.service.OperacionBancoService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class OperacionBancoServiceImpl implements OperacionBancoService {

	Double comision = 0.0;

	private static final Logger log = LoggerFactory.getLogger(OperacionBancoServiceImpl.class);

	@Autowired
	public OperacionBancoDao productoDao;

	@Autowired
	public OperacionBancoDao tipoProductoDao;

	@Autowired
	private ProductoBancoClient productoBancoClient;

	@Autowired
	private ProductoBancoCreditoClient productoBancoCreditoClient;

	@Override
	public Flux<OperacionCuentaBanco> findAllOperacion() {
		return productoDao.findAll();

	}

	@Override
	public Mono<OperacionCuentaBanco> findByIdOperacion(String id) {
		return productoDao.findById(id);

	}

	@Override
	public Mono<OperacionCuentaBanco> saveOperacion(OperacionCuentaBanco producto) {
		return productoDao.save(producto);
	}

	// ----------------------------------------------------------------------------------------------

	// RETIROS - TRANSACCION : UPDATE-CUENTAS-SALDO - 2 TRACCIONES COBRA
	// COMISION(RETIRO O DEPOSITO) - TIPO TARGETA
	@Override
	public Mono<OperacionCuentaBanco> saveOperacionRetiro(OperacionCuentaBanco operacion) {
		// OBTENIENDO EL NUMERO DE CUENTA + EL BANCO AL QUE PERTENECE
		Mono<CuentaBanco> cuentaMono = productoBancoClient.findByNumeroCuenta(operacion.getCuenta_origen(),
	            operacion.getCodigo_bancario_origen());
		
		return cuentaMono.flatMap(cuenta -> {
	        double comision = obtenerComision(cuenta.getTipoProducto().getId(), cuenta.getSaldo());

	        // Contar el número de movimientos
	        Mono<Long> numMovimientosMono = productoDao
	                .consultaMovimientos(operacion.getDni(), operacion.getCuenta_origen(), cuenta.getCodigoBanco())
	                .count();

	        return numMovimientosMono.flatMap(numMovimientos -> {
	            if (numMovimientos > 2) {
	                operacion.setComision(comision);
	            }

	            // Realizar un retiro en el MS-Producto Bancario
	            Mono<CuentaBanco> opRetiroMono = productoBancoClient.retiroBancario(operacion.getCuenta_origen(),
	                    operacion.getMontoPago(), operacion.getComision(), operacion.getCodigo_bancario_origen());

	            return opRetiroMono.flatMap(cuentaRetiro -> {
	                if (cuentaRetiro.getNumeroCuenta() == null) {
	                    return Mono.error(new InterruptedException("Tarjeta inválida"));
	                }

	                // Registrar una transacción//refactorizar
	                TipoOperacionBanco tipoOperacion = new TipoOperacionBanco();
	                tipoOperacion.setId("2");
	                tipoOperacion.setDescripcion("retiro");
	                operacion.setTipoOperacion(tipoOperacion);

	                return productoDao.save(operacion);
	            });
	        });
	    });

	}
	
	private double obtenerComision(String tipoProductoId, double saldo) {
	    double comision = 0.0;
	    if ("1".equals(tipoProductoId)) { // ahorro
	        comision = 5.0;
	    } else if ("2".equals(tipoProductoId)) {// corriente
	        comision = 20.0;
	    } else if ("3".equals(tipoProductoId)) {// plazo fijo
	        comision = 30.0;
	    } else if ("4".equals(tipoProductoId) || "5".equals(tipoProductoId)) { // ahorro personal VIP o empresarial PYME
	        comision = 40.0;
	        if (saldo == 0) {
	            throw new RequestException("No se puede realizar retiros, monto mínimo en la cuenta S/.0");
	        }
	    }
	    return comision;
	}

	// DEPOSITO - 2 TRACCIONES COBRA COMISION(RETIRO O DEPOSITO)
	@Override
	public Mono<OperacionCuentaBanco> saveOperacionDeposito(OperacionCuentaBanco operacion) {
		// Obtener la cuenta de banco - cuenta bancaria
	    Mono<CuentaBanco> cuentaMono = productoBancoClient.findByNumeroCuenta(operacion.getCuenta_origen(),
	            operacion.getCodigo_bancario_origen());

	    return cuentaMono.flatMap(cuenta -> {
	        double comision = obtenerComision(cuenta.getTipoProducto().getId(), cuenta.getSaldo());
	        operacion.setProductoComision(cuenta.getTipoProducto().getDescripcion());

	        // Consultar el número de operaciones
	        Mono<Long> numMovimientosMono = productoDao
	                .consultaMovimientos(operacion.getDni(), operacion.getCuenta_origen(), cuenta.getCodigoBanco())
	                .count();

	        return numMovimientosMono.flatMap(numMovimientos -> {
	            if (numMovimientos > 2) {
	                operacion.setComision(comision);
	            }

	            // Realizar el depósito en la cuenta de banco
	            Mono<CuentaBanco> operacionMono = productoBancoClient.despositoBancario(operacion.getMontoPago(),
	                    operacion.getCuenta_origen(), operacion.getComision(), operacion.getCodigo_bancario_origen());

	            return operacionMono.flatMap(cuentaDeposito -> {
	                if (cuentaDeposito.getNumeroCuenta() == null) {
	                    return Mono.error(new InterruptedException("Tarjeta inválida"));
	                }

	                // Registrar una transacción
	                TipoOperacionBanco tipoOperacion = new TipoOperacionBanco();
	                tipoOperacion.setId("1");
	                tipoOperacion.setDescripcion("deposito");
	                operacion.setTipoOperacion(tipoOperacion);

	                return productoDao.save(operacion);
	            });
	        });
	    });

	}

	// DEPOSITO y RETIROS - OPERACION TRANSFERENCIA DE CUENTA A CUENTA
	@Override
	public Mono<OperacionCuentaBanco> operacionCuentaCuenta(OperacionCuentaBanco operacion) {
		// Obtener la cuenta de banco - cuenta bancaria
	    Mono<CuentaBanco> cuentaMono = productoBancoClient.findByNumeroCuenta(operacion.getCuenta_origen(),
	            operacion.getCodigo_bancario_origen());

	    cuentaMono.subscribe(cuenta -> System.out.println("Cliente: " + cuenta.toString()));

	    return cuentaMono.flatMap(cuenta -> {
	        double comision = obtenerComision(cuenta.getTipoProducto().getId(), cuenta.getSaldo());
	        if (comision > 0) {
	            operacion.setComision(comision);
	        }

	        // Consultar el número de operaciones
	        Mono<Long> numMovimientosMono = productoDao
	                .consultaMovimientos(operacion.getDni(), operacion.getCuenta_origen(), cuenta.getCodigoBanco())
	                .count();

	        return numMovimientosMono.flatMap(numMovimientos -> {
	            if (numMovimientos > 2) {
	                operacion.setComision(comision);
	            }

	            // Realizar el retiro en la cuenta de origen
	            Mono<CuentaBanco> retiroMono = productoBancoClient.retiroBancario(operacion.getCuenta_origen(),
	                    operacion.getMontoPago(), operacion.getComision(), operacion.getCodigo_bancario_origen());

	            return retiroMono.flatMap(retiro -> {
	                if (retiro.getNumeroCuenta() == null) {
	                    return Mono.error(new InterruptedException("Tarjeta inválida"));
	                }

	                // Realizar el depósito en la cuenta de destino
	                Mono<CuentaBanco> depositoMono = productoBancoClient.despositoBancario(operacion.getMontoPago(),
	                        operacion.getCuenta_destino(), operacion.getComision(), operacion.getCodigo_bancario_destino());

	                return depositoMono.flatMap(deposito -> {
	                    if (deposito.getNumeroCuenta() == null) {
	                        return Mono.error(new InterruptedException("Tarjeta inválida"));
	                    }

	                    // Registrar la operación de transferencia
	                    TipoOperacionBanco tipoOperacion = new TipoOperacionBanco();
	                    tipoOperacion.setId("4");
	                    tipoOperacion.setDescripcion("cuentaCuenta");
	                    operacion.setTipoOperacion(tipoOperacion);

	                    return productoDao.save(operacion);
	                });
	            });
	        });
	    });
	}

	// PAGO DE CUENTA CREDITO CON UNA CUENTA DE BANCO
	@Override
	public Mono<OperacionCuentaBanco> saveOperacionCuentaCuentaCredito(OperacionCuentaBanco operacion) {

		// Obtener la cuenta de banco
	    Mono<CuentaBanco> cuentaBancoMono = productoBancoClient.findByNumeroCuenta(operacion.getCuenta_origen(),
	            operacion.getCodigo_bancario_origen());
	    cuentaBancoMono.subscribe(cuenta -> System.out.println("Cliente: " + cuenta.toString()));

	    // Obtener la cuenta de crédito
	    Mono<CuentaBanco> cuentaCreditoMono = productoBancoCreditoClient.findByNumeroCuentaCredito(
	            operacion.getCuenta_destino(), operacion.getCodigo_bancario_destino());

	    return cuentaCreditoMono.defaultIfEmpty(new CuentaBanco()).flatMap(cuentaCredito -> {
	        if (cuentaCredito.getCodigoBanco() == null) {
	            throw new RequestException("La cuenta de crédito no existe y no se puede realizar el pago.");
	        }

	        return cuentaBancoMono.flatMap(cuentaBanco -> {
	            double comision = obtenerComision(cuentaBanco.getTipoProducto().getId(),
	                    cuentaBanco.getSaldo());
	            if (comision > 0) {
	                operacion.setComision(comision);
	            }

	            // Consultar el número de operaciones
	            Mono<Long> numMovimientosMono = productoDao.consultaMovimientos(operacion.getDni(),
	                    operacion.getCuenta_origen(), cuentaBanco.getCodigoBanco()).count();

	            return numMovimientosMono.flatMap(numMovimientos -> {
	                if (numMovimientos > 2) {
	                    operacion.setComision(comision);
	                }

	                // Realizar el retiro en la cuenta de origen
	                Mono<CuentaBanco> retiroMono = productoBancoClient.retiroBancario(operacion.getCuenta_origen(),
	                        operacion.getMontoPago(), operacion.getComision(), operacion.getCodigo_bancario_origen());

	                return retiroMono.flatMap(retiro -> {
	                    if (retiro.getNumeroCuenta() == null) {
	                        return Mono.error(new InterruptedException("Tarjeta inválida"));
	                    }

	                    // Realizar el depósito en la cuenta de crédito
	                    Mono<CuentaBanco> depositoMono = productoBancoCreditoClient.despositoBancario(
	                            operacion.getMontoPago(), operacion.getCuenta_destino(),
	                            operacion.getCodigo_bancario_destino());

	                    return depositoMono.flatMap(deposito -> {
	                        if (deposito.getNumeroCuenta() == null) {
	                            return Mono.error(new InterruptedException("Tarjeta inválida"));
	                        }

	                        // Registrar la operación de transferencia
	                        TipoOperacionBanco tipoOperacion = new TipoOperacionBanco();
	                        tipoOperacion.setId("3");
	                        tipoOperacion.setDescripcion("cuentaCredito");
	                        operacion.setTipoOperacion(tipoOperacion);

	                        return productoDao.save(operacion);
	                    });
	                });
	            });
	        });
	    });

	}

	@Override
	public Flux<OperacionCuentaBanco> findAllOperacionByDniCliente(String dni) {
		return productoDao.viewDniCliente(dni);
	}

	@Override
	public Flux<OperacionCuentaBanco> findComision(String dni, Date fecha) {
		return productoDao.findByDniAndComisionGreaterThanZero(dni, fecha);
	}

	// Operaciones YANKI

	@Override
	public void envioYanki(OperacionCuentaBanco operacion) {				
		log.info("OPERACION[" + operacion + "]");
		Mono<CuentaYanki> oper1 = productoBancoClient.viewCuentaYanki(operacion.getCuenta_origen());
		oper1.subscribe(o -> System.out.println("PRODUCTO BANCO[" + o.toString()));
		
		// REALIZAR UN RETIRNO EN EL MS-PRODUCTO BANCARIO
		Mono<CuentaYanki> oper2 = productoBancoClient.retiroCuentaYanki(operacion.getCuenta_origen(),
				operacion.getMontoPago());
		oper2.subscribe(a -> System.out.println("RETIRO_CUENTA_PROPIA[" + a.toString()));
		
		// REALIZAR UN PAGO/DEPOSITO DESTINO
		Mono<CuentaYanki> oper3 = productoBancoClient.despositoCuentaYanki(operacion.getMontoPago(),
				operacion.getCuenta_destino());
		oper3.subscribe(p -> System.out.println("RETIRO_CUENTA_DESTINO[" + p.toString()));		
	}

	@Override
	public void envioBoitcoin(OperacionCuentaBanco operacion) {		
		log.info("OPERACION BOOTCOIND[" + operacion + "]");
		Mono<CuentaYanki> oper1 = productoBancoClient.viewCuentaYanki(operacion.getCuenta_origen());
		oper1.subscribe(o -> System.out.println("PRODUCTO BANCO[" + o.toString()));
		
		// REALIZAR UN RETIRNO EN EL MS-PRODUCTO BANCARIO
		Mono<CuentaYanki> oper2 = productoBancoClient.retiroCuentaYanki(operacion.getCuenta_origen(),
				operacion.getMontoPago());
		oper2.subscribe(a -> System.out.println("RETIRO_CUENTA_PROPIA[" + a.toString()));
		
		// REALIZAR UN PAGO/DEPOSITO DESTINO
		Mono<CuentaYanki> oper3 = productoBancoClient.despositoCuentaYanki(operacion.getMontoPago(),
				operacion.getCuenta_destino());
		oper3.subscribe(p -> System.out.println("RETIRO_CUENTA_DESTINO[" + p.toString()));
		
	}

}

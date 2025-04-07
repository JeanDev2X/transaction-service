package  com.bank.transaction.service.events;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Document(collection ="Orden")
public class Orden {
	
	private String id;
	private String numOperacion;
	private Usuario usuario;
	private double coinds;//cuanto bitconind comprar o vender
	private String modoPago;
	private String tipoOperacion;//venta o compra
	private double tasaVenta;
	private double tasaCompra;
	
}

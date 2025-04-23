package bank.transaction;

import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;



@AutoConfigureWebTestClient
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class TransactionApplicationTests {
	
//	@Autowired
//	private WebTestClient client;

//	@Test
//	void contextLoads() {
//	}
	
//	@Test
//	public void operacionesCantidad() {
//		client.get().uri("/api/operacionBancaria")
//		.accept(MediaType.APPLICATION_JSON)
//		.exchange()
//		.expectStatus().isOk() 
//		.expectHeader().contentType(MediaType.APPLICATION_JSON)//.hasSize(2); 
//		.expectBodyList(OperacionCuentaBanco.class)
//		.hasSize(10);
//	}
//	
//	@Test
//	public void listarClientes() {
//		client.get().uri("/api/operacionBancaria")
//		.accept(MediaType.APPLICATION_JSON)
//		.exchange()
//		.expectStatus().isOk() 
//		.expectHeader().contentType(MediaType.APPLICATION_JSON)//.hasSize(2); 
//		.expectBodyList(OperacionCuentaBanco.class).consumeWith(response -> {
//				List<OperacionCuentaBanco> ope = response.getResponseBody();
//				ope.forEach(p -> {
//					System.out.println(p.getDni());
//				});
//				Assertions.assertThat(ope.size()>0).isTrue();
//			});		
//	}
	
}

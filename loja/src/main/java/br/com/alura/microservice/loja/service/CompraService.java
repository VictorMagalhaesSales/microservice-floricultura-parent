package br.com.alura.microservice.loja.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

import br.com.alura.microservice.loja.client.FornecedorClient;
import br.com.alura.microservice.loja.dto.CompraDTO;
import br.com.alura.microservice.loja.dto.FornecedorDTO;
import br.com.alura.microservice.loja.dto.InfoPedidoDTO;
import br.com.alura.microservice.loja.model.Compra;
import br.com.alura.microservice.loja.repository.CompraRepository;

@Service
public class CompraService {
	
	private static final Logger LOG = LoggerFactory.getLogger(CompraService.class);
	
	@Autowired
	private RestTemplate client;
	
	@Autowired private CompraRepository compraRepository;

	@Autowired
	private FornecedorClient fornecedorClient;
	
	@Autowired
	private DiscoveryClient eurekaClient;
		
	@HystrixCommand(threadPoolKey = "buscarCompraThreadPool")
	public Compra getById(Long id) {
		LOG.info("Buscando compra com id " + id);
		return compraRepository.findById(id).orElse(new Compra());
	}

	public void realizaCompraComRestTemplate(CompraDTO compra) {
		ResponseEntity<FornecedorDTO> exchange = 
			client.exchange("http://fornecedor/"+compra.getEndereco().getEstado(), HttpMethod.GET, null, FornecedorDTO.class);
		
		eurekaClient.getInstances("fornecedor").stream()
			.forEach(fornecedor -> {System.out.println("localhost:"+fornecedor.getPort());});
		System.out.println(exchange.getBody().getEndereco());
	}

	@HystrixCommand(fallbackMethod="realizaCompraFallback", threadPoolKey = "realizaCompraThreadPool")
	public Compra realizaCompra(CompraDTO compra) {
		
		final String estado = compra.getEndereco().getEstado();
		LOG.info("Buscando informações do fornecedor de {}", estado);
		FornecedorDTO fornecedor = fornecedorClient.findPorEstado(estado);
		InfoPedidoDTO infoPedido = fornecedorClient.realizaPedido(compra.getItens());
		LOG.info("Pedido com id '"+infoPedido.getId()+"' realizado com sucesso!");
		
		Compra compraSalva = new Compra();
		compraSalva.setPedidoId(infoPedido.getId());
		compraSalva.setTempoDePreparo(infoPedido.getTempoDePreparo());
		compraSalva.setEnderecoDestino(fornecedor.getEndereco());
		compraRepository.save(compraSalva);

		LOG.info("Compra com id '"+compraSalva.getId()+"' com sucesso!");
		return compraSalva;
	}
	
	@SuppressWarnings("unused")
	private Compra realizaCompraFallback(CompraDTO compra) {
		Compra compraFb = new Compra();
		compraFb.setEnderecoDestino(compra.getEndereco().toString());
		return compraFb;
	}

}

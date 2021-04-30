package br.com.alura.microservice.loja.service;

import java.time.LocalDate;

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
import br.com.alura.microservice.loja.client.TransportadorClient;
import br.com.alura.microservice.loja.dto.CompraDTO;
import br.com.alura.microservice.loja.dto.FornecedorDTO;
import br.com.alura.microservice.loja.dto.InfoEntregaDTO;
import br.com.alura.microservice.loja.dto.InfoPedidoDTO;
import br.com.alura.microservice.loja.dto.VoucherDTO;
import br.com.alura.microservice.loja.model.Compra;
import br.com.alura.microservice.loja.model.CompraState;
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

	@Autowired
	private TransportadorClient transportadorClient;
	
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
		
		Compra compraSalva = saveCompra(compra);

		InfoPedidoDTO infoPedido = realizaPedidoEAtualizaCompra(compra, compraSalva);
		LOG.info("Pedido com id '"+infoPedido.getId()+"' realizado com sucesso!");
		
		reservaEntregaEAtualizaCompra(compra, compraSalva, infoPedido);
		LOG.info("Entrega reservada com sucesso!");

		LOG.info("Compra com id '"+compraSalva.getId()+"' realizada com sucesso!");
		return compraSalva;
	}

	private void reservaEntregaEAtualizaCompra(CompraDTO compra, Compra compraSalva, InfoPedidoDTO infoPedido) {
		InfoEntregaDTO entregaDto = new InfoEntregaDTO();
		entregaDto.setPedidoId(infoPedido.getId());
		entregaDto.setDataParaEntrega(LocalDate.now().plusDays(infoPedido.getTempoDePreparo()));
		
		FornecedorDTO fornecedor = fornecedorClient.findPorEstado(compra.getEndereco().getEstado());
		entregaDto.setEnderecoOrigem(fornecedor.getEndereco());
		
		VoucherDTO voucher = transportadorClient.reservaEntrega(entregaDto);
		compraSalva.setState(CompraState.RESERVA_ENTREGA_REALIZADA);
		compraSalva.setDataParaEntrega(voucher.getPrevisaoParaEntrega());
		compraSalva.setVoucher(voucher.getNumero());
		compraRepository.save(compraSalva);
	}

	private InfoPedidoDTO realizaPedidoEAtualizaCompra(CompraDTO compra, Compra compraSalva) {
		InfoPedidoDTO infoPedido = fornecedorClient.realizaPedido(compra.getItens());
		compraSalva.setState(CompraState.PEDIDO_REALIZADO);
		compraSalva.setPedidoId(infoPedido.getId());
		compraSalva.setTempoDePreparo(infoPedido.getTempoDePreparo());
		compraRepository.save(compraSalva);
		return infoPedido;
	}

	private Compra saveCompra(CompraDTO compra) {
		Compra compraSalva = new Compra();
		compraSalva.setState(CompraState.RECEBIDO);
		compraSalva.setEnderecoDestino(compra.getEndereco().toString());
		compraRepository.save(compraSalva);
		compra.setCompraId(compraSalva.getId());
		return compraSalva;
	}
	
	@SuppressWarnings("unused")
	private Compra realizaCompraFallback(CompraDTO compra) {
		if(compra.getCompraId() != null) {
			return compraRepository.findById(compra.getCompraId()).get();
		}
		Compra compraFb = new Compra();
		compraFb.setEnderecoDestino(compra.getEndereco().toString());
		return compraFb;
	}

}

package br.com.alura.microservice.loja.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import br.com.alura.microservice.loja.dto.FornecedorDTO;
import br.com.alura.microservice.loja.dto.InfoPedidoDTO;
import br.com.alura.microservice.loja.dto.ItemDaCompraDTO;

@FeignClient("fornecedor")
public interface FornecedorClient {

	@RequestMapping("/fornecedor/{estado}")
	FornecedorDTO findPorEstado(@PathVariable String estado);

	@RequestMapping(method=RequestMethod.POST, value="/pedido")
	InfoPedidoDTO realizaPedido(List<ItemDaCompraDTO> itens);
}

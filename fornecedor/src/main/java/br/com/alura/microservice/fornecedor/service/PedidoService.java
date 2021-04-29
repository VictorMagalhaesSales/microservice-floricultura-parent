package br.com.alura.microservice.fornecedor.service;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.alura.microservice.fornecedor.dto.ItemDoPedidoDTO;
import br.com.alura.microservice.fornecedor.model.Pedido;
import br.com.alura.microservice.fornecedor.model.PedidoItem;
import br.com.alura.microservice.fornecedor.model.Produto;
import br.com.alura.microservice.fornecedor.repository.PedidoRepository;
import br.com.alura.microservice.fornecedor.repository.ProdutoRepository;

@Service
public class PedidoService {
	
	@Autowired
	private PedidoRepository pedidoRepository;
	
	@Autowired
	private ProdutoRepository produtoRepository;
	
	private static final Logger LOG = LoggerFactory.getLogger(PedidoService.class);

	public Pedido realizaPedido(List<ItemDoPedidoDTO> itens) {
		if(itens == null) return null;
		
		List<PedidoItem> pedidoItens = toPedidoItem(itens);
		Pedido pedido = new Pedido(pedidoItens);
		pedido.setTempoDePreparo(itens.size());
		pedidoRepository.save(pedido);
		LOG.info("Salvando pedido com id "+pedido.getId()+" no banco de dados do fornecedor");
		return pedido;
	}
	
	public Pedido getPedidoPorId(Long id) {
		return this.pedidoRepository.findById(id).orElse(new Pedido());
	}

	private List<PedidoItem> toPedidoItem(List<ItemDoPedidoDTO> itens) {
		
		List<Long> idsProdutos = itens
				.stream().map(item -> item.getId())
				.collect(Collectors.toList());
		
		List<Produto> produtosDoPedido = produtoRepository.findByIdIn(idsProdutos);
		
		List<PedidoItem> pedidoItens = itens
			.stream().map(item -> {
				Produto produto = produtosDoPedido
					.stream().filter(p -> p.getId() == item.getId())
					.findFirst().get();
				
				PedidoItem pedidoItem = new PedidoItem();
				pedidoItem.setProduto(produto);
				pedidoItem.setQuantidade(item.getQuantidade());
				return pedidoItem;
			}).collect(Collectors.toList());
		
		return pedidoItens;
	}
}

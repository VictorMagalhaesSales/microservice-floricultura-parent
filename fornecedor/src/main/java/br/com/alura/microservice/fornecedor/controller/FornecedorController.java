package br.com.alura.microservice.fornecedor.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.alura.microservice.fornecedor.model.Fornecedor;
import br.com.alura.microservice.fornecedor.service.FornecedorService;

@RestController
@RequestMapping("")
public class FornecedorController {
	
	@Autowired
	private FornecedorService fornecedorService;

	@GetMapping("/{estado}")
	public Fornecedor getInfoPorEstado(@PathVariable String estado) {
		return fornecedorService.getPorEstado(estado);
	}
}

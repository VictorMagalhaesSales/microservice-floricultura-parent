package br.com.alura.microservice.fornecedor.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.alura.microservice.fornecedor.model.Fornecedor;
import br.com.alura.microservice.fornecedor.repository.FornecedorRepository;

@Service
public class FornecedorService {

	@Autowired
	private FornecedorRepository fornecedorRepository;
	
	public Fornecedor findPorEstado(String estado) {
		return fornecedorRepository.findByEstado(estado);
	}

	public Fornecedor save(Fornecedor fornecedor) throws Exception {
		if(findPorEstado(fornecedor.getEstado()) != null)
			throw new Exception("Já existe um fornecedor para o estado " + fornecedor.getEstado());
		
		return fornecedorRepository.save(fornecedor);
	}
}

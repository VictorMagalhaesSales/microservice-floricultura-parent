package br.com.alura.microservice.loja.dto;

public class InfoPedidoDTO {

	private Long id;
	
	public Integer tempoDePreparo;

	public Integer getTempoDePreparo() {
		return tempoDePreparo;
	}

	public void setTempoDePreparo(Integer tempoDePreparo) {
		this.tempoDePreparo = tempoDePreparo;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
}

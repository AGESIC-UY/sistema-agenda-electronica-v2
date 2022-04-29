package uy.gub.imm.sae.exportar;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "valorPosible")
@XmlAccessorType(XmlAccessType.FIELD)
public class ValorPosibleExport {
	
	private String etiqueta;
	private String valor;
	private Integer orden;
    private Date fechaDesde;
    private Date fechaHasta;
    private Boolean borrarFlag;
    
    
	public String getEtiqueta() {
		return etiqueta;
	}
	public void setEtiqueta(String etiqueta) {
		this.etiqueta = etiqueta;
	}
	public String getValor() {
		return valor;
	}
	public void setValor(String valor) {
		this.valor = valor;
	}
	public Integer getOrden() {
		return orden;
	}
	public void setOrden(Integer orden) {
		this.orden = orden;
	}
	public Date getFechaDesde() {
		return fechaDesde;
	}
	public void setFechaDesde(Date fechaDesde) {
		this.fechaDesde = fechaDesde;
	}
	public Date getFechaHasta() {
		return fechaHasta;
	}
	public void setFechaHasta(Date fechaHasta) {
		this.fechaHasta = fechaHasta;
	}
	public Boolean getBorrarFlag() {
		return borrarFlag;
	}
	public void setBorrarFlag(Boolean borrarFlag) {
		this.borrarFlag = borrarFlag;
	}
    
    
    
}

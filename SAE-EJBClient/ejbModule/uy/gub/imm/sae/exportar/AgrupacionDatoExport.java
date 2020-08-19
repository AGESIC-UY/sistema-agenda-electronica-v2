package uy.gub.imm.sae.exportar;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlRootElement(name = "agrupacionDato")
@XmlAccessorType(XmlAccessType.FIELD)
public class AgrupacionDatoExport {
	
	private String nombre;
	private String etiqueta;
	private Integer orden;
	private Date fechaBaja;
	private boolean borrarFlag;
	
	@XmlElement(name = "datosASolicitar")
	private List<DatoASolicitarExportar> datosAsolicitar = new ArrayList<DatoASolicitarExportar>();
	
	public AgrupacionDatoExport() {
		super();
	}
	
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public Integer getOrden() {
		return orden;
	}
	public void setOrden(Integer orden) {
		this.orden = orden;
	}
	public Date getFechaBaja() {
		return fechaBaja;
	}
	public void setFechaBaja(Date fechaBaja) {
		this.fechaBaja = fechaBaja;
	}
	public boolean getBorrarFlag() {
		return borrarFlag;
	}
	public void setBorrarFlag(boolean borrarFlag) {
		this.borrarFlag = borrarFlag;
	}

	public List<DatoASolicitarExportar> getDatosAsolicitar() {
		return datosAsolicitar;
	}

	public void setDatosAsolicitar(List<DatoASolicitarExportar> datosAsolicitar) {
		this.datosAsolicitar = datosAsolicitar;
	}

  public String getEtiqueta() {
    return etiqueta;
  }

  public void setEtiqueta(String etiqueta) {
    this.etiqueta = etiqueta;
  }
	
}

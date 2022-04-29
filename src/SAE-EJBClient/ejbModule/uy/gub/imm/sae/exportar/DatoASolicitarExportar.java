package uy.gub.imm.sae.exportar;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import uy.gub.imm.sae.common.enumerados.Tipo;

@XmlRootElement(name = "datoASolicitar")
@XmlAccessorType(XmlAccessType.FIELD)
public class DatoASolicitarExportar {
	
	private String nombre;
	private String etiqueta;
	private String textoAyuda;
	private Tipo tipo;
	private Integer largo;	
	private Boolean requerido;
	private Boolean esClave;
  private Boolean soloLectura;
	private Integer fila;
	private Integer columna = 1;
	private Date fechaBaja;
	private Boolean incluirEnReporte;
	private Integer anchoDespliegue;
	private Boolean borrarFlag;
	
	private Boolean incluirEnLlamador;
	private Integer largoEnLlamador;
	private Integer ordenEnLlamador;
	
	@XmlElement(name = "valoresPosibles")
	private List<ValorPosibleExport> valoresPosibles = new ArrayList<ValorPosibleExport>();
	
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getEtiqueta() {
		return etiqueta;
	}
	public void setEtiqueta(String etiqueta) {
		this.etiqueta = etiqueta;
	}
	public String getTextoAyuda() {
		return textoAyuda;
	}
	public void setTextoAyuda(String textoAyuda) {
		this.textoAyuda = textoAyuda;
	}
	public Tipo getTipo() {
		return tipo;
	}
	public void setTipo(Tipo tipo) {
		this.tipo = tipo;
	}
	public Integer getLargo() {
		return largo;
	}
	public void setLargo(Integer largo) {
		this.largo = largo;
	}
	public Boolean getRequerido() {
		return requerido;
	}
	public void setRequerido(Boolean requerido) {
		this.requerido = requerido;
	}
	public Boolean getEsClave() {
		return esClave;
	}
	public void setEsClave(Boolean esClave) {
		this.esClave = esClave;
	}
	public Integer getFila() {
		return fila;
	}
	public void setFila(Integer fila) {
		this.fila = fila;
	}
	public Integer getColumna() {
		return columna;
	}
	public void setColumna(Integer columna) {
		this.columna = columna;
	}
	public Date getFechaBaja() {
		return fechaBaja;
	}
	public void setFechaBaja(Date fechaBaja) {
		this.fechaBaja = fechaBaja;
	}
	public Boolean getIncluirEnReporte() {
		return incluirEnReporte;
	}
	public void setIncluirEnReporte(Boolean incluirEnReporte) {
		this.incluirEnReporte = incluirEnReporte;
	}
	public Integer getAnchoDespliegue() {
		return anchoDespliegue;
	}
	public void setAnchoDespliegue(Integer anchoDespliegue) {
		this.anchoDespliegue = anchoDespliegue;
	}
	public List<ValorPosibleExport> getValoresPosibles() {
		return valoresPosibles;
	}
	public void setValoresPosibles(List<ValorPosibleExport> valoresPosibles) {
		this.valoresPosibles = valoresPosibles;
	}
	public Boolean getBorrarFlag() {
		return borrarFlag;
	}
	public void setBorrarFlag(Boolean borrarFlag) {
		this.borrarFlag = borrarFlag;
	}
	public Boolean getIncluirEnLlamador() {
		return incluirEnLlamador;
	}
	public void setIncluirEnLlamador(Boolean incluirEnLlamador) {
		this.incluirEnLlamador = incluirEnLlamador;
	}
	public Integer getLargoEnLlamador() {
		return largoEnLlamador;
	}
	public void setLargoEnLlamador(Integer largoEnLlamador) {
		this.largoEnLlamador = largoEnLlamador;
	}
	public Integer getOrdenEnLlamador() {
		return ordenEnLlamador;
	}
	public void setOrdenEnLlamador(Integer ordenEnLlamador) {
		this.ordenEnLlamador = ordenEnLlamador;
	}
  public Boolean getSoloLectura() {
    return soloLectura;
  }
  public void setSoloLectura(Boolean soloLectura) {
    this.soloLectura = soloLectura;
  }
	
}

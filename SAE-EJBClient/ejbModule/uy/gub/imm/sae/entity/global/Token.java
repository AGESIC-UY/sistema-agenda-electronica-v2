package uy.gub.imm.sae.entity.global;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "ae_tokens")
public class Token implements Serializable {

	private static final long serialVersionUID = 7173791316892020470L;
	
	private String token;
	private Empresa empresa;
	private String nombre;
	private String email;
	private Date fecha;
	
	@Id
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	@ManyToOne (optional = false)
	@JoinColumn (name = "empresa_id", nullable = false)
	public Empresa getEmpresa() {
		return empresa;
	}
	
	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Date getFecha() {
		return fecha;
	}
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}



}

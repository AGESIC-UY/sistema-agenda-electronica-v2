package uy.gub.imm.sae.entity.global;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

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

	public Token() {
	}

	Token(String token) {
		this.token = token;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Token token1 = (Token) o;
		return Objects.equals(token, token1.token);
	}

	@Override
	public int hashCode() {
		return Objects.hash(token);
	}
}

package uy.gub.imm.sae.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable 
public class RolesUsuarioRecursoId implements Serializable {
	
  private static final long serialVersionUID = 5265449771898937819L;

  private Integer usuarioId;
	private Integer recursoId;
	
	public RolesUsuarioRecursoId() {
	  usuarioId = null;
	  recursoId = null;
	}

  @Column(name="usuario_id")
  public Integer getUsuarioId() {
    return usuarioId;
  }

  public void setUsuarioId(Integer usuarioId) {
    this.usuarioId = usuarioId;
  }

  @Column(name="recurso_id")
  public Integer getRecursoId() {
    return recursoId;
  }

  public void setRecursoId(Integer recursoId) {
    this.recursoId = recursoId;
  }
	
	
}

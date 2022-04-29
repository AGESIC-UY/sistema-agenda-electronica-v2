package uy.gub.imm.sae.entity;

import java.io.Serializable;
import java.util.Objects;

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

    RolesUsuarioRecursoId(Integer usuarioId, Integer recursoId) {
        this.usuarioId = usuarioId;
        this.recursoId = recursoId;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RolesUsuarioRecursoId that = (RolesUsuarioRecursoId) o;
        return Objects.equals(usuarioId, that.usuarioId) && Objects.equals(recursoId, that.recursoId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(usuarioId, recursoId);
    }
}

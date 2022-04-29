package uy.gub.imm.sae.entity;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "ae_roles_usuario_recurso")
public class RolesUsuarioRecurso implements Serializable {

  private static final long serialVersionUID = 1L;

	private RolesUsuarioRecursoId id;
	private String roles;

  public RolesUsuarioRecurso() {
  }

  RolesUsuarioRecurso(RolesUsuarioRecursoId id) {
    this.id = id;
  }

  @Id
  public RolesUsuarioRecursoId getId() {
    return id;
  }
  
  public void setId(RolesUsuarioRecursoId id) {
    this.id=id;
  }
  
  @Column(name="roles")
  public String getRoles() {
    return roles;
  }

  public void setRoles(String roles) {
    this.roles = roles;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    RolesUsuarioRecurso that = (RolesUsuarioRecurso) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}

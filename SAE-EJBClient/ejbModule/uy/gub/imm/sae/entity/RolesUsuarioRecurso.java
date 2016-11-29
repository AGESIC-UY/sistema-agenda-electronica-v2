package uy.gub.imm.sae.entity;

import java.io.Serializable;

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
  
}

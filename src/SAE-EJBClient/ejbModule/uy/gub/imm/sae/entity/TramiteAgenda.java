package uy.gub.imm.sae.entity;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlTransient;

@Entity
@Table(name = "ae_tramites_agendas")
public class TramiteAgenda implements Serializable {

	private static final long serialVersionUID = 7173791316892020470L;
	
	private Integer id;
	private Agenda agenda;
	private String tramiteId;
  private String tramiteCodigo;
	private String tramiteNombre;

  public TramiteAgenda() {
  }

  TramiteAgenda(Integer id) {
    this.id = id;
  }

  @Id
  @GeneratedValue (strategy = GenerationType.SEQUENCE, generator="seq_tramites")
  @SequenceGenerator (name ="seq_tramites", initialValue = 1, sequenceName = "s_ae_tramites_agendas", allocationSize=1)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

  @XmlTransient
  @ManyToOne (optional = false)
  @JoinColumn (name = "agenda_id", nullable = false)
  public Agenda getAgenda() {
    return agenda;
  }

  public void setAgenda(Agenda agenda) {
    this.agenda = agenda;
  }

  @Column(name="tramite_id")
  public String getTramiteId() {
    return tramiteId;
  }

  public void setTramiteId(String tramiteId) {
    this.tramiteId = tramiteId;
  }

  @Column(name="tramite_codigo")
  public String getTramiteCodigo() {
    return tramiteCodigo;
  }

  public void setTramiteCodigo(String tramiteCodigo) {
    this.tramiteCodigo = tramiteCodigo;
  }

  @Column(name="tramite_nombre")
  public String getTramiteNombre() {
    return tramiteNombre;
  }

  public void setTramiteNombre(String tramiteNombre) {
    this.tramiteNombre = tramiteNombre;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    TramiteAgenda that = (TramiteAgenda) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }

  @Override
  public String toString() {
    return getClass().getSimpleName()+" {id="+id+", tramiteCodigo="+tramiteCodigo+", tramiteNombre="+tramiteNombre+"}";
  }


}

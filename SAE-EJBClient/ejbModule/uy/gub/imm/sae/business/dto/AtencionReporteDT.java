package uy.gub.imm.sae.business.dto;

public class AtencionReporteDT {
	
	private String nombFuncionario;
	private String nombAgenda;
	private String nombEmpresa;
	private int asistencias;
	private int inasistencias;
	
	public AtencionReporteDT(String nombEmp ,String nomAgenda,String nomFun,int asist, int inasist)
	{
		this.nombEmpresa = nombEmp;
		this.asistencias = asist;
		this.inasistencias = inasist;
		this.nombAgenda = nomAgenda;
		this.nombFuncionario = nomFun;
	
	}
	public String getNombFuncionario() {
		return nombFuncionario;
	}
	public void setNombFuncionario(String nombFuncionario) {
		this.nombFuncionario = nombFuncionario;
	}
	public String getNombAgenda() {
		return nombAgenda;
	}
	public void setNombAgenda(String nombAgenda) {
		this.nombAgenda = nombAgenda;
	}
	public int getAsistencias() {
		return asistencias;
	}
	public void setAsistencias(int asistencias) {
		this.asistencias = asistencias;
	}
	public int getInasistencias() {
		return inasistencias;
	}
	public void setInasistencias(int inasistencias) {
		this.inasistencias = inasistencias;
	}
	
	public String getNombEmpresa() {
		return nombEmpresa;
	}
	public void setNombEmpresa(String nombEmpresa) {
		this.nombEmpresa = nombEmpresa;
	}

}

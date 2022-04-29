package uy.gub.imm.sae.login;

import org.jboss.security.SimplePrincipal;

public class SAEPrincipal extends SimplePrincipal {
	
	private static final long serialVersionUID = -568820422663686500L;
	
	String tenant = null;
	boolean superadmin = false;
	
	public SAEPrincipal(String codigoUsuario, String tenant, boolean superadmin) {
		super(codigoUsuario);
		this.tenant = tenant;
		this.superadmin = superadmin;
	}
	
	public String getTenant()  {
		return tenant;
	}

	public boolean isSuperadmin() {
		return superadmin;
	}

}

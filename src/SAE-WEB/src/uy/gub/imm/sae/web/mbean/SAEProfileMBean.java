package uy.gub.imm.sae.web.mbean;

import java.util.Properties;

import uy.gub.imm.sae.common.SAEProfile;
import uy.gub.imm.sae.web.common.BaseMBean;

/**
 * Clase de envoltorio para acceder al SAEProfile desde la vista como
 * si fuera un bean manejado.
 * @author alvaro
 *
 */
public class SAEProfileMBean extends BaseMBean {

	public String getName() {
		return SAEProfile.getInstance().getName();
	}
	
	public Properties getProperties() {
		return SAEProfile.getInstance().getProperties();
	}
	
	public String getHeaderTemplateURL() {
		return String.format(SAEProfile.getInstance().getProperties().getProperty(SAEProfile.PROFILE_UI_TEMPLATES_HEADER_KEY), getProfileHostPort());
	}
	
	public String getFooterTemplateURL() {
		return String.format(SAEProfile.getInstance().getProperties().getProperty(SAEProfile.PROFILE_UI_TEMPLATES_FOOTER_KEY), getProfileHostPort());
	}

	public String getHeaderTemplateInternetURL() {
		return String.format(SAEProfile.getInstance().getProperties().getProperty(SAEProfile.PROFILE_UI_TEMPLATES_HEADER_INTERNET_KEY), getProfileHostPort());
	}
	
	public String getFooterTemplateInternetURL() {
		return String.format(SAEProfile.getInstance().getProperties().getProperty(SAEProfile.PROFILE_UI_TEMPLATES_FOOTER_INTERNET_KEY), getProfileHostPort());
	}
	
	private String getProfileHostPort() {
	
		return SAEProfile.getInstance().getProperties().getProperty(SAEProfile.ENVIRONMENT_PROFILE_HOST_PORT_KEY);
	}


}

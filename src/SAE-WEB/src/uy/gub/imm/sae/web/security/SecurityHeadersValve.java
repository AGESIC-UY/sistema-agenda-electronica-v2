package uy.gub.imm.sae.web.security;

import java.io.IOException;

import javax.servlet.ServletException;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;

/**
 * Servlet Filter implementation class HeadersFilter
 */
public class SecurityHeadersValve extends ValveBase {

	private String strictTransportSecurity = null; //Strict-Transport-Security
	private String xContentTypeOptions = null; //X-Content-Type-Options
	private String xFrameOptions = null; //X-Frame-Options
	
  public SecurityHeadersValve() {
  }

	@Override
	public void invoke(Request httpRequest, Response httpResponse) throws IOException, ServletException {
		if(xFrameOptions!=null) {
			httpResponse.setHeader("X-Frame-Options", xFrameOptions);
		}
		if(strictTransportSecurity!=null) {
			httpResponse.setHeader("Strict-Transport-Security", strictTransportSecurity);
		}
		if(xContentTypeOptions!=null) {
			httpResponse.setHeader("X-Content-Type-Options", xContentTypeOptions);
		}
		getNext().invoke(httpRequest, httpResponse);
	}

	public String getXFrameOptions() {
		return xFrameOptions;
	}

	public void setXFrameOptions(String xFrameOptions) {
		this.xFrameOptions = xFrameOptions;
	}

	public String getStrictTransportSecurity() {
		return strictTransportSecurity;
	}

	public void setStrictTransportSecurity(String strictTransportSecurity) {
		this.strictTransportSecurity = strictTransportSecurity;
	}

	public String getXContentTypeOptions() {
		return xContentTypeOptions;
	}

	public void setXContentTypeOptions(String xContentTypeOptions) {
		this.xContentTypeOptions = xContentTypeOptions;
	}

}

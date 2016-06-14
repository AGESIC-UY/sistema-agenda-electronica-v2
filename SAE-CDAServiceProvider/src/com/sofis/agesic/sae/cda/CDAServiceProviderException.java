/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sofis.agesic.sae.cda;

/**
 *
 * @author spio
 */
public class CDAServiceProviderException extends Exception {

	private static final long serialVersionUID = 1L;

	public CDAServiceProviderException(String message) {
		super(message);
	}

	public CDAServiceProviderException(String message, Throwable cause) {
		super(message, cause);
	}

}

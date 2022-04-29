/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uy.gub.imm.sae.business.em;

import java.security.Principal;

import javax.ejb.SessionContext;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;

import uy.gub.imm.sae.login.SAEPrincipal;

/**
 *
 * @author Santiago
 */ 
public class CurrentTenantIdentifierResolverImpl implements CurrentTenantIdentifierResolver {
    @Override
    public String resolveCurrentTenantIdentifier() {
      try {
				Context initContext = new InitialContext();
				SessionContext session = (SessionContext) initContext.lookup("java:comp/EJBContext");
				Principal principal = session.getCallerPrincipal();
				if(principal instanceof SAEPrincipal) {
					SAEPrincipal saePrincipal = (SAEPrincipal)principal;
					return saePrincipal.getTenant();
				}
			} catch (NamingException nEx) {
				nEx.printStackTrace();
			}
			return "public";
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uy.gub.imm.sae.business.em;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.hibernate.HibernateException;
import org.hibernate.service.UnknownUnwrapTypeException;
import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.service.jdbc.connections.spi.MultiTenantConnectionProvider;

/**
 * Simplisitc implementation for illustration purposes showing a single
 * connection pool used to serve multiple schemas using "connection altering".
 * Here we use the T-SQL specific USE command; Oracle users might use the ALTER
 * SESSION SET SCHEMA command; etc.
 */
public class MultiTenantConnectionProviderImpl implements MultiTenantConnectionProvider {

	private static final long serialVersionUID = -3709094721460244520L;
	
	

	@Override
	public Connection getAnyConnection() throws SQLException {
		Context initContext;
		try {
			initContext = new InitialContext();
			DataSource ds = (DataSource) initContext.lookup("java:/postgres-sae-ds");
			Connection conn = ds.getConnection();
			return conn;
		} catch (NamingException ex) {
			Logger.getLogger(MultiTenantConnectionProviderImpl.class.getName()).log(Level.SEVERE, null, ex);
			return null;
		}

		

	}

	@Override
	public void releaseAnyConnection(Connection connection) throws SQLException {
		connection.close();
	}

	@Override
	public Connection getConnection(String tenantIdentifier) throws SQLException {
		
		final Connection connection = getAnyConnection();
		try {
			connection.createStatement().execute("SET SEARCH_PATH TO '" + tenantIdentifier + "'");
		} catch (SQLException e) {
			throw new HibernateException("No se pudo cambiar al esquema [" + tenantIdentifier + "]", e);
		}
		return connection;
	}

	@Override
	public void releaseConnection(String tenantIdentifier, Connection connection) throws SQLException {
		try {
			connection.createStatement().execute("SET SEARCH_PATH TO '" + tenantIdentifier + "'");
		} catch (SQLException e) {
			// on error, throw an exception to make sure the connection is not
			// returned to the pool.
			// your requirements may differ
			throw new HibernateException("No se pudo cambiar al esquema [" + tenantIdentifier + "]", e);
		}
		connection.close();
	}

	@Override
	public boolean supportsAggressiveRelease() {
		return false;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean isUnwrappableAs(Class unwrapType) {
		return ConnectionProvider.class.equals(unwrapType) || MultiTenantConnectionProvider.class.equals(unwrapType)
				|| MultiTenantConnectionProviderImpl.class.isAssignableFrom(unwrapType);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T unwrap(Class<T> unwrapType) {
		if (isUnwrappableAs(unwrapType)) {
			return (T) this;
		} else {
			throw new UnknownUnwrapTypeException(unwrapType);
		}
	}

	
}

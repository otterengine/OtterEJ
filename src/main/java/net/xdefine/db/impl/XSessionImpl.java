package net.xdefine.db.impl;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang.IllegalClassException;

import net.xdefine.XFContext;
import net.xdefine.db.XSession;
import net.xdefine.db.annotations.Table;
import net.xdefine.db.drivers.mysql.XQueryForMySQL;
import net.xdefine.db.utils.XQuery;

public class XSessionImpl implements XSession {

	private String name;
	private XSessionFactoryImpl sessionFactory;
	private Connection connection;
	private Statement statement;

	public XSessionImpl(XSessionFactoryImpl sessionFactory, String name, DataSource dataSource) {
		try {
			this.name = name;
			this.sessionFactory = sessionFactory;
			this.connection = dataSource.getConnection();
		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
	}

	@Override
	public void setReadOnly(boolean readOnly) {
		try {
			connection.setReadOnly(readOnly);
		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public void beginTrasaction() {
		try {
			connection.setAutoCommit(false);
			sessionFactory.setCurrentNullIfTarget(name, this);
		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public void commit() {
		if (this.isClosed())
			throw new IllegalStateException("already closed session.");

		this.close(true);
	}

	@Override
	public void close() {
		this.close(false);
	}

	private void close(boolean success) {
		try {

			if (this.connection == null)
				return;

			if (!this.isClosed()) {
				if (statement != null)
					statement.close();

				if (!connection.getAutoCommit()) {
					if (success)
						connection.commit();
					else
						connection.rollback();
				}

				connection.close();
			}

			if (this.name != null)
				sessionFactory.setCurrentNullIfTarget(name, this);

			this.name = null;
			this.sessionFactory = null;
			this.connection = null;

		} catch (SQLException e) {
			e.printStackTrace();
			throw new IllegalStateException(e);
		}

	}

	@Override
	public boolean isClosed() {
		try {
			return connection.isClosed();
		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public boolean isReadOnly() {
		try {
			return connection.isReadOnly();
		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public boolean isAutoCommit() {
		try {
			return connection.getAutoCommit();
		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public Object get(String table, long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object get(Class<?> clazz, long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object persist(String table, Map<String, Object> object) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object persist(Class<?> clazz) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object merge(String table, Map<String, Object> object) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object merge(Class<?> clazz) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public XQuery createQueryByFile(String name) {

		String path = "/xdefine/queries/" + name + ".sql";
		try {
			StringBuilder buffer = new StringBuilder();
			InputStream in = getClass().getResourceAsStream(path);
			BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));

			String line = "";
			while ((line = br.readLine()) != null) {
				buffer.append(line + "\n");
			}
			
			return new XQueryForMySQL(this, XQuery.Mode.NATIVE_QUERY, buffer.toString());
		}
		catch(Exception ex) {
			throw new IllegalStateException(ex);
		}
	}

	@Override
	public XQuery createQueryByNative(String query) {
		return new XQueryForMySQL(this, XQuery.Mode.NATIVE_QUERY, query);
	}

	@Override
	public XQuery createQueryByBuilder(String name) {
		
		if (!sessionFactory.entities.containsKey(name)) {
			throw new IllegalClassException(XFContext.getLanguage("xdefine.language.db.not_found_table"));
		}
		
		return new XQueryForMySQL(this, XQuery.Mode.BUILDER, name);
	}

	@Override
	public XQuery createQueryByBuilder(Class<?> clazz) {
		
		String name = clazz.getName().substring(0, 1).toLowerCase() + clazz.getName().substring(1);
		Table schema = clazz.getAnnotation(Table.class);
		if (schema != null) name = schema.name();

		if (!sessionFactory.entities.containsKey(name)) {
			throw new IllegalClassException(XFContext.getLanguage("xdefine.language.db.not_found_table"));
		}

		return new XQueryForMySQL(this, XQuery.Mode.BUILDER, name, clazz);
	}

	public ResultSet createResultset(String queryString, List<Object> params) {
		try {
			
			PreparedStatement statement = connection.prepareStatement(queryString);
			for(int i = 1; i <= params.size(); i++) {
				statement.setObject(i, params.get(i - 1));
			}
			
			this.statement = statement;
			return statement.executeQuery();
		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}
	}

	public int execute(String queryString) {
		try {
			if (statement == null)
				statement = connection.createStatement();
			
			return statement.executeUpdate(queryString);
		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}
	}

	public XSessionFactoryImpl getSessionFactory() {
		return this.sessionFactory;
	}

}

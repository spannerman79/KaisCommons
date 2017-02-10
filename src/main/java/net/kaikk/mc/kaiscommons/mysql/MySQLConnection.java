package net.kaikk.mc.kaiscommons.mysql;

import java.lang.ref.WeakReference;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.sql.DataSource;

import net.kaikk.mc.kaiscommons.CommonUtils;

public class MySQLConnection<T extends AMySQLQueries> {
	protected DataSource dataSource;
	protected ThreadLocal<ConnectionData> connData = new ThreadLocal<ConnectionData>();
	protected Class<T> queriesClass;
	protected Queue<WeakReference<ConnectionData>> connections;
	public boolean debug = false;

	public MySQLConnection(DataSource dataSource, Class<T> clazz) throws SQLException {
		this.dataSource = dataSource;
		this.queriesClass = clazz;
		if (System.getProperty("kaiscommons.mysql.debug", "f").equalsIgnoreCase("true")) {
			this.debug = true;
			this.connections = new LinkedBlockingQueue<>();
		}
	}

	public void check() throws SQLException {
		if(this.connData.get() == null || this.connData.get().getConnection().isClosed()) {
			try {
				final ConnectionData cd = new ConnectionData(this.dataSource.getConnection(), this.queriesClass.newInstance());
				this.connData.set(cd);
				this.queries().init(this);
				if (this.debug) {
					System.out.println("Opened ID: "+Integer.toHexString(cd.hashCode())+" from "+CommonUtils.shortStackTrace(2,0));
					connections.offer(new WeakReference<>(cd));
					Iterator<WeakReference<ConnectionData>> it = this.connections.iterator();
					while (it.hasNext()) {
						WeakReference<ConnectionData> wcd = it.next();
						if (wcd.get() == null) {
							it.remove();
						} else if (wcd.get().getLastUsedTime() + 30000 < System.currentTimeMillis()) {
							System.err.println("Connection ID "+Integer.toHexString(wcd.get().hashCode())+" is unused for more than 30 seconds. Potential memory leak. Stack trace: "+wcd.get().stackTrace);
						}
					}
				}
			} catch (InstantiationException | IllegalAccessException | SecurityException e) {
				throw new IllegalArgumentException(e);
			}
		}
	}

	public void close()  {
		try {
			final ConnectionData cd = this.connData.get();
			if (this.debug) {
				connections.remove(cd);
			}
			if (cd != null && !cd.getConnection().isClosed()) {
				if (this.debug) {
					System.out.println("Closing ID: "+Integer.toHexString(cd.hashCode()));
				}
				cd.getConnection().close();
				this.connData.set(null);
			} else if (this.debug) {
				System.out.println("Closing already closed ID: "+(cd == null ? "null" : Integer.toHexString(cd.hashCode()))+". Stack trace: "+CommonUtils.shortStackTrace(2,0));
			}
		} catch (SQLException e) {

		}
	}

	public Connection connection() throws SQLException {
		this.check();
		return connData.get().getConnection();
	}

	public T queries() throws SQLException {
		this.check();
		return connData.get().getQueries();
	}

	public Statement statement() throws SQLException {
		this.check();
		return this.connData.get().getConnection().createStatement();
	}

	public PreparedStatement prepareStatement(String sql) throws SQLException {
		this.check();
		return this.connData.get().getConnection().prepareStatement(sql);
	}

	public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
		this.check();
		return this.connData.get().getConnection().prepareStatement(sql, autoGeneratedKeys);
	}

	protected class ConnectionData {
		protected final Connection connection;
		protected final T queries;
		protected final String stackTrace;
		protected final long creationTime;
		protected long lastUsedTime;

		protected ConnectionData(Connection connection, T queries) {
			this.connection = connection;
			this.queries = queries;
			this.creationTime = System.currentTimeMillis();
			this.stackTrace = CommonUtils.shortStackTrace(3, 1);
			this.lastUsedTime = this.creationTime;
		}

		protected Connection getConnection() {
			return connection;
		}

		protected T getQueries() {
			this.lastUsedTime = System.currentTimeMillis();
			return queries;
		}

		public long getCreationTime() {
			return creationTime;
		}

		public long getLastUsedTime() {
			return lastUsedTime;
		}

		@Override
		protected void finalize() throws Throwable {
			try {
				if (!connection.isClosed()) {
					System.err.println("Finalizing ID "+Integer.toHexString(this.hashCode())+". Potential memory leak. Stack trace: "+this.stackTrace);
					connection.close();
				}
			} catch (Throwable e) { }
		}
	}
}

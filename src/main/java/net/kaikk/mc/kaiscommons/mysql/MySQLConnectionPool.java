package net.kaikk.mc.kaiscommons.mysql;

import java.sql.SQLException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.sql.DataSource;

import net.kaikk.mc.kaiscommons.CommonUtils;

/**
 * A *very* basic connection pool. If no other connection pool is available (Bukkit), it'll be better than nothing.<br>
 * When closed, the connection is not closed, instead it's being hold for future re-use.
 * */
public class MySQLConnectionPool<T extends AMySQLQueries> extends MySQLConnection<T> {
	protected final Queue<ConnectionData> pool = new ConcurrentLinkedQueue<>();
	
	public MySQLConnectionPool(DataSource dataSource, Class<T> clazz) {
		super(dataSource, clazz);
	}

	@Override
	public void check() throws SQLException {
		if(this.connData.get() == null || this.connData.get().getConnection().isClosed()) {
			ConnectionData cd;
			while ((cd = this.pool.poll()) != null) {
				if (!cd.getConnection().isClosed()) {
					this.connData.set(cd);
					if (this.debug) {
						System.out.println("Unpooling ID: "+Integer.toHexString(cd.hashCode()));
					}
					return;
				}
			}

			try {
				cd = new ConnectionData(this.dataSource.getConnection(), this.queriesClass.newInstance());
				this.connData.set(cd);
				this.queries().init(this);
				if (this.debug) {
					System.out.println("Opened ID: "+Integer.toHexString(cd.hashCode())+" from "+CommonUtils.shortStackTrace(2,0));
				}
			} catch (InstantiationException | IllegalAccessException e) {
				throw new IllegalArgumentException(e);
			}
		}
	}

	@Override
	public void close()  {
		try {
			final ConnectionData cd = this.connData.get();
			if (cd != null) {
				if (!cd.getConnection().isClosed()) {
					this.pool.offer(cd);
					if (this.debug) {
						System.out.println("Pooling ID: "+Integer.toHexString(cd.hashCode()));
					}
				} else if (this.debug) {
					System.out.println("Closing already closed ID: "+Integer.toHexString(cd.hashCode())+". Stack trace: "+CommonUtils.shortStackTrace(2,0));
				}
				this.connData.set(null);
			} else if (this.debug) {
				System.out.println("Closing already closed ID: null. Stack trace: "+CommonUtils.shortStackTrace(2,0));
			}
		} catch (SQLException e) {

		}
	}

	public void closePool() {
		ConnectionData cd;
		while ((cd = this.pool.poll()) != null) {
			try {
				if (!cd.getConnection().isClosed()) {
					cd.connection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}

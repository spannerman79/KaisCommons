package net.kaikk.mc.kaiscommons.mysql;

import java.sql.SQLException;

public abstract class AMySQLQueries {
	protected AMySQLQueries() { }
	protected abstract void init(MySQLConnection<? extends AMySQLQueries> connection) throws SQLException;
}

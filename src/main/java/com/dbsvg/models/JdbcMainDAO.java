/*
 * DB-SVG Copyright 2012 Derrick Bowen
 *
 * This file is part of DB-SVG.
 *
 *   DB-SVG is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   DB-SVG is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with DB-SVG.  If not, see <http://www.gnu.org/licenses/>.
 *   
 *   @author Derrick Bowen derrickbowen@dbsvg.com
 */

package com.dbsvg.models;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.dbsvg.objects.model.Column;
import com.dbsvg.objects.model.ColumnObject;
import com.dbsvg.objects.model.ForeignKey;
import com.dbsvg.objects.model.PrimaryKey;
import com.dbsvg.objects.model.Table;

/**
 * 
 */
@SuppressWarnings("serial")
@Service
public class JdbcMainDAO implements Serializable, IMainDAO {

	protected static final Logger LOG = LoggerFactory.getLogger(JdbcMainDAO.class);

	public static final int CHAR_WIDTH = 7;
	public static final int PAD_WIDTH = 25;
	public static final int CHAR_HEIGHT = 15;
	public static final int PAD_HEIGHT = 45;

	/**
	 * Gets the default connection from the Connection Pool in cases where
	 * another hasn't been supplied.
	 * 
	 * @return
	 */
	public Connection getDefaultConnection() {
		Connection conn = ConnectionPool.getConnection();
		return conn;
	}

	/**
	 * Scrubs the database, Generates Pojo Tables with columns, and Primary and
	 * Foreign Keys delineated.
	 * 
	 * @param conn
	 *            (null will use the default DB connection Table)
	 * @return
	 * @throws java.lang.Exception
	 */
	public Map<String, Table> getTables(Connection conn, String schemaId)
			throws Exception {
		Map<String, Table> tableMap = new HashMap<String, Table>();
		if (conn == null) {
			conn = getDefaultConnection();
		}
		Statement st = conn.createStatement();
		ResultSet rs = null;
		DatabaseMetaData meta = conn.getMetaData();

		LOG.debug("Grabbing Table Data");
		rs = meta.getTables(null, null, null, new String[] { "TABLE", "VIEW" });
		while (rs.next()) {
			String tableOrViewName = rs.getString("TABLE_NAME");
			Table t = new Table(tableOrViewName);
			tableMap.put(tableOrViewName, t);
			t.setSchemaId(schemaId);
			populateTable(t, conn);
		}

		LOG.debug("Populating Foreign Keys");
		int i = 0;
		for (Table t : tableMap.values()) {
			i++;
			LOG.debug("Checking " + i + " of " + tableMap.size() + "("
					+ t.getName() + ")");
			checkForForeignKeys(t, meta, conn, tableMap);
			// for (Table fTable: tableMap.values()) {
			// checkForForeignKeys(t, fTable, meta);
			// }
		}

		st.close();
		conn.close();

		return tableMap;
	}

	/**
	 * Grabs all the Columns and fills them with the information from the JDBC.
	 * Also detects Primary keys
	 * 
	 * @param table
	 * @param conn
	 * @return
	 * @throws java.lang.Exception
	 */
	private Table populateTable(Table table, Connection conn) throws Exception {

		int maxWidth = 0;

		maxWidth = (int) (table.getName().length() * 1.5);

		DatabaseMetaData meta = conn.getMetaData();
		ResultSet rs = meta.getColumns(null, null, table.getName(), null);

		while (rs.next()) {
			String columnName = rs.getString("COLUMN_NAME");
			if (columnName.length() > maxWidth)
				maxWidth = columnName.length();
			Column c = new ColumnObject(columnName);
			table.getColumns().put(columnName, c);
			c.setTable(table);
			populateColumn(c, rs);
		}

		table.setWidth(CHAR_WIDTH * maxWidth + PAD_WIDTH);
		table.setHeight(CHAR_HEIGHT * table.getColumns().size() + PAD_HEIGHT);

		try {
			rs = meta.getPrimaryKeys(null, null, table.getName());

			while (rs.next()) {
				String columnName = rs.getString("COLUMN_NAME");
				PrimaryKey pk = table.getColumns().get(columnName)
						.transformToPK();
				table.getColumns().put(columnName, pk);
				table.getPrimaryKeys().put(columnName, pk);
			}
		} catch (Exception e) {
			LOG.error(table.getName() + " Has Primary Key Issues.", e);
		}
		rs.close();

		return table;
	}

	/**
	 * Makes a Column from JDBC result set data
	 * 
	 * @param c
	 * @param rs
	 * @return
	 * @throws java.lang.Exception
	 */
	private Column populateColumn(Column c, ResultSet rs) throws Exception {
		c.setComment(rs.getString("REMARKS"));
		c.setNullable(rs.getString("IS_NULLABLE"));
		c.setOrdinalValue(rs.getInt("ORDINAL_POSITION"));
		c.setDataType(rs.getString("TYPE_NAME"));
		return c;
	}

	/**
	 * This method should be run after all the tables are created. It cross
	 * references two tables to see if there is a foreign key between them. If
	 * so, it transforms the parent column into a foreign key.
	 * 
	 * @param t
	 * @param fTable
	 * @param meta
	 * @throws java.lang.Exception
	 */
	private void checkForForeignKeys(Table t, DatabaseMetaData meta,
			Connection conn, Map<String, Table> tablemap) throws Exception {
		ResultSet rs = meta.getExportedKeys(conn.getCatalog(), null,
				t.getName());
		while (rs.next()) {
			String parentColumn = rs.getString("PKCOLUMN_NAME");
			String fkTableName = rs.getString("FKTABLE_NAME");
			String foreignColumn = rs.getString("FKCOLUMN_NAME");
			Table fTable = tablemap.get(fkTableName);

			ForeignKey fk = fTable.getColumns().get(foreignColumn).transformToFK();
			fTable.getColumns().put(foreignColumn, fk);
			fTable.getForeignKeys().put(foreignColumn, fk);
			// increase the width of the table so that FK->table.column will fit
			// also
			int newWidth = CHAR_WIDTH * parentColumn.length() + CHAR_WIDTH * t.getName().length() + PAD_WIDTH + 8 * CHAR_WIDTH + CHAR_WIDTH
					* foreignColumn.length();
			if (newWidth > fTable.getWidth())
				fTable.setWidth(newWidth);
			t.getReferencingTables().put(fTable.getName(), fTable);
			fk.setReferencedColumn(parentColumn);
			fk.setReferencedTable(t.getName());
			fk.setUpdateRule(rs.getString("UPDATE_RULE"));
			fk.setDeleteRule(rs.getString("DELETE_RULE"));
			try {
				fk.setReference(t.getColumns().get(parentColumn));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}

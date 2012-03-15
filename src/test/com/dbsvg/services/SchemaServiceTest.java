/*
 * DB-SVG Copyright 2009 Derrick Bowen
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
 *
 */
package com.dbsvg.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.dbsvg.models.ConnectionWrapper;
import com.dbsvg.models.IMainDAO;
import com.dbsvg.models.InternalDataDAO;
import com.dbsvg.objects.model.Table;
import com.dbsvg.objects.view.LinkLine;
import com.dbsvg.objects.view.SchemaPage;
import com.dbsvg.objects.view.SortedSchema;
import com.dbsvg.objects.view.TableView;

public class SchemaServiceTest {

	SchemaService instance;

	// Auto injected component mocks
	@Mock
	IMainDAO dao;
	@Mock
	ITablePageSorter tvSorter;
	@Mock
	InternalDataDAO iDAO;
	@Mock
	IDBViewerCache programCache;

	@Mock
	SortedSchema schema;

	@Mock
	ConnectionWrapper mainDbCw;
	@Mock
	Connection iDAOconn;
	@Mock
	Connection mainDbConn;
	@Mock
	Table table;
	@Mock
	TableView tableView;
	@Mock
	SchemaPage page;
	@Mock
	LinkLine line;
	@Captor
	ArgumentCaptor<Map<String, Table>> tableMapCaptor;

	String dbi = "10";

	Map<String, Table> tableMap;

	List<TableView> tableViewList;
	List<LinkLine> lines;

	String connectionTitle = "CW Title";

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		instance = new SchemaService();
		instance.dao = dao;
		instance.tvSorter = tvSorter;
		instance.iDAO = iDAO;
		instance.programCache = programCache;

		when(programCache.getConnection(dbi)).thenReturn(mainDbCw);
		when(mainDbCw.connectToDB()).thenReturn(mainDbConn);

		when(iDAO.getConnection()).thenReturn(iDAOconn);

		tableMap = new HashMap<String, Table>();
		tableMap.put("table", table);
		when(dao.getTables(eq(mainDbConn), anyString())).thenReturn(tableMap);

		tableViewList = new ArrayList<TableView>();
		tableViewList.add(tableView);
		when(page.getTableViews()).thenReturn(tableViewList);
		when(tableView.getTable()).thenReturn(table);

		List<LinkLine> lines = new ArrayList<LinkLine>();
		lines.add(line);
		when(page.getLines()).thenReturn(lines);

		when(mainDbCw.getTitle()).thenReturn(connectionTitle);
		when(schema.getFirstPage()).thenReturn(page);
		when(schema.getNumTables()).thenReturn(0);
		when(page.getSchema()).thenReturn(schema);
		when(schema.getName()).thenReturn(connectionTitle);

	}

	@Test
	public void testPrepareSchema() {
		SchemaPage result = instance.prepareSchema(schema, dbi, null);
		assertEquals(page, result);
	}

	@Test
	public void testPrepareSchemaNullFirstPage() {
		Map<UUID, SchemaPage> pages = new HashMap<UUID, SchemaPage>();
		when(iDAO.readSchemaPages(schema, mainDbConn)).thenReturn(pages);

		SchemaPage result = instance.prepareSchema(schema, dbi, null);
		assertEquals(page, result);
	}

	@Test
	public void readTables() {
		boolean result = instance.readTables(schema, mainDbCw);
		assertTrue(result);
		// verify(schema).setName(connectionTitle);
	}

	@Test
	public void readTablesDBError() throws Exception {
		when(mainDbCw.connectToDB()).thenThrow(
				new SQLException("unable to connect"));
		boolean result = instance.readTables(schema, mainDbCw);
		assertTrue(result);
		verify(schema).setTables(tableMapCaptor.capture());
		Map<String, Table> tableMap = tableMapCaptor.getValue();
		Table messageTable = tableMap.get("Unable to Connect");
		assertNotNull(messageTable);
	}

	@Test
	public void readTablesNotNeeded() {
		when(schema.getNumTables()).thenReturn(10);
		boolean result = instance.readTables(schema, mainDbCw);
		assertFalse(result);
		// verify(schema, times(0)).setName(connectionTitle);

	}

	@Test
	public void testSaveTablePositions() throws Exception {
		instance.saveTablePositions(page);
		verify(iDAO).saveSchemaPage(page, iDAOconn);
		verify(iDAO).saveTable(table, iDAOconn);
		verify(iDAO).saveTablePosition(tableView, iDAOconn);
	}

	@Test
	public void testResortTableViews() {
		instance.resortTableViews(page);
		verify(tvSorter).SortPage(page, true);
		verify(tvSorter).calcLines(page);
		verify(page).calcDimensions();

	}

	@Test
	public void testSaveTableViews() {
		instance.saveTableViews(page);
		verify(iDAO).saveSchemaPage(page, iDAOconn);
		verify(iDAO).saveTablePosition(tableView, iDAOconn);
	}

}

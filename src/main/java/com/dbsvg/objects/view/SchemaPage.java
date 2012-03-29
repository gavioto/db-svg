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
package com.dbsvg.objects.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.dbsvg.common.HashCodeUtil;
import com.dbsvg.objects.model.Table;

/**
 * 
 */
@SuppressWarnings("serial")
public class SchemaPage implements Comparable<SchemaPage>, Serializable {

	private static final int ORIGIN_PADDING = 25;

	private Map<Integer, TableView> tableViews = new HashMap<Integer, TableView>();
	private String title;
	private UUID id;
	private int orderid = 0;
	private int width = 0;
	private int height = 0;
	private SortedSchema schema;
	private List<LinkLine> lines = new ArrayList<LinkLine>();
	private Boolean sorted = false;

	public SchemaPage() {
		this.id = UUID.randomUUID();
	}

	public SchemaPage(UUID id) {
		this.id = id;
	}

	public SchemaPage(String id) {
		this.id = UUID.fromString(id);
	}

	/**
	 * Calculates the schema height width and translation values based on the
	 * tableviews
	 */
	public void calcDimensionsAndTranslatePageToOrigin() {
		Double minx = null;
		Double miny = null;
		Double maxx = null;
		Double maxy = null;

		for (TableView tv : tableViews.values()) {
			double x = tv.getX();
			double y = tv.getY();

			if (minx == null || x < minx)
				minx = x;
			if (miny == null || y < miny)
				miny = y;
			x = x + tv.getTable().getWidth();
			if (maxx == null || x > maxx)
				maxx = x;
			y = y + tv.getTable().getHeight();
			if (maxy == null || y > maxy)
				maxy = y;
		}
		setWidth((int) (maxx - minx) + 300);
		setHeight((int) (maxy - miny) + 500);

		// translate views to origin + ORIGIN_PADDING
		if (minx != ORIGIN_PADDING || miny != ORIGIN_PADDING)
			for (TableView v : tableViews.values()) {
				boolean needsResort = v.needsResort();
				v.setX(v.getX() - minx + ORIGIN_PADDING);
				v.setY(v.getY() - miny + ORIGIN_PADDING);
				v.setNeedsSort(needsResort);
			}
	}

	public void setTableViewPosition(int i, double x_pos, double y_pos) {
		TableView t = tableViews.get(i);
		t.setX(x_pos);
		t.setY(y_pos);
		t.setSorted();
	}

	public Collection<TableView> getTableViews() {
		return tableViews.values();
	}

	public void setTableViews(List<TableView> tableViews) {
		this.tableViews.clear();
		for (TableView tv : tableViews) {
			this.tableViews.put(tv.getId(), tv);
		}
		sorted = false;
	}

	public UUID getId() {
		return id;
	}

	public int getOrderid() {
		return orderid;
	}

	public void setOrderid(int orderid) {
		this.orderid = orderid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public SortedSchema getSchema() {
		return schema;
	}

	public void setSchema(SortedSchema schema) {
		this.schema = schema;
	}

	public List<LinkLine> getLines() {
		return lines;
	}

	public void setLines(List<LinkLine> lines) {
		this.lines = lines;
	}

	public Boolean isSorted() {
		return sorted;
	}

	public void setSorted(Boolean sorted) {
		this.sorted = sorted;
	}

	public int numDirtyTableViews() {
		int numDirty = 0;
		for (TableView tv : tableViews.values()) {
			if (tv.needsResort())
				numDirty++;
		}
		return numDirty;
	}

	public TableView makeViewForTable(Table table) {
		TableView tv = new TableView(table, this);
		table.addTableViewForPage(tv, this);
		tableViews.put(table.getViewId(), tv);
		return tv;
	}

	public TableView removeViewForTable(Table table) {
		return tableViews.remove(table.getViewId());
	}

	public int compareTo(SchemaPage o) {
		if (this.orderid > o.getOrderid())
			return 1;
		else if (this.orderid < o.getOrderid())
			return -1;
		else {
			if (this.title.compareTo(o.getTitle()) != 0) {
				return this.title.compareTo(o.getTitle());
			} else {
				return this.getId().compareTo(o.getId());
			}
		}
	}

	/**
	 * Checks to see if the given table is in the schema page.
	 * 
	 * @param t
	 * @return
	 */
	public boolean contains(Table t) {
		for (TableView tv : tableViews.values()) {
			if (tv.getTable().getId().compareTo(t.getId()) == 0)
				return true;
		}
		return false;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		if (!(o instanceof SchemaPage)) {
			return false;
		}
		SchemaPage v = (SchemaPage) o;
		if (!getTitle().equals(v.getTitle())) {
			return false;
		}
		return compareTo(v) == 0;
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.generateHash(title, id, getOrderid());
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("SchemaPage[");
		builder.append(getTitle());
		builder.append(",id:");
		builder.append(getId());
		builder.append(",order:");
		builder.append(getOrderid());
		builder.append("]");
		return builder.toString();
	}

}

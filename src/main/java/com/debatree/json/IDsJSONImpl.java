package com.debatree.json;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;

public class IDsJSONImpl {
	private long[] ids;

	public long[] getIds() {
		return ids;
	}

	public List<Long> getIdList() {

		Long[] longObjects = ArrayUtils.toObject(ids);
		return Arrays.asList(longObjects);
	}

	public void setIds(long[] ids) {
		this.ids = ids;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public static void setSerialVersionUID(long serialVersionUID) {
		IDsJSONImpl.serialVersionUID = serialVersionUID;
	}

	private long next_cursor;

	private long previous_cursor;
	private String next_cursor_str;

	private String previous_cursor_str;

	private static long serialVersionUID;

	/**
	 * @return the next_cursor
	 */
	public long getNext_cursor() {
		return next_cursor;
	}

	/**
	 * @param next_cursor
	 *            the next_cursor to set
	 */
	public void setNext_cursor(long next_cursor) {
		this.next_cursor = next_cursor;
	}

	/**
	 * @return the previous_cursor
	 */
	public long getPrevious_cursor() {
		return previous_cursor;
	}

	/**
	 * @param previous_cursor
	 *            the previous_cursor to set
	 */
	public void setPrevious_cursor(long previous_cursor) {
		this.previous_cursor = previous_cursor;
	}

	/**
	 * @return the previous_cursor_str
	 */
	public String getPrevious_cursor_str() {
		return previous_cursor_str;
	}

	/**
	 * @param previous_cursor_str
	 *            the previous_cursor_str to set
	 */
	public void setPrevious_cursor_str(String previous_cursor_str) {
		this.previous_cursor_str = previous_cursor_str;
	}

	/**
	 * @return the next_cursor_str
	 */
	public String getNext_cursor_str() {
		return next_cursor_str;
	}

	/**
	 * @param next_cursor_str
	 *            the next_cursor_str to set
	 */
	public void setNext_cursor_str(String next_cursor_str) {
		this.next_cursor_str = next_cursor_str;
	}
}

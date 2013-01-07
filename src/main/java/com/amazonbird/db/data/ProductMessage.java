/**
	TwitStreet - Twitter Stock Market Game
    Copyright (C) 2012  Engin Guller (bisanthe@gmail.com), Cagdas Ozek (cagdasozek@gmail.com)

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 **/

package com.amazonbird.db.data;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ProductMessage extends Message {

	private long productId = -1;

	@Override
	public void getDataFromResultSet(ResultSet rs) throws SQLException {
		super.getDataFromResultSet(rs);
		this.setProductId(rs.getLong("productid"));
		
	}

	public String toString() {

		String str = super.toString() + "productid=" + getProductId() + "\n";
		return str;

	}

	public ProductMessage() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the productId
	 */
	public long getProductId() {
		return productId;
	}

	/**
	 * @param productId the productId to set
	 */
	public void setProductId(long productId) {
		this.productId = productId;
	}

}
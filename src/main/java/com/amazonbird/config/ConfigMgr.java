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
package com.amazonbird.config;

import java.util.Arrays;
import java.util.HashSet;


public interface ConfigMgr {
	
	//currently just one server is master and its id is 0.
	//replace {0} with {0,7,3,2,...} as new master servers are added
	public static HashSet<Integer> masterIdSet = new HashSet<Integer>(Arrays.asList(new Integer[]{0}));

	
	public static final String DEV = "dev";
	public static final String PROD = "prod";
		
	
	public String get(String parm);


	public String getConsumerKey();


	public String getConsumerSecret();
}

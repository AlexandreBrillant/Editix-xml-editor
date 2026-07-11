// Editix XML Editor
// https://www.editix.com
// Copyright (c) 2026 Alexandre Brillant
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// See the GNU General Public License for more details: https://www.gnu.org/licenses/gpl-3.0
//
// AI Training Restriction :
// This source code is provided for human use only.
// Using this code to train, fine-tune, or develop AI models,
// machine learning systems, or similar technologies is
// STRICTLY PROHIBITED. Violations will terminate all rights
// under the applicable license.

package com.japisoft.editix.action.search.file;

import java.util.ArrayList;

public class SearchManager {

	private static ArrayList managers = null;

	public static void install() {
		if ( managers == null ) {
			managers = new ArrayList();
			managers.add( 
					new TextSearchEngineImpl() 
			);
			managers.add(
					new RegexpSearchEngineImpl()
			);
			managers.add(
					new XPathSearchEngineImpl() );
		}
	}

	public static int getSearchEngineCount() {
		if ( managers == null )
			return 0;
		else
			return managers.size();
	}

	public static SearchEngine getSearchEngineAt( int index ) {
		return ( SearchEngine )managers.get( index );
	}

}

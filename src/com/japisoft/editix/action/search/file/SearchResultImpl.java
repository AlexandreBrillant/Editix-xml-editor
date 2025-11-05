// Editix XML Editor
// https://www.editix.com
// Copyright (c) 2025 Alexandre Brillant
// 
// For non-commercial usage :
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
// For commercial use or integration into proprietary software :
// A commercial license is required. Visit https://www.editix.com for details.

package com.japisoft.editix.action.search.file;

import java.io.File;

public class SearchResultImpl implements SearchResult {

	private String res = null;
	private int line = -1;
	private File source = null;
	
	public SearchResultImpl( String res, int line ) {
		this.res = res;
		this.line = line;
		checkRes();
	}
	
	private int nbResult = -1;

	public SearchResultImpl( File source, int nbResult ) {
		this.res = source.getName();
		this.source = source;
		this.nbResult = nbResult;
		checkRes();
	}
		
	public File getSource() {
		return source;
	}
	
	public int getLine() {
		return line;
	}

	private String type = null;
	
	public String getType() {
		if ( source == null )
			return null;
		if ( type == null ) {
			type = source.toString();
			int i = type.lastIndexOf( "." );
			if ( i > -1 ) {
				type = type.substring( i + 1 ).toLowerCase();
			} else
				type = "xml";
		}
		return type;
	}

	private void checkRes() {
		if ( res.length() > 20 ) {
			res = res.substring( 0, 20 ) + "...";
		}
		res = res.trim();
		if ( nbResult > 0 )
			res += " (" + nbResult + " matching)";		
	}
	
	public String toString() {
		return res;
	}

}


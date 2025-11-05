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

package com.japisoft.editix.editor.js.helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternsGroup {

	private List<Object> patterns;
	private boolean caseSensitive;
	
	public PatternsGroup( boolean caseSensitive ) {
		this.caseSensitive = caseSensitive;
	}

	public void addPattern( String regexp, String name ) {
		if ( patterns == null ) {
			patterns = new ArrayList<Object>();
		}
		patterns.add( Pattern.compile( regexp, !caseSensitive ? Pattern.CASE_INSENSITIVE : 0 ) );
		patterns.add( name );
	}

	private Map<String,List<String>> res = null;

	public boolean match( String sequence ) {
		if ( patterns == null ) {
			return false;
		}
		boolean ok = false;
		for ( int i = 0; i < patterns.size(); i += 2 ) {
			Pattern p = ( Pattern )patterns.get( i );
			String name = ( String )patterns.get( i + 1 );
			Matcher m = p.matcher( sequence );
			while ( m.find() ) {
				if ( res == null ) {
					res = new HashMap<String, List<String>>();
				}
				ok = true;
				List<String> l = res.get( name );
				if ( l == null ) {
					res.put( name, l = new ArrayList<String>() );
				}
				for ( int j = 1; j <= m.groupCount(); j++ ) {
					l.add( m.group( j ) );
				}
			}
		}
		return ok;
	}
	
	public boolean hasResult() { return res != null; }
	
	public List<String> getResult( String name ) {
		if ( res == null ) {
			return null;
		}
		List<String> l = res.get( name );
		if ( l != null )
			Collections.sort( l );
		return l;
	}

	public void reset() { res = null; }
	
}


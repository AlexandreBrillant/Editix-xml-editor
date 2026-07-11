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

import java.io.BufferedReader;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.japisoft.editix.toolkit.Toolkit;

public class RegexpSearchEngineImpl implements SearchEngine {

	public List search( File f, String item ) {
		try {
			BufferedReader br = new BufferedReader( 
					Toolkit.getReaderForFile( 
							f ) );
			try {
				String l = null;
				
				Pattern p = Pattern.compile( item );

				ArrayList res = null;
				int cpt = 0;
				while ( ( l = br.readLine() ) != null ) {
					if ( p.matcher( l ).matches() ) {
						if ( res == null )
							res = new ArrayList();
						res.add( new SearchResultImpl( l, cpt ) );
					}
					cpt++;
				}
				return res;
			} finally {
				br.close();
			}
		} catch (Exception e) {
			return null;
		}
	}

	public String toString() {
		return "Regular expression";
	}

}

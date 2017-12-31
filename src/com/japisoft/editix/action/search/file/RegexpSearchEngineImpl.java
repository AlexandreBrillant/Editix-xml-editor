package com.japisoft.editix.action.search.file;

import java.io.BufferedReader;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.japisoft.editix.toolkit.Toolkit;

/**
This program is available under two licenses : 

1. For non commercial usage : 

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

2. For commercial usage :

You need to get a commercial license for source usage at : 

http://www.editix.com/buy.html

Copyright (c) 2018 Alexandre Brillant - JAPISOFT SARL - http://www.japisoft.com

@author Alexandre Brillant - abrillant@japisoft.com
@author JAPISOFT SARL - http://www.japisoft.com

*/
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

package com.japisoft.xmlpad;

import java.io.File;
import javax.swing.filechooser.FileFilter;

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
public class XMLDocumentFileFilter extends FileFilter {
	
	private String[] fileExts;
	private String def;
	private String name;
	private String type;
	
	public XMLDocumentFileFilter( 
			String name, 
			String type, 
			String[] fileExts, 
			String def ) {
		this.fileExts = fileExts;
		this.def = def;
		this.name = name;
		this.type = type;
	}
	
	public String getDescription() {
		StringBuffer sb = new StringBuffer();
		for ( int i = 0; i < fileExts.length; i++ ) {
			if ( i > 0 )
				sb.append( " " );
			sb.append( "*." ).append( fileExts[ i ] );
		}
		return name + " (" + sb.toString() + ")";		
	}
	
	public boolean accept(File f) {
		if (f.isDirectory())
			return true;
		if ( f.toString() == null )
			return false;
		String sf = f.toString().toLowerCase();
		if (fileExts == null)
			return sf.endsWith( def );
		for (int i = 0; i < fileExts.length; i++) {
			if (sf.endsWith(fileExts [i ].toString()))
				return true;
		}
		return false;	
	}

	public String getType() {
		return type;
	}
	
}

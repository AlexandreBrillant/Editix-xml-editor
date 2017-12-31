package com.japisoft.editix.plugin;

import javax.swing.text.Document;

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
public class PluginContext {
	private Document doc;
	private int location;
	private String path;

	public PluginContext( Document doc, int location, String path ) {
		this.doc = doc;
		this.location = location;
		this.path = path;
	}
	
	/** @return the current document */
	public Document getDocument() { return doc; 
	}

	/** @return the current document location */
	public int getLocation() {
		return location;
	} 

	/** @return the current document path */
	public String getPath() {
		return path;
	}
}

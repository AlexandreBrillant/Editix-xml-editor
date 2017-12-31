package com.japisoft.xmlpad;

import java.util.EventObject;

import com.japisoft.framework.xml.parser.node.FPNode;

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
public class LocationEvent extends EventObject {
	private FPNode location;
	private Object source;

	public LocationEvent( Object source, FPNode location ) {
		super( source );
		this.location = location;
	}

	private LocationEvent() {
		super( null );
	}

	private static LocationEvent singleton = null;
	
	
	public static LocationEvent getSharedInstance( Object source, FPNode location ) {
		if ( singleton == null )
			singleton = new LocationEvent();
		singleton.location = location;
		singleton.source = source;
		return singleton;
	}

	public Object getSource() { return source;
	}

	/** @return a node from the current document for the caret position. This method
	 * may return <code>null</code> */ 
	public FPNode getDocumentLocation() { return location; }

	/** @return the current XPath location. This method can return an empty string "" */
	public String getXPathLocation() { 
		if ( location != null )
			return location.getCachedXPathLocation();
		return "";
	}

}

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

package com.japisoft.xmlpad;

import java.util.EventObject;

import com.japisoft.framework.xml.parser.node.FPNode;

/**
 * Event object part for the <code>LocationListener</code>
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
   @see LocationListener */
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

package com.japisoft.xmlpad.action;

import com.japisoft.xmlpad.editor.*;
import com.japisoft.xmlpad.*;
import java.util.Vector;

import javax.swing.Action;

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
public class ActionGroup extends Vector {

	public ActionGroup(String name) {
		setName(name);
	}

	/** Updage action for this group for working on this editor and this container */
	public void resetActionState( XMLEditor editor, XMLContainer container ) {
		for ( int i = 0; i < size(); i++ ) {
			//if ( !(  get( i ) instanceof UnResetableState ) ) {
				if ( get( i ) instanceof XMLAction ) {
					( ( XMLAction )get( i ) ).setXMLContainer( container );
					( ( XMLAction )get( i ) ).setXMLEditor( editor );
				}
			//}
		}
	}

	/** @return a current XMLAction matching this name */
	public Action getActionByName( String name ) {
		for ( int i = 0; i < size(); i++ ) {
			Action a = ( Action )get( i );
			if ( name.equals( getActionName( a ) ) ) {
				return a;
			}
		}
		return null;
	}

	private String getActionName( Action a ) {
		if ( a instanceof XMLAction ) {
			return ( ( XMLAction )a ).getName();
		} else {
			return ( String )a.getValue( Action.NAME );
		}
	}

	private String name;

	/** Set a name for this group */
	public void setName(String name) {
		this.name = name;
	}

	/** @return the name of this group */
	public String getName() {
		return name;
	}

	/** Add a new XML action */
	public void addAction( Action a) {
		add( a );
	}

	/** Remove a known XML action */
	public void removeAction( Action a ) {
		remove( a ); 
	}

	/** Remove an action by name */	
	public void removeAction( String name ) {
		Action a = getActionByName( name );
		if ( a != null )
			removeAction( a );
	}

}

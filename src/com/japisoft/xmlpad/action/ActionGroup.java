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

package com.japisoft.xmlpad.action;

import com.japisoft.xmlpad.editor.*;
import com.japisoft.xmlpad.*;
import java.util.Vector;

import javax.swing.Action;

/** 
 * A Group is a set of XMLAction. A group is managed by an ActionModel. A group
 * has always a name.
 * @version 1.0
 * @see ActionModel
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

package com.japisoft.framework.dockable.action;

import java.util.ArrayList;

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
public class BasicActionModel extends ArrayList implements ActionModel {

	/** Simple way to create an <code>ActionModel</code> instance */
	public static ActionModel getInstance( Action a ) {
		return getInstance( new Action[] { a } );
	}

	/** Simple way to create an <code>ActionModel</code> instance */
	public static ActionModel getInstance( Action[] a ) {
		return new BasicActionModel( a );
	}

	public BasicActionModel() {
		super();
	}
	
	/** Reset the model with the following actions */
	public BasicActionModel( Action[] actions ) {
		if ( actions == null )
			throw new RuntimeException( "Null actions ? " );
		for ( int i = actions.length - 1; i >= 0; i-- ) {
			addAction( actions[ i ] );
		}
	}
	
	/** @return an action matching this class */
	public Action getActionByClass(Class className ) {
		for ( int i = 0; i < size(); i++ ) {
			Object o = get( i );
			if ( o.getClass() == className )
				return (Action)o;
		}
		return null;
	}

	/** @return an action at this index */
	public Action getAction( int index ) {
		return ( Action )get( index );
	}

	/** @return the number of action */
	public int getActionCount() {
		return size();
	}

	/** Add a new action */
	public void addAction( Action a ) {
		add( a );
		fireStateChanged();
	}
	
	/** Add an action at this index starting from 0 */
	public void addAction( Action a, int index ) {
		add( index, a );
		fireStateChanged();
	}

	/** Remove an action */
	public void removeAction( Action a ) {
		remove( a );
		fireStateChanged();
	}

	public void removeAll() {
		super.removeAll( this );
		fireStateChanged();
	}

	private ArrayList listeners = null;

	/** Add a listener for knowing the action model state change */
	public void addModelStateListener( ModelStateListener listener ) {
		if ( listeners == null )
			listeners = new ArrayList();
		listeners.add( listener );
	}

	/** Remove a known listener */
	public void removeModelStateListener( ModelStateListener listener ) {
		if ( listeners != null )
			listeners.remove( listener );
	}

	private void fireStateChanged() {
		if ( listeners != null ) {
			for ( int i = 0; i < listeners.size(); i++ ) {
				ModelStateListener listener = ( ModelStateListener )listeners.get( i );
				listener.modelModified( this );
			}
		}
	}

	public boolean isSeparator(int index) {
		return getAction( index ) == SEPARATOR;
	}	

	public String toString() {
		String s = "[";
		for ( int i = 0; i < size(); i++ ) {
			if ( i > 0 )
				s += ",";
			s += get( i );
		}
		s += "]";
		return s;
	}

}

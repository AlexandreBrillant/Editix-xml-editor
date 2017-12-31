package com.japisoft.multipanes;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JComponent;

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
public class BasicTitledPane implements TitledPane {

	/** 	
	 * @param name Name of the titledPane, it can't be <code>null</code>
	 * @param title Title of the titledPane, it can't be <code>null</code>
	 * @param icon Major icon, it can be <code>null</code>
	 * @param tooltip help for the titledPane, it can be <code>null</code>
	 * @param view, main view, it can't be <code>null</code> */
	public BasicTitledPane( String name, String title, Icon icon, String tooltip, JComponent view ) {
		if ( name == null )
			throw new RuntimeException( "illegal null name " );
		if ( title == null )
			throw new RuntimeException( "illegal null title " );
		if ( view == null )
			throw new RuntimeException( "illegal null view" );
		this.name = name;
		this.title = title;
		this.icon = icon;
		this.tooltip = tooltip;
		this.view = view;
	}

	/** All parameters are required, so they can't be <code>null</code> */
	public BasicTitledPane( String name, String title, JComponent view ) {
		this( name, title, null, null, view );
	}
	
	public BasicTitledPane( String name, String title, String tooltip, JComponent view ) {
		this( name, title, null, tooltip, view );
	}
	
	private String name;
	private String title;
	private Icon icon;
	private String tooltip;
	private JComponent view;

	public String getTitle() {
		return title;
	}

	public void setTitle( String title ) {
		if ( this.title != title ) {
			propertyChanged( Action.NAME, this.title, title );
		}
	}

	public Icon getIcon() {
		return icon;
	}

	public void setIcon( Icon icon ) {
		if ( this.icon != icon )
			propertyChanged( Action.SMALL_ICON, this.icon, icon );
		this.icon = icon;
	}

	public String getToolTip() {
		return tooltip;
	}

	public void setToolTip( String tooltip ) {
		if ( this.tooltip != tooltip ) {
			propertyChanged( Action.SHORT_DESCRIPTION, this.tooltip, tooltip );
		}
		this.tooltip = tooltip;
	}

	public String getName() {
		return name;
	}

	public JComponent getView() {
		return view;
	}
	
	public void open() {
	}

	public void close() {
	}

	private boolean enabled = true;

	public void setEnabled( boolean enabled ) {
		if ( this.enabled != enabled )
			propertyChanged( 
				"enabled", 
				new Boolean( this.enabled ), 
				new Boolean( enabled ) );	
		this.enabled = enabled;
		view.setEnabled( enabled );
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void dispose() {
		if ( listeners != null ) {
			listeners.removeAll( listeners );
			listeners = null;
		}
	}

	private ArrayList listeners = null;
	private Color background = null;
	private Color foreground = null;
	
	public void setBackground( Color background ) {
		this.background = background;
		propertyChanged( 
				"background",
				this.background,
				background );
	}
	
	public void setForeground( Color foreground ) {
		this.foreground = foreground;
		propertyChanged(
				"foreground",
				this.foreground,
				foreground );
	}

	public Color getBackground() { return background; }
	public Color getForeground() { return foreground; }
	
	public void addPropertyChangeListener( PropertyChangeListener listener ) {
		if ( listeners == null )
			listeners = new ArrayList();
		listeners.add( listener );		
	}

	private void propertyChanged( String name, Object oldValue, Object newValue ) {
		if ( listeners != null ) {
			PropertyChangeEvent pce = 
				new PropertyChangeEvent(
					this, name, oldValue, newValue );
			
			for ( int i = 0; i < listeners.size(); i++ ) {
				PropertyChangeListener listener = ( PropertyChangeListener )listeners.get( i );
				listener.propertyChange( pce );
			}
		}
	}

}

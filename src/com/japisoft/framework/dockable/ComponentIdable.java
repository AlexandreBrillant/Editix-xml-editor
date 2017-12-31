package com.japisoft.framework.dockable;

import java.awt.Color;
import java.awt.Rectangle;

import javax.swing.Icon;
import javax.swing.JComponent;

import com.japisoft.framework.dockable.action.ActionModel;
import com.japisoft.framework.dockable.action.BasicActionModel;

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
class ComponentIdable implements Windowable {

	private JComponent comp;

	public ComponentIdable( JComponent comp ) {
		this.comp = comp;
		if ( comp == null )
			throw new RuntimeException( "No component provided !" );
	}

	public String getId() {
		return comp.getName();
	}

	public JComponent getView() {
		return (JComponent)comp;
	}

	public JComponent getUserView() {
		return getView();
	}	
	
	public String getTitle() {
		return null;
	}	
	
	public void setTitle(String title) {
	}
	
	public	ActionModel getActionModel() {
		return new BasicActionModel();
	}
	
	public void setBackground( Color background ) {
		comp.setBackground( background );
	}
	
	public void setForeground( Color foreground ) {
		comp.setForeground( foreground );
	}

	public void requestFocus() {
		comp.requestFocus();
	}
	
	public JComponent getContentPane() {
		throw new RuntimeException( "Illegal usage" );
	}

	public void setContentPane( JComponent container ) {
		throw new RuntimeException( "Illegal usage" );		
	}	

	public void repaint() {
		comp.repaint();
	}

	public Rectangle getFrameBounds() {
		throw new RuntimeException( "Illegal usage" );
	}

	public void setFrameBounds( Rectangle r ) {
		throw new RuntimeException( "Illegal usage" );		
	}

	private boolean mstate = false;
	
	public void setMaximized(boolean max) {
		this.mstate = max;
	}
	
	public boolean isMaximized() {
		return mstate;
	}
	
	public Icon getIcon() {
		return null;
	}

	public void setIcon( Icon icon ) {
	}

	private boolean fixed = false;
		
	public boolean isFixed() {
		return fixed;
	}

	public void setFixed( boolean fixed ) {
		this.fixed = fixed;
	}	

	private boolean resize = true;
	
	public boolean isResizable() {
		return resize;
	}
	public void setResizable(boolean resize) {
		this.resize = resize;
	}

	public void fireDockEvent( String id, int type ) {
	}

}


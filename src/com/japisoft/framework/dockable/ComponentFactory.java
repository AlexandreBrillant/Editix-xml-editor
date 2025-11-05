// Editix XML Editor
// https://www.editix.com
// Copyright (c) 2025 Alexandre Brillant
// 
// For non-commercial usage :
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
// For commercial use or integration into proprietary software :
// A commercial license is required. Visit https://www.editix.com for details.

package com.japisoft.framework.dockable;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 */
public class ComponentFactory {

	private static ComponentFactory SINGLETON = null;
	
	/** Override the default factory by this sub class */
	public static void setComponentFactory( ComponentFactory factory ) {
		SINGLETON = factory;
	}

	/** @return the current component factory */
	public static ComponentFactory getComponentFactory() {
		if ( SINGLETON == null )
			SINGLETON = new ComponentFactory();
		return SINGLETON;
	}

	private boolean autoScrollMode = false;

	/** This is a mode for adding a JScrollPane automatically inside any inner window. By default <code>false</code> */
	public void setAutoScrollMode( boolean autoScrollMode ) {
		this.autoScrollMode = autoScrollMode;
	}

	/** Build a button for the title bar of the <code>DockableFrame</code> */
	public JButton buildButton( Action a ) {
		JButton b = new JButton( a );
		b.setBorderPainted( false );
		b.setBorder( null );
		b.setOpaque( false );
		return b;
	}

	/** Build a new title bar for the <code>DockableFrame</code> */
	public DockableFrameTitleBar buildTitleBar( String title ) {
		return new DefaultTitleBar( title );
	}

	/** Build an individual frame for the <code>DockableFrame</code> 
	 * @param source The Action that has changed the state of the <code>DockableFrame</code>
	 * @param panel The initial DockableFrame
	 * */	
	public JFrame buildDockedFrame( Action source, Windowable panel ) {
		return new DefaultDockedFrame( source, panel );
	}

	/** @return a new contentPane for the <code>DockableFrame</code> */
	public JComponent buildContentPaneForFrame() {
		if ( autoScrollMode )
			return new JScrollPane();
		else
			return new JPanel();
	}

}


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

import javax.swing.Icon;
import javax.swing.JComponent;

import com.japisoft.framework.dockable.action.ActionModel;

/**
 * This object stores the inner window properties like the title, the id
 * and the view which is the final component. This is used inside the <code>addInnerWindow</code>
 * method from the JDock class
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * @see com.japisoft.framework.dockable.JDock
 * */
public class InnerWindowProperties {

	private String id;
	private String title;
	private Icon icon;
	private ActionModel model;
	private JComponent view;
	private boolean autoScroll;

	/**
	 * @param id The inner window id, it cannot be <code>null</code>
	 * @param title The inner window title
	 * @param icon The inner window icon
	 * @param model The inner window actions model for the toolbar
	 * @param view The inner window component
	 * @param autoScroll The inner window autoScroll management
	 */
	public InnerWindowProperties(
			String id, 
			String title, 
			Icon icon, 
			ActionModel model, 
			JComponent view, 
			boolean autoScroll ) {
		
		if ( id == null )
			throw new RuntimeException( "Invalid id, it cannot be null !" );
		if ( view == null )
			throw new RuntimeException( "Invalid view, it cannot be null !");
		
		this.id = id;
		this.title = title;
		this.icon = icon;
		this.model = model;
		this.view = view;
		this.autoScroll = autoScroll;
	}

	/**
	 * @param id The inner window id, it cannot be <code>null</code>
	 * @param title The inner window title
	 * @param icon The inner window icon
	 * @param model The inner window actions model for the toolbar
	 * @param view The inner window component
	 */
	public InnerWindowProperties(
			String id, 
			String title, 
			Icon icon, 
			ActionModel model, 
			JComponent view ) {
		this( id, title, icon, model, view, false );
	}
	
	/**
	 * @param id The inner window id, it cannot be <code>null</code>
	 * @param title The inner window title
	 * @param icon The inner window icon
	 * @param view The inner window component */
	public InnerWindowProperties(
			String id, 
			String title, 
			Icon icon, 
			JComponent view ) {
		this( id, title, icon, null, view, false );
	}	

	/**
	 * @param id The inner window id, it cannot be <code>null</code>
	 * @param title The inner window title
	 * @param icon The inner window icon
	 * @param view The inner window component 
	 * @param autoScroll The inner window autoScroll management
	 * */
	public InnerWindowProperties(
			String id, 
			String title, 
			Icon icon, 
			JComponent view,
			boolean autoScroll ) {
		this( id, title, icon, null, view, autoScroll );
	}	
	
	/**
	 * @param id The inner window id, it cannot be <code>null</code>
	 * @param title The inner window title
	 * @param view The inner window component */
	public InnerWindowProperties(
			String id,
			String title,
			JComponent view ) {
		this( id, title, null, null, view, false );
	}	
	
	/** @return the id for this inner window */
	public String getId() { return id; }
	/** @return the title for this inner window */
	public String getTitle() { return title; }
	/** @return the icon for this inner window */
	public Icon getIcon() { return icon; }
	/** @return the action model for this inner window toolbar */
	public ActionModel getActionModel() { return model; }
	/** @return the main component for this inner window */
	public JComponent getView() { return view; }
	/** @return the autoscroll status. By default <code>false</code> */
	public boolean isAutoScroll() { return autoScroll; }

	void dispose() {
		this.view = null;
	}
}


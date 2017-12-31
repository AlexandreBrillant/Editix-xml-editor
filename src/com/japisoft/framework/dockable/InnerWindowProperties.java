package com.japisoft.framework.dockable;

import javax.swing.Icon;
import javax.swing.JComponent;

import com.japisoft.framework.dockable.action.ActionModel;
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

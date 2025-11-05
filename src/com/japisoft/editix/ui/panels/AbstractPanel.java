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

package com.japisoft.editix.ui.panels;

import javax.swing.Icon;
import javax.swing.JComponent;

import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.xmlpad.XMLContainer;

public abstract class AbstractPanel implements Panel {
	private boolean shownState = false;
	protected String params = null;
	
	public void init() {}

	public void setParams(String params) {
		this.params = params;
	}	

	public void setState( boolean shown ) {
		this.shownState = shown;
	}

	public void close() {}
	public void select(Object path) {}	

	public void showPanel() {		
		preShow();
		shownState = true;
		show();
	}
	
	protected void preShow() {}
	
	public boolean isShown() {
		return shownState;
	}	

	public void hidePanel() {
		shownState = false;
		hide();
	}

	public void showHidePanel() {
		if ( EditixFrame.dockingSpace.isHiddenPanes() )
			shownState = false;
		if ( !shownState ) {
			showPanel();
		} else {
			hidePanel();
		}
	}

	protected void show() {
		if ( !EditixFrame.dockingSpace.hasPane( 
				getId() ) ) {
			EditixFrame.dockingSpace.setPane(
					getId(),
					getTitle(),
					getIcon(),
					getView() );
		}
		EditixFrame.dockingSpace.showPane( getId() );
		postShow();
	}

	protected void postShow() {}
	
	protected void hide() {
		preHide();
		PanelManager.saveState( true );
		EditixFrame.dockingSpace.hidePane( getId() );
	}

	protected void preHide() {}
	
	private JComponent builtView;

	protected abstract String getTitle();

	public JComponent getView() {
		if ( builtView == null )
			builtView = buildView();
		return builtView;
	}

	protected abstract JComponent buildView();
	
	private String id;
	public void setId( String id ) { this.id = id; }
	protected String getId() { return id; }
	
	private Icon icon;
	protected Icon getIcon() {
		return icon;
	}
	public void setIcon( Icon icon ) {
		this.icon = icon;
	}
	
	public boolean isVisible() {
		return shownState;
	}
	
	public void setCurrentXMLContainer( XMLContainer container ) {}

}


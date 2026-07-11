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

package com.japisoft.editix.ui.leftpanels;

import javax.swing.Icon;
import javax.swing.JComponent;

import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.xmlpad.XMLContainer;

public abstract class AbstractLeftPanel implements LeftPanel {
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
		if ( EditixFrame.THIS.dockingSpace.isHiddenPanes() )
			shownState = false;
		if ( !shownState ) {
			showPanel();
		} else {
			hidePanel();
		}
	}

	protected void show() {
		if ( !EditixFrame.THIS.dockingSpace.hasPane( 
				getId() ) ) {
			EditixFrame.THIS.dockingSpace.setPane(
					getId(),
					getTitle(),
					getIcon(),
					getView() );
		}
		EditixFrame.THIS.dockingSpace.showPane( getId() );
		postShow();
	}

	protected void postShow() {}
	
	protected void hide() {
		preHide();
		LeftPanelManager.saveState( true );
		EditixFrame.THIS.dockingSpace.hidePane( getId() );
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

package com.japisoft.editix.ui.panels;

import javax.swing.Icon;
import javax.swing.JComponent;

import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.xmlpad.XMLContainer;

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

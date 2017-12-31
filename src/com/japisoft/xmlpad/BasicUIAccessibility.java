package com.japisoft.xmlpad;

import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.JTree;

import com.japisoft.xmlpad.editor.XMLEditor;
import com.japisoft.xmlpad.elementview.ElementView;
import com.japisoft.xmlpad.error.ErrorView;

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
class BasicUIAccessibility implements UIAccessibility {

	XMLContainer container;

	BasicUIAccessibility(XMLContainer container) {
		this.container = container;
	}

	public JSplitPane getSplitPane() {
		return container.mainSplitPane;
	}

	public void setInitialTreeTextDividerLocation(double location) {
		container.setInitialTreeTextDividerLocation(location);
	}

	public void setInitialElementViewDividerLocation(double location) {
		container.setElementViewDividerLocation(location);
	}

	public JSplitPane getSecondarySplitPane() {
		return container.elementSplitPane;
	}

	public boolean isPopupAvailable() {
		return container.isPopupAvailable();
	}

	public boolean isToolBarAvailable() {
		return container.isToolBarAvailable();
	}

	public boolean isToolBarVisible() {
		return container.isToolBarVisible();
	}

	public boolean isTreePopupAvailable() {
		return container.isTreePopupAvailable();
	}

	public void setPopupAvailable(boolean popupAvailable) {
		container.setPopupAvailable(popupAvailable);
	}

	public void setToolBarAvailable(boolean toolBarAvailable) {
		container.setToolBarAvailable(toolBarAvailable);
	}

	public void setToolBarVisible(boolean toolbarVisible) {
		container.setToolBarAvailable(toolbarVisible);
	}

	public void setTreePopupAvailable(boolean treePopupAvailable) {
		container.setTreePopupAvailable(treePopupAvailable);
	}

	public JPopupMenu getCurrentPopup() {
		return container.getCurrentPopup();
	}

	public JPopupMenu getCurrentTreePopup() {
		return container.getCurrentTreePopup();
	}

	public XMLEditor getEditor() {
		return container.getEditor();
	}

	public JToolBar getToolBar() {
		return container.getToolBar();
	}

	public JToolBar getTreeToolBar() {
		return container.treeToolbar;
	}

	public JTree getTree() {
		return container.getTree();
	}

	public boolean isTreeAvailable() {
		return container.isTreeAvailable();
	}

	public boolean isTreeVisible() {
		return container.isTreeVisible();
	}

	public void setTreeAvailable(boolean treeAvailable) {
		container.setTreeAvailable( treeAvailable );
	}

	public void setTreeDelegate(JTree treeDelegate) {
		container.setTreeDelegate( treeDelegate );
	}

	public void setTreeVisible(boolean treeVisible) {
		container.setTreeVisible( treeVisible );
	}

	public void setTreeToolBarAvailable(boolean treeToolBarAvailable) {
		container.treeToolBarAvailable = treeToolBarAvailable;
	}

	public boolean isTreeToolBarAvailable() {
		if ( container == null )
			return false;
		return container.treeToolBarAvailable;
	}

	public ErrorView getErrorView() {
		return container.getErrorView();
	}

	public void setErrorView(ErrorView view) {
		container.setErrorView( view );
	}

	public ElementView getElementView() {
		return container.getElementView();
	}

	public void setElementView(ElementView view) {
		container.setElementView( view );
	}

	public void dispose() {
		this.container = null;
	}

	private boolean enableDragNDropForRoot = false;
	
	public void setEnableDragNDropForRoot( boolean enabled ) {
		enableDragNDropForRoot = enabled;
	}
	
	public boolean isEnabledDragNDropForRoot() {
		return enableDragNDropForRoot;
	}
	
	
}

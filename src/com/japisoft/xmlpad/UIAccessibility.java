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
public interface UIAccessibility {

	/** @return the current XMLEditor. Note that depending the splitting state of the <code>XMLContainer</code> this is not always the same instance */
	public XMLEditor getEditor();

	/** Show or hide the location tree. This method has no effect
	 * if no tree is available => <code>XMLPadProperties.setProperty("tree", "false" )</code>
	 * @param treeVisible Show or hide the current location tree */
	public void setTreeVisible(boolean treeVisible);
	
	/** @return <code>true</code> if there's a current location tree and the splitpane bar is not closed */
 	public boolean isTreeVisible();

	/** Decide to have a left tree with the XML document content or not. By default <code>true</code> */
	public void setTreeAvailable(boolean treeAvailable);

	/** @return <code>true</code> if the tree is available */
	public boolean isTreeAvailable(); 

	/** Decide to have a toolbar at the top of the tree or not. By default <code>false</code> */
	public void setTreeToolBarAvailable( boolean treeToolBarAvailable );

	/** @return <code>true</code> if the tree is available */
	public boolean isTreeToolBarAvailable(); 
	
	/** @return the current XML tree view */	
	public JTree getTree(); 

	/** This is a way to share a tree which is outside the XMLContainer. This is only useful if the
	 * <code>setTreeAvailable( false )</code> method is called */	
	public void setTreeDelegate(JTree treeDelegate);
		
	/** @return the current tree menu popup */
	public JPopupMenu getCurrentTreePopup();

	/** @return the main toolbar */
	public JToolBar getToolBar();

	/** @return the tree toolBar or <code>null</code> */
	public JToolBar getTreeToolBar();
	
	/** Show or hide the default toolbar. This method has no effect
	 * if the default toolbar is not available. Note that this method
	 * is only for dynamic usage, it has no effect before the visibility
	 * of the <code>XMLContainer</code>. If you wish no toolbar, you must
	 * call <code>setToolBarAvailable( false )</code> */
	public void setToolBarVisible(boolean toolbarVisible);

	/** @return the toolbar state */
	public boolean isToolBarVisible();

	/** Create a default toolbar. By default <code>true</code>. If user has
	 * an external toolbar, this property must be set to <code>false</code>. It is
	 * possible to control the visibility by calling <code>setToolBarVisible( ... )</code> */
	public void setToolBarAvailable(boolean toolBarAvailable);

	/** @return <code>true</code> if a default toolbar is available */
	public boolean isToolBarAvailable();

	/** Create a default popup. By default <code>true</code> */
	public void setPopupAvailable(boolean popupAvailable);

	/** @return <code>true</code> if a default popup is available */
	public boolean isPopupAvailable();

	/** Reset the tree popup. By default to <code>true</code>. This code has no effect if no tree
	 * is used.
	 */
	public void setTreePopupAvailable(boolean treePopupAvailable);

	/** @return <code>true</code> if a tree exists and if the tree popup is available */
	public boolean isTreePopupAvailable();

	/** @return the main splitpane dividing the tree and the source. It may be <code>null</code> depending on the moment you call it */
	public JSplitPane getSplitPane();

	/** @return the secondary splitpane dividing the tree and the element view. It may be <code>null</code> depending on the moment you call it */
	public JSplitPane getSecondarySplitPane();

	/** Reset the initial divider location between tree and text. This value is between 0 to 1
	 * @param location Location between 0 and 1 / 0.5 means at the middle
	 * */
	public void setInitialTreeTextDividerLocation(double location);

	/** Reset the initial divider location between the tree and the element view. 
	 * This value is between 0 to 1
	 * @param location Location between 0 and 1 / 0.5 means at the middle
	 */
	public void setInitialElementViewDividerLocation(double location);

	/** Define a new error view panel. It must be called before showing the first error */
	public void setErrorView( ErrorView view );

	/** @return the current error view panel */
	public ErrorView getErrorView();

	/**
	 * Set a view fo visualizing an XML element under the tree. if view is
	 * <code>null</code> no view will be available */	
	public void setElementView( ElementView view );

	/** @return the current view for visualizing an element */
	public ElementView getElementView();	

	/** This is only for inner usage, it mustn't be called by the user */
	void dispose();

	/** Particular cas for DragNDrop from the tree */
	public void setEnableDragNDropForRoot( boolean enabled );
	
	/** Particular cas for DragNDrop from the tree */
	public boolean isEnabledDragNDropForRoot();

}

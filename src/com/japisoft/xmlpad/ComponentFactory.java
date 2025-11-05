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

package com.japisoft.xmlpad;

import com.japisoft.xmlpad.editor.*;
import com.japisoft.xmlpad.elementview.ElementView;
import com.japisoft.xmlpad.elementview.table.TableElementView;
import com.japisoft.xmlpad.error.DefaultErrorView;
import com.japisoft.xmlpad.error.ErrorView;
import com.japisoft.xmlpad.helper.ui.BasicTitledPanelHelper;
import com.japisoft.xmlpad.helper.ui.TitledPanelHelper;

import javax.swing.*;

/**
 * <p>Here a factory for default swing elements used by the XMLContainer. User can use
 * it by creating a sub-class of the ComponentFactory class and call the
 * <code>setFactory</code> for overriding the default behavior, this it is possible
 * to replace any component used inside JXMLPad by your own.
  
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.2
 * @see XMLContainer */
public class ComponentFactory {
	
	protected ComponentFactory() {
		super();
	}
	
	static ComponentFactory FACTORY;
	
	/** @return the current factory */
	public static ComponentFactory getFactory() {
		if ( FACTORY == null )
			FACTORY = new ComponentFactory();
		return FACTORY;
	}
	
	/** Override the default factory by a subclass */
	public static void setFactory( ComponentFactory factory ) {
		FACTORY = factory;
	}

	/** create a scrollpane for editor or tree */
	public JScrollPane getNewScrollPane() {
		return new JScrollPane();
	}

	/** create a panel for editor and tree */
	public JPanel getNewPanel() {
		return new JPanel();
	}

	/** create a vertical oriented splitpane */
	public JSplitPane getNewVerticalSplitPane() {
		JSplitPane sp = new JSplitPane( JSplitPane.VERTICAL_SPLIT );
		// sp.setOneTouchExpandable( true );
		return sp;
	}

	/** create an horizontal oriented splitpane */
	public JSplitPane getNewHorizontalSplitPane() {
		JSplitPane sp = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT );
		// sp.setOneTouchExpandable( true );
		return sp;
	}

	/** create a default tee */
	public JTree getNewTree() {
		return new JTree();
	}

	/** create a default toolBar */
	public JToolBar getNewToolBar() {
		return new JToolBar();
	}
	
	/** create a default toolBar for the tree */
	public JToolBar getNewTreeToolBar() {
		return new JToolBar();
	}

	/** create a default XMLEditor */
	public XMLEditor getNewXMLEditor( EditorContext context ) {
		XMLEditor e = new XMLEditor( context );
		return e;
	}

	/** create a default popupMenu */
	public JPopupMenu getNewPopupMenu() {
		return new JPopupMenu();
	}

	/** @return a new table element view */
	public ElementView getNewElementView( XMLContainer container ) {
		return new TableElementView( container );
	}
	
	/** @return a component for showing the helper's title */
	public TitledPanelHelper getNewTitledPanelHelper() {
		return new BasicTitledPanelHelper();
	}

	/** @return a component for showing the helper's title */
	public TitledPanelHelper getNewFooterPanelHelper() {
		return null;
	}

	/** @return a view for this container */
	public IView getUIContainer( XMLContainer container ) {
		return new DefaultView( container );
	}

	/** @return the default error view used by the XML container */
	public ErrorView getDefaultErrorView() {
		return new DefaultErrorView();
	}
}



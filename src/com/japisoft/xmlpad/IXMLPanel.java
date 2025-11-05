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

import java.util.Iterator;
import javax.swing.Action;
import javax.swing.JComponent;

import com.japisoft.xmlpad.bookmark.BookmarkContext;
import com.japisoft.xmlpad.tree.parser.Parser;

/**
 * Here an interface for using the <code>XMLContainer</code>. It gives the
 * ability to manages several XMLContainer for one view.
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0 */
public interface IXMLPanel {
	
	public IXMLPanel getPanelParent();
	
	/** @return the current container */
	public XMLContainer getMainContainer();

	public String getCurrentDocumentLocation();	
	
	/** Particular case with several XMLContainer (like XSLT tab) */
	public XMLContainer getSelectedContainer();

	/** For special case with several XMLContainers like for the XSLT debugger */
	public BookmarkContext getBookmarkContext();
	
	/** @return another container for this document type */
	public XMLContainer getSubContainer( String type );

	/** @return a new parser for the document tree */
	public Parser createNewParser( boolean lightweightMode );
	
	// Special case for XSLT
	/** @return set of sub container */
	public int getSubContainerCount();

	/** @return set of sub container */
	public XMLContainer getSubContainerAt( int index );

	/** Force selection of this panel */
	public void selectSubContainer( IXMLPanel panel );

	/** @return the global ui view */
	public JComponent getView();

	/** reset a component property */
	public void setProperty(String name, Object content);
		
	/** @return a property value */
	public Object getProperty(String name);

	/** @return a property value of the default one */
	public Object getProperty(String name, Object def );

	/** @return a list of properties */
	public Iterator getProperties();
		
	/** Dispose this container */
	public void dispose();

	/** Prepare to save the editor */
	public void prepareToSave();
	
	/** Call when loading a new content */
	public void postLoad();
	
	/** Reload the content and return <code>true</code> if the operation is a success */
	public boolean reload();
	
	public void cut();
	public void copy();
	public void paste();
	
	public Object print();
	
	public void setDocumentInfo( XMLDocumentInfo info );	
	
	/**
	 * Set the component in a special mode for freeing internal resource. By default
	 * to <code>false</code> */
	public void setAutoDisposeMode( boolean disposeMode );
	
	/** For custom copy/cut/paste action as samples */
	public Action getAction( String actionId );

}


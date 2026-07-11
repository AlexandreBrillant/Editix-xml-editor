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

package com.japisoft.editix.ui.leftpanels.xslscenarios;

import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.util.Iterator;
import java.util.Properties;

import javax.swing.Action;
import javax.swing.JComponent;

import com.japisoft.xmlpad.IXMLPanel;
import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.XMLDocumentInfo;
import com.japisoft.xmlpad.bookmark.BookmarkContext;
import com.japisoft.xmlpad.tree.parser.Parser;

public class XSLPropertiesXMLPanel implements IXMLPanel {

	private Properties p = null;
	
	public XSLPropertiesXMLPanel( Properties p ) {
		this.p = p;
	}

	public void dispose() {
	}

	public Action getAction(String actionId) {
		return null;
	}

	public BookmarkContext getBookmarkContext() {
		return null;
	}

	public XMLContainer getMainContainer() {
		return null;
	}
	
	@Override
	public String getCurrentDocumentLocation() {
		return null;
	}	

	public Parser createNewParser( boolean lightweightMode ) {
		return null;
	}	
	
	public IXMLPanel getPanelParent() {
		return null;
	}
	
	public Iterator getProperties() {
		return p.keySet().iterator();
	}

	public Object getProperty(String name) {
		return p.get( name );
	}

	public Object getProperty(String name, Object def) {
		Object obj = p.get( name );
		if ( obj == null )
			return def;
		return obj;
	}

	public XMLContainer getSelectedContainer() {
		return null;
	}

	public XMLContainer getSubContainer(String type) {
		return null;
	}

	public XMLContainer getSubContainerAt(int index) {
		return null;
	}

	public int getSubContainerCount() {
		return 0;
	}

	public JComponent getView() {
		return null;
	}

	public void postLoad() {
	}

	public void prepareToSave() {
	}

	public boolean reload() {
		return false;
	}

	public void selectSubContainer(IXMLPanel panel) {
	}

	public void setAutoDisposeMode(boolean disposeMode) {
	}

	public void setDocumentInfo(XMLDocumentInfo info) {
	}

	public void copy() {
	}

	public void cut() {
	}

	public void paste() {
	}
	
	@Override
	public Object print() {
		return null;
	}
	
	public void setProperty(String name, Object content) {
		if ( content instanceof String ) {
			p.setProperty( 
				name, 
				( String )content 
			);
		}
	}

}

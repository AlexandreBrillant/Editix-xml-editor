package com.japisoft.editix.ui.panels.xslscenarios;

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

	public Parser createNewParser() {
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

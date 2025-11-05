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

package com.japisoft.editix.editor.css;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.util.Iterator;

import javax.swing.Action;
import javax.swing.JComponent;

import com.japisoft.editix.editor.html.HTMLParser;
import com.japisoft.editix.ui.container.EditixXMLContainer;
import com.japisoft.framework.dockable.InnerWindowProperties;
import com.japisoft.framework.dockable.JDock;
import com.japisoft.xmlpad.IXMLPanel;
import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.XMLDocumentInfo;
import com.japisoft.xmlpad.bookmark.BookmarkContext;
import com.japisoft.xmlpad.tree.parser.Parser;

public class CSSContainer extends JDock implements IXMLPanel {

	private CSSEditor editor = null;
	private CSSPreview preview = null;
	
	public CSSContainer( Action parseAction ) {
		editor = new CSSEditor( parseAction );		
		setLayout( new BorderLayout() );
		addInnerWindow( new InnerWindowProperties( "editor", "CSS Editor", editor.getView() ), BorderLayout.CENTER );
		preview = new CSSPreview( editor );
		preview.setPreferredSize( new Dimension( 0, 200 ) );
		addInnerWindow( new InnerWindowProperties( "preview", "CSS Preview", preview ), BorderLayout.SOUTH );
	}

	public void copy() {
		editor.copy();
	}
	
	public Parser createNewParser( boolean lightweightMode ) {
		return editor.createNewParser( lightweightMode );
	}

	@Override
	public String getCurrentDocumentLocation() {
		return editor.getCurrentDocumentLocation();
	}
	
	public IXMLPanel getPanelParent() {
		return null;
	}

	public void cut() {
		editor.cut();
	}
	
	public Object print() {
		return editor;
	};
	
	public Action getAction(String actionId) {
		return editor.getAction( actionId );
	}

	public BookmarkContext getBookmarkContext() {
		return editor.getBookmarkContext();
	}

	public XMLContainer getMainContainer() {
		return editor;
	}

	public Iterator getProperties() {
		return editor.getProperties();
	}

	public Object getProperty(String name, Object def) {
		return editor.getProperty( name, def );
	}

	public Object getProperty(String name) {
		return editor.getProperty( name );
	}

	public XMLContainer getSelectedContainer() {
		return editor;
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

	public void paste() {
		editor.paste();
	}

	public void postLoad() {
		editor.postLoad();
	}

	public void prepareToSave() {
		editor.prepareToSave();
	}

	public boolean reload() {
		return editor.reload();
	}

	public void selectSubContainer(IXMLPanel panel) {
	}

	public void setAutoDisposeMode(boolean disposeMode) {
		editor.setAutoDisposeMode( disposeMode );
	}

	public void setDocumentInfo(XMLDocumentInfo info) {
		editor.setDocumentInfo( info );
	}

	public void setProperty(String name, Object content) {
		editor.setProperty( name, content );
	}

	@Override
	public void dispose() {
		super.dispose();
		preview.dispose( editor );
		editor = null;
	}
	
	// --------------------------------------------------------------------------------------------------
	
	class CustomInnerPanel extends InnerPanel implements IXMLPanel {

		public void dispose() {
			CSSContainer.this.dispose();
		}
		
		@Override
		public String getCurrentDocumentLocation() {
			return editor.getCurrentDocumentLocation();
		}
		
		public Parser createNewParser( boolean lightweightMode ) {
			return editor.createNewParser( lightweightMode );
		}		
		
		public IXMLPanel getPanelParent() {
			return CSSContainer.this;
		}

		public void setAutoDisposeMode(boolean disposeMode) {
			CSSContainer.this.setAutoDisposeMode(disposeMode);
		}

		public XMLContainer getMainContainer() {
			return editor;
		}

		public XMLContainer getSubContainerAt(int index) {
			return null;
		}
		
		public int getSubContainerCount() {
			return 0;
		}
		
		public void selectSubContainer(IXMLPanel panel) {
		}
		
		public BookmarkContext getBookmarkContext() {
			return CSSContainer.this.getBookmarkContext();
		}

		public XMLContainer getSelectedContainer() {
			return CSSContainer.this.getSelectedContainer();
		}
		
		public void copy() {
			CSSContainer.this.copy();
		}
		
		public void cut() {
			CSSContainer.this.cut();
		}
		
		public void paste() {
			CSSContainer.this.paste();
		}
		
		@Override
		public Object print() {
			return CSSContainer.this.print();
		}
		
		public void setDocumentInfo(XMLDocumentInfo info) {
			CSSContainer.this.setDocumentInfo( info );
		}		
		
		public Action getAction(String actionId) {
			return CSSContainer.this.getAction( actionId );
		}

		public Iterator getProperties() {
			return CSSContainer.this.getProperties();
		}

		public Object getProperty(String name, Object def) {
			return CSSContainer.this.getProperty(name, def);
		}

		public Object getProperty(String name) {
			return CSSContainer.this.getProperty(name);
		}

		public XMLContainer getSubContainer(String type) {
			return null;
		}

		public void prepareToSave() {
			CSSContainer.this.prepareToSave();
		}	

		public void postLoad() {
			CSSContainer.this.postLoad();
		}

		public boolean reload() {
			return CSSContainer.this.reload();
		}		

		public JComponent getView() {
			return this;
		}

		public void setProperty(String name, Object content) {
			CSSContainer.this.setProperty( name, content );
		}
	}

	protected InnerPanel createInnerView() {
		return new CustomInnerPanel();
	}
	
	
}


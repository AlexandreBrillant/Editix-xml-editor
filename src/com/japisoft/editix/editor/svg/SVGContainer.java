package com.japisoft.editix.editor.svg;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.util.Iterator;

import javax.swing.Action;
import javax.swing.JComponent;

import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.framework.dockable.InnerWindowProperties;
import com.japisoft.framework.dockable.JDock;

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
public class SVGContainer extends JDock implements IXMLPanel {

	private IXMLPanel editor = null;
	private SVGPreview preview = null;
	
	public SVGContainer() {
		editor = EditixFactory.buildNewContainer();	
		
		setLayout( new BorderLayout() );
		addInnerWindow( new InnerWindowProperties( "editor", "SVG Editor", editor.getView() ), BorderLayout.CENTER );
		preview = new SVGPreview( editor );
		preview.setPreferredSize( new Dimension( 0, 200 ) );
		addInnerWindow( new InnerWindowProperties( "preview", "SVG Preview", preview ), BorderLayout.SOUTH );
	}

	public void copy() {
		editor.copy();
	}

	public void cut() {
		editor.cut();
	}
	
	public Parser createNewParser() {
		return null;
	}

	public IXMLPanel getPanelParent() {
		return null;
	}
	
	public Action getAction(String actionId) {
		return editor.getAction( actionId );
	}

	public BookmarkContext getBookmarkContext() {
		return editor.getBookmarkContext();
	}

	public XMLContainer getMainContainer() {
		return editor.getMainContainer();
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
		return editor.getMainContainer();
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
	
	@Override
	public Object print() {
		return editor;
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
		preview.dispose();
		editor = null;
	}
	
	// --------------------------------------------------------------------------------------------------
	
	class CustomInnerPanel extends InnerPanel implements IXMLPanel {

		public IXMLPanel getPanelParent() {
			return SVGContainer.this;
		}
		
		public Parser createNewParser() {
			return null;
		}		
		
		public void dispose() {
			SVGContainer.this.dispose();
		}

		public void setAutoDisposeMode(boolean disposeMode) {
			SVGContainer.this.setAutoDisposeMode(disposeMode);
		}

		public XMLContainer getMainContainer() {
			return SVGContainer.this.getMainContainer();
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
			return SVGContainer.this.getBookmarkContext();
		}

		public XMLContainer getSelectedContainer() {
			return SVGContainer.this.getSelectedContainer();
		}
		
		public void copy() {
			SVGContainer.this.copy();
		}
		
		public void cut() {
			SVGContainer.this.cut();
		}
		
		public void paste() {
			SVGContainer.this.paste();
		}

		@Override
		public Object print() {
			return SVGContainer.this.print();
		}
		
		public void setDocumentInfo(XMLDocumentInfo info) {
			SVGContainer.this.setDocumentInfo( info );
		}		
		
		public Action getAction(String actionId) {
			return SVGContainer.this.getAction( actionId );
		}

		public Iterator getProperties() {
			return SVGContainer.this.getProperties();
		}

		public Object getProperty(String name, Object def) {
			return SVGContainer.this.getProperty(name, def);
		}

		public Object getProperty(String name) {
			return SVGContainer.this.getProperty(name);
		}

		public XMLContainer getSubContainer(String type) {
			return null;
		}

		public void prepareToSave() {
			SVGContainer.this.prepareToSave();
		}	

		public void postLoad() {
			SVGContainer.this.postLoad();
		}

		public boolean reload() {
			return SVGContainer.this.reload();
		}		

		public JComponent getView() {
			return this;
		}

		public void setProperty(String name, Object content) {
			SVGContainer.this.setProperty( name, content );
		}
	}

	protected InnerPanel createInnerView() {
		return new CustomInnerPanel();
	}
	
	
}

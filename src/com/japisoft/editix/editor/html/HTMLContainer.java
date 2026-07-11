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

package com.japisoft.editix.editor.html;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;

import com.japisoft.editix.editor.html.helper.HTMLHandler;
import com.japisoft.editix.ui.container.EditixXMLContainer;
import com.japisoft.framework.dockable.InnerWindowProperties;
import com.japisoft.framework.dockable.JDock;

import com.japisoft.xmlpad.IXMLPanel;
import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.XMLDocumentInfo;
import com.japisoft.xmlpad.bookmark.BookmarkContext;
import com.japisoft.xmlpad.tree.parser.Parser;

public class HTMLContainer extends JDock implements IXMLPanel {

	private IXMLPanel editor = null;
	private HTMLPreview preview = null;
	
	private Action parseAction = null;

	public HTMLContainer( Action parseAction ) {
		this.parseAction = parseAction;

		// With a custom parser

		editor = new EditixXMLContainer() {
			@Override
			public Parser createNewParser( boolean lightweightMode ) {
				return new HTMLParser();
			}
		};
		
		ArrayList assistant = new ArrayList();
		assistant.add( new HTMLHandler() );

		editor.getMainContainer().getHelperManager().resetHandlers( assistant, false );

		getView().getActionMap().put( "br", new InsertBRAction() );
		getView().getInputMap( JComponent.WHEN_IN_FOCUSED_WINDOW ).put(
			KeyStroke.getKeyStroke( KeyEvent.VK_ENTER, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() ), 
			"br" 
		);

		getView().getActionMap().put( "p", new InsertPAction() );
		getView().getInputMap( JComponent.WHEN_IN_FOCUSED_WINDOW ).put(
			KeyStroke.getKeyStroke( KeyEvent.VK_ENTER, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() | KeyEvent.SHIFT_DOWN_MASK ), 
			"p" 
		);
				
		setLayout( new BorderLayout() );
		addInnerWindow( new InnerWindowProperties( "editor", "HTML Editor", editor.getView() ), BorderLayout.CENTER );
		preview = new HTMLPreview( editor );
		preview.setPreferredSize( new Dimension( 0, 200 ) );
		addInnerWindow( new InnerWindowProperties( "preview", "HTML Preview", preview ), BorderLayout.SOUTH );
	}
	
	public IXMLPanel getPanelParent() {
		return null;
	}

	@Override
	public String getCurrentDocumentLocation() {
		return editor.getCurrentDocumentLocation();
	}	
	
	public boolean isHTML5() {
		Document doc = editor.getMainContainer().getDocument();
		Element rootElement = doc.getDefaultRootElement();
		for ( int i = 0; i < rootElement.getElementCount(); i++ ) {
			Element e = rootElement.getElement( i );
			try {
				String line = doc.getText( e.getStartOffset(), e.getEndOffset() - 1 );
				line = line.trim();
				line = line.toUpperCase();
				if ( line.startsWith( "<!" ) ) {
					if ( line.startsWith( "<!DOCTYPE" ) ) {
						return line.endsWith( "HTML>" ) || 
								line.endsWith( "HTML >" );
					}
				}
				if ( line.startsWith( "<HTML" ) || line.startsWith( "<html" ) )
					return false;
			} catch( BadLocationException exc ) {
			}
		}
		return false;
	}

	public void copy() {
		editor.copy();
	}

	public void cut() {
		editor.cut();
	}
		
	public Object print() {
		return editor;
	};
	
	public Parser createNewParser( boolean lightweight ) {
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
	
	class InsertBRAction extends AbstractAction {
		public void actionPerformed( ActionEvent e ) {
			try {
				boolean xmlMode = "XHTML".equals( editor.getMainContainer().getDocumentInfo().getType() );
				editor.getMainContainer().getDocument().insertString( 
					editor.getMainContainer().getEditor().getCaretPosition(), "<br" + ( xmlMode ? " /" : "" ) + ">", null 
				);
			} catch( BadLocationException ble ) {	
			}
		}
		
	}
	
	class InsertPAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			try {
				int caretPosition = editor.getMainContainer().getEditor().getCaretPosition();
				editor.getMainContainer().getEditor().getDocument().insertString( caretPosition, "<p></p>", null );
				editor.getMainContainer().getEditor().setCaretPosition( caretPosition + 3 );
			} catch( BadLocationException ble ) {
			}
		}
	}

	class CustomInnerPanel extends InnerPanel implements IXMLPanel {

		public void dispose() {
			HTMLContainer.this.dispose();
		}

		public Parser createNewParser(boolean lightweightMode) {
			return null;
		}		
		
		@Override
		public String getCurrentDocumentLocation() {
			return editor.getCurrentDocumentLocation();
		}
		
		public IXMLPanel getPanelParent() {
			return HTMLContainer.this;
		}
		
		public void setAutoDisposeMode(boolean disposeMode) {
			HTMLContainer.this.setAutoDisposeMode(disposeMode);
		}

		public XMLContainer getMainContainer() {
			return HTMLContainer.this.getMainContainer();
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
			return HTMLContainer.this.getBookmarkContext();
		}

		public XMLContainer getSelectedContainer() {
			return HTMLContainer.this.getSelectedContainer();
		}
		
		public void copy() {
			HTMLContainer.this.copy();
		}
		
		public void cut() {
			HTMLContainer.this.cut();
		}
		
		public void paste() {
			HTMLContainer.this.paste();
		}
				
		@Override
		public Object print() {
			return HTMLContainer.this.print();
		}
		
		public void setDocumentInfo( XMLDocumentInfo info ) {
			HTMLContainer.this.setDocumentInfo( info );
		}		
		
		public Action getAction(String actionId) {
			return HTMLContainer.this.getAction( actionId );
		}

		public Iterator getProperties() {
			return HTMLContainer.this.getProperties();
		}

		public Object getProperty(String name, Object def) {
			return HTMLContainer.this.getProperty(name, def);
		}

		public Object getProperty(String name) {
			return HTMLContainer.this.getProperty(name);
		}

		public XMLContainer getSubContainer(String type) {
			return null;
		}

		public void prepareToSave() {
			HTMLContainer.this.prepareToSave();
		}	

		public void postLoad() {
			HTMLContainer.this.postLoad();
		}

		public boolean reload() {
			return HTMLContainer.this.reload();
		}		

		public JComponent getView() {
			return this;
		}

		public void setProperty(String name, Object content) {
			HTMLContainer.this.setProperty( name, content );
		}
	}
	
	protected InnerPanel createInnerView() {
		return new CustomInnerPanel();
	}
	
}

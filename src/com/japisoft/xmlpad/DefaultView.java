package com.japisoft.xmlpad;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.print.Printable;
import java.util.Iterator;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

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
class DefaultView extends JPanel implements IView, IXMLPanel {

	private XMLContainer container;

	private boolean init = false;

	public DefaultView( XMLContainer container ) {
		this.container = container;
	}

	public IXMLPanel getPanelParent() {
		return null;
	}
	
	public Parser createNewParser() {
		return null;
	}
	
	public XMLContainer getMainContainer() {
		return container;
	}
	
	public XMLContainer getSelectedContainer() {
		return null;
	}		

	public BookmarkContext getBookmarkContext() {
		return null;
	}	

	public void setDocumentInfo(XMLDocumentInfo info) {
		container.setDocumentInfo( info );
	}	

	public Object print() {
		return container;
	}
	
	/** @return another container for this document type */
	public XMLContainer getSubContainer( String type ) {
		return container.getSubContainer( type );
	}

	public XMLContainer getSubContainerAt(int index) {
		return container.getSubContainerAt( index );
	}
	
	public void selectSubContainer(IXMLPanel panel) {
		container.selectSubContainer( panel );
	}	

	public void copy() {
		container.copy();
	}
	
	public void cut() {
		container.cut();
	}
	
	public void paste() {
		container.paste();
	}
	
	public int getSubContainerCount() {
		return 0;
	}
	
	/** @return the global ui view */
	public JComponent getView() {
		return container.getView();
	}

	/** reset a component property */
	public void setProperty(String name, Object content) {
		container.setProperty( name, content );
	}
		
	/** @return a property value */
	public Object getProperty(String name) {
		return container.getProperty( name );
	}

	/** @return a property value of the default one */
	public Object getProperty(String name, Object def ) {
		return container.getProperty( name, def );
	}

	/** @return a list of properties */
	public Iterator getProperties() {
		return container.getProperties();
	}
	
	
	public void addNotify() {
		super.addNotify();
		container.initOnce( this );
		container.addNotifyDone = true;
		container.setUIReady( true );
	}

	public void removeNotify() {
		super.removeNotify();	
		if ( container != null ) {
			if ( container.disposeMode )
				container.dispose();
			container.setUIReady( false );
		}
	}

	private boolean focusView = false;

	public void setFocusView( boolean focusView ) {
		this.focusView = focusView;
		repaint();
	}

	public void paintComponent(Graphics gc) {
		super.paintComponent(gc);
		if (focusView) {
			gc.setColor(Color.blue);
			gc.drawRect(1, 1, getWidth() - 2, getHeight() - 2);
		}
	}

	public void requestFocus() {
		if (container.getEditor() != null) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					container.getEditor().requestFocusInWindow();
				}
			});
		} else
			super.requestFocus();
	}

	public Object[] getDocumentStateListeners() {
		Object[] listeners = listenerList
				.getListeners(DocumentStateListener.class);
		return listeners;
	}

	public void addDocumentStateListener(DocumentStateListener listener) {
		listenerList.add(DocumentStateListener.class, listener);
	}

	/** Remove a listener */
	public void removeDocumentStateListener(DocumentStateListener listener) {
		listenerList.remove(DocumentStateListener.class, listener);
	}

	public void dispose() {
		if ( container != null )
			this.container.dispose();
		this.container = null;
	}

	public XMLContainer getContainer() {
		return container;
	}

	/** @return the final view */
	public JComponent getFinalView() {
		return this;
	}

	public void setAutoDisposeMode(boolean disposeMode) {
		container.setAutoDisposeMode( disposeMode );
	}	
	
	public void prepareToSave() {
	}	
 
	public void postLoad() {
	}
	
	public boolean reload() {
		return container.reload();
	}	
	
	public Action getAction(String actionId) {
		return container.getAction( actionId );
	}	
}

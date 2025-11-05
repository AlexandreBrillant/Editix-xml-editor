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

package com.japisoft.framework.dialog.basic;

import java.awt.Dimension;
import java.awt.Frame;
import java.io.IOException;
import java.net.URL;

import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import com.japisoft.framework.dialog.BasicOKDialogComponent;

/**
 * Simple HTML dialog using an EditorPane
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * */
public class HTMLDialog extends BasicOKDialogComponent 
		implements HyperlinkListener {

	private JEditorPane pane = null;

	/** Will load this HTML page from the classpath only */
	public HTMLDialog( Frame owner, String title, String pagePath ) { 
		this( owner, title, pagePath, true );
	}

	/** User can decide to use an URL for the path or searching from the classpath */
	public HTMLDialog( Frame owner, String title, String pagePath, boolean classPathAccess ) {
		super( owner, title, title, null, null );
		try {
			if ( classPathAccess )
				pane = new JEditorPane(
						ClassLoader.getSystemResource( pagePath ) );
			else
				pane = new JEditorPane(
						new URL( pagePath ) );
			pane.setEditable( false );
			JScrollPane sp = new JScrollPane( pane );
			sp.setPreferredSize( new Dimension( 670, 500 ) );
			setUI( sp  );
		} catch( IOException exc ) {
			exc.printStackTrace();
		}	
	}
	
	public void addNotify() {
		super.addNotify();
		pane.addHyperlinkListener( this );
	}
	
	public void removeNotify() {
		super.removeNotify();
		pane.removeHyperlinkListener( this );
	}

	public void hyperlinkUpdate( HyperlinkEvent e ) {
	    if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
	    	pane.scrollToReference( e.getDescription().substring(1) );
	    }
	}

}


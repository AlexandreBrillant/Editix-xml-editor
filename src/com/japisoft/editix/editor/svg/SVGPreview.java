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

package com.japisoft.editix.editor.svg;

import java.awt.BorderLayout;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.StringReader;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

import javax.swing.JPanel;
import javax.swing.JToolBar;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

// import org.apache.batik.swing.JSVGCanvas;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.japisoft.editix.main.EditixApplicationModel;
import com.japisoft.xmlpad.IXMLPanel;

public class SVGPreview extends JPanel {

	private boolean autoRefresh = false;
	private IXMLPanel editor = null;
	// private JSVGCanvas svgView = null;
	
	SVGPreview( IXMLPanel editor ) {
		this.editor = editor;
		setLayout( new BorderLayout() );

		editor.getMainContainer().getEditor().getActionMap().put( "refresh", new RefreshAction() );
		editor.getMainContainer().getEditor().getInputMap().put( KeyStroke.getKeyStroke( KeyEvent.VK_F5, 0), "refresh" );

		JToolBar tb = new JToolBar();
		tb.add( editor.getMainContainer().getEditor().getActionMap().get( "refresh" ) );

		add( tb, BorderLayout.SOUTH );
	}

	public void dispose() {
		editor = null;
	}

	void refresh() {
		/*
		try {
			if ( svgView == null ) {
				svgView = new JSVGCanvas();
				add( svgView, BorderLayout.CENTER );
				invalidate();
				validate();
				repaint();
			}

			String xml = editor.getMainContainer().getText();
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware( true );
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse( new InputSource( new StringReader( xml ) ) );
			svgView.setDocument( doc );
		} catch( Throwable exc ) {
			EditixApplicationModel.fireApplicationValue( "error", "Can't update the SVG view :" + exc.getMessage() );
		}
		*/
	}
	
	/////////////////////////////////////////////////////////////////////
	
	class RefreshAction extends AbstractAction {
	
		public RefreshAction() {
			putValue( 
				Action.SHORT_DESCRIPTION, 
				"Refresh the CSS Preview" 
			);
			putValue( 
				Action.SMALL_ICON, 
				new ImageIcon( 
					getClass().getResource( "refresh.png" )
				) 
			);
		}

		public void actionPerformed(ActionEvent e) {
			refresh();
		}
		
	}
	
}

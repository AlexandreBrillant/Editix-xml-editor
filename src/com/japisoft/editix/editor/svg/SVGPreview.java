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

import org.apache.batik.swing.JSVGCanvas;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.japisoft.xmlpad.IXMLPanel;

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
public class SVGPreview extends JPanel {

	private boolean autoRefresh = false;
	private IXMLPanel editor = null;
	private JSVGCanvas svgView = null;
	
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
		if ( svgView == null ) {
			svgView = new JSVGCanvas();
			add( svgView, BorderLayout.CENTER );
			invalidate();
			validate();
			repaint();
		}

		try {
			String xml = editor.getMainContainer().getText();
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware( true );
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse( new InputSource( new StringReader( xml ) ) );
			svgView.setDocument( doc );
		} catch( Exception exc ) {
			exc.printStackTrace();
		}
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

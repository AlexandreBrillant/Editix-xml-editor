package com.japisoft.editix.action.view;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;

import com.japisoft.editix.ui.EditixDialog;
import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.framework.ui.toolkit.BrowserCaller;
import com.japisoft.xmlpad.XMLContainer;

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
public class SystemView extends AbstractAction {

	public void actionPerformed(ActionEvent e) {		
		XMLContainer container = EditixFrame.THIS.getSelectedContainer();
		if ( container == null )
			return;

		if ( container.getCurrentDocumentLocation() != null ) {
			BrowserCaller.displayURL(
					container.getCurrentDocumentLocation() );
		} else {
			String _ = container.getText();
			if ( _.startsWith( "<?") ) {
				int i = _.indexOf( "\n" );
				if ( i > -1 )
					_ = _.substring( i );
			}
			HTMLDialog dialog = new HTMLDialog( _ );
	
			Point p = ( Point )container.getProperty( "view.html.location" );
			Dimension d =( Dimension )container.getProperty( "view.html.dimension" );
	
			if ( p != null ) 
				dialog.setLocation( p );
			if ( d != null )
				dialog.setSize( d );
	
			if ( p != null )
				dialog.setVisible( true );
			else
					dialog.setVisible( true );
	
			if ( dialog.isOk() ) {
				container.setProperty( "view.html.location", dialog.getLocation() );
				container.setProperty( "view.html.dimension", dialog.getSize() );
			}
		}
	}

	class HTMLDialog extends EditixDialog {
		HTMLDialog( String html ) {
			super( "XHTML", "HTML View", "This is a mini browser.\nSave your document for displaying it inside a system viewer" );
			JEditorPane ed = new JEditorPane( "text/html", html );	
			ed.setEditable( false );	
			getContentPane().add( new JScrollPane( ed ) );
		}
		protected Dimension getDefaultSize() {
			return new Dimension( 400, 400 );
		}
	}

}

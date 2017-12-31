package com.japisoft.editix.action.view;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;

import org.apache.batik.swing.JSVGCanvas;
import org.apache.batik.swing.svg.JSVGComponent;

import com.japisoft.framework.application.descriptor.ActionModel;
import com.japisoft.editix.ui.EditixDialog;
import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.editix.ui.EditixFactory;
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
public class SVGView extends AbstractAction {

	public void actionPerformed(ActionEvent e) {
		XMLContainer container = EditixFrame.THIS.getSelectedContainer();
		if ( container == null )
			return;
		if ( EditixFactory.mustSaveDialog( container ) ) {
			return;
		}
		ActionModel.activeActionById( ActionModel.SAVE, e );
		if ( !ActionModel.LAST_ACTION_STATE )
			return;
		SVGDialog dialog = new SVGDialog(
			container.getDocumentInfo().getCurrentDocumentLocation() );
		dialog.setVisible( true );
		dialog.dispose();
	}

	class SVGDialog extends EditixDialog {
		SVGDialog( String svg ) {
			super( "SVG", "SVG View", "This is a SVG preview. Please wait a little delay for final rendering" );
			//JSVGComponent view = new JSVGComponent();
			JSVGCanvas view = new JSVGCanvas();
			getContentPane().add( view );
			view.loadSVGDocument( new File( svg ).toURI().toString() );
		}
		protected Dimension getDefaultSize() {
			return new Dimension( 400, 400 );
		}
		
	}

}

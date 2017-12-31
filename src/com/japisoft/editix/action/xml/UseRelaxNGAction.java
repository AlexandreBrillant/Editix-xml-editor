package com.japisoft.editix.action.xml;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.japisoft.editix.main.EditixApplicationModel;
import com.japisoft.editix.project.ProjectManager;
import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.editix.ui.pathbuilder.RNGPathBuilder;
import com.japisoft.framework.preferences.Preferences;
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
public class UseRelaxNGAction extends AbstractAction {

	public void actionPerformed(ActionEvent e) {
		XMLContainer container = EditixFrame.THIS.getSelectedContainer();
		if ( container == null )
			return;
		UseDefaultDialog udd = new UseDefaultDialog( 
				"RelaxNG", 
				"Use an external RelaxNG document. Please use a project for maintaining the binding", "RelaxNG", "rng", new RNGPathBuilder() );
		udd.disableRoot();
		udd.disableRelativePath();
		String path = null;
		
		if ( container.getSchemaAccessibility() != null && 
				container.getSchemaAccessibility().getRelaxNGValidationLocation() != null ) {
			path = container.getSchemaAccessibility().getRelaxNGValidationLocation().location;
		}

		if ( path == null ) {
			path = Preferences.getPreference( "xmlconfig", "defaultRelaxNG", ( String )null );			
		}

		udd.setFileLocation( path );		
		udd.setVisible( true );

		if ( udd.isOk() ) {
			if ( udd.getFileLocation() != null ) {
				container.setProperty( ProjectManager.RELAXNG_PROPERTY, udd.getFileLocation() );
				container.getSchemaAccessibility().setRelaxNGValidationLocation(
					udd.getFileLocation() );
				Preferences.setPreference( "xmlconfig", "defaultRelaxNG", udd.getFileLocation() );
				EditixFactory.buildAndShowInformationDialog( "<html><body>Your schema has been assigned<br><em>Store your XML document in a project for saving the binding</em></body><html>" );
			}
		}
	}

}

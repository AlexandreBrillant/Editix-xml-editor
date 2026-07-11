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
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
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

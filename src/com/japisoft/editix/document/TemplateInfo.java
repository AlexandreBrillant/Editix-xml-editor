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

package com.japisoft.editix.document;

import java.io.File;

import javax.swing.Icon;

import com.japisoft.editix.wizard.document.DocumentWizard;
import com.japisoft.framework.xml.parser.node.FPNode;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * */
public class TemplateInfo {

	public String label;
	public String location;
	public String type;
	public boolean system;
	public Icon icon;
	public String encoding;
	public String content;
	public String defDTDLocation;
	public String defDTDRoot;
	public String help;
	public String wizard;
	
	public DocumentWizard wizardInstance = null;

	public boolean hasWizard() { return wizard != null; }
	
	public String startWizard() {
		if ( wizard == null )
			return null;
		if ( wizard != null ) {
			if ( wizardInstance == null ) {
				try {
					wizardInstance = ( DocumentWizard )( Class.forName( wizard ) ).newInstance();
				} catch( Exception exc ) {
					exc.printStackTrace();
					wizard = null;
					return null;
				}
			}
			return wizardInstance.start();
		}
		return null;
	}

	public File getWizardSource() { return wizardInstance.getSource(); }
	
	public FPNode toXML() {
		FPNode node2 = new FPNode( FPNode.TAG_NODE, "template" );
		String labelTmp = label.replaceAll( "<", "&lt;" ).replaceAll( ">", "&gt;" );
		node2.setAttribute( "label", labelTmp );
		node2.setAttribute( "type", type );
		if ( help != null )
			node2.setAttribute( "help", help );
		if ( location != null ) {
			node2.setAttribute( "location", location );
		}
		node2.setAttribute( "system", ( system ) ? "true" : "false" );
		if ( wizard != null ) {
			node2.setAttribute( "wizard", wizard );
		}
		return node2;
	}
	
}


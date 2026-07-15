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

package com.japisoft.editix.document;

import java.util.ArrayList;
import java.util.List;
import javax.swing.Icon;

import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.xml.parser.node.FPNode;

public class GroupTemplate {

	private String name;
	private String icon;
	private String docIcon;
	
	public GroupTemplate( String name, String icon, String docIcon ) {
		this.name = name;
		this.icon = icon;
		this.docIcon = docIcon;
	}
	
	private List<TemplateInfo> templates = null;
	
	public void addTemplate( TemplateInfo ti ) {
		if ( templates == null )
			templates = new ArrayList<TemplateInfo>();
		templates.add( ti );
	}

	public int getTemplateInfoCount() {
		if ( templates == null )
			return 0;
		return templates.size();
	}
	
	public TemplateInfo getTemplateInfo( int index ) {
		return templates.get( index );
	}
	
	public String getName() {
		return name;
	}

	public Icon getIcon() {
		try {
			return com.japisoft.framework.toolkit.Toolkit.getImageIcon( icon );
		} catch( Exception exc ) {
			ApplicationModel.debug( "Can't read " + icon );
			return null;
		}
	}

	public Icon getDocIcon() {
		try {
			return com.japisoft.framework.toolkit.Toolkit.getImageIcon( docIcon );
		} catch( Exception exc ) {
			ApplicationModel.debug( "Can't read " + docIcon );
			return null;
		}
	}
		
	public FPNode toXML() {
		FPNode n = new FPNode( FPNode.TAG_NODE, "group" );
		n.setAttribute( 
			"label", 
			name.replace( "<", "&lt;" ).replace( ">", "&gt;" ) 
		);
		
		if ( templates != null ) {
			for ( TemplateInfo ti : templates ) {
				n.appendChild( ti.toXML() );
			}
		}

		return n;
	}

}

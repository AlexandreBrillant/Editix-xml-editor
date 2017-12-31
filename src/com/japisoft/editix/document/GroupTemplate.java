package com.japisoft.editix.document;

import java.util.ArrayList;
import java.util.List;
import javax.swing.Icon;

import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.xml.parser.node.FPNode;

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
			return com.japisoft.framework.app.toolkit.Toolkit.getImageIcon( icon );
		} catch( Exception exc ) {
			ApplicationModel.debug( "Can't read " + icon );
			return null;
		}
	}

	public Icon getDocIcon() {
		try {
			return com.japisoft.framework.app.toolkit.Toolkit.getImageIcon( docIcon );
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

package com.japisoft.editix.editor.xsd.view.designer;

import org.w3c.dom.Element;

import com.japisoft.editix.editor.xsd.view.designer.container.XSDAttributeGroupComponentImpl;
import com.japisoft.editix.editor.xsd.view.designer.container.XSDComplexContentComponentImpl;
import com.japisoft.editix.editor.xsd.view.designer.container.XSDComplexTypeComponentImpl;
import com.japisoft.editix.editor.xsd.view.designer.container.XSDExtensionComponentImpl;
import com.japisoft.editix.editor.xsd.view.designer.container.XSDGroupComponentImpl;
import com.japisoft.editix.editor.xsd.view.designer.container.XSDRestrictionComponentImpl;
import com.japisoft.editix.editor.xsd.view.designer.container.XSDSimpleContentComponentImpl;

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
public class XSDComponentFactory {

	public static XSDComponent getComponent( 
			Element e, 
			XSDComponentListener designer ) {
		String name = e.getLocalName();
		XSDComponent c = null;
		if ( "element".equals( name ) ) {
			c = new XSDElementComponentImpl();
		} else
		if ( "attribute".equals( name ) ) {
			c = new XSDAttributeComponentImpl();
		} else
		if( "complexType".equals( name ) ) {
			c = new XSDComplexTypeComponentImpl();
		} else
		if ( "sequence".equals( name ) ) {
			c = new XSDSequenceComponentImpl();
		} else
		if ( "choice".equals( name ) ) {
			c = new XSDChoiceComponentImpl();
		} else
		if ( "all".equals( name ) ) {
			c = new XSDAllComponentImpl();
		} else
		if ( "simpleContent".equals( name ) ) {
			c = new XSDSimpleContentComponentImpl();
		} else
		if ( "complexContent".equals( name ) ) {
			c = new XSDComplexContentComponentImpl();
		} else
		if ( "restriction".equals( name ) ) {
			c = new XSDRestrictionComponentImpl();
		} else
		if ( "extension".equals( name ) ) {
			c = new XSDExtensionComponentImpl();
		} else
		if ( "group".equals( name ) ) {
			c = new XSDGroupComponentImpl();
		} else
		if ( "attributeGroup".equals( name ) ) {
			c = new XSDAttributeGroupComponentImpl();
		} else
		if ( "anyAttribute".equals( name ) ) {
			c = new XSDAnyAttributeComponentImpl();
		} else
		if ( "key".equals( name ) ) 
			c = new XSDKeyComponentImpl();
		else
		if ( "keyref".equals( name ) ) 
			c = new XSDKeyrefComponentImpl();
		else
		if ( "unique".equals( name ) ) {
			c = new XSDUniqueComponentImpl();
		} else
		if ( "any".equals( name ) ) {
			c = new XSDAnyComponentImpl();
		}

		if ( c != null ) {
			c.setElement( e );
			e.setUserData( "ui", c, null );
			c.setComponentListener( designer );
		}
		return c;
	}

}

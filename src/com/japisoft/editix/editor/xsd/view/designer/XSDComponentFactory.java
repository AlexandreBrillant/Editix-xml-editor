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

package com.japisoft.editix.editor.xsd.view.designer;

import org.w3c.dom.Element;

import com.japisoft.editix.editor.xsd.view.designer.container.XSDAttributeGroupComponentImpl;
import com.japisoft.editix.editor.xsd.view.designer.container.XSDComplexContentComponentImpl;
import com.japisoft.editix.editor.xsd.view.designer.container.XSDComplexTypeComponentImpl;
import com.japisoft.editix.editor.xsd.view.designer.container.XSDExtensionComponentImpl;
import com.japisoft.editix.editor.xsd.view.designer.container.XSDGroupComponentImpl;
import com.japisoft.editix.editor.xsd.view.designer.container.XSDRestrictionComponentImpl;
import com.japisoft.editix.editor.xsd.view.designer.container.XSDSimpleContentComponentImpl;

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

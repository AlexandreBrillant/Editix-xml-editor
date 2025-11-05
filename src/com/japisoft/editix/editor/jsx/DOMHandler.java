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

package com.japisoft.editix.editor.jsx;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.japisoft.editix.editor.jsx.domapi.Document;
import com.japisoft.editix.editor.jsx.domapi.Element;
import com.japisoft.editix.editor.jsx.domapi.Node;
import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.xmlpad.editor.XMLPadDocument;
import com.japisoft.xmlpad.helper.handler.AbstractHelperHandler;
import com.japisoft.xmlpad.helper.model.BasicDescriptor;

public class DOMHandler extends AbstractHelperHandler {

	private List<String> fields;
	private List<String> methods;
	
	public DOMHandler() {
		addClass( Node.class );
		addClass( Element.class );
		addClass( Document.class );
		
		if ( fields != null )
			Collections.sort( fields );
		
		if ( methods != null )
			Collections.sort( methods );
	}
	
	@Override
	public boolean haveDescriptors(FPNode currentNode, XMLPadDocument document, boolean insertBefore, int offset,
			String activatorString) {
		return activatorString == null;
	}

	@Override
	protected void installDescriptors(FPNode currentNode, XMLPadDocument document, int offset, String activatorString) {
		if ( fields != null ) {
			for ( String f : fields ) {				
				( ( BasicDescriptor )addDescriptor( new BasicDescriptor( f, getRaw( activatorString, f ) ) ) ).setComment( "DOM Property" );
			}
		}
		if ( methods != null ) {
			for ( String f : methods ) {
				( ( BasicDescriptor )addDescriptor( new BasicDescriptor( f, getRaw( activatorString, f ) ) ) ).setComment( "DOM Method" );
			}			
		}
	}
	
	private String getRaw( String activatorString, String label ) {
		String prefix = "";
		if ( activatorString != null )
			prefix = activatorString;
		
		int i = label.lastIndexOf( ":" );
		if ( i > -1 )
			return prefix + label.substring( 0, i );
		return prefix + label;
	}
	
	private void addClass( Class cl ) {
		Field[] ff = cl.getDeclaredFields();
		for ( Field f : ff ) {
			if ( Modifier.isPublic( f.getModifiers() ) ) {
				 if ( fields == null )
					 fields = new ArrayList<String>();
				 fields.add( f.getName() + " : " + cl.getSimpleName() );
			}
		}
		Method[] mm = cl.getDeclaredMethods();
		for ( Method m : mm ) {
			if ( Modifier.isPublic( m.getModifiers() ) ) {
				if ( methods == null )
					methods = new ArrayList<String>();
				methods.add( m.getName() + " : " + cl.getSimpleName() );
			}
		}
	}
	
}


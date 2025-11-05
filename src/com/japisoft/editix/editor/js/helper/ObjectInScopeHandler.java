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

package com.japisoft.editix.editor.js.helper;

import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;

import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.xmlpad.editor.XMLPadDocument;
import com.japisoft.xmlpad.helper.handler.AbstractHelperHandler;
import com.japisoft.xmlpad.helper.model.BasicDescriptor;

public class ObjectInScopeHandler extends AbstractHelperHandler {
	
	private boolean caseSensitive = true;
	private PatternsGroup pg = null;
	
	public ObjectInScopeHandler() {
		pg = new PatternsGroup( caseSensitive );
		pg.addPattern( "function\\s+(\\w+)\\(", "function" );
		pg.addPattern( "(\\w+)\\s*=\\s*", "variable" );
	}
	
	@Override
	protected void installDescriptors(
			FPNode currentNode,
			XMLPadDocument document, 
			int offset, 
			String activatorString ) {
		String[] groups = {
				"function",
				"variable"
		};
		for ( String group : groups ) {
			Icon ii = new ImageIcon( getClass().getResource( group + ".png" ) );
			List<String> r = pg.getResult( group );
			if ( r != null ) {
				for ( String f : r ) {
					if ( "function".equals( group ) ) {
						f = f + "(�)";
					}
					( ( BasicDescriptor )addDescriptor( new BasicDescriptor( f, ii ) ) ).setComment( group );
				}
			}
		}
		pg.reset();
	}
	
	public boolean haveDescriptors(
			FPNode currentNode,
			XMLPadDocument document, 
			boolean insertBefore, 
			int offset, 
			String activatorString ) {
		if ( activatorString == null ) {
			int line = document.getDefaultRootElement().getElementIndex( offset );
			for ( int i = line - 1; i >= 0; i-- ) {
				try {
					Element le = document.getDefaultRootElement().getElement(i);
					String content = document.getText( le.getStartOffset(), le.getEndOffset() - le.getStartOffset() );
					pg.match( content );
				} catch( BadLocationException exc ) {}
			}
			return pg.hasResult();
		} else
			return false;
	}

}


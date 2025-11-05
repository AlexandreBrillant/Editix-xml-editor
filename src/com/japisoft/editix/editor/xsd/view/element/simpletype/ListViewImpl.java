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

package com.japisoft.editix.editor.xsd.view.element.simpletype;

import javax.swing.JComponent;
import org.w3c.dom.Element;

import com.japisoft.editix.editor.xsd.Changeable;
import com.japisoft.editix.editor.xsd.toolkit.SchemaHelper;
import com.japisoft.editix.editor.xsd.toolkit.XSDAttribute;
import com.japisoft.editix.editor.xsd.view.View;
import com.japisoft.editix.editor.xsd.view.element.PropertiesViewImpl;

public class ListViewImpl implements View, Changeable {

	private PropertiesViewImpl av = new PropertiesViewImpl() {

		public void init(Element e) {
			initE = e;
			if ( e != null ) {
				contents = new XSDAttribute[] {
						new XSDAttribute( "type (list items)", XSDAttribute.TYPE_REF, null, true )	
				};
				reloadModel();
			} else
				super.init( e );
		}
		
		public void stopEditing() {
			Object value = getEditorValue();
			if ( value != null ) {
				String listType = ( String )value;
				Element parent = SchemaHelper.getFirstChild( initE, "list" );
				if ( parent == null ) {
					parent = SchemaHelper.createElement( initE, "list" );
					initE.appendChild( parent );
				}
				parent.setAttribute( "itemType", listType );
			}
		};
	};
	
	public void init(Element schemaNode) {
		av.init( schemaNode );
	}

	public JComponent getView() {
		return av.getView();
	}

	public void dispose() {
		av.dispose();
	}

	public void stopEditing() {
		av.stopEditing();
	}
	
	@Override
	public boolean isChanged() {
		return av.isChanged();
	}
	
	@Override
	public void copy() {
	}
	@Override
	public void cut() {
	}
	@Override
	public void paste() {
	}

}


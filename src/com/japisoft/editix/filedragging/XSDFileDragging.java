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

package com.japisoft.editix.filedragging;

import java.io.File;
import java.util.List;

import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.xmlpad.editor.XMLEditor;

public class XSDFileDragging extends DefaultFileDragging {

	@Override
	public void drag(XMLEditor editor, List<File> files) {
		FPNode root = editor.getXMLContainer().getRootNode();
		boolean importMode = false;
		if ( root != null ) {
			if ( root.hasAttribute( "targetNamespace" ) ) {
				importMode = true;
			}
			
			String prefix = root.getNameSpacePrefix();
			if ( prefix != null && !"".equals( prefix ) )
				prefix += ":";
			else
				prefix = "";
			
			for ( File f : files ) {
				
				String path = null;
				if ( sameParent(f, editor) ) {
					path = f.getName();
				} else
					path = f.toString();

				if ( "xsd".equals( getFileExt( f ) ) ) {
					if ( importMode ) {
						editor.insertText("<" + prefix + "import schemaLocation=\"" + path + "\"/>" );
					} else {
						editor.insertText("<" + prefix + "include schemaLocation=\"" + path + "\"/>" );
					}
				}
			}
		} else
			super.drag( editor, files );
	}
	
}

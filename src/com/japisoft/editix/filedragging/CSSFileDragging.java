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

package com.japisoft.editix.filedragging;

import java.io.File;
import java.util.List;

import com.japisoft.xmlpad.XMLDocumentInfo;
import com.japisoft.xmlpad.editor.XMLEditor;

public class CSSFileDragging extends DefaultFileDragging {

	public void drag(XMLEditor editor, List<File> files) {
		XMLDocumentInfo info = editor.getXMLContainer().getDocumentInfo();
		if ( "CSS".equals( info.getType() ) ) {
			for ( File f : files ) {
				if ( "css".equals( getFileExt( f ) ) ) {
					editor.insertText( "@import url(\"" );
					if ( sameParent(f, editor ) ) {
						editor.insertText( f.getName() );
					} else {
						editor.insertText( f.toURI().toString() );
					}
					editor.insertText( "\")" );
				}
			}
		} else
		if ( "HTML".equals( info.getType() ) || "XHTML".equals( info.getType() ) ) {
			for ( File f : files ) {
				if ( "css".equals( getFileExt( f ) ) ) {
					editor.insertText( "<link rel=\"stylesheet\" type=\"text/css\" href=\"" );
					if ( sameParent( f, editor ) ) {
						editor.insertText( f.getName() );
					} else
						editor.insertText( f.toURI().toString() );
					if ( "XHTML".equals( info.getType() ) ) {
						editor.insertText( "/" );
					}
					editor.insertText( "\">" );
				}
			}
		} else
		super.drag( editor, files );
	}
	
}


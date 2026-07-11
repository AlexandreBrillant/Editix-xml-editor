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
import java.util.ArrayList;
import java.util.List;

import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.xmlpad.FileDragging;
import com.japisoft.xmlpad.editor.XMLEditor;
import com.japisoft.xmlpad.editor.XMLPadDocument;

public class DefaultFileDragging implements FileDragging {

	protected String getFileExt( File f ) {
		String path = f.getPath();
		int i = path.lastIndexOf( "." );
		if ( i > -1 ) {
			return path.substring( i + 1 ).toLowerCase();
		}
		return null;
	}
	
	private void dragXSD( XMLEditor editor, File f, boolean sameDocumentPath ) {
		// Bind the schema to the document
		FPNode rootNode = editor.getXMLContainer().getRootNode();
		XMLPadDocument doc = editor.getXMLDocument();
		if ( rootNode != null ) {
			rootNode.removeNameSpaceDeclaration( "xsi" );
			rootNode.addNameSpaceDeclaration( "xsi", "http://www.w3.org/2001/XMLSchema-instance" );  

			rootNode.setAttribute( "xsi:noNamespaceSchemaLocation", null );
			rootNode.setAttribute( "xsi:schemaLocation", null );			

			String xsdPath = f.toString();
			if ( sameDocumentPath ) {
				xsdPath = f.getName();
			}

			if ( rootNode.getNameSpaceURI() == null )
				rootNode.setAttribute( "xsi:noNamespaceSchemaLocation", xsdPath );
			else 
				rootNode.setAttribute( "xsi:schemaLocation", rootNode.getNameSpaceURI() + " " + xsdPath );
			
			doc.updateNodeOpeningClosing( rootNode );
			doc.parseSchema();					

		} else {
			EditixFactory.buildAndShowWarningDialog( "No document root for assigning your XSD Schema ?" );
		}		
	}

	protected boolean sameParent( File f, XMLEditor editor ) {
		String docPath = editor.getXMLContainer().getCurrentDocumentLocation();
		if ( docPath == null ) {
			return false;
		} else
			return f.getParentFile().equals( new File( docPath ).getParentFile() );
	}
	
	@Override
	public void drag( XMLEditor editor, List<File> files ) {
		boolean first = true;
		for ( File f : files ) {
			if ( !first ) {
				editor.insertText( " " );
			}

			if ( "xsd".equals( getFileExt( f ) ) ) {
				dragXSD( editor, f, sameParent( f, editor ) );
			} else {
				editor.insertText( f.toString() );
				first = false;
			}
			
		}
	}
	
	protected void drag( XMLEditor editor, File f ) {
		List<File> l = new ArrayList<File>();
		l.add( f );
		drag( editor, l );
	}
	
}

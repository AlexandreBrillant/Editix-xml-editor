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

package com.japisoft.editix.wizard.document;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import com.japisoft.editix.action.dtdschema.generator.MetaNode;
import com.japisoft.editix.action.dtdschema.generator.SchemaGenerator;
import com.japisoft.editix.action.dtdschema.generator.transformer.SchemaTransformer;
import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.framework.toolkit.FileToolkit;
import com.japisoft.framework.xml.parser.FPParser;
import com.japisoft.framework.xml.parser.document.Document;
import com.japisoft.framework.xml.parser.node.FPNode;

public class XML2XSDDocument implements DocumentWizard {

	private File source;
	
	@Override
	public String start() {
		JFileChooser fc = EditixFactory.buildFileChooser( new FileFilter() {			
			@Override
			public String getDescription() {
				return "XML document (*.xml)";
			}
			@Override
			public boolean accept( File f ) {
				if ( f.isFile() ) {
					String tmp = f.getName().toLowerCase();
					return tmp.endsWith( ".xml" );
				} else
					return true;
			}
		});
		if ( fc.showOpenDialog( EditixFrame.THIS ) == 
				JFileChooser.APPROVE_OPTION ) {
			try {
				source = fc.getSelectedFile();
				String content = FileToolkit.getContentFromFileName(source, null );
				FPParser parser = new FPParser();
				Document doc = parser.parseContent( content );
				FPNode root = ( FPNode )doc.getRoot();
				MetaNode metaRoot = SchemaGenerator.getMetaModel( root );
				SchemaTransformer st = new SchemaTransformer();
				st.setSequenceMode( true );
				return "<?xml version='1.0'?>\n\n" + SchemaGenerator.generate( metaRoot, st );
			} catch( Throwable exc ) {
				EditixFactory.buildAndShowErrorDialog( "Can't use this file : " + exc.getMessage() );
			}
			
		}
		return null;
	}
	
	public File getSource() { return source; }
	
}


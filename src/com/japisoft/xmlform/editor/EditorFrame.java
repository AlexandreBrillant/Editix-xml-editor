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

package com.japisoft.xmlform.editor;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.List;

import javax.swing.JFrame;

import org.w3c.dom.Node;

import com.japisoft.framework.application.descriptor.ActionModel;
import com.japisoft.framework.application.descriptor.InterfaceBuilder;
import com.japisoft.xmlform.component.AbstractXMLFormComponent;

public class EditorFrame extends JFrame {

	private InterfaceBuilder builder = null;
	
	public EditorFrame( InterfaceBuilder builder ) {
		this.builder = builder;
		setTitle( "XML Form Editor" );
		setDefaultCloseOperation( 
				DO_NOTHING_ON_CLOSE ); 
		addWindowListener(
				new WindowAdapter() {
					@Override
					public void windowClosing(WindowEvent e) {
						ActionModel.activeActionById( "quit", null );
					}
				} 
		);
	}

	private EditorComponent editor = null;
	
	public void initUI() {
		setLayout( new BorderLayout() );
		
		if ( builder.getMenuBar() != null ) {
			setJMenuBar( builder.getMenuBar() );
		}
		
		add( 
			builder.getToolBarById( "DEFAULT" ), 
			BorderLayout.NORTH 
		);
		
		if ( builder.getToolBarById( "main" ) != null ) {
			add( 
				builder.getToolBarById( "main" ), 
				BorderLayout.NORTH );
		}
		
		editor = new EditorComponent();
		add( editor );
	}

	public void newDocument( File formPath ) throws Exception {
		editor.newDocument( formPath );
	}
	
	public void newDocument( String formURI ) throws Exception {
		editor.newDocument( formURI );
	}
	
	public void loadDocument( String uri ) throws Exception {
		editor.loadDocument( uri );
	}
	
	public void loadDocument( File f ) throws Exception {
		editor.loadDocument( f );
	}

	public void saveDocument( String uri ) throws Exception {
		editor.saveDocument( uri );
	}
	
	public boolean validateDocument() {
		return editor.validateDocument();
	}
	
	public void setFocusTo( AbstractXMLFormComponent component ) {
		editor.setFocusTo( component );
	}
	
	public List<Node> checkEmptyFields() {
		return editor.checkEmptyFields();
	}
}


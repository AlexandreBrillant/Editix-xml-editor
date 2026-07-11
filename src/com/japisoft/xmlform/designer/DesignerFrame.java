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

package com.japisoft.xmlform.designer;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import com.japisoft.framework.application.descriptor.ActionModel;
import com.japisoft.framework.application.descriptor.InterfaceBuilder;

public class DesignerFrame extends JFrame {

	private InterfaceBuilder builder = null;
	private DesignerComponent panel = null;
	
	public DesignerFrame( InterfaceBuilder builder ) {
		this.builder = builder;
		setTitle( "XML Form Designer" );
		setDefaultCloseOperation( 
				DO_NOTHING_ON_CLOSE ); 
		addWindowListener(
				new WindowAdapter() {
					@Override
					public void windowClosing(WindowEvent e) {
						ActionModel.activeActionById( 
								"quit", 
								null );
					}
				} );
	}
	
	
	
	public void initUI() {
		setLayout( new BorderLayout() );
		if ( builder.getMenuBar() != null ) {
			setJMenuBar( builder.getMenuBar() );
		}

		if ( builder.getToolBarById( "DEFAULT" ) != null ) {
			add( builder.getToolBarById( "DEFAULT" ), 
					BorderLayout.NORTH );
		}

		add( panel = new DesignerComponent() );
		
	}

	public void newForm() {
		panel.newForm();
	}
	
	public void save( Document doc ) {
		panel.save( doc );
	}

	public void load( File f ) throws Exception {
		panel.load( f );
	}

	public void load( String uri ) throws Exception {
		panel.load( uri );
	}
	
	public void load( String uri, InputStream input ) {
		panel.load( uri, input );
	}
	
}
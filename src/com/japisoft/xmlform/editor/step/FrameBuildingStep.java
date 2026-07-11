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

package com.japisoft.xmlform.editor.step;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;

import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.ApplicationStepAdapter;
import com.japisoft.framework.preferences.Preferences;
import com.japisoft.xmlform.editor.EditorFrame;
import com.japisoft.xmlform.editor.EditorModel;

public class FrameBuildingStep extends ApplicationStepAdapter {

	@Override
	public void start(String[] args) {

		EditorFrame frame = null;
		frame = new EditorFrame( ApplicationModel.INTERFACE_BUILDER );
		EditorModel.MAIN_FRAME = frame;

		Rectangle r = Preferences.getPreference( 
				"dialog", 
				"frame",
				new Rectangle( 0, 0, 0, 0 ) );

		if ( !((r.x == r.y) && (r.width == r.height) && (r.x == 0)))
			frame.setBounds( r );
		else {
			frame.setLocation( 0, 0 );
			Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
			frame.setSize( dim.width, dim.height - 30 );
		}

		frame.initUI();
		frame.setVisible( true );

		if ( args.length > 0 ) {
			
			try {

				if ( args[ 0 ].toLowerCase().endsWith( ".xf" ) ) {
					
					frame.newDocument( args[ 0 ] );

					if ( args.length > 1 ) {
						EditorModel.CURRENT_DOCUMENT = args[ 1 ];					
					}
					
				} else {
					
					frame.loadDocument( args[ 0 ] );

					if ( args.length > 1 ) {
						EditorModel.CURRENT_DOCUMENT = args[ 1 ];
					}

				}

			} catch ( Exception e ) {
				
				e.printStackTrace();

			}

		}
		
	}
	
}

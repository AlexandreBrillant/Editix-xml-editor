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

package com.japisoft.editix.main.steps;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;

import com.japisoft.editix.main.EditixApplicationModel;
import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.editix.ui.EditixInitPanel;
import com.japisoft.framework.ApplicationStep;
import com.japisoft.framework.preferences.Preferences;

public class MainFrameApplicationStep implements ApplicationStep {

	public boolean isFinal() {
		return false;
	}

	public void start(String[] args) {
		EditixFrame frame = new EditixFrame( EditixApplicationModel.INTERFACE_BUILDER );

		//////////////

		Rectangle r = Preferences.getPreference("dialog", "editix",
				new Rectangle(0, 0, 0, 0));
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();				
		
		if ( !((r.x == r.y) && (r.width == r.height) && (r.x == 0)) ) {

			if ( Preferences.getPreference( 
					"interface", 
						"windowLocationChecked", true ) ) {					
				if ( r.x < 0 )
					r.x = 0;
				if ( r.y < 0 )
					r.y = 0;
				if ( r.x >= dim.width )
					r.x = 0;
				if ( r.y >= dim.height )
					r.y = 0;
				if ( r.width < 1 )
					r.width = dim.width;
				if ( r.height < 1 )
					r.height = dim.height;
			}

			frame.setBounds( r );
		}
		else {
			frame.setLocation(0, 0);

			frame.setSize(dim.width, dim.height - 30);
		}

		EditixApplicationModel.MAIN_FRAME = frame;

		if ( Preferences.getPreference( 
				"system", 
				"browserenabled", 
				false ) ) {
			String lastBrowse = Preferences.getPreference( 
					"system", 
					"browserlastfile", 
					( String )null );				
		}

		if ( Preferences.getPreference("interface", "initialDocument", true ) ) {
			if ( !frame.hasContainer() ) {
				if ( !EditixApplicationModel.isMacOSXPlatform() ) {
					EditixFrame.THIS.addContainer( new EditixInitPanel() );
					EditixFrame.THIS.invalidate();
					EditixFrame.THIS.validate();
					EditixFrame.THIS.repaint();					
					EditixFrame.THIS.selectDefaultContainer();
				}
			}
		}

		if (Preferences.getPreference("interface", "tipOfTheDay", false)) {
			com.japisoft.framework.application.descriptor.ActionModel.activeActionById(
					"tipOfTheDay", null);
		}

		frame.setVisible( true );
		// Splashscreen.restart();
		
	}

	public void stop() {
	}
	
	@Override
	public void quit() {
	}

}

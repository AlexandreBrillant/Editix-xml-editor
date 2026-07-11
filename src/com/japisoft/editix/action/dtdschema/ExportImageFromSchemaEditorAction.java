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

package com.japisoft.editix.action.dtdschema;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.japisoft.editix.editor.xsd.CustomActionListener;
import com.japisoft.editix.editor.xsd.VisualXSDEditor;
import com.japisoft.editix.editor.xsd.view2.DesignerViewImpl;
import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.framework.ui.toolkit.FileManager;

public class ExportImageFromSchemaEditorAction implements CustomActionListener {

	public void action(String name, VisualXSDEditor editor) {

		File file = 
			FileManager.getSelectedFile(
					false,
					new String[] { "jpg", "png" },
					new String[] { "Image (*.jpg)", "Image (*.png)" } 
			);

		if ( file != null ) {
			
			String imageType = "JPG";
			
			if ( file.toString().endsWith( "png" ) )
				imageType = "PNG";

				
				DesignerViewImpl dvi = 
					editor.getDesignerView();

				BufferedImage img = new BufferedImage(
						dvi.getWidth(),
						dvi.getHeight(),
						BufferedImage.TYPE_INT_RGB );

				Graphics2D gc = ( Graphics2D )img.getGraphics();
				
				gc.setRenderingHint(
	                    RenderingHints.KEY_ANTIALIASING, 
	                    RenderingHints.VALUE_ANTIALIAS_ON);				
				
				if ( UIManager.getColor( "editix.xsd.background" ) != null ) 
					gc.setColor( UIManager.getColor( "editix.xsd.background" ) );
				else
					gc.setColor(Color.WHITE);

				gc.fillRect(0, 0, img.getWidth(), img.getHeight());

				dvi.paint( img.getGraphics() );
				
				try {				
					ImageIO.write( 
						img, 
						imageType, 
						file );
				} catch ( IOException e ) {				
					EditixFactory.buildAndShowErrorDialog( 
							"Can't write : " + 
							e.getMessage() 
					);
				}
		}
		
	}	
	
}


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

package com.japisoft.framework.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.io.File;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * Resource manager
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * */
public class Resource {

	// Load an image for this location
	public static Icon getImage( String location ) {
		// Search it from the classpath
		URL url = ClassLoader.getSystemResource( location );
		if ( url != null )
			return new ImageIcon( url );
		else {
			File f = new File( location );
			if ( f.exists() )
				return new ImageIcon( location );
		}
		return getDefaultImage();
	}

	private static EmptyIcon DEFAULT = null; 

	public static Icon getDefaultImage() {
		if ( DEFAULT == null )
			DEFAULT = new EmptyIcon();
		return DEFAULT;
	}

	/** Empty icon for alignment on popup */
	public static class EmptyIcon extends Object implements Icon {
		private final int height;
		private final int width;

		public EmptyIcon() {
			height = 16;
			width = 16;
		}

		public EmptyIcon(Dimension size) {
			this.height = size.height;
			this.width = size.width;
		}

		public EmptyIcon(int height, int width) {
			this.height = height;
			this.width = width;
		}

		public int getIconHeight() {
			return height;
		}

		public int getIconWidth() {
			return width;
		}

		public void paintIcon(Component c, Graphics g, int x, int y) {
		}
	}

}


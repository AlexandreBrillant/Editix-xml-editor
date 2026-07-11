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

package com.japisoft.universalbrowser;

import java.awt.Color;
import java.io.File;

import javax.swing.Icon;

import org.apache.commons.vfs.FileObject;

/**
 * Here an interface for the each file look
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * */
public interface FileView {

	/** @return an icon for this file. The dimension should be 16x16 */
	public Icon getIcon( FileObject file );
	
	/** @return the foreground for this file */
	public Color getForeground( FileObject file );

	/** @return the background color for this file */
	public Color getBackground( FileObject file );
	
}

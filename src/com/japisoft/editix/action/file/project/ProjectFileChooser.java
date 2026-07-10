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

package com.japisoft.editix.action.file.project;

import java.awt.Component;
import java.awt.HeadlessException;
import java.io.File;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileView;

import com.japisoft.editix.ui.SelectableEncoding;
import com.japisoft.editix.ui.leftpanels.project2.DefaultProject;

/**
 * Custom File Chooser for XML documents
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 */
public class ProjectFileChooser extends JFileChooser implements SelectableEncoding {

	private JComboBox combo;

	public ProjectFileChooser() {
		init();
	
		addChoosableFileFilter( new FileFilter() {			
			@Override
			public String getDescription() {
				return "Editix's Project";
			}
			@Override
			public boolean accept( File f ) {
				return DefaultProject.isProjectPath( f );
			}
		} );
		
	}

	private void init() {
		setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
		setMultiSelectionEnabled(false);
		setFileView( new ProjectFileView() );		
	}

	public int showDialog( Component parent, String approveButtonText ) throws HeadlessException {
		return super.showDialog( parent, approveButtonText );
	}
	
	public String getSelectedEncoding() {
		return ( String )combo.getSelectedItem();
	}

	class ProjectFileView extends FileView {
		private Icon projectIcon = null;

		public ProjectFileView() {
			super();
			projectIcon = new ImageIcon( 
				getClass().getResource( "environment2.png" ) 
			);
		}

		@Override
		public Icon getIcon( File f ) {
			if ( f.isDirectory() ) {
				if ( DefaultProject.isProjectPath( f ) ) {
					return projectIcon;
				}
			}
			return super.getIcon( f );
		}
	}

}


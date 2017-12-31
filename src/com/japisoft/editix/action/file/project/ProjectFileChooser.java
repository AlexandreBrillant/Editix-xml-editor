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
import com.japisoft.editix.ui.panels.project2.DefaultProject;

/**
This program is available under two licenses : 

1. For non commercial usage : 

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

2. For commercial usage :

You need to get a commercial license for source usage at : 

http://www.editix.com/buy.html

Copyright (c) 2018 Alexandre Brillant - JAPISOFT SARL - http://www.japisoft.com

@author Alexandre Brillant - abrillant@japisoft.com
@author JAPISOFT SARL - http://www.japisoft.com

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

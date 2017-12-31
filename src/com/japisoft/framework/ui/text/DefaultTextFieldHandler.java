package com.japisoft.framework.ui.text;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;


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
public class DefaultTextFieldHandler implements FileTextFieldHandler {

	public String selectResource(  
			FileTextField source, 
			String previousPath, 
			boolean directoryMode,
			boolean fileMode, 
			boolean openedMode,
			String[] fileExt2, 
			String currentDir) {

		if ( fileExt2 != null && 
				fileExt2.length > 0 && 
					fileExt2[ 0 ] == null )
			fileExt2 = null;

		final String[] fileExt = fileExt2;		
		JFileChooser fc = new JFileChooser();
		
		if ( source.isMultipleSelectionMode() ) {
			fc.setMultiSelectionEnabled( true );
		}
		
		if ( directoryMode && fileMode ) {
			fc.setFileSelectionMode( JFileChooser.FILES_AND_DIRECTORIES );
		} else
		if ( directoryMode )
			fc.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
		else
			fc.setFileSelectionMode( JFileChooser.FILES_ONLY );

		if ( fileExt != null )
			fc.setFileFilter(new FileFilter() {
				public String getDescription() {
					String res = "";
					for ( int i = 0; i < fileExt.length; i++ ) {
						if ( i > 0 )
							res += " ";
						res += "*." + fileExt[ i ];
					}
					return res;
				}
				public boolean accept(File f) {
					if ( f == null )
						return false;
					boolean ok = f.isDirectory();
					if ( !ok  ) {
						for ( int i = 0; i < fileExt.length; i++ ) {
							if ( fileExt[ i ] == null )
								continue;
							if ( 
									f.toString().toLowerCase().endsWith( fileExt[ i ] ) )
								return true;
						}	
					}
					return ok;
				}
			});

		if ( previousPath != null && previousPath.length() > 0) {
			if ( previousPath.indexOf( ";" ) > 0 )
				previousPath = previousPath.split( ";" )[ 0 ];
			File f = new File( previousPath ).getParentFile();
			if ( f != null )
				fc.setCurrentDirectory( f );
		}
		else
		if (currentDir != null) {
			fc.setCurrentDirectory(new File(currentDir));
		} 

		if (openedMode) {

			if (fc.showOpenDialog( source ) == JFileChooser.APPROVE_OPTION) {
				
				if ( fc.isMultiSelectionEnabled() ) {
					File[] res = fc.getSelectedFiles();
					StringBuffer sb = new StringBuffer();
					for ( File f : res ) {
						if ( sb.length() > 0 )
							sb.append( ";" );
						sb.append( f.toString() );
					}
					return sb.toString();
				}

				return fc.getSelectedFile().toString();
			} else
				return null;

		} else {

			if (fc.showSaveDialog( source ) == JFileChooser.APPROVE_OPTION) {
				return fc.getSelectedFile().toString();
			} else
				return null;
		}
	}

	public void createResource( String path ) {

		if ( wrongPathCheck( path ) )
			return;

		
		boolean res = new File( path ).mkdirs();
		if ( !res ) {

			if ( new File ( path ).exists() ) {
				JOptionPane.showMessageDialog( null, "This path " + path + " already exists " + path, "Wrong operation", JOptionPane.WARNING_MESSAGE );
			} else
				JOptionPane.showMessageDialog( null, "Cannot create " + path, "Wrong operation", JOptionPane.WARNING_MESSAGE );

		}

	}

	private boolean wrongPathCheck( String path ) {
		if ( path == null ) {
			JOptionPane.showMessageDialog( null, "No path", "Wrong operation", JOptionPane.WARNING_MESSAGE );
			return true;
		}
		return false;
	}
	
	public void deleteResource( String path ) {
		
		if ( wrongPathCheck( path ) )
			return;
		
		if ( JOptionPane.showConfirmDialog( null, "Delete " + path + " ?" ) == JOptionPane.YES_OPTION ) {
			
			File f = new File( path );
			
			if ( !f.exists() ) {
				JOptionPane.showMessageDialog( null, "Path " + path + " doesn't exist", "Wrong operation", JOptionPane.WARNING_MESSAGE );					
			} else {
				boolean res = f.delete();
				if ( ! res ) {

					if ( f.isDirectory() && f.list() != null ) {
						JOptionPane.showMessageDialog( null, "This path " + path + " is not empty", "Wrong operation", JOptionPane.WARNING_MESSAGE );							
					} else
						JOptionPane.showMessageDialog( null, "Cannot delete " + path, "Wrong operation", JOptionPane.WARNING_MESSAGE );
				}
			}
		}

	}

	public boolean isCreateResourceManaged() {
		return true;
	}
	
	public boolean isDeleteResourceManaged() {
		return false;
	}
	
}

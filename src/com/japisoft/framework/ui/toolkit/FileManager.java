package com.japisoft.framework.ui.toolkit;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import com.japisoft.framework.ApplicationMain;
import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.preferences.Preferences;

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
public class FileManager {

//@@
	static {
		ApplicationMain.class.getName();
	}
//@@

	/** Preference key for storing/restoring the last path */
	public static final String PREFERENCE_KEY_LASTPATH = "toolkit.lastSelectedPath";
	
	private static boolean preferenceForLastFile = false;

	/** Mode for using a preference for saving/restoring the last file path. By default <code>false</code> */
	public static void setPreferenceForLastFilePath( boolean value ) {
		preferenceForLastFile = value;
	}

	private static void saveLastFileDirectory( File file ) {
		if ( preferenceForLastFile ) {
			File directory = file.getParentFile();
			if ( directory != null ) {
				Preferences.setPreference(
						Preferences.SYSTEM_GP,
						PREFERENCE_KEY_LASTPATH,
						directory.toString() );
			}
		}
	}

	private static String getLastFileDirectory() {
		String lastPath = null;
		
		if ( preferenceForLastFile ) {
			lastPath = Preferences.getPreference(
					Preferences.SYSTEM_GP,
					PREFERENCE_KEY_LASTPATH,
					( String )null );
			if ( lastPath != null ) {
				File f = new File( lastPath );
				while ( !f.exists() ) {
					f = f.getParentFile();
					if ( f == null )
						break;
				} 
				if ( f == null )
					lastPath = null;
				else
					lastPath = f.toString();
			}
		}

		if ( lastPath == null )
			lastPath = System.getProperty( "user.home" );
		
		return lastPath;
	}
	
	/** Open a directory dialog */
	public static File getSelectedDirectory( File defaultRoot, String title ) {

		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory( 
				defaultRoot == null ? new File( getLastFileDirectory() ) : defaultRoot );
		chooser.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
		if ( title != null ) {
			chooser.setDialogTitle( title );
		}
		if ( chooser.showOpenDialog( 
				ApplicationModel.MAIN_FRAME ) == 
					JFileChooser.APPROVE_OPTION ) {
			saveLastFileDirectory( chooser.getSelectedFile() );
			return chooser.getSelectedFile();			
		}
		return null;

	}
	
	public static File getSelectedDirectory() {
		return getSelectedDirectory( null, null );
	}

	/** Open a file dialog */
	public static File getSelectedFile(
			boolean openMode,
			String[] fileExt,
			String[] description ) {
		if ( fileExt.length != description.length )
			throw new RuntimeException( "Invalid fileExt and description parameters" );

		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory( new File( getLastFileDirectory() ) );

		for ( int i = 0; i < fileExt.length; i++ ) {
			final String fe = fileExt[ i ];
			final String de = description[ i ];
			chooser.addChoosableFileFilter(
					new FileFilter() {
						public boolean accept( File f ) {
							return 
								f.isDirectory() ||
								f.toString().toLowerCase().endsWith( "." + fe );
						}
						public String getDescription() {
							return de;
						}						
					} );
		}

		if ( openMode ) { 
			if ( chooser.showOpenDialog( ApplicationModel.MAIN_FRAME ) == 
				JFileChooser.APPROVE_OPTION ) {

				saveLastFileDirectory( chooser.getSelectedFile() );
				
				for ( int i = 0; i < description.length; i++ ) {
					if ( chooser.getFileFilter().getDescription().equals( description[ i ] ) ) {
						File f = checkedExt( chooser.getSelectedFile(), fileExt[ i ] );
						return f;		
					}
				}

				return chooser.getSelectedFile();
			}
		} else
			if ( chooser.showSaveDialog( ApplicationModel.MAIN_FRAME ) == 
				JFileChooser.APPROVE_OPTION ) {

				saveLastFileDirectory( chooser.getSelectedFile() );				
				
				for ( int i = 0; i < description.length; i++ ) {
					if ( chooser.getFileFilter().getDescription().equals( description[ i ] ) ) {
						File f = checkedExt( chooser.getSelectedFile(), fileExt[ i ] );
						return f;		
					}
				}
				
				return chooser.getSelectedFile();
			}

		return null;
	}
	
	public static File[] getSelectedFiles(
		boolean openMode, 
		String fileExt, 
		String description
	) {

		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory( new File( getLastFileDirectory() ) );
		chooser.setMultiSelectionEnabled( true );
		
		final String fe = fileExt;
		final String de = description;
		
		if ( fe != null ) {
		
			chooser.setFileFilter(
					new FileFilter() {
						public boolean accept( File f ) {
							return 
								f.isDirectory() ||
								f.toString().toLowerCase().endsWith( "." + fe );
						}
						public String getDescription() {
							return de;
						}
					} 
			);		
		
		}
		
		if ( openMode ) { 
			if ( chooser.showOpenDialog( ApplicationModel.MAIN_FRAME ) == 
				JFileChooser.APPROVE_OPTION ) {
				saveLastFileDirectory( chooser.getSelectedFile() );				
				File[] f = checkedExt( chooser.getSelectedFiles(), fileExt );
				return f;
			}
		} else
			if ( chooser.showSaveDialog( ApplicationModel.MAIN_FRAME ) == 
				JFileChooser.APPROVE_OPTION ) {
				saveLastFileDirectory( chooser.getSelectedFile() );
				File[] f = checkedExt( chooser.getSelectedFiles(), fileExt );
				return f;
			}

		return null;

	}

	/** Open a file dialog */
	public static File getSelectedFile( 
			boolean openMode, 
			String fileExt, 
			String description ) {
		
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory( new File( getLastFileDirectory() ) );

		final String fe = fileExt;
		final String de = description;
		
		if ( fe != null ) {
		
			chooser.setFileFilter(
					new FileFilter() {
						public boolean accept( File f ) {
							return 
								f.isDirectory() ||
								f.toString().toLowerCase().endsWith( "." + fe );
						}
						public String getDescription() {
							return de;
						}
					} 
			);		
		
		}
		
		if ( openMode ) { 
			if ( chooser.showOpenDialog( ApplicationModel.MAIN_FRAME ) == 
				JFileChooser.APPROVE_OPTION ) {
				
				saveLastFileDirectory( chooser.getSelectedFile() );				
				
				File f = checkedExt( chooser.getSelectedFile(), fileExt );
				return f;
			}
		} else
			if ( chooser.showSaveDialog( ApplicationModel.MAIN_FRAME ) == 
				JFileChooser.APPROVE_OPTION ) {
				
				saveLastFileDirectory( chooser.getSelectedFile() );				
				
				File f = checkedExt( chooser.getSelectedFile(), fileExt );
				return f;
			}

		return null;
	}

	private static File[] checkedExt( File[] files, String ext ) {
		File[] res = new File[ files.length ];
		for ( int i = 0; i < files.length; i++ ) {
			res[ i ] = checkedExt( files[ i ], ext );
		}
		return res;
	}
	
	private static File checkedExt( File file, String ext ) {
		if ( ext != null ) {
			if ( file.toString().toLowerCase().endsWith( "." + ext ) )
				return file;
			if ( file.toString().indexOf( "." ) == -1 )
				return new File( file.toString() + "." + ext );
		}
		return file;
	}
	
}

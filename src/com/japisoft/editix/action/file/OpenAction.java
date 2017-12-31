package com.japisoft.editix.action.file;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import org.xml.sax.EntityResolver;

import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.ApplicationModel.ApplicationModelListener;
import com.japisoft.framework.application.descriptor.ActionModel;
import com.japisoft.editix.main.EditixApplicationModel;
import com.japisoft.editix.toolkit.Toolkit;
import com.japisoft.editix.ui.DocumentFileChooser;
import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.panels.universalbrowser.CommonUniversalBrowserPanel;

import com.japisoft.framework.application.descriptor.InterfaceBuilder;
import com.japisoft.framework.preferences.Preferences;
import com.japisoft.framework.xml.XMLFileData;
import com.japisoft.framework.xml.XMLToolkit;
import com.japisoft.xmlpad.IXMLPanel;
import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.XMLDocumentFileFilter;

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
public class OpenAction extends AbstractAction implements ApplicationModelListener {
	
	public OpenAction() {
		super();
	}

	public void fireApplicationData(String key, Object...values) {		
		if ( "open".equals( key ) ) {
			File f = ( File )values[ 0 ];
			String type = null;
			if ( values.length > 1 )
				type = ( String )values[ 1 ];
			String properties = null;
			if ( values.length > 2 ) {
				properties = ( String )values[ 2 ];
			}
			
			openFile( 
				type, 
				true, 
				f.toString(), 
				null, 
				properties 
			);
		}
	}

	public static boolean manageZIPFile( File file ) {
		return false;
	}
	
	public void actionPerformed( ActionEvent e ) {
		String paramValue = ( String )getValue( "param" );

		if ( getValue( "param" ) != null ) {
			String fileName = "" + getValue( "param" );
			if ( acceptFile( fileName ) ) {
				
				String encoding = ( String )getValue( "param3" );
				if ( encoding == null )
					encoding = Toolkit.getCurrentFileEncoding(); 

				String properties = ( String )getValue( "param4" );
				if ( e != null ) {
					String command = e.getActionCommand();
					
					// Particular case for VFS User and Password not in the file path
					if ( command != null && command.indexOf( "@" ) > -1 ) {
						StringTokenizer st = new StringTokenizer( command, "$$" );
						try {
							String user = st.nextToken();
							String password = st.nextToken();
							if ( properties == null )
								properties = "";
							if ( properties.length() > 0 )
								properties += ";";
							properties += "vfs.user=" + user + ";" + "vfs.password=" + password; 
						} catch( NoSuchElementException exc ) {
						}
					}
				}
				
				boolean ok = openFile( 
						( String )getValue( "param2" ), 
						true, 
						fileName,
						encoding,
						properties );

				ActionModel.LAST_ACTION_STATE = ok;				

				if ( !ok ) {
					EditixFactory.buildAndShowErrorDialog( "Can't open file " + getValue( "param" ) );
				}
			}
			return;
		}

		JFileChooser fileChooser = EditixFactory.buildFileChooser();
		
		// Restore the last file location
		String previousPath = Preferences.getPreference( 
			"file",
			"defaultPath",
			""
		);
		
		if ( !"".equals( previousPath  ) ) {
			File f = new File( previousPath );
			if ( f.exists() )
				fileChooser.setCurrentDirectory( f );
		}
		
		FileFilter[] ff = fileChooser.getChoosableFileFilters();
		if ( ff.length > 1 )
			fileChooser.setFileFilter( ff[ 1 ] );
		
		String selectedEncoding = null;

		if ( fileChooser.showOpenDialog( EditixFrame.THIS ) == 
			JFileChooser.APPROVE_OPTION ) {

			File[] files = fileChooser.getSelectedFiles();
			if ( files.length == 1 ) {
				String filePath = files[ 0 ].toString();
			}

			if ( fileChooser instanceof DocumentFileChooser ) {
				selectedEncoding = 
					( ( DocumentFileChooser )fileChooser ).getSelectedEncoding();
			}

			FileFilter filter = fileChooser.getFileFilter();
			String type = null;
			if ( filter != null ) {
				if ( filter instanceof XMLDocumentFileFilter )
					type = ((XMLDocumentFileFilter)filter).getType();
			}
			
			
			if ( files.length > 0 ) {
				if ( files[ 0 ].getParentFile() != null ) {
					if ( Preferences.getPreference(
							"file", "restoredLastPath", true ) ) {
						Preferences.setPreference( "file", "defaultPath", files[ 0 ].getParentFile().toString() );
					}
				}
			}

			StringBuffer sbError = null;
			for ( int i = 0; i < files.length; i++ ) {
				if ( acceptFile( files[ i ] ) ) {
				
					boolean ok = openFile( type, false, files[ i ], selectedEncoding );
					if ( !ok ) {
						if ( sbError == null )
							sbError = new StringBuffer( "Can't open :\n" );
						sbError.append( files[ i ].toString() ).append( "\n" );
						ActionModel.LAST_ACTION_STATE = false;
					} else {
						ActionModel.LAST_ACTION_STATE = true;
					}
				}
			}
			
			if ( sbError != null ) {
				EditixFactory.buildAndShowErrorDialog( sbError.toString() );
			}
		}
	}

	private boolean acceptFile( File f ) {
		return acceptFile( f.toString() );
	}

	private boolean acceptFile( String f ) {
		// Check if this file exist ?
		String str = f.toString();
		if ( EditixFrame.THIS.getContainerByFilePath( str ) != null ) {
			if ( JOptionPane.showConfirmDialog( EditixFrame.THIS, "This document '" + str + "' is already opened, continue ?" ) != 
					JOptionPane.OK_OPTION ) {
				return false;
			}
		}		
		return true;
	}

	public static boolean openFile( String type, boolean param, File f, String encoding ) {
		return openFile( type, param, f.toString(), encoding, null );
	}
	
	public static boolean openFile( 
			String type, 
			boolean param, 
			String f, 
			String encodingMode, 
			String properties ) {

		try {
			
			if ( f.indexOf( "!/" )  > -1 || f.startsWith( "ftp:" ) ) {
				// VSF case

				boolean res = CommonUniversalBrowserPanel.openFile( f, type, properties );
				if ( !res ) {	// Remove from the history
					EditixFrame.THIS.getBuilder().removeMenuItemForParam(
							InterfaceBuilder.MENU_RECENT_FILE, f );
				}
				return res;
				
			} else {
			
				
				try {
				
					XMLFileData file = XMLToolkit.getContentFromURI( f, encodingMode );
		
					if ( file.getContent() == null ) {
						EditixFrame.THIS.getBuilder().removeMenuItemForParam(
								InterfaceBuilder.MENU_RECENT_FILE, f );				
						return false;
					}
		
					return openFile( type, param, encodingMode, f, properties, file );
					
				} catch( OutOfMemoryError memoryError ) {
					
					ApplicationModel.fireApplicationValue( "error", "This document is too big, try opening it as a large document" );

					return false;
				}

			}
			
		} catch( Throwable th ) {
			EditixApplicationModel.debug( th );
			return false;
		}
	}
	
	public static boolean openFile( 
			String type, 
			boolean param, 
			String encodingMode, 
			String f, 
			String properties, 
			XMLFileData file ) {
		return openFile( type, param, encodingMode, f, properties, file, null, null );
	}

	public static boolean openFile( 
			String type, 
			boolean param, 
			String encodingMode, 
			String f, 
			String properties, 
			XMLFileData file, 
			SaveActionDelegate delegate,
			EntityResolver er ) {
		try {
			
			String content = file.getContent();
			String encoding = file.getEncoding();

			NewAction.signalToUser( type );
			
			IXMLPanel panel = null;
			if ( type != null )
				panel = EditixFactory.buildNewContainer( type, f );
			else
				panel = EditixFactory.buildNewContainer( f );

			XMLContainer container = panel.getMainContainer();	
			
			if ( delegate != null )
				container.setProperty( "save.delegate", delegate );
			
			if ( er != null )
				container.setProperty( "entityresolver", er );
			
			container.getDocumentInfo().setCurrentDocumentModifiedDate( file.modifiedDate );
			ApplicationModel.debug( "Open " + f + " with encoding " + encoding );

			if ( encoding != null && !"DEFAULT".equals( encoding ) ) {
				container.setProperty( "encoding", encodingMode );
				container.getDocumentInfo().setEncoding( encoding );
			}

			container.getDocumentInfo().setCurrentDocumentLocation( f );
			container.setText( content );
			panel.postLoad();
			
			if ( properties != null ) {
				StringTokenizer st = new StringTokenizer( properties, ";" );
				while ( st.hasMoreTokens() ) {
					String token = st.nextToken();
					int i = token.indexOf( "=" );
					if ( i > -1 ) {
						String key = token.substring( 0, i );
						String value = token.substring( i + 1 );
						panel.setProperty( key, value );
						
						if ( "encoding".equals( key ) ) {
							container.getDocumentInfo().setEncoding( value );
						}
						
					}
				}
			}
			
			EditixFrame.THIS.addContainer( panel );

			// Store it
			if ( !param ) {
				synchronizedRecentFileMenu( container, f, type, encoding );
			}
			return true;
		} catch( Throwable th ) {
			EditixApplicationModel.debug( th );
			return false;
		}
	}

	public static void synchronizedRecentFileMenu( XMLContainer container, String f, String type, String encoding ) {
		if ( container.getCurrentDocumentLocation() == null )
			return;
		// Database case
		if ( container.hasProperty( "save.delegate" ) )
			return;
		Action a = new OpenAction();
		a.putValue( Action.NAME, f );
		a.putValue( "param", f );
		if ( container.getDocumentInfo().getDocumentIconPath() != null )
			a.putValue( "iconPath", container.getDocumentInfo().getDocumentIconPath() );
		if ( type != null )
			a.putValue( "param2", type );
		if ( encoding != null )
			a.putValue( "param3", encoding );
		
		// Get the param / key values
		StringBuffer sb = null;
		Iterator it = container.getProperties();
		if ( it != null ) {
			while ( it.hasNext() ) {
				String property = ( String )it.next();
				Object v = container.getProperty( property );
				if ( v instanceof String ) {
					if ( sb == null )
						sb = new StringBuffer();
					if ( sb.length() > 0 )
						sb.append( ";" );
					sb.append( property ).append( "=" ).append( v );
				}
			}
			if ( sb != null )
				a.putValue( "param4", sb.toString() );
		}

		int lastOpenedSize = Preferences.getPreference( "file", "lastOpenedSize", 20 );
		EditixFrame.THIS.getBuilder().insertMenuItemAtFirst( InterfaceBuilder.MENU_RECENT_FILE, a, lastOpenedSize );
		
	}
	
}

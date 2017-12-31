package com.japisoft.editix.ui.panels.universalbrowser;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.StringTokenizer;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;

import org.apache.commons.vfs.FileContent;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.FileSystemOptions;
import org.apache.commons.vfs.VFS;
import org.apache.commons.vfs.auth.StaticUserAuthenticator;
import org.apache.commons.vfs.impl.DefaultFileSystemConfigBuilder;
import org.apache.commons.vfs.provider.zip.ZipFileSystem;

import com.japisoft.editix.action.file.OpenAction;
import com.japisoft.editix.action.panels.PanelAction;
import com.japisoft.editix.toolkit.Toolkit;
import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.panels.AbstractPanel;
import com.japisoft.editix.ui.panels.Panel;
import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.application.descriptor.ActionModel;
import com.japisoft.framework.preferences.Preferences;
import com.japisoft.framework.xml.XMLFileData;
import com.japisoft.framework.xml.XMLToolkit;

import com.japisoft.universalbrowser.FileFilter;
import com.japisoft.universalbrowser.JUniversalBrowserTree;
import com.japisoft.universalbrowser.UniversalBrowserListener;
import com.japisoft.xmlpad.XMLContainer;

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
public abstract class CommonUniversalBrowserPanel extends AbstractPanel implements UniversalBrowserListener, ActionListener {

	private static FileObject mustBrowse = null;

	protected JUniversalBrowserTree browserTree = null;

	protected JComponent buildView() {
		initUIComponent();
		return ui;
	}

	private void browse( FileObject fo ) {
		initUIComponent();
		browserTree.browse( fo );
	}
	
	@Override
	protected void postShow() {
		filter.addActionListener( this );
	}
	@Override
	protected void preHide() {
		filter.removeActionListener( this );
	}
	
	public void actionPerformed(ActionEvent e) {
		if ( "AUTOMATIC".equals( filter.getSelectedItem().toString() ) ) 
				browserTree.setFileFilter( null );
		else {
			FileFilter ff = ( FileFilter )filter.getSelectedItem();
			browserTree.setFileFilter( ff );
		}
	}	
	
	private String initURI = null;
	private FileSystemOptions initOpts = null;
	private FTPConfig initFTPConfig = null;
	
	public void close() {
		if ( browserTree != null )
			browserTree.close();
	}
	
	public void select(Object path) {
		browse( initURI, null, null );
		browserTree.select( ( String )path );
	}	

	public void init() {
		super.init();
		String lastURI = 
			Preferences.getPreference( 
					Preferences.SYSTEM_GP, 
					getClass().getName(),
					( String )null );

		String user = null;
		String password = null;
		
		if ( lastURI != null ) {
				
			user = Preferences.getPreference(
					Preferences.SYSTEM_GP,
					getClass().getName() + ".user", ( String )null );
			password = Preferences.getPreference(
					Preferences.SYSTEM_GP, 
					getClass().getName() + ".password",
					( String )null );

		}

		browse( lastURI, user, password );
	}

	public void browse( String uri, String user, String password ) {

		FTPConfig tmp = null;
		if ( user != null ) {
			tmp = new FTPConfig();
			tmp.user = user;
			tmp.password = password;
		}

		FileSystemOptions opts = null;
		if ( user != null ) {
			StaticUserAuthenticator auth = new StaticUserAuthenticator( null, user, password ); 
			opts = new FileSystemOptions(); 
			try {
				DefaultFileSystemConfigBuilder.getInstance().setUserAuthenticator( opts, auth );
			} catch (FileSystemException e) {
			}
		}

		initURI = uri;
		initOpts = opts;
		initFTPConfig = tmp;

		if ( isVisible() )
			synchronizedBrowser();

	}

	protected void preShow() {
		synchronizedBrowser();
		if ( browserTree != null )
			browserTree.setUnivervalBrowserListener( this );
	}	

	private void synchronizedBrowser() {
		try {
			initUIComponent();
			FileSystemManager fsManager = VFS.getManager();
			if ( initOpts != null ) {
				browserTree.browse( 
						fsManager.resolveFile( 
								initURI, 
								initOpts 
						) 
				);
			} else
				browserTree.browse( 
						fsManager.resolveFile( 
								initURI 
						) 
				);
		} catch (FileSystemException e) {
			ApplicationModel.debug( e );
			// Block EditiX if the frame is not visible
			// EditixFactory.buildAndShowErrorDialog( "Can't browse this ressource " + initURI );
		} catch( Throwable th ) {}
	}

	protected boolean storeLastBrowsedFileObject() {
		return true;
	}
	
	public void stop() {
		if ( browserTree == null )
			return;
		if ( storeLastBrowsedFileObject() ) {
			FileObject browsed = 
				browserTree.getBrowsedFileObject();
			if ( browsed != null ) {
				Preferences.setPreference( 
					Preferences.SYSTEM_GP, 
					getClass().getName(),
					browsed.getName().getRootURI() );
				if ( initFTPConfig != null ) {
					Preferences.setPreference( 
							Preferences.SYSTEM_GP, 
							getClass().getName() + ".user",
							initFTPConfig.user );
					Preferences.setPreference( 
							Preferences.SYSTEM_GP, 
							getClass().getName() + ".password",
							initFTPConfig.password );				
				}
			}
		}
		browserTree.close();
	}

	private JPanel ui = null;
	private JComboBox filter = null;

	protected Action[] getBrowserActions() { 
		return null; 
	}

	private void initUIComponent() {
		if ( browserTree == null ) {
			browserTree = EditixFactory.getUniversalBrowserTree();
			browserTree.setUnivervalBrowserListener( this );
			if ( mustBrowse != null )
				browserTree.browse( mustBrowse );
			mustBrowse = null;

			ui = new JPanel();
			ui.setLayout( new BorderLayout() );
			ui.add( new JScrollPane( browserTree ) );

			JPanel northPanel = new JPanel();
			northPanel.setLayout( new BorderLayout() );
			northPanel.add( new JButton( new OpenToolbarAction() ), BorderLayout.EAST );
			filter = EditixFactory.getFileFilterComboBox();
			filter.insertItemAt( "AUTOMATIC", 0 );
			filter.setSelectedIndex( 0 );
			filter.setPreferredSize( new Dimension( 100, 0 ) );
			northPanel.add( filter, BorderLayout.CENTER );
			northPanel.add( new JLabel( "Open as" ), BorderLayout.WEST );
			ui.add( northPanel, BorderLayout.NORTH );		
			
			Action[] actions = getBrowserActions();
			if ( actions != null ) {
				JToolBar tb = new JToolBar();
				for ( int i = 0; i < actions.length; i++ ) {
					tb.add( actions[ i ] );
				}
				tb.setFloatable( false );
				ui.add( tb, BorderLayout.SOUTH );
			}
		}
	}

	protected void hide() {
		super.hide();
		browserTree.setUnivervalBrowserListener( null );
	}

	public static boolean openFile( String uri, String type, String properties ) {
		try {
			FileSystemManager fsManager = VFS.getManager();
			FileSystemOptions opts = null;
			String user = null;
			String password = null;
			
			if ( properties != null ) {

				StringTokenizer st = new StringTokenizer( properties, ";" );
				
				while ( st.hasMoreTokens() ) {
					String token = st.nextToken();
					int i = token.indexOf( "=" );
					if ( i > -1 ) {
						String key = token.substring( 0, i );
						String value = token.substring( i + 1 );
						if ( "vfs.user".equals( key ) )
							user = value;
						else
						if ( "vfs.password".equals( key ) )
							password = value;
					}
 				}
				
				StaticUserAuthenticator auth = new StaticUserAuthenticator(null,user,password); 
				opts = new FileSystemOptions(); 
				DefaultFileSystemConfigBuilder.getInstance().setUserAuthenticator(opts, auth);

			}
			
			FileObject fo = null;
			
			if ( opts != null )
				fo = fsManager.resolveFile( uri, opts );
			else
				fo = fsManager.resolveFile( uri );

			return open( fo, type, user, password );
		} catch (FileSystemException e) {
			return false;
		}
	}

	public static boolean save( XMLContainer container ) {
		String uri = container.getDocumentInfo().getCurrentDocumentLocation();
		try {
			FileSystemManager fsManager = VFS.getManager();
			
			FileSystemOptions opts = null;
			
			if ( container.hasProperty( "vfs.user" ) ) {
				String user = ( String )container.getProperty( "vfs.user" );
				String password = ( String )container.getProperty( "vfs.password" );
				StaticUserAuthenticator auth = new StaticUserAuthenticator(null,user,password); 
				opts = new FileSystemOptions(); 
				DefaultFileSystemConfigBuilder.getInstance().setUserAuthenticator(opts, auth);
			}

			FileObject fo = null;
			
			if ( opts != null )
				fo = fsManager.resolveFile( uri, opts );
			else
				fo = fsManager.resolveFile( uri );

			String encoding = container.getDocumentInfo().getEncoding();
			String content = container.getText();
			if ( encoding == null ||
					"AUTOMATIC".equals( encoding ) ) {
				encoding = XMLToolkit.getXMLEncoding( content );
				if ( encoding == null || "AUTOMATIC".equals( encoding ) )
					encoding = "UTF-8";
			}

			if ( fo.isWriteable() ) {
				OutputStream output = fo.getContent().getOutputStream();
				try {
					OutputStreamWriter ow = 
						new OutputStreamWriter( output, encoding );
					ow.write( content );
					ow.close();
					container.setModifiedState( false );

				} catch (UnsupportedEncodingException e) {
					return false;
				} catch( IOException e ) {
					ApplicationModel.debug( e );
					return false;
				}

			} else {

				// Check for ZIP/JAR case
				if ( fo.getFileSystem() instanceof ZipFileSystem ) {

					// Can write ?
					int loc = uri.lastIndexOf( "!/" );
					String entry = uri.substring( loc + 2 );
					
					int fLoc = uri.lastIndexOf( "file:///" );
					
					if ( fLoc > -1 ) {

						String zipTmp = uri.substring( fLoc + 8, loc );
						File zipFile = new File( zipTmp );
						if ( zipFile.exists() ) {

							try {
								
								PanelAction pa = ( PanelAction )ActionModel.restoreAction( "openZip" );
								Panel paMustClose = null;
								if ( pa != null && pa.isPrepared() ) {
									paMustClose = pa.preparePanel();
								}

								// Close the current browser window
								if ( paMustClose != null )
									paMustClose.close();
								
								boolean res = com.japisoft.framework.toolkit.FileToolkit.writeToZip(
										zipFile,
										entry,
										encoding,
										content );
								container.setModifiedState( false );

								// Re-open it
								if ( paMustClose != null ) {
									paMustClose.select( uri );
								}

								return res;
							} catch (IOException e) {
								return false;
							}

						}

					}

				}

				return false;
			}
		} catch (FileSystemException e) {
			return false;
		}
		return true;
	}

	public static boolean open( FileObject fo, String type, String user, String password ) {
		
		try {

			if ( fo.isReadable() ) {

				String uri = fo.getName().getURI();
				boolean localFile = false;
				
				if ( uri.startsWith( "file:///" ) ) {					
					uri = uri.substring( 8 );
					if ( ( uri.indexOf( ":" ) == -1 ) )	// For Linux/Mac
						uri = "/" + uri;
					localFile = true;
				}

				if ( localFile ) {
					// Open from the framework
					ApplicationModel.fireApplicationValue( "open", new File( uri ) );
					return true;
				}

				FileContent fc = fo.getContent();
				InputStream input = fc.getInputStream();

				try {
					XMLFileData xfd = XMLToolkit.getContentFromInputStream( 
							input, 
							Toolkit.getCurrentFileEncoding() );

					String properties = null;
					
					if ( user != null && password != null ) {
						properties = "vfs.user=" + user + ";" + "vfs.password=" + password;
					}

					if ( localFile )
						xfd.modifiedDate = new File( uri ).lastModified();
					
					return OpenAction.openFile( type, false, null, uri, properties, xfd );

				} catch (Throwable e) {
					EditixFactory.buildAndShowErrorDialog( 
							"Can't open " + 
							fo.getName().getBaseName() );	
				}

			} else {

				EditixFactory.buildAndShowWarningDialog( 
						"Can't open " + 
						fo.getName().getBaseName() );
			}

		} catch (FileSystemException e) {

			EditixFactory.buildAndShowErrorDialog( 
					"Can't open " + 
					fo.getName().getBaseName() );

		}

		return false;
	}

	////////////////////////////////////////////////////////

	public void doubleClick( FileObject fo ) {
		String user = null;
		String password = null;
		if ( initFTPConfig != null ) {
			user = initFTPConfig.user;
			password = initFTPConfig.password;
		}
		open( fo, null, user, password );
	}

	public void select( FileObject fo ) {
	}

	//////////////////////////////////////////////////////

	class OpenToolbarAction extends AbstractAction {
		public OpenToolbarAction() {
			putValue( Action.SHORT_DESCRIPTION, "Open the selected ressource" );
			putValue( Action.SMALL_ICON, ActionModel.getIconActionById( "open" ) );
		}

		public void actionPerformed(ActionEvent e) {
			Object f = filter.getSelectedItem();
			String user = null;
			String password = null;
			if ( initFTPConfig != null ) {
				user = initFTPConfig.user;
				password = initFTPConfig.password;
			}
			if ( "AUTOMATIC".equals( f ) )
				open( browserTree.getSelectedFileObject(), null, user, password );
			else {
				EditixFactory.XMLDocumentInfoFileFilter ff = ( EditixFactory.XMLDocumentInfoFileFilter )f;
				open( browserTree.getSelectedFileObject(), ff.getType(), user, password );
			}
		}
	}
	
}

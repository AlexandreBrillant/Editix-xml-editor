package com.japisoft.framework.ui.text;

import java.awt.BorderLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.preferences.Preferences;
import com.japisoft.framework.ui.TitleLabel;

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
public class FileTextField 
				extends JPanel 
					implements ActionListener, PopupMenuListener {

	public JComboBox combo;
	private TitleLabel lbl;
	private JButton btn;
	private JButton btn2;
	private JButton btn3;
	private String[] fileExt;
	private PathBuilder builder;
	private JPanel pnlBtns;

	/**
	 * @param label Label for the component
	 * @param filePath The initial file path set
	 * @param fileExt The initial file ext */	
	public FileTextField( 
		String label, 
		String filePath, 
		String fileExt ) {
		this( label, filePath, new String[] { fileExt } );
	}

	/**
	 * @param label Label for the component
	 * @param filePath The initial file path set
	 * @param fileExt The initial file exts */	
	public FileTextField( 
		String label, 
		String filePath, 
		String[] fileExt ) {
		this( 
			label, 
			new String[] { filePath }, 
			fileExt,
			null );
		combo.setSelectedIndex( 0 );
	}

	/**
	 * @param label Label for the component
	 * @param filePath The initial file path set
	 * @param fileExt The initial file ext
	 * @param builder A delegate for building the combo box content */		
	public FileTextField( 
		String label, 
		String[] filePath, 
		String fileExt,
		PathBuilder builder ) {
		this( label, filePath, new String[] { fileExt }, builder );
	}

	public void setDocument( Document document ) {
		( ( JTextComponent )combo.getEditor().getEditorComponent() ).setDocument( document );
	}

	/**
	 * @param label Label for the component
	 * @param filePath The initial file path set
	 * @param fileExt The initial file exts
	 * @param builder A delegate for building the combo box content */	
	public FileTextField( 
			String label, 
			String[] filePath, 
			String[] fileExt,
			PathBuilder builder ) {
		this.fileExt = fileExt;
		this.builder = builder;
		if ( label != null )
			lbl = new TitleLabel( label );

		String[] tmp = filePath;
		if ( builder != null ) {
			tmp = builder.buildPathsChoice();
			if ( tmp == null )
				tmp = filePath;
		}
		if ( tmp != null )
			combo = new JComboBox( tmp );
		else
			combo = new JComboBox();

		combo.setEditable( true );
		setLayout( new BorderLayout() );
		if ( label != null )
			add( lbl, BorderLayout.WEST );

		add( combo, BorderLayout.CENTER );
		
		setBorder( null );
		
		pnlBtns = new JPanel();
		pnlBtns.setLayout( new BoxLayout( pnlBtns, BoxLayout.X_AXIS ) );
		pnlBtns.setBorder( null );
		URL url = ClassLoader.getSystemResource( "images/folder.png" );
		if ( url == null ) {
			btn = new JButton( "..." );
			System.err.println( "Can't find images/folder.png" );
		} else		
			btn = new JButton( new ImageIcon( url ) );

		btn.setMargin( null );
		btn.setBorder( null );
		btn.setToolTipText( "Choose a file from the dialog filesystem" );

		pnlBtns.add( btn );
		add( pnlBtns, BorderLayout.EAST );

		if ( combo.getEditor().getEditorComponent() instanceof JComponent ) {
			( ( JComponent )combo.getEditor().getEditorComponent() ).setTransferHandler(
					new SystemTransferHandler() );
		}
	}

	public FileTextField(String filePath, String[] fileExt) {
		this(null, filePath, fileExt);
	}

	public FileTextField(String filePath, String fileExt) {
		this(null, filePath, new String[] { fileExt } );
	}
	
	public void setFilePath(String path) {
		combo.setSelectedItem( path );
	}
	
	/** Builder for selecting easily a file */
	public void setPathBuilder( PathBuilder pb ) {
		this.builder = pb;
	}

	/** Override the current document model of the combo */
	public void overrideDocument( Document d ) {
		((JTextField)combo.getEditor().getEditorComponent()).setDocument( d );
	}

	private String prefGroup = null;
	private String prefName = null;
	
	public void setPreferenceBinding( String group, String name ) {
		this.prefGroup = group;
		this.prefName = name;
		if ( group != null && name != null ) {
			setCurrentDirectory( 
					Preferences.getPreference( group, name, (String)null ) );
		}
	}

	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		combo.setEnabled(enabled);
		btn.setEnabled(enabled);
		if ( btn2 != null )
			btn2.setEnabled( enabled );
		if ( btn3 != null )
			btn3.setEnabled( enabled );
	}

	private String currentDir;

	public void setCurrentDirectory(String loc) {
		this.currentDir = loc;
	}

	private boolean directoryMode = false;

	public void setDirectoryMode( boolean directoryMode ) {
		this.directoryMode = directoryMode;
		if ( directoryMode ) {
			URL	url = ClassLoader.getSystemResource( 
			"images/folder_new.png" );
			if ( url == null ) {
				btn2 = new JButton( "New" );
				System.err.println( "Can't find images/folder_new.png" );
			} else
				btn2 = new JButton( new ImageIcon( url ) );
			btn2.setMargin( null );
			btn2.setBorder( null );
			btn2.setToolTipText( "Create a directory" );
			
			url = ClassLoader
				.getSystemResource( "images/folder_delete.png" );
	
			if ( url == null ) {
				btn3 = new JButton( "Del" );
				System.err.println( "Can't find images/folder_delete.png" );
			}
			else
				btn3 = new JButton( new ImageIcon( url ) );

			btn3.setToolTipText( "Delete a file or an empty directory" );
			btn3.setMargin( null );
			btn3.setBorder( null );

			pnlBtns.add( btn2 );
			pnlBtns.add( btn3 );
		} 
	}

	private boolean fileMode = true;
	
	public void setFileMode( boolean fileMode ) {
		this.fileMode = fileMode;
	}
	
	private ActionListener customListener;

	public void setActionListener( ActionListener customListener ) {
		this.customListener = customListener;
	}

	public void addNotify() {
		super.addNotify();
		btn.addActionListener( this );
		if ( btn2 != null )
			btn2.addActionListener( this );
		if ( btn3 != null )
			btn3.addActionListener( this );
		combo.addActionListener( this );
		combo.addPopupMenuListener( this );
	}

	public void removeNotify() {
		super.removeNotify();
		btn.removeActionListener( this );
		if ( btn2 != null )
			btn2.removeActionListener( this );
		if ( btn3 != null )
			btn3.removeActionListener( this );
		combo.removeActionListener( this );
		combo.removePopupMenuListener( this );
	}

	public void popupMenuCanceled(PopupMenuEvent e) {}
	public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {}

	public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
		if ( builder != null ) {		
			String[] choice = builder.buildPathsChoice();
			if ( choice != null ) {
				combo.setModel(
					new DefaultComboBoxModel( choice ) );
				if ( choice.length == 1 )
					combo.setSelectedIndex( 0 );
			}
		}
	}

	public void setFileExt( String[] ext ) {
		this.fileExt = ext;
	}

	public void setFileExt( String ext ) {
		this.fileExt = new String[] { ext };
	}
		
	/** @return the current text content */
	public String getText() {
		String tmp = ( String )combo.getSelectedItem();
		if ( "".equals( tmp ) )
			return null;
		return tmp;
	}
	
	public String getText( String defaultValue ) {
		String tmp = getText();
		if ( tmp == null )
			return defaultValue;
		else
			return null;
	}

	private boolean wsCheck = false;
	
	// Check for whitespace inside the path
	public void setWhitespaceChecker( boolean check ) {
		this.wsCheck = check;
	}

	public void setText( String text ) {
		if  ( !directoryMode && 
					fileExt != null && 
						fileExt.length > 0 && 
							fileExt[ 0 ] != null && 
								text != null && 
									!"".equals( text ) && 
										( text.indexOf( "." ) == -1 ) )
			text = text + "." + fileExt[ 0 ];

		if ( wsCheck ) {
			for ( int i = 0; i < text.length(); i++ ) {
				if ( Character.isWhitespace( text.charAt( i ) ) ) {
					JOptionPane.showMessageDialog( 
							ApplicationModel.MAIN_FRAME, 
							"Please don't use path with whitespaces inside", "Illegal character in your path", JOptionPane.WARNING_MESSAGE );
					return;
				}
			}
		}
		
		combo.setSelectedItem( text );
		// Force it due to a refresh bug ??
		combo.getEditor().setItem( text );

		if ( prefGroup != null && 
				prefName != null && text != null ) {
			File f = new File( text );
			if ( f.isFile() )
				f = f.getParentFile();
			Preferences.setPreference( prefGroup, prefName, f.toString() );
		}
	}

	public String getCurrentDirectory() {
		if ( ( combo.getSelectedItem() != null && 
				getText().length() == 0 ) || 
					getText() == null )
			return null;
		return new File( getText() ).getParent();
	}

	private boolean openedMode = true;

	public void setOpenedMode(boolean openedMode) {
		this.openedMode = openedMode;
	}

	private boolean multipleSelectionMode = false;
	
	public void setMultipleSelectionMode( boolean mode ) {
		this.multipleSelectionMode = mode;
	}

	public boolean isMultipleSelectionMode() {
		return this.multipleSelectionMode;
	}
	
	public void actionPerformed(ActionEvent e) {
		
		if ( e.getSource() == btn2 ) {	// New
			getFileTextFieldHandler().createResource( getText() );
			
		} else
		if ( e.getSource() == btn3 ) {	// Delete

			getFileTextFieldHandler().deleteResource( getText() );
			
		} else
		if (e.getSource() == btn) {

			String res = 
				getFileTextFieldHandler().selectResource(
					this,
					getText(),
					directoryMode,
					fileMode,
					openedMode,
					fileExt,
					currentDir );

			if ( res != null ) {
				// Avoid double actionPerformed
				combo.removeActionListener( this );
				setText( res );
				combo.addActionListener( this );
			}
		}
		fireCustomEvent( e );
	}

	private void fireCustomEvent( ActionEvent e ) {
		if ( customListener != null ) {
			customListener.actionPerformed( 
					new ActionEvent( this, e.getID(), e.getActionCommand() ) );
		}
	}
	
	private void fireCustomEvent() {
		fireCustomEvent( new ActionEvent( this, 0, "" ) );
	}
	
	////////////////////////////////////////////

	FileTextFieldHandler handler = null;

	public void setFileTextFieldHandler( FileTextFieldHandler handler ) {
		this.handler = handler;
		btn2.setEnabled( 
				handler == null || 
				handler.isCreateResourceManaged() );
		btn3.setEnabled( handler == null || 
				handler.isDeleteResourceManaged() );	
	}

	public FileTextFieldHandler getFileTextFieldHandler() {
		if ( handler == null )
			handler = new DefaultTextFieldHandler();
		return handler;
	}
	
	class SystemTransferHandler extends TransferHandler {

		public boolean canImport( JComponent comp, DataFlavor[] transferFlavors ) {
			return transferFlavors[ 0 ].isFlavorJavaFileListType() || transferFlavors[ 0 ].isFlavorTextType();
		}

		public boolean importData(JComponent arg0, Transferable arg1) {
			try {

				try {
					String fileName = ( String  )arg1.getTransferData( DataFlavor.stringFlavor );
					if ( fileName != null && ( arg0 instanceof JTextComponent ) ) {
						setText( fileName );
						return true;
					}
				} catch( UnsupportedFlavorException e ) {
				}

				java.util.List list = ( java.util.List )arg1.getTransferData(
						DataFlavor.javaFileListFlavor
					);
				if ( list != null && list.size() == 1 ) {
					String fileName = list.get( 0 ).toString();
					if ( arg0 instanceof JTextComponent ) {
						setText( fileName );
					}
					return true;
				}
			} catch (UnsupportedFlavorException e) {
			} catch (IOException e) {
			}
			return false;
		}
		
	}

}

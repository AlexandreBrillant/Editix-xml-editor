package com.japisoft.editix.ui;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.filechooser.FileFilter;

import com.japisoft.editix.document.DocumentModel;
import com.japisoft.xmlpad.CaretListener;
import com.japisoft.xmlpad.DocumentStateListener;
import com.japisoft.xmlpad.LocationEvent;
import com.japisoft.xmlpad.LocationListener;

import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.XMLDocumentInfo;

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
public class Factory {

	public static XMLContainer buildNewContainer() {
		return buildNewContainer(false);
	}

	public static XMLContainer buildNewContainer( String file ) {
		int i = file.lastIndexOf( "." );
		String ext = null;
		if ( i > -1 )
			ext = file.substring( i + 1 );
		XMLContainer container = new EditixXMLContainer();
		XMLDocumentInfo info = DocumentModel.getDocumentForExt( ext );
		if ( info != null )
			container.setDocumentInfo( info );
		return container;
	}

	/** @return a new XMLContainer */
	public static XMLContainer buildNewContainer(boolean def) {
		XMLContainer container = new EditixXMLContainer();
		if (def) {
			container.setDocumentInfo( DocumentModel.getDefaultDocument() );
			container.getDocumentInfo().setCurrentDocumentLocation("Empty.xml");
		}
		return container;
	}

	private static JFileChooser fileChooser = null;

	/** @return a FileChooser for opening file */
	public static JFileChooser buildFileChooser() {
		if (fileChooser != null)
			return fileChooser;
		JFileChooser fc = new JFileChooser();
		fileChooser = fc;
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setMultiSelectionEnabled(true);
		
		FileFilter first = null;
		
		// Create one filter by documentInfo
		for ( int i = 0; i < DocumentModel.getDocumentCount(); i++ ) {
			XMLDocumentInfo info = DocumentModel.getDocumentAt( i );
			fc.addChoosableFileFilter( info.getFileFilter() );
			if ( first == null )
				first = info.getFileFilter();
		}
		fc.setFileFilter( first );
		return fc;
	}

	private static JFileChooser projectChooser = null;

	/** @return a FileChooser for opening a project file */
	public static JFileChooser buildProjectFileChooser() {
		if (projectChooser != null)
			return projectChooser;
		JFileChooser fc = new JFileChooser();
		projectChooser = fc;
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setFileFilter(new FileFilter() {
			public String getDescription() {
				return "*.pre (editix project file)";
			}
			public boolean accept(File f) {
				return ( f.isDirectory() || f.toString().toLowerCase().endsWith(".pre") );
			}
		});
		return fc;
	}

	public static void buildAndShowErrorDialog(String message) {
		JOptionPane.showMessageDialog(
			EditixFrame.THIS,
			message,
			"Error",
			JOptionPane.ERROR_MESSAGE);
	}

	public static String buildAndShowInputDialog( String title ) {
		return JOptionPane.showInputDialog(
			EditixFrame.THIS,
			title );
	}

	public static void buildAndShowInformationDialog(String message) {
		JOptionPane.showMessageDialog(
			EditixFrame.THIS,
			message,
			"Info",
			JOptionPane.INFORMATION_MESSAGE );
	}

	public static int buildAndShowChoiceDialog(String message) {
		return JOptionPane.showConfirmDialog(
			EditixFrame.THIS,
			message,
			"Choice",
			JOptionPane.YES_NO_OPTION);
	}

	/////////////////////////////////////////////////////////////////////////////////

	static class EditixXMLContainer extends XMLContainer implements 
			LocationListener, DocumentStateListener, CaretListener {
		
		public EditixXMLContainer() {
			super();
			setToolBarAvailable( false );
			setStatusBarAvailable( false );
			setPopupAvailable( false );
			setTreePopupAvailable( false );
		}

		public void addNotify() {
			setLocationListener( this );	
			addDocumentStateListener( this );
			setCaretListener( this );
		}

		public void removeNotify() {
			unsetLocationListener();
			removeDocumentStateListener( this );
			unsetCaretListener();
		}

		public void setUIReady( boolean ok ) {
			super.setUIReady( ok );
			if ( ok )
				addNotify();
			else
				removeNotify();
		}
		
		public void locationChanged(LocationEvent e) {
			EditixStatusBar.ACCESSOR.setXPathLocation( e.getXPathLocation() );
		}
		public void documentModified( XMLContainer source ) {
		}

		public void newDocument( XMLContainer source ) {
		}
		
		public void errorFound(
			String errorMessage,
			int line,
			boolean temporary) {
			EditixStatusBar.ACCESSOR.setError( null, true, null, errorMessage, line );
			int i = EditixFrame.THIS.getMainTabbedPane().getSelectedIndex();
			//EditixFrame.ACCESSOR.getMainTabbedPane().setForegroundAt( i, Color.RED );
			if ( temporary ) {
				getEditor().highlightErrorLine( line );
			}
		}

		public JPopupMenu getCurrentPopup() {
			return EditixFrame.THIS.getBuilder().getPopup( "EDITOR" );
		}

		protected boolean useCustomPopupMenu() {
			return true;
		}


		public void noErrorFound(boolean temporary) {
			EditixStatusBar.ACCESSOR.setError( null, true, null, null, 0 );
			int i = EditixFrame.THIS.getMainTabbedPane().getSelectedIndex();
			//EditixFrame.ACCESSOR.getMainTabbedPane().setForegroundAt( i, Color.BLACK );
			if ( temporary )
				getEditor().removeHighlightedErrorLine();
		}

		public void caretLocation(int col, int line) {
			EditixStatusBar.ACCESSOR.setLocation( col, line );
		}

	}
	
}

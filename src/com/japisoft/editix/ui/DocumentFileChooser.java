package com.japisoft.editix.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.HeadlessException;
import java.io.File;

import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileView;

import com.japisoft.editix.document.DocumentModel;
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
public class DocumentFileChooser extends JFileChooser implements SelectableEncoding {

	private JComboBox combo;

	public DocumentFileChooser() {
		super();
		FileFilter first = null;
		// Create one filter by documentInfo
		for (int i = 0; i < DocumentModel.getDocumentCount(); i++) {
			XMLDocumentInfo info = DocumentModel.getDocumentAt(i);
			addChoosableFileFilter(info.getFileFilter());
			if (first == null)
				first = info.getFileFilter();
		}
		init();
		setFileFilter( first );
	}
	
	public DocumentFileChooser( XMLDocumentInfo doc ) {
		init();
		setFileFilter( doc.getFileFilter() );
	}

	private void init() {
		setFileSelectionMode( JFileChooser.FILES_ONLY );
		setMultiSelectionEnabled(true);
		setFileView( new DocumentFileView() );		
	}
	
	protected JDialog createDialog(Component parent) throws HeadlessException {
		JDialog dialog = super.createDialog( parent );
		JPanel panel = new JPanel();
		panel.setLayout( new BorderLayout() );
		panel.setBorder( new EmptyBorder(10, 10, 10, 10 ) );
		JLabel lbl = new JLabel( 
			"Be sure to select the right file filter (XML, XSLT...) before opening a file." 
		);
		lbl.setForeground( Color.DARK_GRAY );
		panel.add( lbl, BorderLayout.CENTER );
		JPanel encoPanel = new JPanel();
		encoPanel.setLayout( new BorderLayout() );
		combo = EncodingMenuBuilder.encodingComboBox();
		encoPanel.add( new JLabel( "Encoding :" ), BorderLayout.CENTER );
		encoPanel.add( combo, BorderLayout.SOUTH );
		panel.add( encoPanel, BorderLayout.SOUTH );
		dialog.getContentPane().add( BorderLayout.SOUTH, panel );
		return dialog;
	}

	public int showDialog( Component parent, String approveButtonText ) throws HeadlessException {
		return super.showDialog( parent, approveButtonText );
	}
	
	public String getSelectedEncoding() {
		return ( String )combo.getSelectedItem();
	}
	
	class DocumentFileView extends FileView {

		@Override
		public Icon getIcon( File f ) {
			if ( f.isFile() ) {
				String name = f.getName();
				int _i = name.lastIndexOf( "." );
				if ( _i > -1 ) {
					String ext = name.substring( _i + 1 );
					for ( int i = 0; i < DocumentModel.getDocumentCount(); i++ ) {
						if ( DocumentModel.getDocumentAt( i ).matchFileExt( ext ) ) {
							return DocumentModel.getDocumentAt( i ).getDocumentIcon();
						}
					}
				}
				return UIManager.getIcon("FileView.fileIcon");
			}
			return UIManager.getIcon("FileView.directoryIcon");
		}

	}

}

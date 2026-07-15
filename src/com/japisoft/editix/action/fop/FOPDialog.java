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

package com.japisoft.editix.action.fop;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.windows.EditixDialog;
import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.ui.text.FileTextField;
import com.japisoft.framework.ui.toolkit.BrowserCaller;
import com.japisoft.xmlpad.XMLContainer;

import net.miginfocom.swing.MigLayout;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * */
public class FOPDialog extends EditixDialog implements ActionListener {

	public FOPDialog() {
		super(
			"FOP",
			"FOP usage",
			"Transform your FO document to multiple outputs. Choose an output file and save it inside a project for reusing it the next time");
		jbInit();

		cbRenderer.setModel(
			new DefaultComboBoxModel(
				new String[] { 
					"PDF",
					"MIF",
					"PRINT",
					"PCL",
					"PS",
					"TXT",
					"RTF",
					"SVG" } ) );
	}

	protected Dimension getDefaultSize() {
		return new Dimension( 350, 250 );
	}
	
	JLabel lblRenderer = new JLabel();
	JComboBox cbRenderer = new JComboBox();
	FileTextField fileSelector = new FileTextField( "Output: ", null, (String[])null );

	JCheckBox cbExternalProg = null;

	private void jbInit() {
		lblRenderer.setText("Renderer type: ");
		
		fileSelector.checkMode( false );
		
		this.getContentPane().setLayout( new MigLayout( "fill,ins 10" ) );

		this.setLocale(java.util.Locale.getDefault());
		
		this.getContentPane().add(
			lblRenderer, "span,wrap" );
			
		this.getContentPane().add(
			cbRenderer,
			"grow,span,wrap" );

		this.getContentPane().add(
			fileSelector,
			"grow,span,wrap" );
		
		if ( BrowserCaller.isDesktopSupported() ) {
			cbExternalProg = new JCheckBox( "Show the result" );
			this.getContentPane().add(
					cbExternalProg,
					"grow,span,wrap" );
		}
	}

	public void beforeShowing() {
		super.beforeShowing();
		cbRenderer.addActionListener( this );
	}

	public void beforeClosing() {
		super.beforeClosing();
		cbRenderer.removeActionListener( this );
	}

	public void actionPerformed( ActionEvent e ) {
		if ( e.getSource() == cbRenderer ) {
			checkSelection();
		}
	}

	public void resetFile() {
	}

	private void checkSelection() {
		fileSelector.setEnabled(
			!( cbRenderer.getSelectedIndex() == 1 ||
			cbRenderer.getSelectedIndex() == 3 ) );
		
		if ( "PDF".equals( cbRenderer.getSelectedItem() ) ) {
			fileSelector.setFileExt( "pdf" );
		} else
		if ( "PS".equals( cbRenderer.getSelectedItem() ) ) {
			fileSelector.setFileExt( "ps" );
		} else
		if ( "RTF".equals( cbRenderer.getSelectedItem() ) ) {
			fileSelector.setFileExt( "rtf" );
		} else
		if ( "TXT".equals( cbRenderer.getSelectedItem() ) ) {
			fileSelector.setFileExt( "txt" );
		} else
		if ( "SVG".equals( cbRenderer.getSelectedItem() ) ) {
			fileSelector.setFileExt( "svg" );
		} else
			fileSelector.setFileExt( (String[])null );
	}
	
	private String getFileExtension() {
		if ( "PDF".equals( cbRenderer.getSelectedItem() ) ) {
			return ".pdf";
		} else
		if ( "PS".equals( cbRenderer.getSelectedItem() ) ) {
			return ".ps";
		} else
		if ( "RTF".equals( cbRenderer.getSelectedItem() ) ) {
			return ".rtf";
		} else
		if ( "TXT".equals( cbRenderer.getSelectedItem() ) ) {
			return ".txt";
		} else
		if ( "SVG".equals( cbRenderer.getSelectedItem() ) ) {
			return ".svg";
		}
		return "";
	}
	
	public void init( XMLContainer container ) {
		cbRenderer.setSelectedItem( 
				container.getProperty( "fo.render", "PDF" ) );
		fileSelector.setFilePath( 
				"" + container.getProperty( "fo.output", "" ) );
		if ( cbExternalProg != null ) {
			cbExternalProg.setSelected( 
				"true".equals( container.getProperty( "fo.viewer", "false" ) ) );
		}
		checkSelection();
	}

	public void store( XMLContainer container ) {
		container.setProperty( "fo.render", cbRenderer.getSelectedItem() );
		container.setProperty( "fo.ok", "true" );
		
		if ( fileSelector.isEnabled() ) {
			
			// Check for relative access
			String f = fileSelector.getText();
			if ( f != null ) {	// ?
				if ( f.indexOf( '/' ) == -1 && 
						f.indexOf( '\\' ) == -1 ) {
					// Relative case
					String docLocation = container.getCurrentDocumentLocation();
					if ( docLocation != null ) {
						try {
							File __ = new File( new File( docLocation ).getParentFile(), f );
							f = __.toString();
						} catch (RuntimeException e) {
						}
					}
				}
			} else {
				// No output file
				return;
			}
			
			// Check for file extension
			int i = f.lastIndexOf( '/' );
			if ( i == -1 )
				i = f.lastIndexOf( '\\' );
			String tmpName = null;
			if ( i == -1 )
				tmpName = f;
			else
				tmpName = f.substring( i + 1 );
			if ( tmpName.indexOf( '.' ) ==-1 ) {
				f = f + getFileExtension();
			}
			container.setProperty( "fo.output", f );
		}
		else
			container.setProperty( "fo.output", "" );

		if ( cbExternalProg != null ) {
			container.setProperty( "fo.viewer", "" + cbExternalProg.isSelected() );
		}
	}

}	

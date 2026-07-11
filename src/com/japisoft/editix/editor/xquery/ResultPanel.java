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

package com.japisoft.editix.editor.xquery;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.text.html.HTMLEditorKit;

import com.japisoft.editix.ui.xslt.Factory;
import com.japisoft.editix.ui.xslt.LineSelectionListener;
import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.app.toolkit.Toolkit;
import com.japisoft.framework.xml.XMLFileData;
import com.japisoft.framework.xml.XMLToolkit;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0 */
public class ResultPanel extends JPanel implements ActionListener {
	private JComboBox comboSource;
	private CardLayout resultLayout;
	private JTextArea textAreaResult;
	private JEditorPane htmlResult;
	private JPanel panel5; 
	private Factory factory;

	JTabbedPane tpResultDebug;

	public ResultPanel( 
			Factory factory, 
			boolean debugMode, 
			LineSelectionListener container ) {
		this.factory = factory;

		JPanel panel4 = null;
		if ( !debugMode ) {
			panel4 = this;
		} else {
			panel4 = new JPanel();
			tpResultDebug = new JTabbedPane( JTabbedPane.BOTTOM );
			tpResultDebug.addTab( "Result", panel4 );
			setLayout( new BorderLayout() );
		}

		panel4.setLayout( new BorderLayout() );
		panel4.add(
			BorderLayout.NORTH,
			comboSource = new JComboBox( new String[] { "Source", "HTML 3.2" } ) );

		panel5 = new JPanel();
		panel5.setLayout( resultLayout = new CardLayout() );
		panel5.add( new JScrollPane( textAreaResult = new JTextArea() ), "source" );
		panel5.add( new JScrollPane( htmlResult = new JEditorPane() ), "html" );

		panel4.add( panel5, BorderLayout.CENTER );

		resultLayout.show( panel5, "source" );

		if ( debugMode )
			add( 
					BorderLayout.CENTER, 
					tpResultDebug );

		textAreaResult.setEditable( false );
		htmlResult.setEditable( false );
		htmlResult.setEditorKit( new HTMLEditorKit() );		
	}

	public void addNotify() {
		super.addNotify();
		comboSource.addActionListener( this );
	}
	
	public void removeNotify() {
		super.removeNotify();
		comboSource.removeActionListener( this );
	}

	private String lastResult = null;
	private String lastEncoding = null;
	
	public void loadResultFile( String result, String fileEncoding ) {
		this.lastResult = result;
		this.lastEncoding = fileEncoding;

		// Reset all
		if ( comboSource.getSelectedIndex() == 1 )
			htmlResult.setText( "" );
		else
		if ( comboSource.getSelectedIndex() == 0 )
			textAreaResult.setText( "" );

		try {
			if (result != null) {
				if ( comboSource.getSelectedIndex() == 0 ) { // Source
					XMLFileData xfd = XMLToolkit.getContentFromURI(result,Toolkit.getCurrentFileEncoding());
					textAreaResult.setText(
							xfd.getContent()
					);
					resultLayout.show(panel5, "source" );
					textAreaResult.setCaretPosition( 0 );
				} else 
				if ( comboSource.getSelectedIndex() == 1 ) { // HTML 3.2
					htmlResult.setContentType( "text/html" );
					XMLFileData xfd = XMLToolkit.getContentFromURI(result,Toolkit.getCurrentFileEncoding());					
					htmlResult.setText(
						xfd.getContent() );
					resultLayout.show(panel5, "html" );
					htmlResult.setCaretPosition( 0 );
				}
			}
		} catch (Throwable th) {
			ApplicationModel.debug( th );
			factory.buildAndShowErrorDialog(
				"Can't show the result file " + result);
		}
	}

	public void actionPerformed( ActionEvent e ) {
		loadResultFile( lastResult, lastEncoding );
	}

	public void dispose() {
/*		if ( debugContainer != null ) {
			debugContainer.dispose();
		}
		debugContainer = null; */
		factory = null;
		panel5 = null;
	}
}

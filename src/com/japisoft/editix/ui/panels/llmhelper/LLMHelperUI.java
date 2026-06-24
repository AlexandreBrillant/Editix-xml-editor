// Editix XML Editor
// https://www.editix.com
// Copyright (c) 2025 Alexandre Brillant
// 
// For non-commercial usage :
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
// For commercial use or integration into proprietary software :
// A commercial license is required. Visit https://www.editix.com for details.

package com.japisoft.editix.ui.panels.llmhelper;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;

import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.editix.ui.llm.LLMRunner;
import com.japisoft.framework.dialog.DialogManager;
import com.japisoft.framework.dialog.actions.DialogActionModel;
import com.japisoft.framework.llm.LLM;
import com.japisoft.framework.llm.LLMManager;
import com.japisoft.xmlpad.XMLContainer;

import net.miginfocom.swing.MigLayout;

public class LLMHelperUI extends JPanel implements ActionListener {
	
	public static String SCOPE_DEFAULT = "DEFAULT";
	public static String SCOPE_SELECTION = "SELECTION";
	public static String SCOPE_CURRENTNODE = "CURRENT NODE";
	public static String SCOPE_CURRENTDOCUMENT = "CURRENT DOCUMENT";

	public static final String[] SCOPES = {
		SCOPE_DEFAULT,
		SCOPE_SELECTION,
		SCOPE_CURRENTNODE,
		SCOPE_CURRENTDOCUMENT
	};

	private JButton btRun = null;
	private JComboBox<LLM> cbLLM = null;
	private JTextArea txtPrompt = null;
	private JComboBox<String> cbScope = null;
	
	LLMHelperUI() {
		setLayout( new MigLayout( "fill, insets 5", "[grow]", "[][][][][][grow 50][][]" ) );
		add( new JLabel( "Choose your LLM" ), "wrap" );
		List<LLM> llms = LLMManager.instance();		
		add( cbLLM = new JComboBox<LLM>( llms.toArray( new LLM[ llms.size() ]) ), "wrap, grow" );
		add( new JLabel( "Scope" ), "wrap" );
		add( cbScope = new JComboBox<String>( SCOPES ), "wrap, grow" );
		add( new JLabel( "Your prompt" ), "wrap" );
		add( new JScrollPane( txtPrompt = new JTextArea() ), "wrap, grow, pushy" );
		add( new JSeparator(), "wrap" );
		add( btRun = new JButton( "Run" ) );
	}

	public void addNotify() {
		super.addNotify();
		btRun.addActionListener( this );
	}	

	public void removeNotify() {
		super.removeNotify();
		btRun.removeActionListener( this );
	}

	void updateForXMLContainer( XMLContainer container ) {}

	public void actionPerformed(ActionEvent e) {
		String scope = (String)cbScope.getSelectedItem();
		XMLContainer container = EditixFrame.THIS.getSelectedContainer();
		if ( e.getSource() == btRun ) {
			String prompt = txtPrompt.getText();
			if ( "".equals( prompt ) )
				EditixFactory.buildAndShowInformationDialog( "No prompt ?" );
			else {
				LLM currentLLM = (LLM)cbLLM.getSelectedItem();
				if ( currentLLM == null ) {
					EditixFactory.buildAndShowWarningDialog( "No LLM found ?" );
				} else {
					btRun.setEnabled( false );
					new LLMRunner( currentLLM, ( response ) -> { 
						btRun.setEnabled( true );
						DialogManager.showDialog( EditixFrame.THIS, "LLM response", "Response", "Manage LLM response, if the LLM generates a document just click on 'Create new document'", null, new LLMResponsePanel( scope, response ), DialogActionModel.getDefaultDialogOkActionModel(), new Dimension( 600, 500 ) );
					}).run( 
						this,
						txtPrompt.getText() 
					);
				}
			}
		}
	}

	public static void main( String[] args ) {
		JFrame f = new JFrame();
		f.setSize( new Dimension( 200, 200 ));
		f.add( new LLMHelperUI() );
		f.setVisible( true );
	}

}


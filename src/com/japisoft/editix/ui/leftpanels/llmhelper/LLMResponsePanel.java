// Editix XML Editor
// https://www.editix.com
// Copyright (c) 2026 Alexandre Brillant
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

package com.japisoft.editix.ui.leftpanels.llmhelper;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;

import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.xml.XMLFileData;
import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.xmlpad.XMLContainer;

import net.miginfocom.swing.MigLayout;

public class LLMResponsePanel extends JPanel implements ActionListener {

	private JButton btnUpdate;
	private JButton btnCopy;
	private JButton btnInsert;
	private JButton btnNewDocument;
	
	private JTextArea textArea;

	private String response_doc_type = null;
	private String response_doc_content = null;

	private String scope;
	
	public LLMResponsePanel( String scope, String response ) {
		this.scope = scope;
		setLayout( new MigLayout( "fill", "[grow]", "[][grow,fill]" ) );
		JToolBar tb = new JToolBar();
		
		if ( !LLMHelperUI.SCOPE_DEFAULT.equals( scope ) ) {
			tb.add( btnUpdate = new JButton( "Replace" ) );
			tb.addSeparator();
		}

		tb.add( btnCopy = new JButton( "Copy" ) );
		tb.add( btnInsert = new JButton( "Insert" ) );

		String regex = "```(?<type>\\w+)\\s*\\n(?<content>.*?)```";
		Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
		Matcher matcher = pattern.matcher( response );

		if ( matcher.find() ) {
			response_doc_type = matcher.group("type").trim();
			response_doc_content = matcher.group("content").trim();

			tb.addSeparator();			
			tb.add( btnNewDocument = new JButton( "Create new document..." ) );			
		}

		add( tb, "grow,wrap" );
		add( new JScrollPane( textArea = new JTextArea( response ) ), "grow,push" );
	}

	@Override
	public void addNotify() {
		super.addNotify();
		btnCopy.addActionListener( this );
		btnInsert.addActionListener( this );
		
		if ( btnNewDocument != null )
			btnNewDocument.addActionListener( this );
		
		if ( btnUpdate != null )
			btnUpdate.addActionListener( this );
	}

	@Override
	public void removeNotify() {
		super.removeNotify();
		btnCopy.removeActionListener( this );
		btnInsert.removeActionListener( this );

		if ( btnNewDocument != null ) 
			btnNewDocument.removeActionListener( this );

		if ( btnUpdate != null )
			btnUpdate.removeActionListener( this );
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		XMLContainer container = EditixFrame.THIS.getSelectedContainer();
		if ( e.getSource() == btnUpdate ) {
			String newContent = textArea.getText();
			if ( response_doc_content != null ) {
				if ( LLMHelperUI.SCOPE_CURRENTDOCUMENT.equals( scope ) || 
						LLMHelperUI.SCOPE_CURRENTNODE.equals( scope ) )
					newContent = response_doc_content;
			}
		
			if ( container != null ) {
				if ( container.getEditor().getSelectionEnd() > container.getEditor().getSelectionStart() ) {
					if ( LLMHelperUI.SCOPE_CURRENTDOCUMENT.equals( scope ) ) {
						if ( !EditixFactory.buildAndShowConfirmDialog( "Update the current document ?" ) ) {
							return;
						}
					}
					container.getEditor().replaceSelection( newContent );
				} else {
					EditixFactory.buildAndShowWarningDialog( "No selection part ?" );
				}
			}
		} else
		if ( e.getSource() == btnCopy ) {
			textArea.selectAll();
			textArea.copy();	
			textArea.setSelectionEnd( 0 );
		} else
		if ( e.getSource() == btnInsert ) {
			String finalText = textArea.getText();
			
			if ( container == null )
				EditixFactory.buildAndShowWarningDialog( "No current document ?");
			else
				container.insertText( finalText );
		} else
		if ( e.getSource() == btnNewDocument ) {
			if ( response_doc_type == null ) {
				EditixFactory.buildAndShowWarningDialog( "Cannot detect a new document inside the LLM response ?" );
			} else {
				XMLFileData content = new XMLFileData("UTF-8", response_doc_content );
				EditixFrame.THIS.addContainer(
					EditixFactory.buildNewContainerWithType(response_doc_type, content ) 
				);
			}
		}
	}

	public static void main( String[] args ) {
		ApplicationModel.SHORT_APPNAME = "test";
		JFrame f = new JFrame();
		f.add( new LLMResponsePanel( "NOTHING", "dlksjf lsfkjsf lksfj lsfkj " ) );
		f.pack();
		f.setVisible( true );
	}

}

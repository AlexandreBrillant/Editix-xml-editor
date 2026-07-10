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

package com.japisoft.editix.ui.leftpanels.llmhelper;

import java.awt.Dimension;
import java.awt.TextArea;
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
import javax.swing.text.BadLocationException;

import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.editix.ui.llm.config.LLMRunner;
import com.japisoft.framework.dialog.DialogManager;
import com.japisoft.framework.dialog.actions.DialogActionModel;
import com.japisoft.framework.llm.LLM;
import com.japisoft.framework.llm.LLMManager;
import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.xmlpad.XMLContainer;

import net.miginfocom.swing.MigLayout;

public class LLMHelperUI extends JPanel implements ActionListener {
	
	public static final String SCOPE_DEFAULT = "DEFAULT";
	public static final String SCOPE_SELECTION = "SELECTION";
	public static final String SCOPE_CURRENTNODE = "CURRENT NODE";
	public static final String SCOPE_CURRENTDOCUMENT = "CURRENT DOCUMENT";

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

	private String getContext( String scope, XMLContainer container ) {
		switch( scope ) {
			case SCOPE_CURRENTNODE:
				FPNode node = container.getCurrentNode();
				if ( node != null ) {
					int start = node.getStartingOffset();
					int end = node.getStoppingOffset();
					if ( end > start && start >= 0 ) {
						container.getEditor().select( start, end);
					}
				}
			case SCOPE_CURRENTDOCUMENT:			
				if ( SCOPE_CURRENTDOCUMENT.equals( scope ) )
					container.getEditor().selectAll();
			case SCOPE_SELECTION:
				return container.getEditor().getSelectedText();
		}
		return "";
	}

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

					String userPrompt = txtPrompt.getText();
					
					if ( !SCOPE_DEFAULT.equals( scope ) ) {
						
						String contextType = null;
						String instructions = null;
						String contextText = getContext( scope, container );
						if ( null == contextText || "".equals( contextText ) ) {
							EditixFactory.buildAndShowWarningDialog( "Can't find your selected text,node or document ?" );
							return;
						}

						switch( scope ) {
					        case SCOPE_SELECTION:
					            contextType = "selection";
					            instructions = "- Modify **only the selected XML fragment**.\n" +
					                           "- Return **only the modified fragment**, no extra text or tags.\n" +
					                           "- Preserve the surrounding structure if applicable.";
					            break;
	
					        case SCOPE_CURRENTNODE:
					            contextType = "current node";
					            instructions = "- Modify **only the current XML node** (including its children).\n" +
					                           "- Return the **entire modified node** (with children).\n" +
					                           "- Do NOT modify parent/sibling nodes.";
					            break;
	
					        case SCOPE_CURRENTDOCUMENT:
					            contextType = "full document";
					            instructions = "- Modify the **entire XML document**.\n" +
					                           "- Return the **full modified document**.\n" +
					                           "- Ensure the output is a valid XML document (with <?xml...> if present).";
					            break;
						}
					            
			            userPrompt = String.format(
		                    "[CONTEXT: %s]%n" +
		                    "[CONTENT:%n%s%n]%n" +
		                    "[USER PROMPT: %s]%n" +
		                    "[INSTRUCTIONS:%n%s%n]",
		                    contextType,
		                    getContext( scope, container ),
		                    userPrompt,
		                    instructions
		                );

					}

					btRun.setEnabled( false );

					new LLMRunner( currentLLM, ( response ) -> { 
						btRun.setEnabled( true );
						DialogManager.showDialog( EditixFrame.THIS, "LLM response", "Response", "Manage LLM response, use 'Replace' to update the current selection with the LLMM response.", null, new LLMResponsePanel( scope, response ), DialogActionModel.getDefaultDialogOkActionModel(), new Dimension( 600, 500 ) );
					}).run( 
						this,
						userPrompt 
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


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

package com.japisoft.editix.ui.leftpanels.llmhelper;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.text.BadLocationException;

import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.llm.config.LLMRunner;
import com.japisoft.editix.ui.windows.EditixFrame;
import com.japisoft.framework.llm.LLM;
import com.japisoft.framework.llm.LLMManager;
import com.japisoft.framework.llm.LLMToolkit;
import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.xmlpad.XMLContainer;

import net.miginfocom.swing.MigLayout;

public class LLMHelperUI extends JPanel implements ActionListener {
	
	public static final String SCOPE_DEFAULT = "DEFAULT";
	public static final String SCOPE_CURRENTLINE = "CURRENT LINE";
	public static final String SCOPE_CURRENTSELECTION = "CURRENT SELECTION";
	public static final String SCOPE_CURRENTTEXT = "XML/CURRENT TEXT";
	public static final String SCOPE_CURRENTNODE = "XML/CURRENT NODE";
	public static final String SCOPE_CURRENTDOCUMENT = "CURRENT DOCUMENT";

	public static final String[] SCOPES = {
		SCOPE_DEFAULT,
		SCOPE_CURRENTLINE,
		SCOPE_CURRENTSELECTION,
		SCOPE_CURRENTTEXT,
		SCOPE_CURRENTNODE,
		SCOPE_CURRENTDOCUMENT
	};

	private JButton btRun = null;
	private JButton btReplace = null;
	private JButton btInsert = null;
	private JButton btCopy = null;	
	private JComboBox<LLM> cbLLM = null;
	private JTextArea txtPrompt = null;
	private JTextArea txtResponse = null;
	private JComboBox<String> cbScope = null;

	LLMHelperUI() {
		setLayout( new MigLayout( "fill, insets 5", "[grow]", "[][][][][][grow 50][][]" ) );
		add( new JLabel( "Choose your LLM" ), "wrap" );
		List<LLM> llms = LLMManager.instance();		
		add( cbLLM = new JComboBox<LLM>( llms.toArray( new LLM[ llms.size() ]) ), "wrap, grow" );
		add( new JLabel( "Scope" ), "wrap" );
		add( cbScope = new JComboBox<String>( SCOPES ), "wrap, grow" );

		add( new JLabel( "Your prompt" ), "wrap" );

		JSplitPane sp  = new JSplitPane( JSplitPane.VERTICAL_SPLIT );
		sp.setDividerLocation( 200 );
		sp.setLeftComponent( new JScrollPane( txtPrompt = new JTextArea() ) );
		sp.setRightComponent( new JScrollPane( txtResponse = new JTextArea() ) );

		add( sp, "wrap, grow, pushy" );
		add( new JSeparator(), "wrap" );
		
		JToolBar tb = new JToolBar();
		tb.setFloatable( false );
		tb.add( btRun = new JButton( "Run" ) );
		tb.addSeparator();
		tb.add( btReplace = new JButton( "Replace" ) );
		tb.add( btInsert = new JButton( "Insert" ) );
		tb.add( btCopy = new JButton( "Copy" ) );

		add( tb );

		txtPrompt.setWrapStyleWord( false );
		txtPrompt.setLineWrap( true );		
		
		txtResponse.setWrapStyleWord( false );
		txtResponse.setLineWrap( true );		
	}

	public void addNotify() {
		super.addNotify();
		btRun.addActionListener( this );
		btReplace.addActionListener( this );
		btInsert.addActionListener( this );
		btCopy.addActionListener( this );

		txtPrompt.getInputMap( WHEN_FOCUSED ).put( KeyStroke.getKeyStroke( "ENTER"), "run.it" );
		txtPrompt.getActionMap().put( "run.it", new AbstractAction() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				run( false );
			}
		} );

		txtPrompt.getInputMap( WHEN_FOCUSED ).put( KeyStroke.getKeyStroke( "ctrl ENTER"), "run.replace.it" );
		txtPrompt.getActionMap().put( "run.replace.it", new AbstractAction() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				run( true );
			}
		} );		
	}

	public void removeNotify() {
		super.removeNotify();
		btRun.removeActionListener( this );
		btReplace.removeActionListener( this );
		btInsert.removeActionListener( this );
		btCopy.removeActionListener( this );
		
		txtPrompt.getInputMap( WHEN_FOCUSED ).remove( KeyStroke.getKeyStroke( "ENTER" ) );
		txtPrompt.getActionMap().remove( "run.it" );
		txtPrompt.getInputMap( WHEN_FOCUSED ).remove( KeyStroke.getKeyStroke( "ctrl ENTER" ) );
		txtPrompt.getActionMap().remove( "run.replace.it" );
		
	}

	public void actionPerformed(ActionEvent e) {
		String scope = (String)cbScope.getSelectedItem();
		XMLContainer container = EditixFrame.THIS.getSelectedContainer();
		if ( e.getSource() == btRun ) {
			run( scope, container, false );
		} else
		if ( e.getSource() == btCopy ) {
			txtResponse.selectAll();
			txtResponse.copy();
			EditixFactory.buildAndShowInformationDialog( "Copy done" );
		} else
		if ( e.getSource() == btInsert ) {
			container.insertText( txtResponse.getText() );
		} else
		if ( e.getSource() == btReplace ) {
			replace( scope, container );
		}
	}

	private void replace( String scope, XMLContainer container ) {
		if ( SCOPE_CURRENTSELECTION.equals( scope ) ) {
			container.replaceSelection( txtResponse.getText() );
		} else
		if ( SCOPE_CURRENTLINE.equals( scope ) ) {
			container.replaceCurrentLine( txtResponse.getText() );
		} else {		
			int lastCaret = container.getCaretPosition();
			if ( context_end > context_start ) {
				container.getEditor().requestFocus();
				container.getEditor().select( context_start, context_end );
			}
			if ( container.getEditor().getSelectionStart() < 0 || container.getEditor().getSelectionEnd() < 0 ) {
				EditixFactory.buildAndShowWarningDialog( "No selection part ?" );
				return;
			}
			if ( scope.equals( SCOPE_CURRENTDOCUMENT ) ) {
				if ( !EditixFactory.buildAndShowConfirmDialog( "Replace the full document ?" ))
					return;
			}
			container.getEditor().replaceSelection( txtResponse.getText() );					
			container.setCaretPosition( lastCaret );
		}
	}
	
	public void requestTextFocus() {
		txtPrompt.requestFocus();
	}

	void updateForXMLContainer( XMLContainer container ) {
		txtPrompt.requestFocus();
	}

	private int context_start;
	private int context_end;
	
	private String getContext( String scope, XMLContainer container ) {
		context_start = 0;
		context_end = 0;

		switch( scope ) {
			case SCOPE_CURRENTSELECTION: {
				String selection = container.getSelectedText();
				if ( !"".equals( selection ) || selection == null ) {
					return null;
				}
				return selection;
			}
			case SCOPE_CURRENTLINE : {
				return container.getCurrentLine();
			}
			case SCOPE_CURRENTNODE: {
				FPNode node = container.getCurrentElementNode();
				if ( node != null ) {
					int start = node.getStartingOffset();
					int end = node.getStoppingOffset();
					if ( end > start && start >= 0 ) {
						try {
							context_start = start;
							context_end = end + 1;
							return container.getDocument().getText( start, end - start );
						} catch( BadLocationException ble ) {
						}
					}						
				}
			}
			case SCOPE_CURRENTDOCUMENT: {
				context_start = 0;
				context_end = container.getDocument().getLength();					
				return container.getText();
			}
			case SCOPE_CURRENTTEXT: {
				FPNode node = container.getCurrentNode();

				if ( node != null ) {					
					int start = node.getStartingOffset();
					int end = node.getStoppingOffset();
					
					if ( end > start && start >= 0 ) {
						try {
							String textPart = container.getDocument().getText( start, end - start ); 							
							if ( node.getType() == FPNode.TAG_NODE ) {
								int delimiter1 = textPart.indexOf( ">" );
								int delimiter2 = textPart.indexOf( "<", delimiter1 + 1 );
								if ( delimiter1 > 0 && delimiter2 > 0 ) {
									textPart = textPart.substring( delimiter1 + 1, delimiter2 );
									start += delimiter1 + 1;
									end = start + ( delimiter2 - delimiter1 ) - 1;
								}
							}
							context_start = start;
							context_end = end;
							return textPart; 
						} catch( BadLocationException ble ) {
						}
					}						
				}
			}
		}
		return "";
	}

	private void run( boolean replaceMode ) {
		String scope = (String)cbScope.getSelectedItem();
		XMLContainer container = EditixFrame.THIS.getSelectedContainer();

		if ( container != null ) {	
			run( scope, container, replaceMode );
		} else {
			EditixFactory.buildAndShowWarningDialog( "No document ?" );
		}
	}

	private void run( String scope, XMLContainer container, boolean replaceMode ) {

		String prompt = txtPrompt.getText();
		if ( container == null ) {
			EditixFactory.buildAndShowInformationDialog( "No document ?" );
		} else
		if ( "".equals( prompt ) )
			EditixFactory.buildAndShowInformationDialog( "No prompt ?" );
		else {
			
			String document_type = container.getDocumentInfo().getType();
			if ( LLMHelperUI.SCOPE_CURRENTTEXT.equals( scope ) || LLMHelperUI.SCOPE_CURRENTNODE.equals( scope ) ) {
				if ( !( "XML".equals( document_type ) || document_type.startsWith( "XSL" ) ) ) {
					EditixFactory.buildAndShowWarningDialog( "This scope is only for XML documents" );
					return;
				}
			}
			
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
						EditixFactory.buildAndShowWarningDialog( "Can't find your selected text, node or document ?" );
						return;
					}

					switch( scope ) {
						case SCOPE_CURRENTSELECTION:
						case SCOPE_CURRENTLINE:
				        case SCOPE_CURRENTTEXT:
				            contextType = "selection";
				            instructions = "- Modify **only the Text fragment**.\n" +
				                           "- Return **only the modified fragment**, no extra text or tags.\n";
				            break;

				        case SCOPE_CURRENTNODE:
				            contextType = "current node";
				            instructions = "- Modify **only the current XML node** (including its children).\n" +
				                           "- Return the **entire modified node** (with children).\n" +
				                           "- Do NOT modify parent/sibling nodes.";
				            break;

				        case SCOPE_CURRENTDOCUMENT:
				            contextType = "full document";
				            instructions = "- Modify the **entire " + document_type + " document**.\n" +
				                           "- Return the **full modified document**.\n" +
				                           "- Ensure the output is a valid " + document_type + " document";
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
					
					Entry<String,String> otherPart = LLMToolkit.extractTypeContent( response );
					if ( otherPart != null )
						response = otherPart.getValue();

					txtResponse.setText( response );	
					
					if ( replaceMode ) {
						replace( scope, container );
					}
					
				}).run( 
					this,
					userPrompt 
				);
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

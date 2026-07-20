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

package com.japisoft.editix.ui.bottompanels;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.japisoft.editix.main.EditixApplicationModel;
import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.llm.PrompterPanel;
import com.japisoft.editix.ui.llm.config.LLMRunner;
import com.japisoft.editix.ui.windows.EditixFrame;
import com.japisoft.framework.llm.DefaultLLMExchange;
import com.japisoft.framework.llm.LLM;
import com.japisoft.framework.llm.LLMContext;
import com.japisoft.framework.llm.LLMExchange;
import com.japisoft.xmlpad.IXMLPanel;
import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.editor.XMLEditor;

public class EditixPrompter extends JTabbedPane implements BottomPanel, ActionListener, ListSelectionListener {

	private PrompterPanel pp = null;
	private EditixPrompterResponsePanel rp = null;
	
	private LLMContextPanel contextPanel = null;
	private JCheckBox cb;
	private JButton btInjectSelection;
	private JButton btInjectDocument;
	private JButton btZoomPlus;
	private JButton btZoomMinus;
	private JButton btClear;

	public EditixPrompter() {
		super( JTabbedPane.RIGHT );
		addTab( "Request", pp = new PrompterPanel( true ) {
			@Override
			protected void runPrompt( String request ) {
				EditixPrompter.this.runPrompt( request );
			}
		} 
		);

		addTab( "Response", rp = new EditixPrompterResponsePanel() );		
		addTab( "Context", contextPanel = new LLMContextPanel() );

		pp.getToolBar().add( cb = new JCheckBox( "Keep" ) );
		pp.getToolBar().addSeparator();
		pp.getToolBar().add( btClear = new JButton( "Clear" ) );
		pp.getToolBar().addSeparator();		
		pp.getToolBar().add( btInjectSelection = new JButton( "+Selection" ) );
		pp.getToolBar().add( btInjectDocument = new JButton( "+Document" ) );
		pp.getToolBar().addSeparator();
		pp.getToolBar().add( btZoomPlus = new JButton( "+1" ) );
		pp.getToolBar().add( btZoomMinus = new JButton( "-1" ) );		
	}

	@Override
	public void addNotify() {
		super.addNotify();
		btInjectSelection.addActionListener( this );
		btInjectDocument.addActionListener( this );
		contextPanel.addSelectionListener( this );
		btZoomPlus.addActionListener( this );
		btZoomMinus.addActionListener( this );
		btClear.addActionListener( this );
	}
	
	@Override
	public void removeNotify() {
		super.removeNotify();
		btInjectSelection.removeActionListener( this );
		btInjectDocument.removeActionListener( this );
		contextPanel.removeSelectionListener( this );
		btZoomPlus.removeActionListener( this );
		btZoomMinus.removeActionListener( this );
		btClear.removeActionListener( this );
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		// Selected context row
		LLMExchange exchange = contextPanel.getSelectedLLMExchange();
		if ( exchange != null ) {
			pp.setPrompt( exchange.getPrompt() );
			rp.setText( exchange.getResponse() );
		}
	}

	private XMLEditor getCurrentEditor() {
		XMLContainer container = EditixFrame.THIS.getSelectedContainer();
		if ( container == null )
			return null;
		return container.getEditor();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		XMLEditor editor = getCurrentEditor();

		if ( e.getSource() == btInjectSelection ) {
			
			if ( editor == null ) {
				EditixFactory.buildAndShowWarningDialog( "No current editor ?" );
				return;
			}			
			
			String selection = editor.getSelectedText();
			if ( selection == null || "".equals( selection ) ) {
				EditixFactory.buildAndShowWarningDialog( "No selected text ?" );
			} else {
				pp.inject( "\"" + selection + "\"" );
			}
		} else
		if ( e.getSource() == btInjectDocument ) {
			
			if ( editor == null ) {
				EditixFactory.buildAndShowWarningDialog( "No current editor ?" );
				return;
			}			

			String document = editor.getText();
			pp.inject( "\"" + document + "\"" );
		} else
		if ( e.getSource() == btZoomPlus ) {
			float size = rp.getTextFont().getSize();
			size++;
			Font newfont = null;
			rp.setTextFont( newfont = rp.getTextFont().deriveFont( size ) );
			pp.setTextFont( newfont );
		} else
		if ( e.getSource() == btZoomMinus ) {
			float size = rp.getTextFont().getSize();
			size--;
			Font newfont = null;
			rp.setTextFont( newfont = rp.getTextFont().deriveFont( size ) );
			pp.setTextFont( newfont );
		} else
		if ( e.getSource() == btClear ) {
			pp.clear();
		}
	}

	@Override
	public String getTitle() {
		return "Prompt";
	}

	@Override
	public JComponent getView() {
		return this;
	}
	
	public void addLLMExchange( LLMExchange exchange ) {
		contextPanel.addLLMExchange( exchange );
	}

	@Override
	public void activate() {
		setSelectedIndex( 0 );
		pp.promptFocus();
		LLM llm = pp.getSelectedLLM();
		contextPanel.updateContext( EditixApplicationModel.getDefaultEditixLLMContext() );
	}

	@Override
	public void deactivate() {
		LLM llm = pp.getSelectedLLM();
		llm.setContext( null );
	}

	private void showResponse( String response ) {
		rp.setText( response );
		setSelectedIndex( 1 );
		rp.requestTextFocus();		
	}

	protected void runPrompt( String request ) {
		LLM llm = pp.getSelectedLLM();		
		if ( llm == null )
			EditixFactory.buildAndShowWarningDialog( "No LLM found ?" );		
		else {		
			llm.setContext( contextPanel.getContext() );

			new LLMRunner( llm, ( response ) -> {
				EditixPrompter.this.showResponse( response );
				addLLMExchange( new DefaultLLMExchange( request, response ) );
				if ( !cb.isSelected() )
					pp.clearPrompt();
			}).run( 
				this,
				request
			);
		}
	}

}
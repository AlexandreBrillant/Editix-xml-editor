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

package com.japisoft.editix.ui.llm;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;

import com.japisoft.framework.llm.LLM;
import com.japisoft.framework.llm.LLMManager;

import net.miginfocom.swing.MigLayout;

public class PrompterPanel extends JPanel {

	private JComboBox<LLM> cbLLM = null;
	private JTextArea txtPrompt = null;

	public PrompterPanel() {
		this( false );
	}

	private JToolBar tb = null;

	public PrompterPanel( boolean toolbar ) {
		
		String extra = "";
		if ( toolbar )
			extra = "[]";
		
		setLayout( new MigLayout( "fill,insets 5", "[grow]", "[][][][grow,fill]" + extra ) );
		add( new JLabel( "Your LLM" ), "wrap" );
		
		LLMManager manager = LLMManager.instance();
		add( cbLLM = new JComboBox<LLM>( manager.toArray( new LLM[ manager.size() ]) ), "grow, wrap" );

		add( new JLabel( "Your prompt" ), "wrap" );
		
		JScrollPane sp = null;
		
		extra = "";
		if ( toolbar )
			extra = ",wrap";
		
		
		add( sp = new JScrollPane( txtPrompt = new JTextArea() ), "grow,pushy" + extra );
						
		txtPrompt.setLineWrap( true );
		txtPrompt.setWrapStyleWord( true );
		
		if ( toolbar )
		{
			add( tb = new JToolBar(), "wrap" );
			tb.setFloatable( false );
		}
	}

	public JToolBar getToolBar() { return tb; }
	public LLM getSelectedLLM() { return (LLM)cbLLM.getSelectedItem(); }
	public String getPrompt() { return txtPrompt.getText(); }

	protected void runPrompt( String request ) {
		
	}
	
	public void setTextFont( Font newFont ) {
		if ( txtPrompt != null )
			txtPrompt.setFont( newFont );
	}
	
	public void clear() {
		txtPrompt.setText( "" );
	}
	
	public void inject( String content ) {
		txtPrompt.insert( content, txtPrompt.getCaretPosition() );
	}
	
	public void setPrompt( String prompt ) {
		txtPrompt.setText( prompt );
	}

	@Override
	public void addNotify() {
		super.addNotify();
		txtPrompt.getInputMap().put( KeyStroke.getKeyStroke( KeyEvent.VK_ENTER, 0 ), "enterPressed" );
		txtPrompt.getActionMap().put( "enterPressed", 
				new AbstractAction() {
					@Override
					public void actionPerformed(ActionEvent e) {						
						runPrompt( txtPrompt.getText() );
					}
				} 
		);
	}

	@Override
	public void removeNotify() {
		super.removeNotify();
		txtPrompt.getInputMap().remove( KeyStroke.getKeyStroke( KeyEvent.VK_ENTER, 0 ) );		
		txtPrompt.getActionMap().remove( "enterPressed" );
	}
	
	public void promptFocus() {
		txtPrompt.requestFocus();
	}
	
	public void clearPrompt() {
		txtPrompt.setText( "" );
	}
	
	public static void main( String[] args ) {
		JDialog t = new JDialog();
		t.setSize( 400,  400 );		
		t.add( new PrompterPanel() );
		t.pack();
		t.setVisible( true );
	}

}
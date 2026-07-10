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
// 
// AI Training Restriction :
// This source code is provided for human use only.
// Using this code to train, fine-tune, or develop AI models,
// machine learning systems, or similar technologies is
// STRICTLY PROHIBITED. Violations will terminate all rights
// under the applicable license.

package com.japisoft.editix.ui.bottompanels;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.llm.PrompterPanel;
import com.japisoft.editix.ui.llm.config.LLMRunner;
import com.japisoft.framework.llm.DefaultLLMContext;
import com.japisoft.framework.llm.DefaultLLMExchange;
import com.japisoft.framework.llm.LLM;

public class EditixPrompter extends JTabbedPane implements BottomPanel {
	private PrompterPanel pp = null;
	private JTextArea txtResponse = new JTextArea();

	public EditixPrompter() {
		super( JTabbedPane.BOTTOM );
		addTab( "Request", pp = new PrompterPanel() {
			@Override
			protected void runPrompt( String request ) {
				EditixPrompter.this.runPrompt( request );
			}
		} );
		addTab( "Response", new JScrollPane( txtResponse = new JTextArea() ) );
	}

	@Override
	public String getTitle() {
		return "Prompt";
	}

	@Override
	public JComponent getView() {
		return this;
	}

	@Override
	public void activate() {
		setSelectedIndex( 0 );
		pp.promptFocus();		
	}

	@Override
	public void deactivate() {
		LLM llm = pp.getSelectedLLM();
		llm.setContext( null );
	}

	private DefaultLLMContext context = null;

	private void showResponse( String response ) {
		txtResponse.setText( response );
		setSelectedIndex( 1 );
		txtResponse.requestFocus();		
	}

	protected void runPrompt( String request ) {
		LLM llm = pp.getSelectedLLM();		
		if ( llm == null )
			EditixFactory.buildAndShowWarningDialog( "No LLM found ?" );		
		else {		
			if ( context == null )
				context = new DefaultLLMContext();
			llm.setContext( context );

			new LLMRunner( llm, ( response ) -> {
				EditixPrompter.this.showResponse( response );
				context.add( new DefaultLLMExchange( request, response ) );
				pp.clearPrompt();
			}).run( 
				this,
				request
			);
		}
	}

}

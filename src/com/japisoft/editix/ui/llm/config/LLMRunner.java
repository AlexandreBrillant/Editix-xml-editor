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

package com.japisoft.editix.ui.llm.config;

import java.awt.Window;
import java.util.concurrent.ExecutionException;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.framework.llm.LLM;

public class LLMRunner {

	private LLM currentLLM;
	private LLMRunnerListener listener;
	
	public LLMRunner( LLM llm, LLMRunnerListener listener ) {
		this.currentLLM = llm;
		this.listener = listener;
	}
	
	public LLMRunner( LLM llm ) {
		this( llm, null );
	}

	public void run( JComponent source, String prompt ) {
		Window owner = SwingUtilities.getWindowAncestor( source );
		SwingWorker<String,Void> worker = new SwingWorker<String,Void>() {
			@Override
			protected String doInBackground() throws Exception {
				try {
					return currentLLM.prompt( prompt );
				} catch( Exception exc ) {
					return "Can't use this LLM [" + exc.getMessage() + "]";								
				}
			}
			@Override
			protected void done() {
				EditixFactory.hideProcessDialog( "llmtest" );
				try {
					String result = get();
					if ( listener != null )
						listener.LLMDone( result );
					else	// Default dialog for testing
						EditixFactory.buildAndShowProcessDialog( null, owner, "Response", result );
				} catch( InterruptedException | ExecutionException e ) {
					EditixFactory.buildAndShowErrorDialog( "Error [" + e.getMessage() + "]" );
				}
				
			}
		};
		EditixFactory.buildAndShowProcessDialog( "llmtest", owner, "wait", "Please wait for a response..." );
		worker.execute();				
	}

}
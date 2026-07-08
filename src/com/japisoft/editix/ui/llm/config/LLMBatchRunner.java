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

package com.japisoft.editix.ui.llm.config;

import java.awt.Window;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.framework.llm.LLM;

public class LLMBatchRunner {

	private static final String LLMBATCH = "llmbatch";
	private LLM currentLLM;
	private LLMBatchRunnerListener listener;
	ExecutorService executor = null;

	public LLMBatchRunner( LLM llm, LLMBatchRunnerListener listener ) {
		this.currentLLM = llm;
		this.listener = listener;
	}
	
	private List<String> prompts = null;
	
	public void addPrompt( String prompt ) {
		if ( prompts == null )
			prompts = new ArrayList<String>();
		prompts.add( prompt );
	}
	
	public void run( JComponent source ) {
		if ( prompts == null || prompts.size() == 0 )
			EditixFactory.buildAndShowWarningDialog( "No prompt ?" );
		else {
			if ( EditixFactory.buildAndShowConfirmDialog( "Apply " + prompts.size() + " requests ?" ) ) {
				Window owner = SwingUtilities.getWindowAncestor( source );
				executor = Executors.newSingleThreadExecutor();
				EditixFactory.buildAndShowProcessDialog(LLMBATCH, owner, "wait", "Starting, please wait..." );
				SwingUtilities.invokeLater( () -> runNextPrompt( 0 ) );
			}
		}		
	}
	
	private void runNextPrompt( int index ) {
		if ( index >= prompts.size() || index < 0 ) {
			EditixFactory.hideProcessDialog( LLMBATCH );
			shutdown();
			return;
		}
		
	    SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
	        @Override
	        protected String doInBackground() {
	            EditixFactory.updateProcessMessage(LLMBATCH, "Processing " + (index + 1) + "/" + prompts.size());
	            try {
	                return currentLLM.prompt(prompts.get(index));
	            } catch (Exception exc) {
	                return "Can't use this LLM [" + exc.getMessage() + "]";
	            }
	        }

	        @Override
	        protected void done() {
	            try {
	                String result = get();
	                listener.LLMDone(index, result);
	                runNextPrompt(index + 1);
	            } catch (Exception e) {
	            	runNextPrompt( -1 );
	            	EditixFactory.buildAndShowErrorDialog( e.getMessage() );
	            	e.printStackTrace();
	            }

	        }
	    };
	    
	    executor.execute(worker);		
	}
	
	private void shutdown() {
	    if (executor != null) {
	        executor.shutdown();
	        executor = null;
	    }
	}
	
}

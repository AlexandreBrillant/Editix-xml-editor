package com.japisoft.editix.ui.llm;

import java.awt.Window;

import javax.swing.SwingUtilities;

import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.llm.config.LLMRunner;
import com.japisoft.editix.ui.llm.config.LLMRunnerListener;
import com.japisoft.framework.dialog.BasicDialogComponent;
import com.japisoft.framework.llm.LLM;

public class RunnablePrompterPanel extends SimplePrompterPanel implements LLMRunnerListener {

	private LLMRunnerListener listener;
	private boolean autoCloseMode;

	public RunnablePrompterPanel( String prompt, LLMRunnerListener listener, boolean autoCloseMode ) {
		super( prompt );
		this.listener = listener;
		this.autoCloseMode = autoCloseMode;
	}
	
	public RunnablePrompterPanel( String prompt, LLMRunnerListener listener ) {
		this( prompt, listener, true );
	}
		
	@Override
	public void LLMDone(String result) {
		if ( autoCloseMode ) {
			Window w = SwingUtilities.getWindowAncestor( this );
			if ( w instanceof BasicDialogComponent ) {
				(( BasicDialogComponent )w).pressOK();
			}
		}
		if ( listener != null )
			listener.LLMDone( result );
	}

	private boolean startedRequest = false;
	
	public boolean hasRequest() {
		return startedRequest;
	}
	
	@Override
	protected void runPrompt(String request) {
		super.runPrompt(request);
		startedRequest = true;
		LLM currentLLM = getSelectedLLM();
		if ( currentLLM == null ) {
			EditixFactory.buildAndShowWarningDialog( "No LLM Found ?" );
		} else {
			String prompt = getPrompt();
			new LLMRunner( currentLLM, this ).run( null, prompt );		
		}
	}
	
	public void runPrompt() {
		runPrompt( getPrompt() );
	}

}

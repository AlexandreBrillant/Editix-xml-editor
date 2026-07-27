package com.japisoft.editix.action.edit.selection;

import com.japisoft.framework.ApplicationModel;

public class AskLLMAction extends AbstractSelectionAction {

	@Override
	protected String processSelection( int start, int end, String selection) {
		ApplicationModel.fireApplicationValue( "show.leftpanel", "llmassistant", selection, start, end );
		return null;
	}

}

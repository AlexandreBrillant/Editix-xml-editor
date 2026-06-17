package com.japisoft.editix.action.edit.selection;

public class TrimAction extends AbstractSelectionAction {

	@Override
	protected String processSelection(String selection) {
		return selection.trim();
	}

}

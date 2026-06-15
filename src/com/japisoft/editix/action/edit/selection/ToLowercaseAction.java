package com.japisoft.editix.action.edit.selection;

public class ToLowercaseAction extends AbstractSelectionAction {

	@Override
	protected String processSelection(String selection) {
		return selection.toLowerCase();
	}

}

package com.japisoft.editix.action.edit.selection;

public class ToUppercaseAction extends AbstractSelectionAction {

	@Override
	protected String processSelection(String selection) {
		return selection.toUpperCase();
	}

}

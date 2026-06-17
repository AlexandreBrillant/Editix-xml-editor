package com.japisoft.editix.action.edit.selection;

import com.japisoft.editix.ui.EditixFactory;

public class SplitAction extends AbstractSelectionAction {

	@Override
	protected String processSelection(String selection) {
		String separator = EditixFactory.buildAndShowInputDialog( "Choose a separator" );
		if ( separator != null && !"".equals( separator ) ) {
			String[] content = selection.split( separator );
			if ( content != null && content.length > 0 ) {
				return String.join( "\n", content );
			} else {
				EditixFactory.buildAndShowWarningDialog( "Can't split this selection");
				return null;
			}
		} else			
			return null;
	}

}

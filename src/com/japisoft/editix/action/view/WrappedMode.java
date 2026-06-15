package com.japisoft.editix.action.view;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.framework.preferences.Preferences;

public class WrappedMode extends AbstractAction {

	@Override
	public void actionPerformed(ActionEvent e) {
		boolean current = Preferences.getPreference( "editor", "wrappedMode", false );
		current = !current;
		Preferences.setPreference( "editor", "wrappedMode", current );
		EditixFactory.buildAndShowInformationDialog( ( current ? "Wrapped mode is enabled " : "Wrapped mode is disabled" ) + " / Reload your documents for the changes to take effect..." );
	}

}

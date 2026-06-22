package com.japisoft.editix.action.options;

import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.editix.ui.llm.LLMConfigPanel;
import com.japisoft.framework.dialog.DialogManager;

import com.japisoft.framework.llm.LLMManager;

public class LLMManagementAction extends AbstractAction {

	@Override
	public void actionPerformed(ActionEvent e) {
		LLMConfigPanel configPanel = new LLMConfigPanel();		
		if ( DialogManager.showDialog( EditixFrame.THIS, "LLM Management", "LLM Management", "Set your LLM for IA usage", null, configPanel, new Dimension( 600,400 )  ) == DialogManager.OK_ID ) {
			try {
				LLMManager.instance().save();
			} catch( Exception exc ) {
				EditixFactory.buildAndShowErrorDialog( "Can't save your configuration [" + exc.getMessage() + "]" );
			}
		}
	}

}

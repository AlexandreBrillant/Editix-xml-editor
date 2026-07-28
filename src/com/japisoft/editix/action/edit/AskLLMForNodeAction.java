package com.japisoft.editix.action.edit;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.japisoft.framework.ApplicationModel;

public class AskLLMForNodeAction extends AbstractAction {

	@Override
	public void actionPerformed(ActionEvent e) {
		ApplicationModel.fireApplicationValue( "show.leftpanel", "llmassistant", "current.node" );		
	}

}

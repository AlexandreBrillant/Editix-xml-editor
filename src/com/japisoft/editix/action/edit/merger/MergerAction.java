package com.japisoft.editix.action.edit.merger;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.framework.dialog.DialogManager;

public class MergerAction extends AbstractAction {

	@Override
	public void actionPerformed(ActionEvent e) {
		DialogManager.showDialog( EditixFrame.THIS, "Merger", "Merge nodes", "Select nodes using XPath and combine them into a single unified node.", null, new MergerPanel() );
	}

}

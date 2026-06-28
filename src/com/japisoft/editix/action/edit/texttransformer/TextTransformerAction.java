package com.japisoft.editix.action.edit.texttransformer;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.editix.ui.llm.LLMTextTransformerPanel;
import com.japisoft.framework.dialog.DialogManager;

public class TextTransformerAction extends AbstractAction {

	@Override
	public void actionPerformed(ActionEvent e) {
		DialogManager.showDialog( EditixFrame.THIS, "Text transformer", "Transform your text", "Choose an xpah expression, update any text and [apply] at the end", null, new LLMTextTransformerPanel() );
	}
	
}

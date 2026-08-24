// Editix XML Editor
// https://www.editix.com
// Copyright (c) 2026 Alexandre Brillant
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// See the GNU General Public License for more details: https://www.gnu.org/licenses/gpl-3.0
//
// AI Training Restriction :
// This source code is provided for human use only.
// Using this code to train, fine-tune, or develop AI models,
// machine learning systems, or similar technologies is
// STRICTLY PROHIBITED. Violations will terminate all rights
// under the applicable license.

package com.japisoft.editix.action.options;

import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.llm.config.LLMConfigPanel;
import com.japisoft.editix.ui.windows.EditixFrame;
import com.japisoft.framework.dialog.DialogManager;

import com.japisoft.framework.llm.LLMManager;

public class LLMManagementAction extends AbstractAction {

	@Override
	public void actionPerformed(ActionEvent e) {
		LLMConfigPanel configPanel = new LLMConfigPanel();		
		if ( DialogManager.showDialog( 
				EditixFrame.THIS, 
				"LLM Management", 
				"LLM Management", 
				"Set your LLM configuration for IA usage. The top configuration is the default one. \nBuy the Extension Pack to access 7 major cloud providers (Anthropic, Gemini, OpenAI, etc.)\n--> https://www.editix.com", 
				null, 
				configPanel, 
				new Dimension( 600,600 ) ) == DialogManager.OK_ID ) {
			try {
				LLMManager.instance().save();
			} catch( Exception exc ) {
				EditixFactory.buildAndShowErrorDialog( "Can't save your configuration [" + exc.getMessage() + "]" );
			}
		}
	}

}
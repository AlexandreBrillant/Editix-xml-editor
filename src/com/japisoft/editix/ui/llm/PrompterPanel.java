// Editix XML Editor
// https://www.editix.com
// Copyright (c) 2025 Alexandre Brillant
// 
// For non-commercial usage :
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
// For commercial use or integration into proprietary software :
// A commercial license is required. Visit https://www.editix.com for details.

package com.japisoft.editix.ui.llm;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import com.japisoft.framework.llm.LLM;
import com.japisoft.framework.llm.LLMManager;

import net.miginfocom.swing.MigLayout;

public class PrompterPanel extends JPanel {

	private JComboBox<LLM> cbLLM = null;
	private JTextArea txtPrompt = null;
	
	public PrompterPanel() {
		setLayout( new MigLayout( "fill, insets 5", "[grow]", "[][][][grow]") );
		add( new JLabel( "Your LLM" ), "wrap" );
		
		LLMManager manager = LLMManager.instance();
		add( cbLLM = new JComboBox<LLM>( manager.toArray( new LLM[ manager.size() ]) ), "grow, wrap" );

		add( new JLabel( "Your prompt" ), "wrap" );
		add( txtPrompt = new JTextArea(), "grow,pushy,wrap" );				
	}

	public LLM getSelectedLLM() { return (LLM)cbLLM.getSelectedItem(); }
	public String getPrompt() { return txtPrompt.getText(); }
	
}

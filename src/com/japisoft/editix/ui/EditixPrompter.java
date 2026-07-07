package com.japisoft.editix.ui;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import com.japisoft.editix.ui.llm.PrompterPanel;

public class EditixPrompter extends JPanel {

	public EditixPrompter() {
		setLayout( new BorderLayout() );
		add( new PrompterPanel(), BorderLayout.CENTER );
	}

}

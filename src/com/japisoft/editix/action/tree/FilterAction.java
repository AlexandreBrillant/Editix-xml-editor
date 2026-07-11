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

package com.japisoft.editix.action.tree;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.action.MultipleChoice;
import com.japisoft.xmlpad.tree.renderer.FastTreeRenderer;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 */
public class FilterAction extends AbstractAction implements MultipleChoice {

	public FilterAction() {
		try {
			putValue("label1", "'Default' Filter");
			putValue("label2", "'Prefix' Filter");
			putValue("label3", "'Namespace' Filter");
			putValue("label4", "'Qualified Name' Filter");
			putValue("cmd1", "0");
			putValue("cmd2", "1");
			putValue("cmd3", "2");
			putValue("cmd4", "3");
		} catch (Throwable th) {
			th.printStackTrace();
		}
	}

	public static final FilterAction SINGLETON = new FilterAction();

	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();

		if ("".equals(cmd) || cmd == null)
			return;

		XMLContainer container = EditixFrame.THIS.getSelectedContainer();
		if (container == null) {
			// ??
		} else {

			int mode = Integer.parseInt(cmd);
			((FastTreeRenderer) container.getTree().getCellRenderer())
					.setRenderingMode(mode);
			TreePath tp = container.getTree().getSelectionPath();
			((DefaultTreeModel) container.getTree().getModel()).reload();
			if (tp != null)
				container.getTree().setSelectionPath(tp);

			container.getTree().repaint();
		}
	}

}
package com.japisoft.xflows.task.ui.logger;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import com.japisoft.framework.ui.text.FileTextField;
import com.japisoft.xflows.LoggerModel;
import com.japisoft.xflows.XFlowsApplicationModel;
import com.japisoft.xflows.task.ui.XFlowsFactory;

/**
This program is available under two licenses : 

1. For non commercial usage : 

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

2. For commercial usage :

You need to get a commercial license for source usage at : 

http://www.editix.com/buy.html

Copyright (c) 2018 Alexandre Brillant - JAPISOFT SARL - http://www.japisoft.com

@author Alexandre Brillant - abrillant@japisoft.com
@author JAPISOFT SARL - http://www.japisoft.com

*/
public class FileLogPanel extends JPanel implements ActionListener {
	private JCheckBox cbInfo = new JCheckBox();

	private FileTextField tfInfo = new FileTextField(null, "log");

	private JCheckBox cbWarning = new JCheckBox();

	private FileTextField tfWarning = new FileTextField(null, "log");

	private JCheckBox cbError = new JCheckBox();

	private FileTextField tfError = new FileTextField(null, "log");

	private GridBagLayout gridBagLayout1 = new GridBagLayout();

	public FileLogPanel() {
		try {
			jbInit();

			tfInfo.setEnabled(false);
			tfWarning.setEnabled(false);
			tfError.setEnabled(false);

			tfInfo.setOpenedMode(false);
			tfWarning.setOpenedMode(false);
			tfError.setOpenedMode(false);

			cbInfo.addActionListener(this);
			cbWarning.addActionListener(this);
			cbError.addActionListener(this);

			cbInfo.setIcon(XFlowsFactory.getImageIcon("images/bug_green2.png"));
			cbInfo.setSelectedIcon(XFlowsFactory
					.getImageIcon("images/bug_green.png"));

			cbWarning.setIcon(XFlowsFactory
					.getImageIcon("images/bug_yellow2.png"));
			cbWarning.setSelectedIcon(XFlowsFactory
					.getImageIcon("images/bug_yellow.png"));

			cbError.setIcon(XFlowsFactory.getImageIcon("images/bug_red2.png"));
			cbError.setSelectedIcon(XFlowsFactory
					.getImageIcon("images/bug_red.png"));

			cbInfo.setSelected(false);
			cbWarning.setSelected(false);
			cbError.setSelected(false);

			tfInfo.overrideDocument(new InfoFieldDocument());
			tfWarning.overrideDocument(new WarningFieldDocument());
			tfError.overrideDocument(new ErrorFieldDocument());

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void synchroUI(LoggerModel model) {
		cbInfo.setSelected(model.isFileLogInfoEnabled());
		cbWarning.setSelected(model.isFileLogWarningEnabled());
		cbError.setSelected(model.isFileLogErrorEnabled());

		tfInfo.setText(model.getFileLogInfo());
		tfWarning.setText(model.getFileLogWarning());
		tfError.setText(model.getFileLogError());

		tfInfo.setEnabled(cbInfo.isSelected());
		tfWarning.setEnabled(cbWarning.isSelected());
		tfError.setEnabled(cbError.isSelected());
		
		resetFieldsEnabled();
	}

	void synchroModel(LoggerModel model) {
		model.setFileLogError(tfError.getText());
		model.setFileLogErrorEnabled(cbError.isSelected());

		model.setFileLogWarning(tfWarning.getText());
		model.setFileLogWarningEnabled(cbWarning.isSelected());

		model.setFileLogInfo(tfInfo.getText());
		model.setFileLogInfoEnabled(cbInfo.isSelected());
	}

	private void resetFieldsEnabled() {
		tfInfo.setEnabled(cbInfo.isSelected());
		tfWarning.setEnabled(cbWarning.isSelected());
		tfError.setEnabled(cbError.isSelected());
	}

	public void actionPerformed(ActionEvent e) {
		resetFieldsEnabled();
		LoggerModel model = XFlowsApplicationModel.ACCESSOR.getLogger();
		model.setFileLogInfoEnabled(cbInfo.isSelected());
		model.setFileLogWarningEnabled(cbWarning.isSelected());
		model.setFileLogErrorEnabled(cbError.isSelected());
		XFlowsApplicationModel.setModified();
	}

	void jbInit() throws Exception {
		cbInfo.setText("Info");
		this.setLayout(gridBagLayout1);
		tfInfo.setText("");
		cbWarning.setText("Warning");
		tfWarning.setText("");
		cbError.setText("Error");
		tfError.setText("");
		this.add(cbInfo, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						10, 10, 10, 10), 0, 0));
		this.add(tfInfo, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
				new Insets(10, 10, 10, 10), 343, 0));
		this.add(cbWarning, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						10, 10, 10, 10), 0, 0));
		this.add(tfWarning, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
				new Insets(10, 10, 10, 10), 343, 0));
		this.add(cbError, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						10, 10, 10, 10), 0, 0));
		this.add(tfError, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
				new Insets(10, 10, 10, 10), 343, 0));
	}

	class InfoFieldDocument extends PlainDocument {
		public void insertString(int offs, String str, AttributeSet a)
				throws BadLocationException {
			super.insertString(offs, str, a);
			XFlowsApplicationModel.setModified();
			XFlowsApplicationModel.ACCESSOR.getLogger().setFileLogInfo(
					getText(0, getLength()));
		}

		public void remove(int offs, int len) throws BadLocationException {
			super.remove(offs, len);
			XFlowsApplicationModel.setModified();
			XFlowsApplicationModel.ACCESSOR.getLogger().setFileLogInfo(
					getText(0, getLength()));
		}
	}

	class WarningFieldDocument extends PlainDocument {
		public void insertString(int offs, String str, AttributeSet a)
				throws BadLocationException {
			super.insertString(offs, str, a);
			XFlowsApplicationModel.setModified();
			XFlowsApplicationModel.ACCESSOR.getLogger().setFileLogWarning(
					getText(0, getLength()));
		}

		public void remove(int offs, int len) throws BadLocationException {
			super.remove(offs, len);
			XFlowsApplicationModel.setModified();
			XFlowsApplicationModel.ACCESSOR.getLogger().setFileLogWarning(
					getText(0, getLength()));
		}
	}

	class ErrorFieldDocument extends PlainDocument {
		public void insertString(int offs, String str, AttributeSet a)
				throws BadLocationException {
			super.insertString(offs, str, a);
			XFlowsApplicationModel.setModified();
			XFlowsApplicationModel.ACCESSOR.getLogger().setFileLogError(
					getText(0, getLength()));
		}

		public void remove(int offs, int len) throws BadLocationException {
			super.remove(offs, len);
			XFlowsApplicationModel.setModified();
			XFlowsApplicationModel.ACCESSOR.getLogger().setFileLogError(
					getText(0, getLength()));
		}
	}

}

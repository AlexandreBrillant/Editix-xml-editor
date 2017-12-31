package com.japisoft.xflows.action.options.tasks.wizard;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import com.japisoft.framework.ui.SimpleFileFilter;
import com.japisoft.framework.wizard.BasicStepView;
import com.japisoft.framework.wizard.BasicWizardStep;
import com.japisoft.framework.wizard.JWizard;
import com.japisoft.framework.wizard.StepView;
import com.japisoft.framework.wizard.WizardStepContext;
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
public class NewTaskWizard extends JWizard {

	public NewTaskWizard() {
		super();
		getWizardStepModel().addWizardStep(new FirstStep());
		getWizardStepModel().addWizardStep(new SecondStep());
		getWizardStepModel().addWizardStep(new ThirdStep());
		getWizardStepModel().addWizardStep(new FourthStep());
	}
	
	class FirstStep extends BasicWizardStep {

		JFileChooser chooser = null;

		public FirstStep() {
			super("archive");
			setShortTitle("Java archive");
			setLongTitle("Choose a java archive file containing your Task classes");
			chooser = new JFileChooser();
			chooser.setControlButtonsAreShown(false);

			chooser.addChoosableFileFilter(new SimpleFileFilter("Java Archive",
					"jar"));
			setView( new BasicStepView( chooser ) );
		}

		public void stop(WizardStepContext context) {
			context.setSharedData("file", chooser.getSelectedFile());
		}
	}

	class SecondStep extends BasicWizardStep {

		DefaultListModel model = new DefaultListModel();

		JList list;

		public SecondStep() {
			super("task");
			setShortTitle("Task class");
			setLongTitle("Choose your java Task class");
			setView( new BasicStepView( new JScrollPane(list = new JList( model ) ) ) );
		}

		public String storeFileKey = "running";

		public boolean canStart(WizardStepContext context) {
			File f = (File) context.getSharedData( "file" );
			if (f == null) {
				XFlowsFactory.buildAndShowErrorDialog( "Wrong archive file" );
				return false;
			}
			return true;
		}

		public boolean start(WizardStepContext context) {
			File f = (File) context.getSharedData( "file" );
			model.removeAllElements();

			try {
				JarInputStream input = new JarInputStream(
						new FileInputStream(f));
				try {
					JarEntry entry = null;
					do {
						entry = input.getNextJarEntry();
						if ( entry != null && entry.getName().endsWith( ".class" ) ) {

							String cl = entry.getName();
							cl = cl.replace('/', '.');
							cl = cl.replace('\\', '.');
							cl = cl.substring(0, entry.getName().lastIndexOf(
									"."));
							model.addElement(cl);
						}

					} while (entry != null);
				} finally {
					try {
						input.close();
					} catch (IOException exc) {
					}
				}
			} catch (FileNotFoundException e) {
				XFlowsFactory.buildAndShowErrorDialog(e.getMessage());
			} catch (IOException e) {
				XFlowsFactory.buildAndShowErrorDialog(e.getMessage());
			}

			return true;
		}

		public void stop(WizardStepContext context) {
			context.setSharedData(storeFileKey, list.getSelectedValue());
		}

	}

	class ThirdStep extends SecondStep {

		public ThirdStep() {
			storeFileKey = "ui";
			setShortTitle( "UI class" );
			setLongTitle( "Choose your java User Interface Task class" );
			setView( new BasicStepView( new JScrollPane(list = new JList( model ) ) ) );
		}
	}

	class FourthStep extends BasicWizardStep {

		private JTextField tfName;
		
		public FourthStep() {
			super("third");
			setShortTitle("Name");
			setLongTitle("Choose a short task name");

			JLabel lblName = new JLabel();
			tfName = new JTextField();

			lblName.setText("Name");

			JPanel panel = new JPanel();
			
			panel.setLayout(new GridBagLayout());
			tfName.setText("");
			panel.add(lblName, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
					GridBagConstraints.WEST, GridBagConstraints.NONE,
					new Insets(71, 7, 0, 0), 0, 0));
			panel.add(tfName, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0,
					GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
					new Insets(8, 7, 101, 7), 382, 0));
			
			setView( new BasicStepView( panel ) );
		}
		
		public void stop(WizardStepContext context) {
			context.setSharedData( "name", tfName.getText() );
		}

	}

}

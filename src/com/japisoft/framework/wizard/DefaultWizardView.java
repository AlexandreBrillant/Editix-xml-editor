package com.japisoft.framework.wizard;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.border.EmptyBorder;

import com.japisoft.framework.application.descriptor.ActionModel;
import com.japisoft.framework.dialog.BasicDialogFooter;
import com.japisoft.framework.dialog.actions.AbstractDialogAction;
import com.japisoft.framework.dialog.actions.CancelAction;
import com.japisoft.framework.dialog.actions.DialogAction;
import com.japisoft.framework.dialog.actions.DialogActionModel;
import com.japisoft.framework.dialog.actions.OKAction;
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
public class DefaultWizardView extends JComponent implements WizardView {

	private JWizard wizard;

	private LeftPanel leftPanel = null;

	private CenterPanel centerPanel = null;

	private BottomPanel bottomPanel = null;

	private DialogAction previousAction;
	private DialogAction nextAction;
	private DialogAction okAction;
	private DialogAction cancelAction;

	public DefaultWizardView(JWizard wizard) {
		this.wizard = wizard;
	}

	/** @return the previous Action */
	public DialogAction getPreviousAction() {
		if (previousAction == null) {
			previousAction = new PreviousAction();
			previousAction.putValue(Action.NAME, wizard.getLabel("PREVIOUS",
					"Previous"));
		}
		return previousAction;
	}

	/** @return the next Action */
	public DialogAction getNextAction() {
		if (nextAction == null) {
			nextAction = new NextAction();
			nextAction.putValue(Action.NAME, wizard.getLabel("NEXT", "Next"));
		}
		return nextAction;
	}

	/** @return the Ok Action */
	public DialogAction getOkAction() {
		if (okAction == null) {
			okAction = new WizardOkAction();
		}
		return okAction;
	}

	/** @return the Cancel Action */
	public DialogAction getCancelAction() {
		if (cancelAction == null) {
			cancelAction = new WizardCancelAction();
		}
		return cancelAction;
	}

	public void addNotify() {
		super.addNotify();
		init();
	}

	private boolean init = false;

	private void init() {
		if (init)
			return;
		init = true;
		setLayout(new BorderLayout());
		add(leftPanel = new LeftPanel(), BorderLayout.WEST);
		add(bottomPanel = new BottomPanel(), BorderLayout.SOUTH);
		add(centerPanel = new CenterPanel(), BorderLayout.CENTER);

		if (!wizard.preparedDialog)
			wizard.prepareWizards();
		if (delayedModel != null)
			updateView(delayedModel);
		if (delayedStep != null)
			activate(delayedStep);
	}

	public JComponent getView() {
		return this;
	}

	public void updateView(WizardStepModel model) {
		if (!init) {
			delayedModel = model;
			return;
		}
		leftPanel.update(model);
	}

	private WizardStepModel delayedModel = null;

	private WizardStep delayedStep = null;

	public void activate(WizardStep step) {
		if (!init) {
			delayedStep = step;
			return;
		}

		leftPanel.show(step);
		centerPanel.show(step);
		int index = wizard.getWizardStepModel().getWizardStepIndex(step);
		int total = wizard.getWizardStepModel().getWizardStepCount();
		bottomPanel.setEnabledPrevious(index > 0);
		bottomPanel.setEnabledNext(index < total - 1);
		bottomPanel.setEnabledOk(index == total - 1);
	}

	public void setEnabledNextAction(boolean enabled) {
		getNextAction().setEnabled(enabled);
	}

	public void setEnabledPreviousAction(boolean enabled) {
		getPreviousAction().setEnabled(enabled);
	}

	///////////////////////////////////////////////////////////////////////////////

	class LeftPanel extends JPanel {

		private JPanel p = null;

		public LeftPanel() {
			super();

			Color c = wizard.getStepLabelsBackground();

			if (c != null)
				setBackground(c);

			setLayout(new BorderLayout());

			TitledLabel tl = new TitledLabel();
			tl.setText(wizard.getLabel("STEPS", "Steps"));

			add(tl, BorderLayout.NORTH);

			p = new JPanel();

			if (wizard.getImage() == null) {
				if (c != null)
					p.setBackground(c);
			} else
				p.setOpaque(false);

			p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
			add(p, BorderLayout.CENTER);
		}

		void show(WizardStep step) {
			for (int i = 0; i < p.getComponentCount(); i++) {
				JComponent c = (JComponent) p.getComponent(i);
				c.setEnabled(false);
			}
			int index = wizard.getWizardStepModel().getWizardStepIndex(step);
			if (index > -1) {
				JLabel l = (JLabel) p.getComponent(index);
				l.setEnabled(true);
			}
		}

		void update(WizardStepModel model) {
			p.removeAll();
			for (int i = 0; i < model.getWizardStepCount(); i++) {
				WizardStep step = model.getWizardStep(i);
				String title = step.getShortTitle();

				if (title == null)
					title = wizard.getLabel("STEP", "step");

				if (wizard.isNumberedStep())
					title = (i + 1) + ". " + title;
				JLabel l = new JLabel(title, step.getSmallIcon(), JLabel.LEFT);
				l.setOpaque(false);
				p.add(l);
			}
			p.invalidate();
			p.validate();
		}

		public void paintComponent(Graphics gc) {
			super.paintComponent(gc);
			if (wizard.image != null) {
				wizard.image.paintIcon(this, gc, 0, 0);
			}
		}

		public Dimension getPreferredSize() {
			if (wizard.image != null)
				return new Dimension(wizard.image.getIconWidth(), wizard.image
						.getIconHeight());
			else
				return wizard.getLabelsPreferredSize();
		}
	}

	/////////////////////////////////////////////////////////////////////

	class CenterPanel extends JPanel {
		TitledLabel l = null;

		JPanel v = null;

		public CenterPanel() {
			setLayout(new BorderLayout());
			if (wizard.isWizardStepTitle())
				add(l = new TitledLabel(), BorderLayout.NORTH);

			v = new JPanel();
			v.setLayout(new BorderLayout());
			v.setBorder(new EmptyBorder(5, 5, 5, 5));
			add(v, BorderLayout.CENTER);
		}

		public Dimension getPreferredSize() {
			return wizard.getViewPreferredSize();
		}

		public void show(WizardStep step) {
			if (l != null) {
				l.setIcon(step.getLargeIcon());
				l.setText(step.getLongTitle() != null ? step.getLongTitle()
						: step.getShortTitle());
			}
			if (v.getComponentCount() > 0) {
				v.remove(0);
			}

			if (step.getStepView() == null)
				throw new RuntimeException("No view for the step "
						+ step.getName() + " !!");

			JComponent component = step.getStepView().getView();
			if (component != null) {
				v.add(step.getStepView().getView(), BorderLayout.CENTER);
				v.invalidate();
				v.validate();
				v.repaint();
			} else {
				throw new RuntimeException(
						"Your step "
								+ step.getName()
								+ " has now final view (getView). Please update the StepView implementation");
			}
		}
	}

	////////////////////////////////////////////////////////////////////////

	class PreviousAction extends AbstractDialogAction {
		public PreviousAction() {
			super( 100 );
		}
		public void actionPerformed(ActionEvent e) {
			wizard.actionPrevious();
		}
	}

	class NextAction extends AbstractDialogAction {
		public NextAction() {
			super( 100 + 1 );
		}
		public void actionPerformed(ActionEvent e) {
			wizard.actionNext();
		}
	}

	class WizardOkAction extends OKAction {
		public void actionPerformed(ActionEvent e) {
			super.actionPerformed( e );
			wizard.actionOk();
		}
	}

	class WizardCancelAction extends CancelAction {
		public void actionPerformed(ActionEvent e) {
			super.actionPerformed( e );
			wizard.actionCancel();
		}
	}

	class BottomPanel extends BasicDialogFooter {
		public BottomPanel() {
			
			DialogActionModel
				model = new DialogActionModel();
			
			model.addDialogAction(
					getNextAction() );			
			model.addDialogAction(
					getPreviousAction() );
			model.addDialogAction(
					getOkAction() );
			model.addDialogAction(
					getCancelAction() );
			setModel( model );
		}

		public void setEnabledPrevious(boolean enabled) {
			setEnabled( 100, enabled );
		}

		public void setEnabledNext(boolean enabled) {
			setEnabled( 100 + 1, enabled );			
		}

		public void setEnabledOk(boolean enabled) {
			setEnabled( OKAction.ID, enabled );
		}

		public void setEnabledCancel(boolean enabled) {
			setEnabled( CancelAction.ID, enabled );
		}

	}

}

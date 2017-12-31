package com.japisoft.xmlpad.dialog;

import java.awt.Container;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
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
public class OkCancelDialog extends JDialog implements ActionListener {
	private JPanel mainPanel;

	public static final String CANCEL_ACTION = "cancel";
	public static final String OK_ACTION = "ok";

	/** Use bundle for changing title/icon.. By defaul true */
	public static boolean BUNDLE = true;
	private String[] actions = new String[] { OK_ACTION, CANCEL_ACTION };

	public OkCancelDialog(String dialogTitle, String title, String comment) {
		super();
		init(dialogTitle, title, comment);
	}

	public OkCancelDialog(
		Dialog owner,
		String dialogTitle,
		String title,
		String comment) {
		super(owner);
		init(dialogTitle, title, comment);
	}

	public OkCancelDialog(
		Frame owner,
		String dialogTitle,
		String title,
		String comment) {
		super(owner);
		init(dialogTitle, title, comment);
	}

	protected void setEnabledButton( String label, boolean state ) {
		for (int i = pnlOkCancel.getComponentCount() - 1; i >= 0; i--) {
			if ( label.equals( 
			 	( (JButton ) pnlOkCancel.getComponent(i)).getText() ) ) {
				pnlOkCancel.getComponent(i).setEnabled( state );
				break;
			}				
		}
	}

	//JPF
	public void removeNotify() {
		super.removeNotify();
		if (pnlOkCancel != null) {
			for (int i = pnlOkCancel.getComponentCount() - 1; i >= 0; i--) {
				((JButton) pnlOkCancel.getComponent(i)).removeActionListener(
					this);
			}
		}
	}

	JPanel pnlOkCancel;

	/** @return the panel that contains action buttons */
	public JPanel getButtonPanel() {
		return pnlOkCancel;
	}

	/** Add-on panel button */
	protected JPanel getAddonPanel() {
		return null;
	}

	private void init(String dialogTitle, String title, String comment) {
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setModal(true);
		init(title, comment);
		setSize(400, 400);
		setTitle(dialogTitle);

		getRootPane().registerKeyboardAction(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				OkCancelDialog.this.actionPerformed(
					new ActionEvent(e.getSource(), e.getID(), CANCEL_ACTION));
			}
		},
			KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false),
			JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
	}

	public static String DEFAULT_ICON =
		"images/OkCancelDialog.gif";

	/** @return the default classpath path for the icon */
	protected String getDefaultIcon() {
		return DEFAULT_ICON;
	}

	private void init(String title, String comment) {
		String icon = getDefaultIcon();
		try {
			if (BUNDLE) {
				ResourceBundle resource = ResourceBundle.getBundle(getName());
				try {
					title = resource.getString("TITLE");
				} catch (MissingResourceException th) {
				}
				try {
					comment = resource.getString("COMMENT");
				} catch (MissingResourceException th) {
				}
				try {
					actions[0] = resource.getString("OK");
				} catch (MissingResourceException th) {
				}
				try {
					actions[1] = resource.getString("CANCEL");
				} catch (MissingResourceException th) {
				}
				try {
					icon = resource.getString("ICON");
				} catch (MissingResourceException th) {
				}
			}
		} catch (Throwable th) {
		}

		pnlOkCancel = new JPanel(); // JPF
		JButton btn;

		for (int i = 0; i < actions.length; i++) {
			btn = new JButton(actions[i]);
			if (i == 0) {
				getRootPane().setDefaultButton(btn);
			}
			btn.addActionListener(this);
			btn.setActionCommand(actions[i]);
			pnlOkCancel.add(btn);
		}

		//pnlOkCancel.setBorder(new TitledBorder(""));

		JPanel pnlHeader = new JPanel();
		pnlHeader.setBackground(Color.WHITE);
		pnlHeader.setLayout(new BoxLayout(pnlHeader, BoxLayout.Y_AXIS));
		JLabel lblTitle = new JLabel(title);
		lblTitle.setFont(new Font("dialog", Font.BOLD, 14));
		lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
		lblTitle.setHorizontalTextPosition(SwingUtilities.CENTER);

		JTextArea txtComment = new JTextArea();
		txtComment.setWrapStyleWord(true);
		txtComment.setEditable(false);
		txtComment.setFont(new Font("dialog", Font.PLAIN, 10));
		txtComment.setText(comment);

		JPanel pnlTitle = new JPanel();
		pnlTitle.add(lblTitle);
		pnlTitle.setBackground(Color.WHITE);

		JPanel pnlHeaderIcon = new JPanel();
		pnlHeaderIcon.setBackground(Color.WHITE);
		pnlHeaderIcon.setLayout(new BorderLayout());
		JLabel _;

		URL url = ClassLoader.getSystemClassLoader().getResource(icon);

		if ( url != null )
		pnlHeaderIcon.add(
			_ =
				new JLabel(
					new ImageIcon( url 
						)),
			BorderLayout.EAST);
		pnlHeaderIcon.add(pnlHeader);

		pnlHeader.add(pnlTitle);
		pnlHeader.add(txtComment);

		pnlHeaderIcon.setBorder(new TitledBorder(""));

		super.getContentPane().add(pnlHeaderIcon, BorderLayout.NORTH);
		super.getContentPane().add(getContentPane(), BorderLayout.CENTER);

		JPanel _pnl = new JPanel();
		_pnl.setLayout(new BorderLayout());
				
		_pnl.add(new JSeparator( JSeparator.HORIZONTAL ), BorderLayout.NORTH);

		JPanel __pnl = new JPanel();
		__pnl.setLayout( new BorderLayout() );
		__pnl.add( pnlOkCancel, BorderLayout.EAST );
		
		if ( getAddonPanel() != null ) {
			__pnl.add( getAddonPanel(), BorderLayout.WEST );
		}

		_pnl.add( __pnl, BorderLayout.CENTER );

		super.getContentPane().add(_pnl, BorderLayout.SOUTH);
	}

	public Container getContentPane() {
		if (mainPanel == null) {
			mainPanel = new JPanel();
			mainPanel.setLayout(new BorderLayout());
		}
		Border b = createContentBorder();
		if (b != null)
			mainPanel.setBorder(b);
		return mainPanel;
	}

	/** @return a border for dialog content. By default none */
	protected Border createContentBorder() {
		return null;
	}

	protected Rectangle getLocationAndSize() {
		Dimension dim = getToolkit().getScreenSize();
		Rectangle r = 
			new Rectangle(
			( dim.width - getWidth() ) / 2,
			( dim.height - getHeight() ) / 2,
			getWidth(), 
			getHeight() );
		return r;
	}

	/** Call each time the non visible state is needed */
	protected void preNonVisible() {
	}

	public void setVisible( boolean state ) {
		if ( state ) {
			setBounds( getLocationAndSize() );
		} else
			preNonVisible();
		super.setVisible( state );
	}

	/** Show this dialog box without an auto center location */
	public void setVisibleWithoutAutoLocation(boolean state) {
		super.setVisible(state);
	}

	private String action;

	public void actionPerformed(ActionEvent evt) {
		action = evt.getActionCommand();
		if (isOk() || isCancel()) {
			setVisible(false);
			dispose();
		}
	}

	public String getAction() {
		return action;
	}

	public boolean isOk() {
		return OK_ACTION.equals(getAction());
	}

	public boolean isCancel() {
		return CANCEL_ACTION.equals(getAction());
	}

	/** @return the name of this dialog box */
	public String getName() {
		return getClass().getName();
	}

	public static void main(String[] args) {
		OkCancelDialog ok =
			new OkCancelDialog("", "Titre", "COMMENT GREAT\nOK .........");
		ok.setVisible(true);
	}

}

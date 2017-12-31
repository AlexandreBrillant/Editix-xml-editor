package com.japisoft.findreplace;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.text.*;

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
public class FindReplacePanel extends javax.swing.JPanel {

	JTextComponent source = null;

	/**
	 * This panel will react with a text component provided here.
	 * @param textComponent
	 *            This is the working text component */
	public FindReplacePanel(JTextComponent textComponent) {
		this( textComponent, false );
	}

	/**
	 * This panel will react with a text component provided here.
	 * @param textComponent This is the working text component 
	 * @param searchForSelected Initialize the search part with the selected text 
	 */
	public FindReplacePanel(JTextComponent textComponent, boolean searchForSelected ) {
		if ( textComponent == null )
			throw new RuntimeException( "Can't use a null textComponent !" );
		//initUI();
		
		initComponents();

		btnFind.setEnabled( false );
		btnReplace.setEnabled( false );
		btnReplaceAll.setEnabled( false );
		btnReplaceFind.setEnabled( false );
		cbFind.setEditable( true );
		cbReplace.setEditable( true );

		((JTextComponent) (cbFind.getEditor().getEditorComponent()))
				.setDocument(documentFind);
		((JTextComponent) (cbReplace.getEditor().getEditorComponent()))
				.setDocument(documentReplace);

		this.source = textComponent;
		updateTextComponent( textComponent, searchForSelected );
	}

	/** Initial find expression */
	public void setFindValue( String value ) {
		try {
			documentFind.remove( 0, documentFind.getLength() );
			documentFind.insertString( 0, value, null );
		} catch( BadLocationException exc ) {
		}
	}

	/** Update for the target text component */
	public void updateTextComponent( JTextComponent textComponent, boolean searchForSelected ) {
		this.source = textComponent;
		manager = null;
		if ( searchForSelected && textComponent != null ) {
			String t = textComponent.getSelectedText();
			if ( t != null ) {
				cbFind.setSelectedItem( t );
			}
		}		
	}
	
	/** @return the current used component */
	public JTextComponent getCurrentTextComponent() {
		return source;
	}
	
	private boolean replaceMode = true;

	/** if <code>true</code> this method will disable the replace feature */
	public void setReplaceMode( boolean replaceMode ) {
		this.replaceMode = replaceMode;
		cbReplace.setEnabled( replaceMode );
	}
	
	/**Check if this panel is inside a find/replace state 
	 * @return <code>true</code> if the replaceMode is enabled. By default <code>true</code> */
	public boolean isReplaceMode() { return replaceMode; }
	
/*	
	static {

		char[] _ = new char[ 84];
		_[ 0]=74;
		_[ 1]=70;
		_[ 2]=105;
		_[ 3]=110;
		_[ 4]=100;
		_[ 5]=82;
		_[ 6]=101;
		_[ 7]=112;
		_[ 8]=108;
		_[ 9]=97;
		_[ 10]=99;
		_[ 11]=101;
		_[ 12]=32;
		_[ 13]=45;
		_[ 14]=32;
		_[ 15]=51;
		_[ 16]=48;
		_[ 17]=32;
		_[ 18]=68;
		_[ 19]=97;
		_[ 20]=121;
		_[ 21]=32;
		_[ 22]=69;
		_[ 23]=118;
		_[ 24]=97;
		_[ 25]=108;
		_[ 26]=117;
		_[ 27]=97;
		_[ 28]=116;
		_[ 29]=105;
		_[ 30]=111;
		_[ 31]=110;
		_[ 32]=32;
		_[ 33]=86;
		_[ 34]=101;
		_[ 35]=114;
		_[ 36]=115;
		_[ 37]=105;
		_[ 38]=111;
		_[ 39]=110;
		_[ 40]=10;
		_[ 41]=40;
		_[ 42]=99;
		_[ 43]=41;
		_[ 44]=32;
		_[ 45]=50;
		_[ 46]=48;
		_[ 47]=48;
		_[ 48]=53;
		_[ 49]=32;
		_[ 50]=74;
		_[ 51]=65;
		_[ 52]=80;
		_[ 53]=73;
		_[ 54]=83;
		_[ 55]=111;
		_[ 56]=102;
		_[ 57]=116;
		_[ 58]=32;
		_[ 59]=58;
		_[ 60]=32;
		_[ 61]=104;
		_[ 62]=116;
		_[ 63]=116;
		_[ 64]=112;
		_[ 65]=58;
		_[ 66]=47;
		_[ 67]=47;
		_[ 68]=119;
		_[ 69]=119;
		_[ 70]=119;
		_[ 71]=46;
		_[ 72]=106;
		_[ 73]=97;
		_[ 74]=112;
		_[ 75]=105;
		_[ 76]=115;
		_[ 77]=111;
		_[ 78]=102;
		_[ 79]=116;
		_[ 80]=46;
		_[ 81]=99;
		_[ 82]=111;
		_[ 83]=109;
		System.out.println( new String( _ ) );		
		
	}
*/

	/**
	 * @param initialFind
	 *            Reset the find text with this value
	 * @param textComponent
	 *            This is the working text component
	 */
	public FindReplacePanel(String initialFind, JTextComponent textComponent) {
		this(textComponent);
		cbFind.setSelectedItem( initialFind );
	}

	  // Variables declaration - do not modify                     
    private javax.swing.ButtonGroup bgDirection;
    private javax.swing.ButtonGroup bgScope;
    private javax.swing.JButton btnFind;
    private javax.swing.JButton btnReplace;
    private javax.swing.JButton btnReplaceAll;
    private javax.swing.JButton btnReplaceFind;
    private javax.swing.JCheckBox cbCaseSensitive;
    private javax.swing.JComboBox cbFind;
    private javax.swing.JCheckBox cbIncremental;
    private javax.swing.JCheckBox cbEscapeSequence;
    private javax.swing.JCheckBox cbRegularExpressions;
    private javax.swing.JComboBox cbReplace;
    private javax.swing.JCheckBox cbWholeWord;
    private javax.swing.JCheckBox cbWrapSearch;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel lbInfo;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel pnlDirection;
    private javax.swing.JPanel pnlOptions;
    private javax.swing.JRadioButton rbAll;
    private javax.swing.JRadioButton rbBackward;
    private javax.swing.JRadioButton rbForward;
    private javax.swing.JRadioButton rbSelectedLines;
    // End of variables declaration                   	
	
    private void initComponents() {
        bgDirection = new javax.swing.ButtonGroup();
        bgScope = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        cbFind = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        cbReplace = new javax.swing.JComboBox();
        pnlDirection = new javax.swing.JPanel();
        rbForward = new javax.swing.JRadioButton();
        rbBackward = new javax.swing.JRadioButton();
        jPanel2 = new javax.swing.JPanel();
        rbAll = new javax.swing.JRadioButton();
        rbSelectedLines = new javax.swing.JRadioButton();
        pnlOptions = new javax.swing.JPanel();
        cbCaseSensitive = new javax.swing.JCheckBox();
        cbWholeWord = new javax.swing.JCheckBox();
        cbRegularExpressions = new javax.swing.JCheckBox();
        cbWrapSearch = new javax.swing.JCheckBox();
        cbIncremental = new javax.swing.JCheckBox();
        cbEscapeSequence = new javax.swing.JCheckBox();
        btnFind = new javax.swing.JButton();
        btnReplaceFind = new javax.swing.JButton();
        btnReplace = new javax.swing.JButton();
        btnReplaceAll = new javax.swing.JButton();
        lbInfo = new javax.swing.JLabel();

        jLabel1.setText("Find:");

//        cbFind.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel2.setText("Replace with:");

//        cbReplace.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        pnlDirection.setBorder(javax.swing.BorderFactory.createTitledBorder("Direction"));
        bgDirection.add(rbForward);
        rbForward.setSelected(true);
        rbForward.setText("Forward");
        rbForward.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rbForward.setMargin(new java.awt.Insets(0, 0, 0, 0));

        bgDirection.add(rbBackward);
        rbBackward.setText("Backward");
        rbBackward.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rbBackward.setMargin(new java.awt.Insets(0, 0, 0, 0));

        org.jdesktop.layout.GroupLayout pnlDirectionLayout = new org.jdesktop.layout.GroupLayout(pnlDirection);
        pnlDirection.setLayout(pnlDirectionLayout);
        pnlDirectionLayout.setHorizontalGroup(
            pnlDirectionLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlDirectionLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnlDirectionLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(rbForward)
                    .add(rbBackward))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlDirectionLayout.setVerticalGroup(
            pnlDirectionLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlDirectionLayout.createSequentialGroup()
                .add(rbForward)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(rbBackward))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Scope"));
        bgScope.add(rbAll);
        rbAll.setSelected(true);
        rbAll.setText("All");
        rbAll.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rbAll.setMargin(new java.awt.Insets(0, 0, 0, 0));

        bgScope.add(rbSelectedLines);
        rbSelectedLines.setText("Selected lines");
        rbSelectedLines.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rbSelectedLines.setMargin(new java.awt.Insets(0, 0, 0, 0));

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(rbAll)
                    .add(rbSelectedLines))
                .addContainerGap(35, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .add(rbAll)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(rbSelectedLines))
        );

        pnlOptions.setBorder(javax.swing.BorderFactory.createTitledBorder("Option"));
        cbCaseSensitive.setText("Case sensitive");
        cbCaseSensitive.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        cbCaseSensitive.setMargin(new java.awt.Insets(0, 0, 0, 0));

        cbWholeWord.setText("Whole word");
        cbWholeWord.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        cbWholeWord.setMargin(new java.awt.Insets(0, 0, 0, 0));

        cbRegularExpressions.setText("Reg. Exp.");
        cbRegularExpressions.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        cbRegularExpressions.setMargin(new java.awt.Insets(0, 0, 0, 0));

        cbWrapSearch.setText("Wrap search");
        cbWrapSearch.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        cbWrapSearch.setMargin(new java.awt.Insets(0, 0, 0, 0));

        cbIncremental.setText("Incremental");
        cbIncremental.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        cbIncremental.setMargin(new java.awt.Insets(0, 0, 0, 0));

        cbEscapeSequence.setText( "\\t \\n \\r \\b");
        cbEscapeSequence.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        cbEscapeSequence.setMargin(new java.awt.Insets(0, 0, 0, 0));
        
        org.jdesktop.layout.GroupLayout pnlOptionsLayout = new org.jdesktop.layout.GroupLayout(pnlOptions);
        pnlOptions.setLayout(pnlOptionsLayout);
        pnlOptionsLayout.setHorizontalGroup(
            pnlOptionsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlOptionsLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnlOptionsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(cbRegularExpressions)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlOptionsLayout.createSequentialGroup()
                        .add(pnlOptionsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(cbCaseSensitive)
                            .add(cbWholeWord))
                        .add(37, 37, 37)
                        .add(pnlOptionsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        	
                            .add(cbIncremental)
                            .add(cbWrapSearch)
                            .add(cbEscapeSequence)
                        		
                        		)
                        .add(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pnlOptionsLayout.setVerticalGroup(
            pnlOptionsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlOptionsLayout.createSequentialGroup()
                .add(pnlOptionsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(cbCaseSensitive)
                    .add(cbWrapSearch))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlOptionsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(cbWholeWord)
                    .add(cbIncremental))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlOptionsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(cbRegularExpressions)
                .add(cbEscapeSequence)
                .add(0,0,Short.MAX_VALUE)
                //.addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        )));

        btnFind.setText("Find");

        btnReplaceFind.setText("Replace/Find");

        btnReplace.setText("Replace");

        btnReplaceAll.setText("Replace All");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(pnlOptions, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(pnlDirection, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel1)
                            .add(jLabel2))
                        .add(15, 15, 15)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(cbReplace, 0, 159, Short.MAX_VALUE)
                            .add(cbFind, 0, 159, Short.MAX_VALUE)))
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(btnReplace, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(btnFind, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(layout.createSequentialGroup()
                                .add(5, 5, 5)
                                .add(btnReplaceFind))
                            .add(layout.createSequentialGroup()
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(btnReplaceAll, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                    .add(lbInfo, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 239, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(cbFind, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(cbReplace, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(14, 14, 14)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(jPanel2, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(pnlDirection, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlOptions, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(btnFind)
                    .add(btnReplaceFind))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(btnReplace)
                    .add(btnReplaceAll))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 20, Short.MAX_VALUE)
                .add(lbInfo))
        );
    }// </editor-fold>                        

	
	
	// Prepare the user interface
	private void initUI() {
/*		border1 = BorderFactory.createLineBorder(SystemColor.controlText, 1);
		titledBorder1 = new TitledBorder(border1, "Direction");
		border2 = BorderFactory.createLineBorder(SystemColor.controlText, 1);
		titledBorder2 = new TitledBorder(border2, "Scope");
		border3 = BorderFactory.createLineBorder(SystemColor.controlText, 1);
		titledBorder3 = new TitledBorder(border3, "Options");
		lblReplace.setDoubleBuffered(false);
		lblReplace.setDisplayedMnemonic('E');
		lblReplace.setLabelFor(cbReplace);
		lblReplace.setText("Replace with :");
		lblFind.setDisplayedMnemonic('F');
		lblFind.setLabelFor(cbFind);
		lblFind.setText("Find :");
		this.setLayout(gridBagLayout1);
		cbFind.setEnabled(true);
		cbFind.setEditable(true);
		cbFind.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						find();
					}
				} );
		cbReplace.setEditable(true);		
		pnlDirection.setBorder(titledBorder1);
		pnlDirection.setLayout(gridBagLayout3);
		rbForward.setMnemonic('O');
		rbForward.setSelected(true);
		rbForward.setText("Forward");
		rbBackward.setMnemonic('B');
		rbBackward.setText("Backward");
		jPanel1.setBorder(titledBorder2);
		jPanel1.setMaximumSize(new Dimension(32767, 32767));
		jPanel1.setLayout(gridBagLayout4);
		rbAll.setMnemonic('L');
		rbAll.setSelected(true);
		rbAll.setText("All");
		rbSelectedLines.setMnemonic('T');
		rbSelectedLines.setText("Selected lines");
		pnlOptions.setLayout(gridBagLayout5);
		pnlOptions.setBorder(titledBorder3);
		cbCaseSensitive.setMnemonic('C');
		cbCaseSensitive.setText("Case sensitive");
		cbWholeWord.setMnemonic('W');
		cbWholeWord.setText("Whole word");
		cbRegularExpressions.setMnemonic('X');
		cbRegularExpressions.setText("Regular expressions");
		cbWrapSearch.setMnemonic('P');
		cbWrapSearch.setText("Wrap search");
		cbIncremental.setMnemonic('I');
		cbIncremental.setText("Incremental");
		pnlButtons.setLayout(gridBagLayout2);
		btnFind.setMnemonic('N');
		btnFind.setText("Find");
		btnReplace.setMnemonic('R');
		btnReplace.setText("Replace");
		btnReplaceFind.setMnemonic('D');
		btnReplaceFind.setText("Replace/Find");
		btnReplaceAll.setMnemonic('A');
		btnReplaceAll.setText("Replace All");
		lblInfo.setText("");
		this.add(cbFind, new GridBagConstraints(1, 0, 2, 1, 1.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(5, 0, 0, 7), 59, 0));
		this.add(lblFind, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,
						10, 0, 54), 0, 0));
		this.add(cbReplace, new GridBagConstraints(1, 1, 2, 1, 1.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(0, 0, 0, 7), 59, 0));
		this.add(lblReplace, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(
						10, 10, 0, 0), 0, 0));
		this.add(pnlDirection, new GridBagConstraints(0, 2, 2, 1, 1.0, 1.0,
				GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(1,
						1, 0, 1), 90, 5));
		pnlDirection.add(rbForward, new GridBagConstraints(0, 0, 1, 1, 0.0,
				0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
				new Insets(0, 0, 0, 0), 0, 0));
		pnlDirection.add(rbBackward, new GridBagConstraints(0, 1, 1, 1, 0.0,
				0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
				new Insets(0, 0, 0, 0), 0, 0));
		this.add(jPanel1, new GridBagConstraints(2, 2, 1, 1, 1.0, 1.0,
				GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(1,
						1, 1, 1), 14, 5));
		jPanel1.add(rbAll, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,
						0, 0, 0), 0, 0));
		jPanel1.add(rbSelectedLines, new GridBagConstraints(0, 1, 1, 1, 0.0,
				0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
				new Insets(0, 0, 0, 0), 0, 0));
		this.add(pnlOptions, new GridBagConstraints(0, 3, 3, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
						1, 1, 1, 1), 11, 8));
		pnlOptions.add(cbCaseSensitive, new GridBagConstraints(0, 0, 1, 1, 0.0,
				0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
				new Insets(0, 0, 0, 0), 0, 0));
		pnlOptions.add(cbWholeWord, new GridBagConstraints(0, 1, 1, 1, 0.0,
				0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
				new Insets(0, 0, 0, 0), 0, 0));
		pnlOptions.add(cbRegularExpressions, new GridBagConstraints(0, 2, 1, 1,
				0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
				new Insets(0, 0, 0, 0), 0, 0));
		pnlOptions.add(cbWrapSearch, new GridBagConstraints(1, 0, 1, 1, 0.0,
				0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
				new Insets(0, 0, 0, 0), 0, 0));
		pnlOptions.add(cbIncremental, new GridBagConstraints(1, 1, 1, 1, 0.0,
				0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
				new Insets(0, 0, 0, 0), 0, 0));
		this.add(pnlButtons, new GridBagConstraints(0, 4, 3, 1, 0.0, 0.0,
				GridBagConstraints.SOUTH, GridBagConstraints.HORIZONTAL,
				new Insets(5, 20, 5, 20), 0, 0));
		
		pnlButtons.setLayout( new GridLayout( 2, 2, 2, 2 ) );
		pnlButtons.add( btnFind );
		pnlButtons.add( btnReplaceFind );
		pnlButtons.add( btnReplace );
		pnlButtons.add( btnReplaceAll );
		
		/*pnlButtons.add(btnReplaceFind, new GridBagConstraints(1, 0, 1, 1, 0.0,
				0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
				new Insets(1, 1, 1, 1), 0, 0));
		pnlButtons.add(btnReplaceAll, new GridBagConstraints(1, 1, 1, 1, 0.0,
				0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
				new Insets(1, 1, 1, 1), 0, 0));
		pnlButtons.add(btnReplace, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						1, 1, 1, 1), 0, 0));
		pnlButtons.add(btnFind, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						1, 1, 1, 1), 0, 0));
	
		this.add(lblInfo, new GridBagConstraints(0, 5, 3, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(0, 0, 0, 0), 2, 0));

		btnFind.setEnabled( false );
		btnReplace.setEnabled( false );
		btnReplaceAll.setEnabled( false );
		btnReplaceFind.setEnabled( false );

		((JTextComponent) (cbFind.getEditor().getEditorComponent()))
				.setDocument(documentFind);
		((JTextComponent) (cbReplace.getEditor().getEditorComponent()))
				.setDocument(documentReplace);

		ButtonGroup group1 = new ButtonGroup();
		group1.add(rbForward);
		group1.add(rbBackward);

		ButtonGroup group2 = new ButtonGroup();
		group2.add(rbAll);
		group2.add(rbSelectedLines);*/
	}

	private CustomActionListener customActionListener = new CustomActionListener();

	GridBagLayout gridBagLayout3 = new GridBagLayout();
	GridBagLayout gridBagLayout4 = new GridBagLayout();
	GridBagLayout gridBagLayout5 = new GridBagLayout();

	public void addNotify() {
		super.addNotify();
		btnFind.addActionListener(customActionListener);
		btnReplace.addActionListener(customActionListener);
		btnReplaceAll.addActionListener(customActionListener);
		btnReplaceFind.addActionListener(customActionListener);
		//
		rbForward.addActionListener(customActionListener);
		rbBackward.addActionListener(customActionListener);
		rbAll.addActionListener(customActionListener);
		rbSelectedLines.addActionListener(customActionListener);

		cbIncremental.addActionListener(customActionListener);
		cbRegularExpressions.addActionListener(customActionListener);

		if (getMotif().length > 0)
			btnFind.requestFocus();
		else {
			(cbFind.getEditor().getEditorComponent()).requestFocus();
		}
		initReplaceManager();

		Window w = SwingUtilities.getWindowAncestor( this );
		if ( w != null ) {
			if ( w instanceof JWindow ) {
				JWindow jw = ( JWindow )w;
				jw.getRootPane().setDefaultButton( btnFind );
			}
		}

		if ( (cbFind.getEditor()).getEditorComponent() instanceof JTextField ) {
			( ( JTextField )( cbFind.getEditor() ).getEditorComponent()).addActionListener( 
					customActionListener );
		}

	}

	public void removeNotify() {
		super.removeNotify();
		btnFind.removeActionListener( customActionListener );
		btnReplace.removeActionListener( customActionListener );
		btnReplaceAll.removeActionListener( customActionListener );
		btnReplaceFind.removeActionListener( customActionListener );
		//
		rbForward.removeActionListener( customActionListener );
		rbBackward.removeActionListener( customActionListener );
		rbAll.removeActionListener( customActionListener );
		rbSelectedLines.removeActionListener( customActionListener );

		cbIncremental.removeActionListener( customActionListener );
		cbRegularExpressions.removeActionListener( customActionListener );
		cbFind.removeActionListener( customActionListener );
		
		if ( (cbFind.getEditor()).getEditorComponent() instanceof JTextField ) {
			( ( JTextField )( cbFind.getEditor() ).getEditorComponent()).removeActionListener( 
					customActionListener );
		}		
	}
	
	////////////////////////////////////////////

	private FindReplaceManager manager = null;

	private void prepareReplaceManager() {
		if (manager == null) {
			manager = new FindReplaceManager();
		}
		manager.caseSensitive = cbCaseSensitive.isSelected();
		manager.escapeSequence = cbEscapeSequence.isSelected();
		manager.forward = rbForward.isSelected();
		manager.motif = getMotif();
		manager.regularExpressions = cbRegularExpressions.isSelected();
		manager.scope_all = rbAll.isSelected();
		manager.wholeWord = cbWholeWord.isSelected();
		manager.wrapSearch = cbWrapSearch.isSelected();
	}

	private void initReplaceManager() {
		prepareReplaceManager();
		manager.init();
	}

	private char[] getMotif() {
		if ( cbFind.getSelectedItem() == null )
			return new char[] {};
		
		String text = ( ( JTextComponent )cbFind.getEditor().getEditorComponent() )
				.getText();

		if ( cbEscapeSequence.isSelected() ) {
			text = text.replace( "\\t", "\t" );
			text = text.replace( "\\r", "\r" );
			text = text.replace( "\\n", "\n" );
			text = text.replace( "\\b", "\b" );
		}

		char[] motif = text.toCharArray(); 		
		return motif; 
	}

	/**
	 * Search a text sequence
	 * @return <code>false</code> only if no text is matching */
	public boolean find() {
		return find( -1 );
	}
	
	private boolean find( int command ) {
		return find( command, null );
	}
	
	private boolean find( int command, char[] defaultContent ) {
		if ( command == -1 )
			prepareReplaceManager();

		manager.motifCaret = -1;

		oldSelectionStart = source.getSelectionStart();
		oldSelectionEnd = source.getSelectionEnd();
				
		final int c = manager.nextSearch( source, defaultContent );

		if ( c <= -1 ) {
			lbInfo.setText( "String Not Found" );
			if ( command == -1 )
				manager.init();
			
			btnReplace.setEnabled( false );
			btnReplaceFind.setEnabled( false );

			return false;
		} else {
			manager.caret = manager.nextCaret;

			if ( command == -2 )
				selectMotifAt2( c );
			else
				selectMotifAt( c );			
			
		}
		return true;
	}

	private void selectMotifAt( int c ) {
		lbInfo.setText( null );
		giveFocusToSource();
		
		source.select( c, c + manager.getMotifLength() );
		
		btnReplace.setEnabled( true && replaceMode );
		btnReplaceFind.setEnabled( true && replaceMode );
		
		addOneItem( cbFind, ( String )cbFind.getSelectedItem() );
	}

	private ArrayList al = null;
	
	/** For ReplaceALL */
	private void selectMotifAt2( int c ) {
		if ( al == null )
			al = new ArrayList();
		al.add( new Point( c, c + manager.getMotifLength() ) );
	}

	private void addOneItem( JComboBox cb, String item ) {
		if ( item == null || "".equals( item ) )
			return;
		for ( int i = 0; i < cb.getItemCount(); i++ ) {
			if ( cb.getItemAt( i ).equals( item ) )
				return;
		}
		cb.addItem( item );
	}

	int oldSelectionStart = -1;
	int oldSelectionEnd = -1;
	int deltaPerf = 0;
	
	/** Replace the selected part */
	public void replace() {
				
		if ( source.getSelectionStart() < source.getSelectionEnd() ) {
			
			int motifLength = source.getSelectedText().length();

			String replaceContent = cbReplace.getSelectedItem() == null ? "" : 
				( String )cbReplace.getSelectedItem();

			int lastCaret = manager.caret;

			// Replace the selection by this value
			source.replaceSelection( replaceContent );

			addOneItem( cbReplace, replaceContent );
			
			// Reset the inner state
			manager.init();
			manager.caret = ( lastCaret + ( replaceContent.length() - motifLength ) );

			oldSelectionEnd = oldSelectionEnd + ( replaceContent.length() - motifLength );

			if ( rbSelectedLines.isSelected() ) {
				if  ( oldSelectionStart > -1 && oldSelectionEnd > -1 ) {
					source.setSelectionStart( oldSelectionStart );
					source.setSelectionEnd( oldSelectionEnd );
				}
			}

			// Reset the caret for the manager
			btnReplace.setEnabled( false );
			btnReplaceFind.setEnabled( false );

		} else {
			
			btnReplace.setEnabled( false );
			btnReplaceFind.setEnabled( false );
			
		}
	}

	/** Replace all the text by the replacing part */
	public void replaceAll() {
		prepareReplaceManager();
		int match = 0;
		manager.forward = true;
		manager.wrapSearch = false;
		
		if ( rbSelectedLines.isSelected() )
			manager.caret = -1;
		else
			manager.caret = 0;

		// Store all the content once
		char[] content = source.getText().toCharArray();
		
		boolean ok = find( -2, content );

		while ( ok ) {
			match++;
			ok = find( -2, content );
		}

		if ( match == 0 ) {
			lbInfo.setText( "String Not found" );
		} else { 
			
			boolean toTop = true;
			if ( al.size() > 2 ) {
				Point p1 = ( Point )al.get( 0 );
				Point p2 = ( Point )al.get( al.size() - 1 );
				toTop = p1.x < p2.x;
			}
			
			int i = toTop ? al.size() - 1 : 0;
			int e = toTop ? 0 : al.size() - 1;
			int s = toTop ? -1 : +1;
			
			String replaceContent = cbReplace.getSelectedItem() == null ? "" : 
				( String )cbReplace.getSelectedItem();			

			int max = al.size();
			
			for ( int n = i; n >= 0 && n < max ; n += s ) {
				Point p = ( Point )al.get( n );
				source.select( p.x, p.y );				
				source.replaceSelection( replaceContent );
			}

			al = null;
			
			addOneItem( cbReplace, ( String )cbReplace.getSelectedItem() );
			lbInfo.setText( match + " Match replaced" );
		}

		if ( match > 0 && rbSelectedLines.isSelected() )
			rbAll.setSelected( true );
	}

	/** Replace the current selection and find for the next matching text 
	 * @return <code>true</code> if the next find is matching */
	public boolean replaceFind() {
		return replaceFind( -1 );
	}

	private boolean replaceFind( int command ) {
		return replaceFind( command, null );
	}
	
	private boolean replaceFind( int command, char[] defaultContent ) {
		if ( command != -2 )
			replace();
		if ( find( command, defaultContent ) ) {
			btnReplaceFind.setEnabled( true );
			return true;
		}
		return false;
	}

	private void giveFocusToSource() {
		Window w = SwingUtilities.getWindowAncestor(source);
		if ( w != null ) // ??
			w.toFront();
		source.requestFocus();
	}

	private void giveFocusToFind() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				Window w = SwingUtilities.getWindowAncestor(cbFind);
				if ( w != null ) // ??
					w.toFront();
				cbFind.getEditor().getEditorComponent().requestFocus();
			}
		});
	}

	CustomFindPlainDocument documentFind = new CustomFindPlainDocument();
	CustomReplacePlainDocument documentReplace = new CustomReplacePlainDocument();

	class FindAction extends AbstractAction {
		public FindAction() {
			putValue( Action.NAME, "Find" );
		}
		public void actionPerformed( ActionEvent e ) {
			System.out.println( "FIND");
			find();
		}
	}

	class CustomActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == btnFind || e.getSource() instanceof JTextField ) { // || e.getSource() == cbFind ) {
				find();
			} else if (e.getSource() == btnReplace) {
				replace();
			} else if (e.getSource() == btnReplaceAll) {
				replaceAll();
			} else if (e.getSource() == btnReplaceFind) {
				replaceFind();
			} else {

				if (e.getSource() == cbIncremental) {

					int length = ((JTextComponent) cbReplace.getEditor()
							.getEditorComponent()).getDocument().getLength();
					int length2 = ((JTextComponent) cbFind.getEditor()
							.getEditorComponent()).getDocument().getLength();

					btnFind.setEnabled( length > 0 );
					btnReplace.setEnabled( length > 0 && length2 > 0 );
					btnReplaceFind.setEnabled( length > 0 && length2 > 0 );
					btnReplaceAll.setEnabled( length > 0 && length2 > 0 && replaceMode );

					boolean state = cbIncremental.isSelected();
					cbRegularExpressions.setEnabled(!state);

				} else if (e.getSource() == cbRegularExpressions) {

					boolean state = cbRegularExpressions.isSelected();
					rbBackward.setEnabled( !state );
					rbForward.setEnabled( !state );
					cbIncremental.setEnabled( !state );
					cbWholeWord.setEnabled( !state );

				} else {
					prepareReplaceManager();
					manager.init();
				}
			}
		}
	}

	class CustomFindPlainDocument extends PlainDocument {

		public void remove(int offs, int len) throws BadLocationException {
			super.remove(offs, len);
			btnFind.setEnabled(getLength() > 0);

			btnReplaceAll.setEnabled( documentFind.getLength() > 0 && replaceMode );

			if (cbIncremental.isSelected()) {
				backwardIncrementalFind();
			}
		}

		private void backwardIncrementalFind() {
			forwardIncrementalFind();
		}

		private int oldIncrementalCaret = -1;

		private void forwardIncrementalFind() {
			manager.caret = oldIncrementalCaret;
			manager.motifCaret = -1;
			boolean ok = find( -1 );
			if ( ok ) {
				giveFocusToFind();

				oldIncrementalCaret = manager.forward ? 
						(manager.caret - manager.motif.length)
							: (manager.caret + manager.motif.length);
			}
		}

		public void insertString( int offs, String str, AttributeSet a )
				throws BadLocationException {
			super.insertString( offs, str, a );
			btnFind.setEnabled( getLength() > 0 );
			btnReplaceAll.setEnabled( documentFind.getLength() > 0 && replaceMode );
			if ( cbIncremental.isSelected() ) {
				forwardIncrementalFind();
			}
		}
	}

	class CustomReplacePlainDocument extends PlainDocument {

		public void remove(int offs, int len) throws BadLocationException {
			super.remove( offs, len );
			btnReplaceAll.setEnabled(
					documentFind.getLength() > 0 );
		}

		public void insertString(int offs, String str, AttributeSet a)
				throws BadLocationException {
			super.insertString(offs, str, a);
			btnReplaceAll.setEnabled( documentFind.getLength() > 0 );
		}
	}

	/** When stopping using this panel. It will free inner references. You CANT use
	 * this instance after calling this method */
	public void dispose() {
		source = null;
	}

	public static void main( String[] args ) {
		
		JFrame f = new JFrame();
		f.add( new FindReplacePanel( new JTextArea() ) );
		f.setVisible( true );
		
	}
	
}

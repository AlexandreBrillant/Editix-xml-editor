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

package com.japisoft.editix.action.search;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.*;

import com.japisoft.editix.ui.windows.EditixFrame;
import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.ui.findreplace.FindReplaceManager;
import com.japisoft.framework.ui.findreplace.Findable;
import com.japisoft.xmlpad.XMLContainer;
import com.sleepycat.dbxml.XmlContainer;

/**
 * Here the main panel for finding/replacing a part of the text. The user must
 * include it inside a dialog box like this :
 *<pre>
 *  // Here our text component 
 *  JTextArea area = new JTextArea();
 *  JFrame frame = new JFrame();
 *  frame.getContentPane().add( new JScrollPane( area ), BorderLayout.CENTER );
 *  frame.setSize( 500 ,500 );
 * 
 *  // Here a button calling the findReplace dialog
 *  JButton button = new JButton( "Search" );
 *  frame.getContentPane().add( button, BorderLayout.NORTH );
 *  ...
 *  JDialog findReplace = new JDialog( frame );
 *  findReplace.getContentPane().add( new FindReplacePanel( area ) );
 *  findReplace.setSize( 300, 400 );
 *  findReplace.setVisible( true );
 * </pre>
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.1
 */
public class FindReplacePanel2 extends javax.swing.JPanel implements Findable {

	private JTextComponent source = null;

	/**
	 * This panel will react with a text component provided here.
	 * @param textComponent
	 *            This is the working text component */
	public FindReplacePanel2(JTextComponent textComponent) {
		this( textComponent, false );
	}
	
	/**
	 * This panel will react with a text component provided here.
	 * @param textComponent This is the working text component 
	 * @param searchForSelected Initialize the search part with the selected text 
	 */
	public FindReplacePanel2(JTextComponent textComponent, boolean searchForSelected ) {
		if ( textComponent == null )
			throw new RuntimeException( "Can't use a null textComponent !" );
		
		Color c = getBackground().darker();
		
		setBorder( BorderFactory.createCompoundBorder( new LineBorder( c, 1 ), new EmptyBorder( 4, 4, 4, 4 ) ) );		
		
		initComponents();
				
		btnFind.setEnabled( false );
		btnReplace.setEnabled( false );
		btnReplaceAll.setEnabled( false );
		btnReplaceFind.setEnabled( false );
		cbFind.setEditable( true );
		cbReplace.setEditable( true );
		
		btnFind.setOpaque( false );
		
		((JTextComponent) (cbFind.getEditor().getEditorComponent()))
				.setDocument(documentFind);
		((JTextComponent) (cbReplace.getEditor().getEditorComponent()))
				.setDocument(documentReplace);

		this.source = textComponent;
		updateTextComponent( textComponent, searchForSelected );
		
		
		panelOptions.getParent().remove( panelOptions );
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
	
	/**
	 * @param initialFind
	 *            Reset the find text with this value
	 * @param textComponent
	 *            This is the working text component
	 */
	public FindReplacePanel2(String initialFind, JTextComponent textComponent) {
		this(textComponent);
		cbFind.setSelectedItem( initialFind );
	}


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        bgDirection = new javax.swing.ButtonGroup();
        bgScope = new javax.swing.ButtonGroup();
        lbInfo = new javax.swing.JLabel();
        panelOptions = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        cbReplace = new javax.swing.JComboBox();
        pnlOption = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        rbForward = new javax.swing.JRadioButton();
        rbBackward = new javax.swing.JRadioButton();
        jPanel4 = new javax.swing.JPanel();
        rbAll = new javax.swing.JRadioButton();
        rbSelectedLines = new javax.swing.JRadioButton();
        jPanel5 = new javax.swing.JPanel();
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
        panelFind = new javax.swing.JPanel();
        cbFind = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        btnFind2 = new javax.swing.JButton();
        btnOpenClose = new javax.swing.JButton();
        btnClose = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());
        add(lbInfo, java.awt.BorderLayout.PAGE_END);

        jLabel2.setText("Replace:");

        cbReplace.setEditable(true);

        bgDirection.add(rbForward);
        rbForward.setSelected(true);
        rbForward.setText("Forward");

        bgDirection.add(rbBackward);
        rbBackward.setText("Backward");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(rbForward)
                    .addComponent(rbBackward))
                .addContainerGap(519, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(rbForward)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(rbBackward)
                .addGap(0, 50, Short.MAX_VALUE))
        );

        pnlOption.addTab("Direction", jPanel3);

        bgScope.add(rbAll);
        rbAll.setSelected(true);
        rbAll.setText("All");

        bgScope.add(rbSelectedLines);
        rbSelectedLines.setText("Selected lines");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(rbAll)
                    .addComponent(rbSelectedLines))
                .addGap(0, 505, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(rbAll)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rbSelectedLines)
                .addGap(0, 53, Short.MAX_VALUE))
        );

        pnlOption.addTab("Scope", jPanel4);

        cbCaseSensitive.setText("Case sensitive");

        cbWholeWord.setText("Whole word");

        cbRegularExpressions.setText("Reg. Exp.");

        cbWrapSearch.setText("Wrap search");

        cbIncremental.setText("Incremental");

        cbEscapeSequence.setText("\\t \\n \\r \\b");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                            .addComponent(cbCaseSensitive)
                            .addGap(32, 32, 32))
                        .addGroup(jPanel5Layout.createSequentialGroup()
                            .addComponent(cbWholeWord)
                            .addGap(44, 44, 44)))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(cbRegularExpressions)
                        .addGap(54, 54, 54)))
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cbEscapeSequence)
                    .addComponent(cbIncremental)
                    .addComponent(cbWrapSearch))
                .addGap(0, 382, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbCaseSensitive)
                    .addComponent(cbWrapSearch))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbWholeWord)
                    .addComponent(cbIncremental))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbRegularExpressions)
                    .addComponent(cbEscapeSequence))
                .addGap(0, 30, Short.MAX_VALUE))
        );

        pnlOption.addTab("Option", jPanel5);

        btnFind.setText("Find");

        btnReplaceFind.setText("Replace/Find");

        btnReplace.setText("Replace");

        btnReplaceAll.setText("Replace all");

        javax.swing.GroupLayout panelOptionsLayout = new javax.swing.GroupLayout(panelOptions);
        panelOptions.setLayout(panelOptionsLayout);
        panelOptionsLayout.setHorizontalGroup(
            panelOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelOptionsLayout.createSequentialGroup()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbReplace, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(12, 12, 12))
            .addComponent(pnlOption)
            .addGroup(panelOptionsLayout.createSequentialGroup()
                .addComponent(btnFind)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnReplaceFind)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnReplace)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnReplaceAll)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        panelOptionsLayout.setVerticalGroup(
            panelOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelOptionsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(cbReplace, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlOption, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnFind)
                    .addComponent(btnReplaceFind)
                    .addComponent(btnReplace)
                    .addComponent(btnReplaceAll))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        add(panelOptions, java.awt.BorderLayout.CENTER);

        cbFind.setEditable(true);

        jLabel1.setText("");
        jLabel1.setPreferredSize( new Dimension( 100, 10 ) );

        
        btnFind2.setFont(new java.awt.Font("Tahoma", 0, 8));
        
        btnFind2.setText(">");
        btnFind2.setBorderPainted(false);
        btnFind2.setContentAreaFilled(false);
        btnFind2.setFocusPainted(false);
        btnFind2.setFocusable(false);
        btnFind2.setMargin(null);
        btnFind2.setOpaque(false);

        btnOpenClose.setText("v");
        btnOpenClose.setBorderPainted(false);
        btnOpenClose.setContentAreaFilled(false);
        btnOpenClose.setMargin(null);

        btnClose.setText("X");
        btnClose.setBorderPainted(false);
        btnClose.setContentAreaFilled(false);
        btnClose.setFocusPainted(false);
        btnClose.setFocusable(false);
        btnClose.setOpaque(false);

        javax.swing.GroupLayout panelFindLayout = new javax.swing.GroupLayout(panelFind);
        panelFind.setLayout(panelFindLayout);
        panelFindLayout.setHorizontalGroup(
            panelFindLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFindLayout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbFind, 0, 464, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnFind2, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(7, 7, 7)
                .addComponent(btnOpenClose, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnClose, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        panelFindLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnFind2, btnOpenClose});

        panelFindLayout.setVerticalGroup(
            panelFindLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFindLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(cbFind, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jLabel1)
                .addComponent(btnFind2)
                .addComponent(btnOpenClose)
                .addComponent(btnClose))
        );

        panelFindLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnFind2, btnOpenClose});

        add(panelFind, java.awt.BorderLayout.PAGE_START);
    }// </editor-fold>                        


    // Variables declaration - do not modify                     
    private javax.swing.ButtonGroup bgDirection;
    private javax.swing.ButtonGroup bgScope;
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnFind;
    private javax.swing.JButton btnFind2;
    private javax.swing.JButton btnOpenClose;
    private javax.swing.JButton btnReplace;
    private javax.swing.JButton btnReplaceAll;
    private javax.swing.JButton btnReplaceFind;
    private javax.swing.JCheckBox cbCaseSensitive;
    private javax.swing.JCheckBox cbEscapeSequence;
    private javax.swing.JComboBox<String> cbFind;
    private javax.swing.JCheckBox cbIncremental;
    private javax.swing.JCheckBox cbRegularExpressions;
    private javax.swing.JComboBox<String> cbReplace;
    private javax.swing.JCheckBox cbWholeWord;
    private javax.swing.JCheckBox cbWrapSearch;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JLabel lbInfo;
    private javax.swing.JPanel panelFind;
    private javax.swing.JPanel panelOptions;
    private javax.swing.JTabbedPane pnlOption;
    private javax.swing.JRadioButton rbAll;
    private javax.swing.JRadioButton rbBackward;
    private javax.swing.JRadioButton rbForward;
    private javax.swing.JRadioButton rbSelectedLines;
    // End of variables declaration 	
    
	// Prepare the user interface
	private void initUI() {
	}

	private CustomActionListener customActionListener = new CustomActionListener();

	GridBagLayout gridBagLayout3 = new GridBagLayout();
	GridBagLayout gridBagLayout4 = new GridBagLayout();
	GridBagLayout gridBagLayout5 = new GridBagLayout();

	public void addNotify() {
		super.addNotify();
		btnFind.addActionListener(customActionListener);
		btnFind2.addActionListener( customActionListener );
		btnReplace.addActionListener(customActionListener);
		btnReplaceAll.addActionListener(customActionListener);
		btnReplaceFind.addActionListener(customActionListener);
		btnClose.addActionListener( customActionListener );
		btnOpenClose.addActionListener( customActionListener );
		
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
		btnFind2.removeActionListener( customActionListener );
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
		
		btnClose.removeActionListener( customActionListener );
		btnOpenClose.removeActionListener( customActionListener );
		
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
		String text = ( ( JTextComponent )cbFind.getEditor().getEditorComponent() )
				.getText();
		
		if ( text == null )
			return new char[] {};

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
	
	private void setMessage( String txt ) {
		ApplicationModel.fireApplicationValue( "information", txt );
	}
	
	private boolean find( int command, char[] defaultContent ) {
		if ( command == -1 )
			prepareReplaceManager();
		
		manager.motif = getMotif();

		XMLContainer container = ( EditixFrame.THIS.getSelectedContainer());
		if ( container == null )
			return false;
		if ( container.getEditor() != source ) {
			updateTextComponent( container.getEditor(), true );
			prepareReplaceManager();
		}
		
		manager.motifCaret = -1;

		oldSelectionStart = source.getSelectionStart();
		oldSelectionEnd = source.getSelectionEnd();

		final int c = manager.nextSearch( source, defaultContent );

		if ( c <= -1 ) {
			
			setMessage( "String Not Found" );
			
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
			setMessage( "String Not found" );
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
			setMessage( match + " Match replaced" );
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
			find();
		}
	}
	
	class CustomActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if ( e.getSource() == btnOpenClose ) {
				if ( panelOptions.getParent() == null )
					add( panelOptions, BorderLayout.CENTER );
				else
					remove( panelOptions );
				JDialog win = ( JDialog )SwingUtilities.getWindowAncestor( panelFind );
				if ( win != null ) {
					win.pack();
				}
			} else
			if ( e.getSource() == btnClose ) {
				JDialog win = ( JDialog )SwingUtilities.getWindowAncestor( panelFind );
				win.setVisible( false );
			} else			
			if (e.getSource() == btnFind || e.getSource() == btnFind2 || e.getSource() instanceof JTextField) { // || e.getSource() == cbFind ) {
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
		f.add( new FindReplacePanel2( new JTextArea() ) );
		f.setVisible( true );
		
	}
	
}
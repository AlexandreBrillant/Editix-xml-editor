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

package com.japisoft.editix.ui.leftpanels.charref;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import com.japisoft.editix.ui.windows.EditixFrame;
import com.japisoft.framework.ui.table.ExportableTable;
import com.japisoft.xmlpad.XMLContainer;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0 */
public class CharRefUI extends JPanel implements 
	ListSelectionListener, 
	MouseListener, 
	ActionListener {

  private JScrollPane spTable = new JScrollPane();

  private JTable tbChar = new ExportableTable() {
    public TableCellRenderer getDefaultRenderer(Class columnClass) {
		return new CustomCellRenderer();
	}
  };
  
  private JPanel pnlInfo = new JPanel();
  private TitledBorder titledBorder1;
  private JLabel lbHexa = new JLabel();
  private JLabel lbDec = new JLabel();
  private JTextField tfHexa = new JTextField();
  private JTextField tfDec = new JTextField();
  private JButton btn = new JButton( "Insert" );
  private GridBagLayout gridBagLayout1 = new GridBagLayout();
  private GridBagLayout gridBagLayout2 = new GridBagLayout();

  public CharRefUI() {
      init( new CharRefAllTableModel() );
  }

  void init( TableModel model ) {
	tbChar.getSelectionModel().setSelectionMode(
			ListSelectionModel.SINGLE_SELECTION );
    titledBorder1 = new TitledBorder(
    		BorderFactory.createLineBorder(
    				new Color(153, 153, 153),
    				2),
    		"CharRef");
    this.setLayout(gridBagLayout2);
    pnlInfo.setBorder(titledBorder1);
    pnlInfo.setLayout(gridBagLayout1);
    lbHexa.setText("Hexa (&#x...;)");
    lbDec.setText("Dec (&#...;)");
    tfHexa.setText("");
    this.add(spTable, new GridBagConstraints(0, 0, 1, 1, 1.0, 4.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(13, 12, 0, 16), 0, 0));
    spTable.getViewport().add(tbChar, null);
    this.add(pnlInfo, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0
            ,GridBagConstraints.SOUTH, GridBagConstraints.BOTH, new Insets(6, 12, 10, 16), 0, 0));
    pnlInfo.add(lbDec, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(12, 9, 15, 35), 0, 0));
    pnlInfo.add(lbHexa, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(7, 9, 0, 0), 0, 0));
    pnlInfo.add(tfHexa, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0),0, 0));
    pnlInfo.add(tfDec, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0),0, 0));
    pnlInfo.add(btn, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.RELATIVE, new Insets(0,0,0,0),0, 0));
    tbChar.setModel( model );
    tfDec.setDocument( new DeciDocument() );
    tfHexa.setDocument( new HexaDocument() );
    setPreferredSize( new Dimension( 50, 50 ) );
  }

  public void addNotify() {
  	super.addNotify();
  	tbChar.addMouseListener( this );
  	btn.addActionListener( this );
  	tbChar.getSelectionModel().addListSelectionListener( this );
  }

  public void removeNotify() {
  	super.removeNotify();
  	tbChar.removeMouseListener( this );
  	btn.removeActionListener( this );
  	tbChar.getSelectionModel().removeListSelectionListener( this );
  }

  public void actionPerformed(ActionEvent e) {
		XMLContainer container = EditixFrame.THIS.getSelectedContainer();
		if ( container == null )
			return;
		container.getEditor().insertText( getSelectedChar() );	  
  }  

  public void mouseClicked(MouseEvent e) {
  	valueChanged( null );
  	if ( e.getClickCount() > 1 ) {
  		actionPerformed( null );
  	}
  }

  public void mouseEntered(MouseEvent e) {}
  public void mouseExited(MouseEvent e) {}
  public void mousePressed(MouseEvent e) {}
  public void mouseReleased(MouseEvent e) {}

  public void valueChanged( ListSelectionEvent e ) {
  	int col = tbChar.getSelectedColumn();
  	int row = tbChar.getSelectedRow();
  	Character c = (Character)tbChar.getModel().getValueAt( row, col );
  	enabledDeci = false;
  	tfDec.setText( "" + (int)c.charValue() );
  	enabledDeci = true;
  	enabledHexa = false;
  	tfHexa.setText( Integer.toHexString( c.charValue() ) );
  	enabledHexa = true;
  }  

  public String getSelectedChar() {
  	return "&#x" +
  			Integer.toHexString(
  			((Character)tbChar.getModel().getValueAt( 
  			tbChar.getSelectedRow(),
			tbChar.getSelectedColumn() )).charValue()) + ";";
  }

  public void scrollToCenter(JTable table, int rowIndex, int vColIndex) {
    if (!(table.getParent() instanceof JViewport)) {
        return;
    }
    JViewport viewport = (JViewport)table.getParent();
    Rectangle rect = table.getCellRect(rowIndex, vColIndex, true);
    Rectangle viewRect = viewport.getViewRect();
    rect.setLocation( rect.x - viewRect.x, rect.y - viewRect.y );

    int centerX = (viewRect.width-rect.width)/2;
    int centerY = (viewRect.height-rect.height)/2;

    if (rect.x < centerX) {
        centerX = -centerX;
    }
    if (rect.y < centerY) {
        centerY = -centerY;
    }
    rect.translate(centerX, centerY);
    viewport.scrollRectToVisible(rect);
  }

  boolean enabledDeci = true;
  boolean enabledHexa = true;
  
  class DeciDocument extends PlainDocument {
  	
  	public void insertString(int offs, String str, AttributeSet a)
		throws BadLocationException {
  		super.insertString(offs, str, a);
  		if ( !enabledDeci )
  			return;
  		try {
  			String tmp = tfDec.getText();
  			int aa = Integer.parseInt( tmp );
  			int colCount = tbChar.getModel().getColumnCount();
  			
  			int row = aa / colCount;
  			int col = aa % colCount;

  			tbChar.getSelectionModel().setSelectionInterval( row, row );
  			tbChar.getColumnModel().getSelectionModel().setSelectionInterval( col, col );

  			scrollToCenter( tbChar, row, col );
  			
  			tbChar.requestFocus();
  			
  		} catch( NumberFormatException ex ) {

  		}
  	}

  }

  class HexaDocument extends PlainDocument {

  	public void insertString(int offs, String str, AttributeSet a)
		throws BadLocationException {
  		super.insertString( offs, str, a );
  		if ( !enabledHexa )
  			return;
  		try {
  			String tmp = tfHexa.getText();
  			int aa = Integer.parseInt( tmp, 16 );
  			int colCount = tbChar.getModel().getColumnCount();
  			
  			int row = aa / colCount;
  			int col = aa % colCount;

  			tbChar.getSelectionModel().setSelectionInterval( row, row );
  			tbChar.getColumnModel().getSelectionModel().setSelectionInterval( col, col );

  			scrollToCenter( tbChar, row, col );
  			
  			tbChar.requestFocus();
  			
  		} catch( NumberFormatException ex ) {

  		}
  	}

  }

  //////////////////////////////////////////////////////////////
  
  class CustomCellRenderer extends DefaultTableCellRenderer {	
	  
	  public CustomCellRenderer() {
		  setFont( UIManager.getFont( "textarea.font" ) );
	  }
	  
  	public Component getTableCellRendererComponent(JTable table, Object value,
		    boolean isSelected, boolean hasFocus, 
		    int row, int column) {
  		
  		char cc = ( ( Character )value ).charValue(); 
  		
  		if ( cc == '\n' ) {
  			value = "\\n";
  		}

  		Component c = super.getTableCellRendererComponent(
  				table, value, isSelected, hasFocus, row, column );
  		
  		if ( hasFocus ) {
  			c.setBackground( Color.BLACK );
  			c.setForeground( Color.WHITE );
  		} else {  			
  			c.setBackground( UIManager.getColor( "list.background" ) );
  			c.setForeground(getBackground());  			
  	  		if ( !getFont().canDisplay( cc ) ) c.setBackground( Color.LIGHT_GRAY );
  		}
  		return c;
  	}
  }

}

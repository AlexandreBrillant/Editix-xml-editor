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

package com.japisoft.editix.action.xml;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JPanel;
import javax.swing.text.Document;
import javax.swing.text.Element;

import com.japisoft.editix.main.EditixApplicationModel;
import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.framework.dialog.DialogManager;
import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.bookmark.BookmarkPosition;

public class SurroundAction extends AbstractAction {

	private SurroundPanel panel = null;

	public void surround( XMLContainer container, String tag, String attributes, boolean splitMode, String splitSeq, boolean trimValues, boolean bookmarkSelection ) {
		if ( !bookmarkSelection )
			surround( 
				container, 
				tag, 
				attributes, 
				splitMode, 
				splitSeq, 
				trimValues, 
				container.getEditor().getSelectionStart(), 
				container.getEditor().getSelectionEnd(), false );
		else {

			for ( int i = 0;i < container.getBookmarkContext().getModel().getBookmarkCount(); i++ ) {
				BookmarkPosition position = container.getBookmarkContext().getModel().getBookmarkPositionAt( i );
				int offset = position.getOffset();
				int index = container.getDocument().getDefaultRootElement().getElementIndex( offset );
				Element e = container.getDocument().getDefaultRootElement().getElement( index );
				container.getEditor().setSelectionStart( e.getStartOffset() );
				container.getEditor().setSelectionEnd( e.getEndOffset() );

				surround( 
						container, 
						tag, 
						attributes, 
						splitMode, 
						splitSeq, 
						trimValues, 
						e.getStartOffset(), 
						e.getEndOffset(),
						true );	
				
				
			}
			
			container.getBookmarkContext().getModel().removeAllBookmarks();
			container.getEditor().getHighlighter().removeAllHighlights();
			
		}
	}
	
	public void surround( XMLContainer container, String tag, String attributes, boolean splitMode, String splitSeq, boolean trimValues, int start, int end, boolean cr ) {
	
		if ( end == start ) {
			// For a selection to the end of the line
			Document doc = container.getEditor().getDocument();
			int index = doc.getDefaultRootElement().getElementIndex( start );
			end = doc.getDefaultRootElement().getElement( index ).getEndOffset();
			start = doc.getDefaultRootElement().getElement( index ).getStartOffset();
			container.getEditor().setSelectionStart( start );
			container.getEditor().setSelectionEnd( end );
		}
		
		String content = container.getEditor().getSelectedText();
		String[] parts = null;

		if ( splitMode ) {
			parts = content.split( splitSeq );
		} else {
			parts = new String[] { content };
		}
		
		
		if ( tag.startsWith( "<" ) ) {
			tag = tag.substring( 1 );
		}
		if ( tag.endsWith( ">" ) )
			tag = tag.substring( 0, tag.length() - 1 );
		
		String startTag = "<" + tag;
		
		if ( attributes != null && !"".equals( attributes ) ) {
			startTag += " ";
			startTag += attributes;
		}
		
		startTag += ">";

		int i = tag.indexOf( " " );
		if ( i == -1 )
			i = tag.length();
		String endTag = "</" + tag.substring( 0, i ) + ">";
		
		StringBuffer sbRes = new StringBuffer();
		for ( i = 0; i < parts.length; i++ ) {
			sbRes.append( startTag );
			
			String value = parts[ i ];
			if ( trimValues ) {
				value = value.trim();
			}
			
			sbRes.append( value );
			sbRes.append( endTag );
			if ( splitMode || cr )
				sbRes.append( "\n" );
		}
		
		container.getEditor().replaceSelection( sbRes.toString() );
		
	}
	
	public void surround( XMLContainer container ) {
		surround( container, panel.getTag(), panel.getAttributes(), panel.splitMode(), panel.getSplitSeq(), panel.trimMode(), panel.bookmarkSelections() );		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {

		XMLContainer container = EditixFrame.THIS.getSelectedContainer();
		if ( container == null ) {
			return;
		}

		if ( panel == null )
			panel = new SurroundPanel();

		if ( DialogManager.showDialog( 
			EditixFrame.THIS, 
			"Surround by", 
			"Surround", 
			"Surround your lines by a tag\nYou may insert new attributes too", 
			null, panel ) == DialogManager.OK_ID ) {

			if ( panel.getTag() == null || panel.getTag().length() == 0 ) {
				EditixFactory.buildAndShowErrorDialog( "Invalid tag" );
				return;
			}
			
			surround( container );

			panel.addTag( panel.getTag() );
			panel.addSplit( panel.getSplitSeq() );

			EditixApplicationModel.INTERFACE_BUILDER.setEnabledActionForId( "repeatSurroundTag", true );
			
		}

	}

	static class SurroundPanel extends JPanel {
		
		public SurroundPanel() {
			initComponents();
		}

 // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
 private void initComponents() {

     bgSelection = new javax.swing.ButtonGroup();
     jLabel3 = new javax.swing.JLabel();
     jLabel1 = new javax.swing.JLabel();
     cbTag = new javax.swing.JComboBox();
     chkSplitMode = new javax.swing.JCheckBox();
     cbSplit = new javax.swing.JComboBox();
     jLabel2 = new javax.swing.JLabel();
     cbAttributes = new javax.swing.JComboBox();
     rbSelection = new javax.swing.JRadioButton();
     jLabel4 = new javax.swing.JLabel();
     rbBookmarks = new javax.swing.JRadioButton();
     cbTrim = new javax.swing.JCheckBox();

     jLabel3.setText("jLabel3");

     setBorder(javax.swing.BorderFactory.createTitledBorder("Surround by"));

     jLabel1.setText("Tag");

     cbTag.setEditable(true);

     chkSplitMode.setText("Split lines with :");
     chkSplitMode.setToolTipText("Cut into several lines");

     cbSplit.setEditable(true);
     cbSplit.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "carriage return" }));

     jLabel2.setText("Attributes");

     cbAttributes.setEditable(true);

     bgSelection.add(rbSelection);
     bgSelection.add(rbBookmarks);
     
     rbSelection.setSelected(true);
     rbSelection.setText("Selection");

     jLabel4.setText("Apply to");

     rbBookmarks.setText("Bookmarks");

     cbTrim.setText("Trim values");
     cbTrim.setSelected( true );
     cbTrim.setToolTipText("Remove whitespaces before and after");

     javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
     this.setLayout(layout);
     layout.setHorizontalGroup(
         layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGroup(layout.createSequentialGroup()
             .addContainerGap()
             .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                 .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                     .addComponent(cbTrim)
                     .addGap(32, 32, 32)
                     .addComponent(chkSplitMode)
                     .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 6, Short.MAX_VALUE)
                     .addComponent(cbSplit, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))
                 .addGroup(layout.createSequentialGroup()
                     .addComponent(jLabel1)
                     .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                     .addComponent(cbTag, javax.swing.GroupLayout.PREFERRED_SIZE, 264, javax.swing.GroupLayout.PREFERRED_SIZE))
                 .addGroup(layout.createSequentialGroup()
                     .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                         .addComponent(jLabel2)
                         .addComponent(jLabel4))
                     .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                     .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                         .addGroup(layout.createSequentialGroup()
                             .addComponent(rbSelection)
                             .addGap(18, 18, 18)
                             .addComponent(rbBookmarks)
                             .addGap(0, 0, Short.MAX_VALUE))
                         .addComponent(cbAttributes, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
             .addContainerGap())
     );
     layout.setVerticalGroup(
         layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGroup(layout.createSequentialGroup()
             .addGap(18, 18, 18)
             .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                 .addComponent(jLabel1)
                 .addComponent(cbTag, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
             .addGap(18, 18, 18)
             .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                 .addComponent(jLabel2)
                 .addComponent(cbAttributes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
             .addGap(18, 18, 18)
             .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                 .addComponent(rbSelection)
                 .addComponent(jLabel4)
                 .addComponent(rbBookmarks))
             .addGap(18, 18, 18)
             .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                 .addComponent(chkSplitMode)
                 .addComponent(cbSplit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                 .addComponent(cbTrim))
             .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
     );
 }// </editor-fold>                        

	    public String getTag() { 
	    	return ( String )cbTag.getSelectedItem(); 
	    }
	    
	    public String getAttributes() {
	    	return (String)cbAttributes.getSelectedItem();
	    }
	    
	    public boolean bookmarkSelections() {
	    	return rbBookmarks.isSelected();
	    }
	    
	    public boolean trimMode() {
	    	return cbTrim.isSelected();
	    }

	    public boolean splitMode() { 
	    	return chkSplitMode.isSelected();
	    }

	    public String getSplitSeq() {
	    	String tmp = ( String )cbSplit.getSelectedItem();
	    	if ( "carriage return".equalsIgnoreCase( tmp ) )
	    		tmp = "\n";
	    	if ( tmp == null )
	    		tmp = "\n";
	    	return tmp; 
	    }

	    public void addTag( String tag ) {
	    	DefaultComboBoxModel model = ( DefaultComboBoxModel )cbTag.getModel();
	    	for ( int i = 0; i < model.getSize(); i++ ) {
	    		if ( tag.equalsIgnoreCase( ( String )model.getElementAt( i ) ) )
	    			return;
	    	}
	    	model.addElement( tag );
	    }
	    
	    public void addSplit( String tag ) {
	    	DefaultComboBoxModel model = ( DefaultComboBoxModel )cbSplit.getModel();
	    	for ( int i = 0; i < model.getSize(); i++ ) {
	    		if ( tag.equalsIgnoreCase( ( String )model.getElementAt( i ) ) )
	    			return;
	    	}
	    	model.addElement( tag );
	    }
	    
	    // Variables declaration - do not modify                     
	    private javax.swing.ButtonGroup bgSelection;
	    private javax.swing.JComboBox cbTag;
	    private javax.swing.JCheckBox chkSplitMode;
	    private javax.swing.JCheckBox cbTrim;
	    private javax.swing.JComboBox cbSplit;
	    private javax.swing.JComboBox<String> cbAttributes;
	    private javax.swing.JLabel jLabel1;
	    private javax.swing.JLabel jLabel2;
	    private javax.swing.JLabel jLabel3;
	    private javax.swing.JLabel jLabel4;
	    private javax.swing.JRadioButton rbBookmarks;
	    private javax.swing.JRadioButton rbSelection;
	    // End of variables declaration       		
	}
	
	
}


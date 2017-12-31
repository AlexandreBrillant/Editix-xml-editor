package com.japisoft.editix.action.xml;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JPanel;
import javax.swing.text.Document;

import com.japisoft.editix.main.EditixApplicationModel;
import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.framework.dialog.DialogManager;
import com.japisoft.xmlpad.XMLContainer;

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
public class SurroundAction extends AbstractAction {

	private SurroundPanel panel = null;

	public void surround( XMLContainer container, String tag, boolean splitMode, String splitSeq ) {
		
		int start = container.getEditor().getSelectionStart();
		int end = container.getEditor().getSelectionEnd();
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

		String startTag = "<" + tag + ">";
		int i = tag.indexOf( " " );
		if ( i == -1 )
			i = tag.length();
		String endTag = "</" + tag.substring( 0, i ) + ">";
		
		StringBuffer sbRes = new StringBuffer();
		for ( i = 0; i < parts.length; i++ ) {
			sbRes.append( startTag );
			sbRes.append( parts[ i ].trim() );
			sbRes.append( endTag );
			sbRes.append( "\n" );
		}
		
		container.getEditor().replaceSelection( sbRes.toString() );
		
	}
	
	public void surround( XMLContainer container ) {
		surround( container, panel.getTag(), panel.splitMode(), panel.getSplitSeq() );		
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
			"Surround your lines by a tag", 
			null, panel ) == DialogManager.OK_ID ) {

			if ( panel.getTag() == null || panel.getTag().length() == 0 ) {
				EditixFactory.buildAndShowErrorDialog( "Invalid tag" );
				return;
			}
			
			surround( container, panel.getTag(), panel.splitMode(), panel.getSplitSeq() );

			panel.addTag( panel.getTag() );
			panel.addSplit( panel.getSplitSeq() );

			EditixApplicationModel.INTERFACE_BUILDER.setEnabledActionForId( "repeatSurroundTag", true );
			
		}

	}

	static class SurroundPanel extends JPanel {
		
		public SurroundPanel() {
			initComponents();
		}

	    private void initComponents() {

	        jLabel1 = new javax.swing.JLabel();
	        cbTag = new javax.swing.JComboBox();
	        chkSplitMode = new javax.swing.JCheckBox();
	        cbSplit = new javax.swing.JComboBox();

	        jLabel1.setText("Tag");

	        cbTag.setEditable(true);

	        chkSplitMode.setText("Split lines");

	        cbSplit.setEditable(true);
	        cbSplit.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "carriage return" }));

	        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
	        this.setLayout(layout);
	        layout.setHorizontalGroup(
	            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGroup(layout.createSequentialGroup()
	                .addContainerGap()
	                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	                    .addGroup(layout.createSequentialGroup()
	                        .addComponent(jLabel1)
	                        .addGap(18, 18, 18)
	                        .addComponent(cbTag, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
	                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
	                        .addGap(0, 89, Short.MAX_VALUE)
	                        .addComponent(chkSplitMode)
	                        .addGap(18, 18, 18)
	                        .addComponent(cbSplit, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)))
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
	                    .addComponent(chkSplitMode)
	                    .addComponent(cbSplit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
	                .addContainerGap(18, Short.MAX_VALUE))
	        );
	    }// </editor-fold>                        

	    public String getTag() { 
	    	return ( String )cbTag.getSelectedItem(); 
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
	    private javax.swing.JComboBox cbTag;
	    private javax.swing.JCheckBox chkSplitMode;
	    private javax.swing.JComboBox cbSplit;
	    private javax.swing.JLabel jLabel1;
		
	}
	
	
}

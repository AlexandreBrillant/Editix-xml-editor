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

package com.japisoft.xmlpad.action.edit;

import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.japisoft.framework.xml.parser.document.Document;
import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.xmlpad.action.XMLAction;
import com.japisoft.xmlpad.dialog.DialogManager;
import com.japisoft.xmlpad.helper.model.TagDescriptor;

/**
 * Surround by a tag the selection
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * */
public class SurroundTagAction extends XMLAction {
	
	public static final String ID = SurroundTagAction.class.getName();
	
	public SurroundTagAction() {
		super();
	}

	public boolean notifyAction() {
		String selection = container.getEditor().getSelectedText();

		if ( selection == null ) {
			// Force selection to the current node
			FPNode n = container.getCurrentElementNode();
			if ( n != null ) {
				int start = n.getStartingOffset();
				int stop = n.getStoppingOffset();
				container.getEditor().select( start, stop + 1 );
				selection = container.getEditor().getSelectedText();
			}
		}

		try {
			if (selection != null) {

				String[] dico = null;
				SurroundTagPane pane = new SurroundTagPane( dico ); 
				
				if ( DialogManager.showDialog( 
						SwingUtilities.getWindowAncestor( container.getView() ),
						"Surrond",
						"Surround action",
						"Surround your selection by a tag",
						null,
						pane ) == DialogManager.OK ) {

					String startTag = pane.getStartingTag();
					String endTag = pane.getEndingTag();
				
					if ( startTag == null )
						return false;
				
					String newContent =
						startTag + selection + endTag;
				
					container.getEditor().replaceSelection(newContent);
					return true;
				} return false;
			} else 
			JOptionPane.showMessageDialog( container.getView(), "No selection found", "Error", JOptionPane.ERROR_MESSAGE );
		} catch (Throwable th) {
			th.printStackTrace();
			System.err.println("Can't surround");
		}
		return false;
	}

	///////////////////////////////////////////////////////////

	class SurroundTagPane extends JPanel {

		String[] dico;
		
		public SurroundTagPane( String[] dico ) {
			this.dico = dico;
			initUI();
			setPreferredSize( new Dimension( 300, 200 ) );
		}

		JComboBox cb;

		public void addNotify() {
			super.addNotify();
			cb.requestFocus();
		}		
		
		private void initUI() {
			if ( dico == null )
				cb = new JComboBox();
			else {
				DefaultComboBoxModel 
					dcm = new DefaultComboBoxModel();
				for ( int i = 0; i < dico.length; i++ ) {
					if ( dico[ i ] != null )
						dcm.addElement( dico[ i ] );
				}
				cb = new JComboBox( dcm );
			}
			cb.setEditable( true );
			cb.setPreferredSize( new Dimension( 250, 20 ) );
			setLayout( new FlowLayout() );
			add( cb );
		}
		
		public String getStartingTag() {
			if ( cb.getSelectedItem() == null )
				return null;
			
			if ( cb.getSelectedItem() instanceof TagDescriptor )
				return ( ( TagDescriptor )cb.getSelectedItem() ).getStartingTag();
			
			return "<" + ( String )cb.getSelectedItem() + ">";
		}
		
		public String getEndingTag() {
			if ( cb.getSelectedItem() == null )
				return null;

			if ( cb.getSelectedItem() instanceof TagDescriptor )
				return ( ( TagDescriptor )cb.getSelectedItem() ).getEndingTag();
			
			return "</" + ( String )cb.getSelectedItem() + ">";
		
		}
	}
	
}


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

package com.japisoft.xmlform.designer.data;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JPanel;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.w3c.dom.Document;

import com.japisoft.framework.ui.text.FileTextField;
import com.japisoft.framework.xml.grammar.GrammarElement;
import com.japisoft.framework.xml.grammar.xsd.XSDGrammar;
import com.japisoft.xmlform.UIToolkit;
import com.japisoft.xmlform.component.AbstractXMLFormComponent;
import com.japisoft.xmlform.component.ComponentContext;
import com.japisoft.xmlform.component.XMLFormComponentFactory;

public class DataPanel extends JPanel 
		implements
			MouseListener,
			ItemListener, 
			ComponentContext, 
			TreeSelectionListener {

	/** Creates new form DataPanel */
    public DataPanel( DataPanelListener listener ) {
    	this.listener = listener;
        initComponents();
        treeStructure.setModel( null );
        txtSchemaLocation.setActionListener( new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		loadSchema( txtSchemaLocation.getText() );
        	}
        });
        treeStructure.setCellRenderer( new DataTreeRenderer() );
        new TreeDragDrop( treeStructure );
    }

    @Override
    public void addNotify() {
    	super.addNotify();
    	cbElementRoot.addItemListener( this );
    	treeStructure.addTreeSelectionListener( this );
    	treeStructure.addMouseListener( this );
    }
    
    @Override
    public void removeNotify() {
    	super.removeNotify();
    	cbElementRoot.removeItemListener( this );
    	treeStructure.removeTreeSelectionListener( this );
    	treeStructure.removeMouseListener( this );
    }

    public void mouseClicked(MouseEvent e) {
    	if ( e.getClickCount() >= 2 ) {
    		
    	}
	}
	public void mouseEntered(MouseEvent e) {
	}
	public void mouseExited(MouseEvent e) {
	}
	public void mousePressed(MouseEvent e) {
	}
	public void mouseReleased(MouseEvent e) {
	}

	public void valueChanged( TreeSelectionEvent e ) {
    	// Avoiding reentrant code
    	GrammarNodeTreeNode gntn = 
    		( GrammarNodeTreeNode )treeStructure.getLastSelectedPathComponent();
    	if ( gntn != null ) {
    		if ( gntn.getUserObject() != null ) {
				setCurrentComponent(
					( AbstractXMLFormComponent )gntn.getUserObject(), false );
				listener.scrollTo(
					( AbstractXMLFormComponent )gntn.getUserObject() );
    		}
    	}
    }
    
    public void itemStateChanged(ItemEvent e) {
    	selectRoot( ( String )cbElementRoot.getSelectedItem() );
    }	

    private DataPanelListener listener = null;
    
    private HashMap<String,GrammarTreeModel> cacheModel = null;

    private void selectRoot( String selectedItem ) {
    	if ( listener != null )
    		listener.newRoot( selectedItem );
    	
    	if ( grammar != null ) {
    		
    		if ( cacheModel == null )
    			cacheModel = new HashMap<String, GrammarTreeModel>();

    		GrammarTreeModel model = cacheModel.get( selectedItem );
    		if ( model == null ) {
    		
				List<GrammarElement> le = 
					grammar.getGlobalElements();

				for ( GrammarElement ge : le ) {
					if ( selectedItem.equals( ge.getName() ) ) {
						model = new GrammarTreeModel( ge );
						treeStructure.setModel( model );
						break;
					}
				}

    		}

    		if ( model != null ) {
    			cacheModel.put( selectedItem, model );
    			treeStructure.setModel( model );
    		}
    	}
	}
    
    public String getSchemaLocation() {
    	return txtSchemaLocation.getText();
    }

    public String getRoot() {
    	return ( String )cbElementRoot.getSelectedItem();
    }
    
    public GrammarNodeTreeNode getGrammarNodeTreeNodeRoot() {
    	if ( treeStructure.getModel() == null )
    		return null;
    	return ( GrammarNodeTreeNode )treeStructure.getModel().getRoot();
    }

    public GrammarNodeTreeNode getCurrentTreeNode() {
    	if ( treeStructure.getSelectionPath() != null )
    		return 
    			( GrammarNodeTreeNode )treeStructure.getSelectionPath().getLastPathComponent();
    	return null;
    }
    
    public Document getDocument() {
    	return null;
    }
    
    private XMLFormComponentFactory factory = null;

    public XMLFormComponentFactory getComponentFactory() {
    	if ( factory == null )
    		factory = new XMLFormComponentFactory( true, this );
    	return factory;
    }    
    
    private AbstractXMLFormComponent currentComponent = null;

    public void setCurrentComponent( AbstractXMLFormComponent component ) {
    	setCurrentComponent( component, true );
    }
    
    public void setCurrentComponent( AbstractXMLFormComponent component, boolean selectNode ) {
    	if ( currentComponent != null ) {
    		currentComponent.setSelectedComponent( false );
    		   			
    	}
    	this.currentComponent = component;
    	currentComponent.setSelectedComponent( true );
    	// Dispatch to the properties panel
    	listener.setCurrentComponent( component );
    	if ( selectNode && component.getGrammarNode() != null )
    		selectNode( component.getGrammarNode() );
    }

    public void action( int actionCode, Object parameter ) {
    	switch( actionCode ) {
	    	case ComponentContext.DELETE_ACTION : {
	    		if ( ( currentComponent != null ) && 
	    				( currentComponent.getXMLFormComponentParent() != null ) ) {
	    			if ( UIToolkit.confirm( 
	    					"Do you want to delete this component ?" ) ) {
	    				AbstractXMLFormComponent 
	    					componentToDelete = currentComponent;
	    				setCurrentComponent( 
	    						currentComponent.getXMLFormComponentParent() );
	    				currentComponent.removeXMLFormComponent( componentToDelete );
	    			}
	    		}
	    		break;
	    	}
	    	case ComponentContext.SELECT_ACTION : {
	    		setCurrentComponent( ( AbstractXMLFormComponent )parameter );
	    		break;
	    	}
	    	case ComponentContext.WARNING_MESSAGE : {
	    		UIToolkit.warn( "" + parameter );
	    		break;
	    	}
    	}
    }

	private XSDGrammar grammar = null;
    
	public void resetSchema( String location ) {
		if ( location == null ) {
			grammar = null;
			cbElementRoot.removeAllItems();
			treeStructure.setModel( null );
			txtSchemaLocation.setText( null );
		} else
			txtSchemaLocation.setText( location );
	}
	
	public void resetRoot( String root ) {
		cbElementRoot.setSelectedItem( root );
	}

	public void selectNode( TreeNode node ) {
		ArrayList al = new ArrayList();
		while ( node != null ) {
			al.add( 0, node );
			node = node.getParent();
		}
		treeStructure.removeTreeSelectionListener( this );
		TreePath tp = new TreePath( al.toArray() );
		treeStructure.setSelectionPath( tp );
		treeStructure.scrollPathToVisible( tp );
		treeStructure.addTreeSelectionListener( 
				DataPanel.this );						
	}

    private void loadSchema( String location ) {
    	if ( location == null )
    		return;
    	try {
			grammar = new XSDGrammar( location );
			List<GrammarElement> le = grammar.getGlobalElements();
			
			DefaultComboBoxModel model = new DefaultComboBoxModel();
			String first = null;
			for ( GrammarElement ge : le ) {
				if ( first == null )
					first = ge.getName();
				model.addElement( ge.getName() );
			}
			cbElementRoot.setModel( model );
			if ( first != null )
				selectRoot( first );
		} catch (Exception e) {
			UIToolkit.dispatchError( e.getMessage() );
			e.printStackTrace();
		}
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */    
    private void initComponents() {
        jLabel1 = new javax.swing.JLabel();
        txtSchemaLocation = new FileTextField( null, "xsd" );
        jLabel2 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        cbElementRoot = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        spStructure = new javax.swing.JScrollPane();
        treeStructure = new javax.swing.JTree();

        jLabel1.setText("Schema Location");
        jLabel2.setText("Element Root");
        jLabel3.setText("Document Structure");

        spStructure.setViewportView( treeStructure );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, spStructure, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 301, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, txtSchemaLocation, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 301, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel1)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jSeparator1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 301, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel2)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, cbElementRoot, 0, 301, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel3))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(txtSchemaLocation, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jSeparator1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel2)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbElementRoot, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel3)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(spStructure, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 256, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>                        
    
/*    
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        txtSchemaLocation = new FileTextField( null, "xsd" );
        jLabel2 = new javax.swing.JLabel();
        cbElementRoot = new javax.swing.JComboBox();
        spStructure = new javax.swing.JScrollPane();
        treeStructure = new javax.swing.JTree();

        jLabel1.setText("Schema Location");

        jLabel2.setText("Element root");

        spStructure.setViewportView(treeStructure);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, spStructure, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 231, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, txtSchemaLocation, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 231, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel1)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel2)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, cbElementRoot, 0, 231, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(txtSchemaLocation, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jLabel2)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbElementRoot, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(spStructure, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 290, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>

*/

    // Variables declaration - do not modify
    private javax.swing.JComboBox cbElementRoot;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane spStructure;
    private javax.swing.JSeparator jSeparator1;    
    private javax.swing.JTree treeStructure;
    private FileTextField txtSchemaLocation;
    // End of variables declaration

}

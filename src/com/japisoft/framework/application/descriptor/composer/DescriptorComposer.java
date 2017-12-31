package com.japisoft.framework.application.descriptor.composer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import javax.swing.tree.TreePath;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.application.descriptor.ActionModel;
import com.japisoft.framework.ui.text.FileTextField;
import com.japisoft.framework.ui.toolkit.FileManager;

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
public class DescriptorComposer extends javax.swing.JPanel implements TreeSelectionListener, ActionListener {

    /** Creates new form DescriptorComposer */
	public DescriptorComposer() {
        initComponents();
        tree.setCellRenderer( new DescriptorTreeRenderer() );
        tb.setFloatable( false );
        setEnabledProperties( false );
        txtIcon.setFileExt( new String[] { "gif", "jpg", "png" } );
        txtLibrairies.setMultipleSelectionMode( true );

        btAdd.setIcon( new ImageIcon( getClass().getResource( "navigate_plus.png" ) ) );
        btDelete.setIcon( new ImageIcon( getClass().getResource( "navigate_minus.png" ) ) );
        btDown.setIcon( new ImageIcon( getClass().getResource( "navigate_down.png" ) ) );
        btUp.setIcon( new ImageIcon( getClass().getResource( "navigate_up.png" ) ) );

        btAdd.setEnabled( false );
        btDelete.setEnabled( false );
        btDown.setEnabled( false );
        btUp.setEnabled( false );
	}

	@Override
	public void addNotify() {
		super.addNotify();
		tree.addTreeSelectionListener( this );
		
		JButton[] bts = {
			btAdd,
			btSave,
			btDelete,
			btDown,
			btRestore,
			btUp,
			btExport,
			btImport
		};
		
		for ( JButton bt : bts )  {
			bt.addActionListener( this );
		}
	}

	@Override
	public void removeNotify() {
		super.removeNotify();
		tree.removeTreeSelectionListener( this );

		JButton[] bts = {
			btAdd,
			btSave,
			btDelete,
			btDown,
			btRestore,
			btUp,
			btExport,
			btImport
		};
		
		for ( JButton bt : bts )  {
			bt.removeActionListener( this );
		}		
	}

	public void actionPerformed(ActionEvent e) {

		if ( e.getSource() == btSave ) {

			if ( JOptionPane.showConfirmDialog(
					ApplicationModel.MAIN_FRAME, 
					"Can you confirm you wish to write this descriptor as the new one ? " ) == JOptionPane.OK_OPTION ) {

				try {
					Element descriptor = ( Element )tree.getModel().getRoot();			
					Transformer t = TransformerFactory.newInstance().newTransformer();
					t.transform( new DOMSource( descriptor ), new StreamResult( descriptorCustom ) );
					JOptionPane.showMessageDialog( ApplicationModel.MAIN_FRAME, "Please restart your application" );
				} catch( Exception exc ) {
					JOptionPane.showMessageDialog( ApplicationModel.MAIN_FRAME, "Can't save this descriptor [" + exc.getMessage() + "]" );
				}
				
			}

		} else
		if ( e.getSource() == btRestore ) {

			if ( JOptionPane.showConfirmDialog(
					ApplicationModel.MAIN_FRAME, 
					"Can you confirm you wish to restore the default descriptor ? " ) == JOptionPane.OK_OPTION ) {
				descriptorCustom.delete();
				JOptionPane.showMessageDialog( ApplicationModel.MAIN_FRAME, "Please restart your application" );
			}			

		} else
		if ( e.getSource() == btExport ) {

			File d = FileManager.getSelectedFile( false, "xml", "Editix Descriptor (*.xml)" );
			if ( d != null ) {
				try {
					Element descriptor = ( Element )tree.getModel().getRoot();			
					Transformer t = TransformerFactory.newInstance().newTransformer();
					t.transform( new DOMSource( descriptor ), new StreamResult( d ) );
				} catch( Exception exc ) {
					JOptionPane.showMessageDialog( ApplicationModel.MAIN_FRAME, "Can't save this descriptor [" + exc.getMessage() + "]" );
				}
			}
						
		} else
		if ( e.getSource() == btImport ) {
			
			File d = FileManager.getSelectedFile( true, "xml", "Editix Descriptor (*.xml)" );
			if ( d != null ) {
				try {
					loadDescriptor( d.toURL(), descriptorCustom );
				} catch( Exception exc ) {
					JOptionPane.showMessageDialog( ApplicationModel.MAIN_FRAME, "Can't load this descriptor [" + exc.getMessage() + "]" );
				}
			}

		} else

		if ( e.getSource() == btDown || e.getSource() == btUp ) {

			TreePath tp = tree.getSelectionPath();
			
			if ( tp == null ) {
				return;
			}

			Element toMove = ( Element )tp.getLastPathComponent();			 
			Element parent = ( Element )toMove.getParentNode();
		
			DescriptorTreeModel dtm = ( ( DescriptorTreeModel )tree.getModel() );

			int index = dtm.getIndexOfChild( parent, toMove );
			
			if ( e.getSource() == btDown ) {
				index += 2;
			}
			else {
				index--;
			}

			Element refNode = ( Element )dtm.getChild( parent, index );

			if ( refNode == null ) {
				
				if ( index == dtm.getChildCount( parent ) ) {
					
					parent.removeChild( toMove );
					parent.appendChild( toMove );
					
					dtm.fireStructurChange( tp.getParentPath() );
					
				}

			} else

			if ( refNode != null ) {

				parent.removeChild( toMove );				
				
				if ( e.getSource() == btUp ) {

					parent.insertBefore( toMove, refNode );
					
				} else {
					
					parent.insertBefore( toMove, refNode );
					
				}

				dtm.fireStructurChange( tp.getParentPath() );
				
			}

		}

		if ( e.getSource() == btDelete ) {
			
			 if ( tree.getSelectionPath() == null )
				 return;
						
			if ( JOptionPane.showConfirmDialog( 
					ApplicationModel.MAIN_FRAME, 
					"Please, confirm you want to delete it ?" ) == JOptionPane.OK_OPTION ) {

				TreePath tp = tree.getSelectionPath();
				Element toRemove = ( Element )tp.getLastPathComponent();
				toRemove.getParentNode().removeChild( toRemove );

				DescriptorTreeModel dtm = ( ( DescriptorTreeModel )tree.getModel() ); 

				dtm.fireStructurChange( tp.getParentPath() );
				
				
			}
		}

		if ( e.getSource() == btAdd ) {			
			String id = JOptionPane.showInputDialog( ApplicationModel.MAIN_FRAME, "Choose an id for your item ?" );
			if ( ActionModel.hasAction( id ) ) {
				JOptionPane.showMessageDialog( ApplicationModel.MAIN_FRAME, "This [id] already exists" );
			} else {
				 if ( tree.getSelectionPath() == null )
					 return;
				 TreePath tp = tree.getSelectionPath();
				 Element parent = ( Element )tp.getLastPathComponent();

				 Document doc = parent.getOwnerDocument();
				 Element item = doc.createElement( "item" );
				 item.setAttribute( "id", id );
				 Element ui = doc.createElement( "ui" );
				 Element action = doc.createElement( "action" );
				 item.appendChild( ui );
				 item.appendChild( action );
				 
				 parent.appendChild( item );

				 DescriptorTreeModel dtm = ( ( DescriptorTreeModel )tree.getModel() ); 

				 dtm.fireStructurChange( tp );
				 
				 tp = tp.pathByAddingChild( item );
				 
				 tree.setSelectionPath( tp );
				 tree.scrollPathToVisible( tp );
			}
		}
	}

	private URL descriptorInit;
	private File descriptorCustom;
	private Document doc;

	public void loadDescriptor( 
		URL source, 
		File output ) throws Exception {
		this.descriptorInit = source;
		this.descriptorCustom = output;
		DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		doc = db.parse( new InputSource( source.openStream() ) );
		tree.setModel( new DescriptorTreeModel( doc.getDocumentElement() ) );
	}

	public void valueChanged( TreeSelectionEvent e ) {
		if ( tree.getSelectionPath() == null ) {
			return;
		}
		Element el = ( Element )tree.getSelectionPath().getLastPathComponent();
		
		btAdd.setEnabled( "menu".equals( el.getNodeName() ) || "toolBar".equals( el.getNodeName() ) );
		btDelete.setEnabled( "menu".equals( el.getNodeName() ) || "item".equals( el.getNodeName() ) || "separator".equals( el.getNodeName() ) );
		btDown.setEnabled( "menu".equals( el.getNodeName() ) || "item".equals( el.getNodeName() ) );
        btUp.setEnabled( "menu".equals( el.getNodeName() ) || "item".equals( el.getNodeName() ) );
		
		setEnabledProperties( false );
		Element ui = null;
		Element action = null;
		tp.setEnabledAt( 1, true );
		if ( "item".equals( el.getNodeName() ) ) {
			setEnabledProperties( true );
			ui = getUI( el );
			action = getAction( el );
		} else 
		if ( "menu".equals( el.getNodeName() ) ) {
			jLabel1.setEnabled( true );
			jLabel4.setEnabled( true );
			txtLabel.setEnabled( true );
			txtMnemonic.setEnabled( true );
			tp.setEnabledAt( 1, false );
			tp.setSelectedIndex( 0 );
			ui = getUI( el );
		}
		if ( ui != null )
			dispatchElements( ui, action );
	}

	private void dispatchElements( Element ui, Element action ) {

		txtLabel.setDocument( new MappedPlainDocument( ui, "label" ) );
		txtHelp.setDocument( new MappedPlainDocument( ui, "help" ) );
		txtShortcut.setDocument( new MappedPlainDocument( ui, "shortcut" ) );
		txtMnemonic.setDocument( new MappedPlainDocument( ui, "mnemonic" ) );
		txtIcon.setDocument( new MappedPlainDocument( ui, "icon" ) );

		if ( action != null ) {
			txtClass.setDocument( new MappedPlainDocument( action, "class" ) );
			txtLibrairies.setDocument( new MappedPlainDocument( action, "libraries" ) );
		}

	}

	private Element getUI( Element parent ) {
		NodeList nl = parent.getChildNodes();
		for ( int i = 0; i < nl.getLength(); i++ ) {
			if ( "ui".equals( nl.item( i ).getNodeName() ) )
				return ( Element )nl.item( i );
		}
		return null;
	}

	private Element getAction( Element parent ) {
		NodeList nl = parent.getChildNodes();
		for ( int i = 0; i < nl.getLength(); i++ ) {
			if ( "action".equals( nl.item( i ).getNodeName() ) )
				return ( Element )nl.item( i );
		}
		return null;
	}
		
	private void setEnabledProperties( boolean enabled ) {
		setEnabledProperties( uiPanel, enabled );
		setEnabledProperties( classPanel, enabled );
	}

	private void setEnabledProperties( JPanel panel, boolean enabled ) {
		for ( int i = 0; i < panel.getComponentCount(); i++ ) {
			panel.getComponent( i ).setEnabled( enabled );
		}
	}
	
	class MappedPlainDocument extends PlainDocument {
		private Element element;		
		private String attribute;
		
		public MappedPlainDocument( Element element, String attribute ) {
			this.element = element;
			this.attribute = attribute;
			try {
				super.insertString( 0, element.getAttribute( attribute ), null );
			} catch( BadLocationException exc ) {
			}
		}

		@Override
		public void insertString(int offs, String str, AttributeSet a)
				throws BadLocationException {
			super.insertString( offs, str, a );
			element.setAttribute( attribute, getText( 0, getLength() ) );
		}

		@Override
		public void remove(int offs, int len) throws BadLocationException {
			super.remove( offs, len );
			element.setAttribute( attribute, getText( 0, getLength() ) );
		}

	}
	
	
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        tb = new javax.swing.JToolBar();
        btAdd = new javax.swing.JButton();
        btDelete = new javax.swing.JButton();
        btUp = new javax.swing.JButton();
        btDown = new javax.swing.JButton();
        tp = new javax.swing.JTabbedPane();
        uiPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtLabel = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtHelp = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtShortcut = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtMnemonic = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtIcon = new FileTextField(null, "png" );
        classPanel = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        txtClass = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtLibrairies = new FileTextField( null, new String[] { "jar", "js" } );
        btSave = new javax.swing.JButton();
        btRestore = new javax.swing.JButton();
        btExport = new javax.swing.JButton();
        btImport = new javax.swing.JButton();
        spTree = new javax.swing.JScrollPane();
        tree = new javax.swing.JTree();

        tb.setRollover(true);
        tb.setName("tb"); // NOI18N

        btAdd.setToolTipText("Add a new item");
        btAdd.setFocusable(false);
        btAdd.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btAdd.setName("btAdd"); // NOI18N
        btAdd.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        tb.add(btAdd);

        btDelete.setToolTipText("delete the selected item");
        btDelete.setFocusable(false);
        btDelete.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btDelete.setName("btDelete"); // NOI18N
        btDelete.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        tb.add(btDelete);

        btUp.setToolTipText("move to the top the selected item");
        btUp.setFocusable(false);
        btUp.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btUp.setName("btUp"); // NOI18N
        btUp.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        tb.add(btUp);

        btDown.setToolTipText("move down the selected item");
        btDown.setFocusable(false);
        btDown.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btDown.setName("btDown"); // NOI18N
        btDown.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        tb.add(btDown);

        tp.setName("tp"); // NOI18N

        uiPanel.setName("uiPanel"); // NOI18N

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel1.setText("Label");
        jLabel1.setName("jLabel1"); // NOI18N

        txtLabel.setToolTipText("Visible name of your item");
        txtLabel.setName("txtLabel"); // NOI18N

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel2.setText("Help");
        jLabel2.setName("jLabel2"); // NOI18N

        txtHelp.setToolTipText("Tooltip for your item");
        txtHelp.setName("txtHelp"); // NOI18N

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel3.setText("Shorcut");
        jLabel3.setName("jLabel3"); // NOI18N

        txtShortcut.setToolTipText("Accelerator key");
        txtShortcut.setName("txtShortcut"); // NOI18N

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel4.setText("Mnemonic");
        jLabel4.setName("jLabel4"); // NOI18N

        txtMnemonic.setToolTipText("Underlined Character");
        txtMnemonic.setName("txtMnemonic"); // NOI18N

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel5.setText("Icon");
        jLabel5.setName("jLabel5"); // NOI18N

        txtIcon.setToolTipText("Icon for your item");
        txtIcon.setName("txtIcon"); // NOI18N

        org.jdesktop.layout.GroupLayout uiPanelLayout = new org.jdesktop.layout.GroupLayout(uiPanel);
        uiPanel.setLayout(uiPanelLayout);
        uiPanelLayout.setHorizontalGroup(
            uiPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(uiPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(uiPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel2)
                    .add(jLabel4)
                    .add(jLabel1)
                    .add(jLabel3)
                    .add(jLabel5))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(uiPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(txtIcon, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 323, Short.MAX_VALUE)
                    .add(txtLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 323, Short.MAX_VALUE)
                    .add(txtHelp, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 323, Short.MAX_VALUE)
                    .add(txtMnemonic, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 323, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, txtShortcut, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 323, Short.MAX_VALUE))
                .addContainerGap())
        );
        uiPanelLayout.setVerticalGroup(
            uiPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(uiPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(uiPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(txtLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(uiPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 8, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(txtMnemonic, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(uiPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(txtHelp, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(uiPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(txtShortcut, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel3))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(uiPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel5)
                    .add(txtIcon, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
        );

        tp.addTab("User Interface", uiPanel);

        classPanel.setName("classPanel"); // NOI18N

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel6.setText("Java Class");
        jLabel6.setName("jLabel6"); // NOI18N

        txtClass.setToolTipText("Java class inside your Jar");
        txtClass.setName("txtClass"); // NOI18N

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel7.setText("Libraries (*.jar *.js)");
        jLabel7.setName("jLabel7"); // NOI18N

        txtLibrairies.setToolTipText("Required librairies for running your action. It must be jar for a java class");
        txtLibrairies.setName("txtLibrairies"); // NOI18N

        org.jdesktop.layout.GroupLayout classPanelLayout = new org.jdesktop.layout.GroupLayout(classPanel);
        classPanel.setLayout(classPanelLayout);
        classPanelLayout.setHorizontalGroup(
            classPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(classPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(classPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel7)
                    .add(jLabel6))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(classPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(txtClass, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 287, Short.MAX_VALUE)
                    .add(txtLibrairies, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 287, Short.MAX_VALUE))
                .addContainerGap())
        );
        classPanelLayout.setVerticalGroup(
            classPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(classPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(classPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel6)
                    .add(txtClass, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(classPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel7)
                    .add(txtLibrairies, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(104, Short.MAX_VALUE))
        );

        tp.addTab("Code", classPanel);

        btSave.setText("Save");
        btSave.setToolTipText("Save this interface descriptor");
        btSave.setName("btDefault"); // NOI18N

        btRestore.setText("Restore Default");
        btRestore.setToolTipText("Restore to the default interface");
        btRestore.setName("btSave"); // NOI18N

        btExport.setText("Export...");
        btExport.setName("jButton1"); // NOI18N

        btImport.setText("Import...");
        btImport.setName("jButton2"); // NOI18N

        spTree.setName("spTree"); // NOI18N

        tree.setName("tree"); // NOI18N
        spTree.setViewportView(tree);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(tb, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 420, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .addContainerGap()
                        .add(btSave)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 25, Short.MAX_VALUE)
                        .add(btRestore)
                        .add(18, 18, 18)
                        .add(btExport)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(btImport)
                        .add(37, 37, 37))
                    .add(layout.createSequentialGroup()
                        .addContainerGap()
                        .add(tp, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 410, Short.MAX_VALUE))
                    .add(spTree, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 420, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(spTree, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 256, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(3, 3, 3)
                .add(tb, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(tp, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 194, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(btRestore)
                        .add(btExport)
                        .add(btImport))
                    .add(btSave))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

    }// </editor-fold>


    // Variables declaration - do not modify
    private javax.swing.JButton btAdd;
    private javax.swing.JButton btSave;
    private javax.swing.JButton btDelete;
    private javax.swing.JButton btDown;
    private javax.swing.JButton btRestore;
    private javax.swing.JButton btUp;
    private javax.swing.JPanel classPanel;
    private javax.swing.JButton btExport;
    private javax.swing.JButton btImport;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JScrollPane spTree;
    private javax.swing.JToolBar tb;
    private javax.swing.JTabbedPane tp;
    private javax.swing.JTree tree;
    private javax.swing.JTextField txtClass;
    private javax.swing.JTextField txtHelp;
    // private javax.swing.JTextField txtIcon;
    private FileTextField txtIcon;
    private javax.swing.JTextField txtLabel;
//    private javax.swing.JTextField txtLibrairies;
    private FileTextField txtLibrairies;
    private javax.swing.JTextField txtMnemonic;
    private javax.swing.JTextField txtShortcut;
    private javax.swing.JPanel uiPanel;
    // End of variables declaration
    
}

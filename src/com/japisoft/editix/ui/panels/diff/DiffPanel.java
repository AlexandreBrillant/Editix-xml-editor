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

package com.japisoft.editix.ui.panels.diff;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.apache.xerces.parsers.SAXParser;
import org.jdesktop.layout.GroupLayout;

import com.japisoft.editix.main.EditixApplicationModel;
import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.editix.ui.pathbuilder.XMLPathBuilder;
import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.application.descriptor.ActionModel;
import com.japisoft.framework.collection.FastArrayList;
import com.japisoft.framework.ui.FastLabel;
import com.japisoft.framework.ui.table.ExportableTable;
import com.japisoft.framework.ui.text.FileTextField;
import com.japisoft.framework.xml.XMLFileData;
import com.japisoft.framework.xml.XMLToolkit;
import com.japisoft.framework.xml.parser.FPParser;
import com.japisoft.framework.xml.parser.document.Document;
import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.framework.xml.parser.node.NodeFactoryImpl;
import com.japisoft.xmlpad.IXMLPanel;
import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.tree.renderer.FastTreeRenderer;
import com.topologi.diffx.DiffXException;
import com.topologi.diffx.Main;
import com.topologi.diffx.config.DiffXConfig;

public class DiffPanel extends JPanel implements ActionListener, ListSelectionListener {

	private JButton btReportFile = new JButton( 
    		new ImageIcon( ClassLoader.getSystemClassLoader().getResource( 
    				"images/briefcase2_document.png" 
    		) )
    );

	private JButton btReportEdit = new JButton( 
    		new ImageIcon( ClassLoader.getSystemClassLoader().getResource( 
    				"images/briefcase2_view.png" 
    		) )
    );

	private JButton btRefresh = new JButton( 
    		new ImageIcon( ClassLoader.getSystemClassLoader().getResource( 
    				"images/refresh.png" 
    		) )
    );

	private JButton btAdded = new JButton( 
			DiffResourceFactory.getIconForType( DiffResourceFactory.ADD_TYPE )
    );

	private JButton btDelete = new JButton( 
			DiffResourceFactory.getIconForType( DiffResourceFactory.REMOVE_TYPE )    
	);

	private JButton btAtt = new JButton( 
			DiffResourceFactory.getIconForType( DiffResourceFactory.ATT_TYPE )
	);

	
	public DiffPanel( XMLContainer currentOne ) {
		
	//	SAXRecorder.setXMLReaderClass( SAXParser.class.getName() );
		
		leftTree = new JTree( new DefaultMutableTreeNode( "No document" ) );
		leftTree.setCellRenderer( new CustomTreeRenderer() );
		rightTree = new JTree( new DefaultMutableTreeNode( "No document" ) );
		rightTree.setCellRenderer( new FastTreeRenderer( null ) );
		initComponents();

		if ( currentOne != null &&
				currentOne.getCurrentDocumentLocation() != null ) {
			cbLeft.setText( currentOne.getCurrentDocumentLocation() );
			updateTree( cbLeft, leftTree );
		}

        jSplitPane1.setDividerLocation(200); 
        tbBottom.getSelectionModel().setSelectionMode( ListSelectionModel.SINGLE_SELECTION );

        bottomToolbar.add( btReportFile );
        bottomToolbar.add( btReportEdit );
        
        
        btReportFile.setToolTipText( "Save a reporting file" );
        btReportEdit.setToolTipText( "Edit a reporting file" );
        btRefresh.setToolTipText( "Update the diff operation" );

        btAdded.setToolTipText( "Filter on added parts" );
        btDelete.setToolTipText( "Filter on deleted parts" );
        btAtt.setToolTipText( "Filter on attributes changing" );        

        bottomToolbar.addSeparator();

        bottomToolbar.add( btRefresh );

        bottomToolbar.addSeparator();

        bottomToolbar.add( btAdded );
        bottomToolbar.add( btDelete );
        bottomToolbar.add( btAtt );

        btReportFile.setEnabled( false );
        btReportEdit.setEnabled( false );
        btRefresh.setEnabled( false );
        
        btAdded.setEnabled( false );
        btDelete.setEnabled( false );
        btAtt.setEnabled( false );

        message = new JLabel();
        message.setPreferredSize( new Dimension( 100, 0 ) );
        bottomToolbar.add( message );
	}

	public void addNotify() {
		super.addNotify();
		cbLeft.setActionListener( this );
		cbRight.setActionListener( this );
		tbBottom.getSelectionModel().addListSelectionListener( this );
		btReportFile.addActionListener( this );
		btReportEdit.addActionListener( this );
		btRefresh.addActionListener( this );
		btAdded.addActionListener( this );
		btDelete.addActionListener( this );
		btAtt.addActionListener( this );
	}

	public void removeNotify() {
		super.removeNotify();
		cbLeft.setActionListener( null );
		cbRight.setActionListener( null );
		tbBottom.getSelectionModel().removeListSelectionListener( null );
		btReportFile.removeActionListener( this );
		btReportEdit.removeActionListener( this );
		btRefresh.removeActionListener( this );
		btAdded.removeActionListener( this );
		btDelete.removeActionListener( this );
		btAtt.removeActionListener( this );		
	}

	public void actionPerformed( ActionEvent e ) {
		if ( e.getSource() == cbLeft ) {
			updateTree( cbLeft, leftTree );
		} else
		if ( e.getSource() == cbRight ) {
			updateTree( cbRight, rightTree );
		} else
		if ( e.getSource() == btReportEdit ) {

			String leftPath = ( ( CustomTreeModel )leftTree.getModel() ).getSource();
			String rightPath = ( ( CustomTreeModel )rightTree.getModel() ).getSource();
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			try {
				/*Main.diff( 
						new FileInputStream( leftPath ), 
						new FileInputStream( rightPath ), 
						bout ); */
				DiffXConfig config = new DiffXConfig();
				config.setIgnoreWhiteSpace( true );
				config.setNamespaceAware( false );
				Main.diff( leftPath, rightPath, bout, config );
				IXMLPanel container = EditixFactory.buildNewContainer();
				container.getMainContainer().setText( bout.toString() );
				EditixFrame.THIS.addContainer( container );
				ActionModel.activeActionById( "format" , e, "silence" );
				ActionModel.restoreAction( "format" ).putValue( "param", null );
			} catch ( Exception exc ) {
			}

		} else
		if ( e.getSource() == btReportFile ) {
			
			String leftPath = ( ( CustomTreeModel )leftTree.getModel() ).getSource();
			String rightPath = ( ( CustomTreeModel )rightTree.getModel() ).getSource();
			
			JFileChooser fc = EditixFactory.buildFileChooserForDocumentType( "XML" );
			if ( fc.showSaveDialog( EditixFrame.THIS ) == 
				JFileChooser.APPROVE_OPTION ) {

				String f = fc.getSelectedFile().toString();
				if ( f.indexOf( "." ) == -1 )
					f += ".xml";
				
				try {
					DiffXConfig config = new DiffXConfig();
					config.setIgnoreWhiteSpace( true );
					config.setNamespaceAware( false );
					Main.diff( 
							//new FileInputStream( leftPath ), 
							//new FileInputStream( rightPath ),
							leftPath,
							rightPath,
							new FileOutputStream( f ),
							config );
				} catch ( Exception exc ) {
					EditixFactory.buildAndShowErrorDialog( "Can't save file " + fc.getSelectedFile() );
				}

			}
		} else
		if ( e.getSource() == btRefresh ) {
			runDiff();
		} else
		if ( e.getSource() == btAdded ) {
			runDiff( DiffResourceFactory.ADD_TYPE );
		} else
		if ( e.getSource() == btDelete ) {
			runDiff( DiffResourceFactory.REMOVE_TYPE );
		} else
		if ( e.getSource() == btAtt ) {
			runDiff( DiffResourceFactory.ATT_TYPE );
		}
	}

	private void updateTree( FileTextField ftf, JTree tree ) {
		String path = ftf.getText();
		updateTree( path, tree );
	}

	private boolean leftUpdated = false, rightUpdated = false;

	private void updateTree( String path, JTree tree ) {
		FPParser p = new FPParser();
		try {

			XMLFileData xfd = XMLToolkit.getContentFromURI( path, null );
			Document d = p.parse( new StringReader( xfd.getContent()));
			tree.setModel( 
					new CustomTreeModel(
							path,
							( TreeNode )d.getRoot() ) );

			// Run DIFF action ?
			if ( tree == leftTree )
				leftUpdated = true;
			else
				rightUpdated = true;

			if ( leftUpdated && rightUpdated ) {
				runDiff();
			}
			
		} catch (Throwable e) {
			ApplicationModel.debug( e );
			EditixFactory.buildAndShowErrorDialog( "Can't load " + path + " : " + e.getMessage() );
		}

	}

	private DiffThread dt = null;

	private void runDiff() {
		runDiff( -1 );
	}

	private void runDiff( int type ) {
		if ( dt != null ) {
			EditixFactory.buildAndShowWarningDialog( "A Diff task is running, please wait before asking" );
		} else {
			dt = new DiffThread( type );
			dt.start();
		}
	}

	public void valueChanged( ListSelectionEvent e ) {
		int row = tbBottom.getSelectedRow();
		if ( row != -1 ) {
			FPNode node = ( FPNode )tbBottom.getModel().getValueAt( row, 1 );
			ArrayList al = new ArrayList();
			while ( node != null ) {
				al.add( node );
				node = node.getFPParent();
			}
			Collections.reverse( al );
			TreePath tp = new TreePath( al.toArray() );
			leftTree.setSelectionPath( tp );
			leftTree.scrollPathToVisible( tp );
		}
	}

	class DiffThread extends Thread {
		private int filterType;
		
		DiffThread( int filterType ) {
			this.filterType = filterType;
		}

		public void run() {
			message.setText( "  Diff working, please wait..." );
			try {
				runProxy();
			} finally {
				message.setText( null );
			}
		}

		public void runProxy() {
			String leftPath = 
				( ( CustomTreeModel )leftTree.getModel() ).getSource();
			String rightPath = 
				( ( CustomTreeModel )rightTree.getModel() ).getSource();

			try {

				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				
				DiffXConfig config = new DiffXConfig();
				config.setNamespaceAware( false );
				config.setIgnoreWhiteSpace( true );

				Main.diff( 
						//new FileInputStream( leftPath ), 
						//new FileInputStream( rightPath ),
						leftPath,
						rightPath,
						bout,
						config
				);

				FPParser p = new FPParser();

				p.setFlatView( 
						true );
				p.setParsingMode( 
						FPParser.CONTINUE_PARSING_MODE );

				Document d = null;
				
				try {
					d = p.parse(new ByteArrayInputStream( 
							bout.toByteArray() ));
				} catch( Exception e ) {
					return;
				}

				leftTree.setModel( 
						new CustomTreeModel( 
								leftPath, 
								( TreeNode )d.getRoot() ) );

				DefaultTableModel dtm = new DefaultTableModel(
						new String[] { "T", "Click on a row for selecting in the tree" }, 0 );

				List<FPNode> f = d.getFlatNodes();

				boolean addType = ( filterType > -1 );
				boolean deleteType = ( filterType > -1 );
				boolean attType = ( filterType > -1 );
				
				for ( int i = 0; i < f.size(); i++ ) {
					FPNode n = ( FPNode )f.get( i );
					int type = CustomTreeRenderer.guessType( n );
					
					if ( type > -1 ) {

						if ( ( filterType == -1 ) || 
								( filterType == type ) ) {
							String msg = null;
							switch( type ) {
								case DiffResourceFactory.ADD_TYPE:
									msg = "The node ( element ) " + n.getContent() + " has been added";
									if ( filterType == -1 ) 
										addType = true;
									break;
								case DiffResourceFactory.ATT_TYPE:
									if ( filterType == -1 )									
										attType = true;
									String allAtt = "";
									for ( int j = 0; j < n.getViewAttributeCount(); j++ ) {
										String attName = n.getViewAttributeAt( j );
										if ( attName.startsWith( "del:" ) ) {
											if ( !"".equals( allAtt) )
												allAtt += " / ";
											allAtt += attName.substring( 4 ) + "=" + n.getAttribute( attName );
										}
	
										msg = "The attribute(s) " + allAtt + " has been deleted";
									}
									break;
								case DiffResourceFactory.REMOVE_TYPE:
									if ( filterType == -1 )
										deleteType = true;
									msg = "The node ( element ) " + n.getContent() + " has been deleted";
									break;
							}
	
							n.setApplicationObject( msg );
							
							dtm.addRow(
									new Object[] {
											new Integer( type ),
											n
									} );	

						}
					}
				}

				tbBottom.setModel( dtm );
				tbBottom.getColumnModel().getColumn( 0 ).setCellRenderer( 
						new CustomTypeRenderer() );
				tbBottom.getColumnModel().getColumn( 1 ).setCellRenderer( 
						new CustomMsgRenderer() );				
				tbBottom.getColumnModel().getColumn( 0 ).setMaxWidth( 16 );

				if ( dtm.getRowCount() == 0 ) {
					EditixFactory.buildAndShowInformationDialog( "Documents are equal" );
					
					btReportEdit.setEnabled( false );
					btReportFile.setEnabled( false );					
					btRefresh.setEnabled( false );

					btAdded.setEnabled( false );
					btDelete.setEnabled( false );					
					btAtt.setEnabled( false );
										
				} else {
				
					btReportEdit.setEnabled( true );
					btReportFile.setEnabled( true );					
					btRefresh.setEnabled( true );

					btAdded.setEnabled( addType );
					btDelete.setEnabled( deleteType );					
					btAtt.setEnabled( attType );
										
				}

			} catch (FileNotFoundException e1) {
				EditixFactory.buildAndShowErrorDialog( "Can't compare : "  + e1.getMessage() );
				EditixApplicationModel.debug( e1 );
			} catch (DiffXException e1) {
				EditixFactory.buildAndShowErrorDialog( "Can't compare : "  + e1.getMessage() );
				EditixApplicationModel.debug( e1 );
			} catch (IOException e1) {
				EditixFactory.buildAndShowErrorDialog( "Can't compare : "  + e1.getMessage() );
			} catch( Exception e) {
				EditixFactory.buildAndShowErrorDialog( "Can't compare : "  + e.getMessage() );
			} catch( Throwable t ) {
				EditixApplicationModel.debug( t );
			}

			dt = null;
		}
	}

    private void initComponents() {
        jSplitPane1 = new javax.swing.JSplitPane();
        panelTop = new javax.swing.JPanel();
        jSplitPane2 = new javax.swing.JSplitPane();
        panelTopLeft = new javax.swing.JPanel();
        cbLeft = new FileTextField( null, null, new String[] { "xml" }, new XMLPathBuilder() );
        spLeft = new javax.swing.JScrollPane(leftTree);
        panelTopRight = new javax.swing.JPanel();
        cbRight = new FileTextField( null, null, new String[] { "xml" }, new XMLPathBuilder() );
        spRight = new javax.swing.JScrollPane(rightTree);
        bottomPanel = new javax.swing.JPanel();
        bottomToolbar = new javax.swing.JToolBar();
        spBottom = new javax.swing.JScrollPane();
        tbBottom = new ExportableTable() {
        	public boolean isCellEditable(int row, int column) {
        		return false;
        	}
        };

        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane1.setOneTouchExpandable(true);
        jSplitPane2.setDividerLocation(300);
        jSplitPane2.setOneTouchExpandable(true);
        panelTopLeft.setBorder(javax.swing.BorderFactory.createTitledBorder("Current Source"));

        org.jdesktop.layout.GroupLayout panelTopLeftLayout = new org.jdesktop.layout.GroupLayout(panelTopLeft);
        panelTopLeft.setLayout(panelTopLeftLayout);
        panelTopLeftLayout.setHorizontalGroup(
            panelTopLeftLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(cbLeft, 0, 283, Short.MAX_VALUE)
            .add(spLeft, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 283, Short.MAX_VALUE)
        );
        panelTopLeftLayout.setVerticalGroup(
            panelTopLeftLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(panelTopLeftLayout.createSequentialGroup()
                .add(cbLeft, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(spLeft, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 289, Short.MAX_VALUE))
        );
        jSplitPane2.setLeftComponent(panelTopLeft);

        panelTopRight.setBorder(javax.swing.BorderFactory.createTitledBorder("Custom Source"));

        org.jdesktop.layout.GroupLayout panelTopRightLayout = new org.jdesktop.layout.GroupLayout(panelTopRight);
        panelTopRight.setLayout(panelTopRightLayout);
        panelTopRightLayout.setHorizontalGroup(
            panelTopRightLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(cbRight, 0, 274, Short.MAX_VALUE)
            .add(spRight, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 274, Short.MAX_VALUE)
        );
        panelTopRightLayout.setVerticalGroup(
            panelTopRightLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(panelTopRightLayout.createSequentialGroup()
                .add(cbRight, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(spRight, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 289, Short.MAX_VALUE))
        );
        jSplitPane2.setRightComponent(panelTopRight);

        org.jdesktop.layout.GroupLayout panelTopLayout = new org.jdesktop.layout.GroupLayout(panelTop);
        panelTop.setLayout(panelTopLayout);
        panelTopLayout.setHorizontalGroup(
            panelTopLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jSplitPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 596, Short.MAX_VALUE)
        );
        panelTopLayout.setVerticalGroup(
            panelTopLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jSplitPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 349, Short.MAX_VALUE)
        );
        jSplitPane1.setTopComponent(panelTop);

        bottomToolbar.setFloatable(false);

        tbBottom.setModel(new javax.swing.table.DefaultTableModel(
            new String [] {
                "Type", "Diff"
            }, 0
        ));

        spBottom.setViewportView(tbBottom);

        org.jdesktop.layout.GroupLayout bottomPanelLayout = new org.jdesktop.layout.GroupLayout(bottomPanel);
        bottomPanel.setLayout(bottomPanelLayout);
        bottomPanelLayout.setHorizontalGroup(
            bottomPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(bottomToolbar, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 596, Short.MAX_VALUE)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, spBottom, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 596, Short.MAX_VALUE)
        );
        bottomPanelLayout.setVerticalGroup(
            bottomPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(bottomPanelLayout.createSequentialGroup()
                .add(bottomToolbar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(spBottom, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                .add(0, 0, 0))
        );
        jSplitPane1.setRightComponent(bottomPanel);

        org.jdesktop.layout.GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jSplitPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 598, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(jSplitPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 475, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>
    
    // Variables declaration - do not modify
    private javax.swing.JPanel bottomPanel;
    private javax.swing.JToolBar bottomToolbar;
    private com.japisoft.framework.ui.text.FileTextField cbLeft;
    private com.japisoft.framework.ui.text.FileTextField cbRight;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JPanel panelTop;
    private javax.swing.JPanel panelTopLeft;
    private javax.swing.JPanel panelTopRight;
    private javax.swing.JScrollPane spBottom;
    private javax.swing.JScrollPane spLeft;
    private javax.swing.JScrollPane spRight;
    private javax.swing.JTable tbBottom;
    // End of variables declaration	

    private javax.swing.JTree leftTree;
    private javax.swing.JTree rightTree;
    private javax.swing.JLabel message;

    class CustomTreeModel extends DefaultTreeModel {

    	private String source;
    	
    	public CustomTreeModel( String source, TreeNode root ) {
    		super( root );
    		this.source = source;
    	}

    	public String getSource() { return source; }
    }

    class CustomTypeRenderer extends FastLabel implements TableCellRenderer {
    
    	public Component getTableCellRendererComponent(JTable table,
		Object value, boolean isSelected, boolean hasFocus, int row,
		int column) {
    		
    		Integer i = ( Integer )value;
    		setIcon( 
    				DiffResourceFactory.getIconForType( i.intValue()) 
    		);

    		return this;
    	}

    }

    class CustomMsgRenderer extends FastLabel implements TableCellRenderer {
        
    	public Component getTableCellRendererComponent(JTable table,
		Object value, boolean isSelected, boolean hasFocus, int row,
		int column) {
    		
    		FPNode i = ( FPNode )value;
    		setText( ( String )i.getApplicationObject() );
    		
    		if ( isSelected ) {
    			setBackground( table.getSelectionBackground() );
    			setForeground( table.getSelectionForeground() );
    		} else {
    			setForeground( Color.BLACK );
    			Integer type = ( Integer )table.getModel().getValueAt( row, 0 );
    			setBackground( DiffResourceFactory.getBgColorForType( type.intValue() ) );
    		}
    		return this;
    	}

    }

}


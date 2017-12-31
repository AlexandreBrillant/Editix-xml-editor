package com.japisoft.editix.action.search.file;

import java.awt.Color;
import java.awt.Component;
import java.awt.ItemSelectable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;

import com.japisoft.editix.document.DocumentModel;
import com.japisoft.editix.main.EditixApplicationModel;
import com.japisoft.editix.project.ProjectManager;
import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.editix.ui.EditixFactory.XMLDocumentInfoFileFilter;
import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.ui.FastLabel;
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
public class FileSearchUI extends JPanel implements 
		ActionListener, 
		TreeSelectionListener,
		ItemListener {

	/** Creates new form MultiSearchPanel */
    public FileSearchUI() {
    	SearchManager.install();
        initComponents();
        init();
    }

    public void addNotify() {
    	super.addNotify();
    	btSearch.addActionListener( this );
    	btChooseDirectory.addActionListener( this );
    	treeResult.addTreeSelectionListener( this );
    	txtWith.addActionListener( this );
    	cbSearchingIn.addItemListener( this );
    }

    public void removeNotify() {
    	super.removeNotify();
    	btSearch.removeActionListener( this );
    	btChooseDirectory.removeActionListener( this );
    	treeResult.removeTreeSelectionListener( this );
    	txtWith.removeActionListener( this );
    	cbSearchingIn.removeItemListener( this );
    }

    private void init() {
    	cbSearchingIn.addItem( "Directory" );
    	cbSearchingIn.addItem( "Project" );
    	cbSearchingIn.setSelectedIndex( 0 );

    	for ( int i = 0; i < SearchManager.getSearchEngineCount(); i++ ) {
    		cbContaining.addItem( 
    				SearchManager.getSearchEngineAt( 
    						i 
    				)
    		);
    	}

    	cbContaining.setSelectedIndex( 0 );
    	DefaultTreeModel m = new DefaultTreeModel(
    			new DefaultMutableTreeNode( "No result" ) );
    	treeResult.setModel( m );
    	
    	cbFilter.addItem( "Any" );
    	EditixFactory.fillComboBoxFilter( cbFilter );
    	cbFilter.setSelectedIndex( 0 );
    	treeResult.setCellRenderer( new FileSearchRenderer() );
    }
    
    void readParameters( File f ) {
    	try {
			BufferedReader br = new BufferedReader( new FileReader( f ) );
			try {
				int i = Integer.parseInt( br.readLine() );
				if ( i > -1 )
					cbContaining.setSelectedIndex( i );
				i = Integer.parseInt( br.readLine() );
				if ( i > -1 )
					cbSearchingIn.setSelectedIndex( i );
				i = Integer.parseInt( br.readLine() );
				if ( i > -1 )
					cbFilter.setSelectedIndex( i );
				String dir = br.readLine();
				if ( !"".equals( dir ) )
					directory = new File( dir );
				txtWith.setText( br.readLine() );
			} finally {
				br.close();
			}
		} catch (Throwable e) {
			EditixApplicationModel.debug( e );
		}    	
    }

    boolean mustSave() {
    	return cbSave.isSelected();
    }
    
    void saveParameters( File f ) {
    	try {
			BufferedWriter bw = new BufferedWriter( new FileWriter( f ) );
			try {
				bw.write( "" + cbContaining.getSelectedIndex() );bw.newLine();
				bw.write( "" + cbSearchingIn.getSelectedIndex() );bw.newLine();
				bw.write( "" + cbFilter.getSelectedIndex() );bw.newLine();
				if ( directory != null ) {
					bw.write( directory.toString() );
				}
				bw.newLine();
				bw.write( txtWith.getText() );bw.newLine();
			} finally {
				bw.close();
			}
		} catch ( IOException e ) {
			EditixApplicationModel.debug( e );
		}
    }

    public void actionPerformed(ActionEvent e) {
    	if ( e.getSource() == btSearch || 
    			e.getSource() == txtWith ) {

    		if ( "".equals( txtWith.getText() ) ) {
    			txtWith.requestFocus();
    		} else {

	    		// Select a directory for searching
	    		if ( cbSearchingIn.getSelectedIndex() == 0 && 
	    				directory == null ) {
	    			chooseDirectory();
	    		}
	    		search();

    		}
    	} else
    	if ( e.getSource() == cbSearchingIn ) {

    	} else
    	if ( e.getSource() == btChooseDirectory ) {
    		chooseDirectory();	
    	}
    }

    public void itemStateChanged(ItemEvent e) {
    	btChooseDirectory.setEnabled(
    			cbSearchingIn.getSelectedIndex() <= 0 );
    }
    
    public void valueChanged( TreeSelectionEvent e ) {
    	if ( treeResult.getSelectionPath() == null )
    		return;
    	DefaultMutableTreeNode dmtn = ( DefaultMutableTreeNode )treeResult.getSelectionPath().getLastPathComponent();
    	if ( dmtn.getUserObject() instanceof SearchResult ) {
    		SearchResult sr = ( SearchResult )dmtn.getUserObject();
    		
    		File source = sr.getSource();
    		
    		if ( source == null ) {
    			source = 
    				( ( SearchResult )( ( ( DefaultMutableTreeNode )dmtn.getParent() ).getUserObject() ) ).getSource();
    		}

    		if ( EditixFrame.THIS.activeXMLContainerOrOpenIt( 
    				source.toString() ) ) {
    			XMLContainer container = EditixFrame.THIS.getSelectedContainer();
    			
    			int line = sr.getLine();
    			
    			if ( dmtn.getChildCount() > 0 ) {
    				// Get the search result of the first result node
    				line = 
    					( ( SearchResult )( ( DefaultMutableTreeNode )dmtn.getChildAt( 0 ) ).getUserObject() ).getLine(); 
    			}

    			container.getEditor().highlightLine( line + 1 );
    		}
    	}
    }

    private File directory = null; 

    private void chooseDirectory() {
    	JFileChooser chooser = new JFileChooser();
    	if ( directory != null )
    		chooser.setCurrentDirectory( directory );
    	chooser.setMultiSelectionEnabled( false );
    	chooser.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
    	chooser.setDialogTitle( "Search in ?" );
    	if ( chooser.showOpenDialog( EditixFrame.THIS ) == 
    		JFileChooser.APPROVE_OPTION ) {
    		directory = chooser.getSelectedFile();
    	}
    }

    private void search() {
    	btSearch.setEnabled( false );
    	WorkingThread wt = new WorkingThread();
    	wt.start();
    }

    private void fillProjectFile( ArrayList al, File lastProject ) {
    	String[] content = lastProject.list();
    	if ( content != null ) {
    		for ( String f : content ) {
    			File ff = new File( lastProject, f );
    			if ( ff.isFile() )
    				al.add( ff );
    			else
    				if ( ff.isAbsolute() ) {
    					fillProjectFile( al, ff );
    				}
    		}
    	}
    }

    private ArrayList getFilesToSearch() {

    	ArrayList al = null;
    	
    	// Get all the files
    	
    	if ( cbSearchingIn.getSelectedIndex() == 1 ) { // Project
    		File f = ( File )ApplicationModel.getSharedProperty( "lastProject" );
    		if ( f != null ) {
    			al = new ArrayList();
    			fillProjectFile( al, f );
    		}
    	} else { // Directory

        	File f = directory;
        	if ( f != null ) {
	        	String[] fichiers = f.list();
	        	if ( fichiers != null ) {
	        		if ( al == null )
	        			al = new ArrayList();
	        		for ( int i = 0; i < fichiers.length; i++ ) {
	        			File ff = new File( f, fichiers[ i ] );
	        			if ( ff.isFile() )
	        				al.add( ff );
	        		}
	        	}
        	}
    	}

    	// Apply filter
    	if ( al != null ) {
    		boolean all = ( cbFilter.getSelectedIndex() <= 0 );
    		XMLDocumentInfoFileFilter ff = null;
    		if ( !all ) {
    			ff = ( XMLDocumentInfoFileFilter )cbFilter.getSelectedItem();
    		}
    		int i = 0;
    		while ( i < al.size() ) {
    			File f = ( File )al.get( i );
    			if ( !all && !ff.accept( f ) ) {
    				al.remove( f );
    			} else
    				i++;
    		}
    	}

    	return al;

    }

    class WorkingThread extends Thread {
    	
    	public void run() {
    		try {
    			doIt();
    		} finally {
    			btSearch.setEnabled( true );
    			pbTime.setValue( 0 );
    		}
    	}
    	
    	private void doIt() {

        	SearchEngine engine = ( SearchEngine )cbContaining.getSelectedItem();
        	ArrayList alFiles = getFilesToSearch();
        	DefaultMutableTreeNode result = new DefaultMutableTreeNode( "Result" );
        	if ( alFiles != null ) {
        		pbTime.setMaximum( alFiles.size() - 1 );
    	    	for ( int i = 0; i < alFiles.size(); i++ ) {
    	    		pbTime.setValue( i );
    	    		File f = ( File )alFiles.get( i );
    	    		List fr = engine.search( f, txtWith.getText() );
    	    		if ( fr != null && fr.size() > 0 ) {
    	    			DefaultMutableTreeNode file = new DefaultMutableTreeNode( 
    	    					new SearchResultImpl( f, fr.size() ) 
    	    			);
    	    			result.add( file );

    	    			for ( int j = 0; j < fr.size(); j++ ) {
    	    				DefaultMutableTreeNode res = new DefaultMutableTreeNode( 
    	    						fr.get( j ) );
    	    				file.add( res );
    	    				if ( j > 100 )	// Avoid more
    	    					break;
    	    			}
    	    		}
    	    	}
        	}

        	DefaultTreeModel model = ( DefaultTreeModel )treeResult.getModel();
        	result.setUserObject( result.getChildCount() + " result(s)" );
        	model.setRoot( result );
        	// Need to refresh ?
    		
    	}
    	
    }
    
	class FileSearchRenderer implements TreeCellRenderer {
		FastLabel fastlabel = new FastLabel(false);
		Icon drive = null;
		Icon item = null;

		public FileSearchRenderer() {
			try {
				drive = new ImageIcon( ClassLoader
						.getSystemResource( "images/diskdrive.png" ) );
				item = new ImageIcon( ClassLoader
						.getSystemResource( "images/bookmark.png" ) );
			} catch ( Throwable th ) {
				System.err.println("Can't init icons ? : " + th.getMessage());
			}
			fastlabel.setOpaque( true );
		}

		public Component getTreeCellRendererComponent(JTree tree, Object value,
				boolean selected, boolean expanded, boolean leaf, int row,
				boolean hasFocus) {

			DefaultMutableTreeNode tmpNode = ( DefaultMutableTreeNode )value;
			value = tmpNode.getUserObject();
			
			if ( !( value instanceof SearchResult ) ) {
				fastlabel.setText( value.toString() );
				fastlabel.setIcon( drive );
			} else {			
				SearchResult node = (SearchResult) value;
				
				if ( node.getSource() != null ) {
					
					// File node
					fastlabel.setText( 
							value.toString() );
					fastlabel.setIcon( 
							DocumentModel.getDocumentForExt( node.getType() ).getDocumentIcon() );
					
					
				} else {
	
					fastlabel.setIcon( item );
					fastlabel.setText( 
							value.toString() );
					
				}
			}

			if (selected) {
				fastlabel.setForeground(UIManager
						.getColor("List.selectionForeground"));
				fastlabel.setBackground(UIManager
						.getColor("List.selectionBackground"));
			} else {
				Color foreground = treeResult.getForeground();
				Color background = treeResult.getBackground();
				fastlabel.setForeground(foreground);
				fastlabel.setBackground(background);
			}

			return fastlabel;
		}
	}
    
    
    
    ////////////////////////////////////////////////////////////////////////    

    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">
    private void initComponents() {
        jLabel1 = new javax.swing.JLabel();
        cbContaining = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        cbSearchingIn = new javax.swing.JComboBox();
        txtWith = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        cbFilter = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        btSearch = new javax.swing.JButton();
        cbSave = new javax.swing.JCheckBox();
        jSeparator1 = new javax.swing.JSeparator();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        treeResult = new javax.swing.JTree();
        btChooseDirectory = new javax.swing.JButton();
        pbTime = new javax.swing.JProgressBar();

        setBorder(javax.swing.BorderFactory.createTitledBorder("File Search"));
        jLabel1.setText("Matching this");

        cbContaining.setToolTipText("Type of research");

        jLabel2.setText("Searching in");

        cbSearchingIn.setToolTipText("File location");

        txtWith.setToolTipText("Item to search");

        jLabel3.setText("Filter");

        cbFilter.setToolTipText("Filter a document type");

        jLabel4.setText("With");

        btSearch.setText("Search");

        cbSave.setText("Save parameters");
        cbSave.setToolTipText("Save this parameters for the next editix usage");
        cbSave.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        cbSave.setMargin(new java.awt.Insets(0, 0, 0, 0));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Result"));
        jScrollPane1.setViewportView(treeResult);

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 234, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 238, Short.MAX_VALUE)
        );

        btChooseDirectory.setText("...");
        btChooseDirectory.setToolTipText("Directory chooser activator");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jSeparator1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel1)
                            .add(jLabel2)
                            .add(jLabel4)
                            .add(jLabel3)
                            .add(btSearch))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, cbFilter, 0, 179, Short.MAX_VALUE)
                            .add(txtWith, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 179, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, cbContaining, 0, 179, Short.MAX_VALUE)
                            .add(cbSave)
                            .add(layout.createSequentialGroup()
                                .add(cbSearchingIn, 0, 130, Short.MAX_VALUE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(btChooseDirectory, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 43, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                    .add(pbTime, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(cbContaining, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel1))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(cbSearchingIn, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(btChooseDirectory))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(txtWith, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel4))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel3)
                    .add(cbFilter, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(btSearch)
                    .add(cbSave))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jSeparator1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pbTime, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        layout.linkSize(new java.awt.Component[] {btChooseDirectory, cbSearchingIn}, org.jdesktop.layout.GroupLayout.VERTICAL);

    }// </editor-fold>

    // Variables declaration - do not modify
    private javax.swing.JButton btChooseDirectory;
    private javax.swing.JButton btSearch;
    private javax.swing.JComboBox cbContaining;
    private javax.swing.JComboBox cbFilter;
    private javax.swing.JCheckBox cbSave;
    private javax.swing.JComboBox cbSearchingIn;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JProgressBar pbTime;
    private javax.swing.JTree treeResult;
    private javax.swing.JTextField txtWith;
    // End of variables declaration
    
}

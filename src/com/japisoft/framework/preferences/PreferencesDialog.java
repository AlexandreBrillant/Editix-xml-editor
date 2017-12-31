package com.japisoft.framework.preferences;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import javax.swing.AbstractCellEditor;

import javax.swing.Action;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.CellEditorListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import com.japisoft.framework.dialog.BasicOKCancelDialogComponent;
import com.japisoft.framework.dialog.DialogManager;
import com.japisoft.framework.dialog.actions.AbstractDialogAction;
import com.japisoft.framework.dialog.actions.DialogActionModel;
import com.japisoft.framework.ui.toolkit.Toolkit;

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
public class PreferencesDialog
		extends BasicOKCancelDialogComponent
	implements TreeSelectionListener {

	public static final String HIDDEN_GROUP1 = "system";
	public static final String HIDDEN_GROUP2 = "dialog";

	private Properties subGroup = null;
	private HashMap mapGroupTreeNode = null;
	
	public PreferencesDialog( Frame owner, URL subGroupFile ) {
		super(
			owner,
			"Preferences",
			"User preferences",
			"Update/Import preferences and restart the application. Press the 'enter' key for validating a preference change",
			null );
		try {
			if ( subGroupFile != null ) {
				subGroup = new Properties();
				subGroup.load( 
						subGroupFile.openStream() );
				mapGroupTreeNode = new HashMap();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		initUI();
	}

	public PreferencesDialog( Frame owner ) {
		this( owner, 
			  null 
		);
	}

	private JTree treeGroup;
	private JPanel cardPanel;
	private CardLayout card;

	private void initUI() {
		JPanel container = new JPanel();
		container.setLayout(new BorderLayout());
		
		JSplitPane sp = new JSplitPane( 
				JSplitPane.HORIZONTAL_SPLIT,
				new JScrollPane( treeGroup = new TreeGroup() ),
				cardPanel = new JPanel() );

		sp.setDividerLocation( 150 );
		
		container.add( sp );
		cardPanel.setLayout( card = new CardLayout() );
		preparePreferences();
		prepareTree();
		setUI( container );
		container.setPreferredSize( new Dimension( 600, 500 ) );	
	}

	protected DialogActionModel prepareActionModel() {
		DialogActionModel model = DialogManager.buildNewActionModel();
		model.addDialogAction( new ImportAction() );
		model.addDialogAction( new ExportAction() );
		model.addDialogAction( new RestoreAction() );
		return model;
	}	

	public void beforeShowing() {
		super.beforeShowing();
		treeGroup.addTreeSelectionListener(this);
	}

	public void beforeClosing() {
		super.beforeClosing();
		treeGroup.removeTreeSelectionListener(this);
	}

	public void valueChanged(TreeSelectionEvent e) {
		DefaultMutableTreeNode node =
			(DefaultMutableTreeNode) e.getPath().getLastPathComponent();
		if ( !node.isLeaf() )
			return;
		card.show(cardPanel, (String) node.getUserObject());
	}

	private void preparePreferences() {
		Properties prop = Preferences.getCurrentPreferences();
		for (Enumeration enume = prop.keys(); enume.hasMoreElements();) {
			String key = (String) enume.nextElement();
			if (key.endsWith(".value")) {
				StringTokenizer st = new StringTokenizer(key, ".");
				String group = st.nextToken();
				String name = st.nextToken();
				String type = group + "." + name + ".type";
				String value = prop.getProperty(key);
				try {
					int _ = Integer.parseInt(
							prop.getProperty( type ) );
					addValue(group, name, _, value);
				} catch (NumberFormatException exc) {
				}
			}
		}
	}

	private void prepareTree() {
		DefaultTreeModel model = new DefaultTreeModel(createRoot());
		treeGroup.setModel(model);
	}

	private TreeNode createRoot() {
		DefaultMutableTreeNode node = new DefaultMutableTreeNode( "Preferences" );
		if (models == null)
			return node;
		Set s = models.keySet();
		Iterator it = s.iterator();
		while ( it.hasNext() ) {
			String group = ( String ) it.next();
			
			if ( group.equalsIgnoreCase( HIDDEN_GROUP1 ) || 
					group.equalsIgnoreCase( HIDDEN_GROUP2 ) )
				continue;
			
			DefaultMutableTreeNode parentNode = node;
			
			if ( mapGroupTreeNode != null ) {
				if ( subGroup != null ) {
					String parent = 
						subGroup.getProperty( group );
					if ( parent == null ) {
						parent = "other";
					}
					if ( !mapGroupTreeNode.containsKey( parent ) ) {
						parentNode = new DefaultMutableTreeNode( 
								parent );
						
						Toolkit.addNodeOrdering(
								node,
								parentNode );						

						mapGroupTreeNode.put( parent, parentNode );
					} else
						parentNode = 
							( DefaultMutableTreeNode )mapGroupTreeNode.get( parent );
				}
			}

			Toolkit.addNodeOrdering(
					parentNode,
					new DefaultMutableTreeNode( 
							group ) );

			addPanel(group, (DefaultTableModel) models.get(group));
		}
		return node;
	}

	private JTable table;

	private void addPanel(
			String group, 
			DefaultTableModel model ) {
		JPanel panel = new JPanel();
		panel.setBorder( new TitledBorder( group ) );
		table = new JTable();
		table.setModel( model );

		Font f = table.getFont();
		table.setFont( f.deriveFont( Font.BOLD, 11 ) );
		table.setForeground( Color.DARK_GRAY );
		
		table.getColumnModel().getColumn(1).setCellRenderer(
			new ValueRenderer());
		table.getColumnModel().getColumn(1).setCellEditor(new ValueEditor());
		
		table.setSelectionBackground( table.getBackground() );
		table.setSelectionForeground( table.getForeground() );
		table.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
		
		panel.setLayout(new BorderLayout());
		panel.add(new JScrollPane(table));
		cardPanel.add(panel, group);
	}

	private HashMap models = null;

	private void addValue( 
			String group, 
			String name, 
			int type, 
			String value ) {
		if (models == null)
			models = new HashMap();

		DefaultTableModel model = (DefaultTableModel) models.get( group );

		if ( model == null ) {
			model =
				new DefaultTableModel(new Object[] { "Name", "Value" }, 0) {
				public boolean isCellEditable( int row, int column ) {
					return column == 1;
				}
			};
			models.put( group, model );
		}

		boolean inserted = false;
		
		for ( int i = 0; i < model.getRowCount(); i++ ) {
			String currentName = ( String )model.getValueAt( i, 0 );
			if ( currentName.compareTo( name ) > 0 ) {
				model.insertRow( 
					i, 
					new Object[] { name, Preferences.getValueByType( type, value ) } 
				);
				inserted = true;
				break;
			}
		}

		if ( !inserted )
			model.addRow(
					new Object[] { name, Preferences.getValueByType(type, value)}
			);
	}

	public void storePreferences() {
		if ( models == null )
			return;
		Iterator it = models.keySet().iterator();
		while ( it.hasNext() ) {
			String group = ( String ) it.next();
			DefaultTableModel model = ( DefaultTableModel ) models.get( group );
			for (int i = 0; i < model.getRowCount(); i++) {
				Preferences.setRawPreference(
					group,
					( String ) model.getValueAt( i, 0 ),
					model.getValueAt( i, 1 ) );
			}
		}
		Preferences.savePreferences();
	}

	///////////////
	
	class ValueEditor
		extends AbstractCellEditor
		implements ActionListener, TableCellEditor {
		private JButton buttonColor = new JButton("...");
		private JButton buttonFont = new JButton("FONT");
		private JButton buttonRectangle = new JButton("...");
		private JTextField textFieldInteger = new JTextField();
		private JComboBox cbChoice = new JComboBox();
		private JComboBox cbBoolean = new JComboBox( new String[] { "true", "false" } );
		private IntegerDocument doci = new IntegerDocument();
		private CharacterDocument docc = new CharacterDocument();
		
		public ValueEditor() {
			super();
		}
		
		public void addCellEditorListener(CellEditorListener l) {
			super.addCellEditorListener( l );
			buttonColor.addActionListener(this);
			buttonFont.addActionListener(this);
			buttonRectangle.addActionListener(this);
			textFieldInteger.addActionListener(this);
			cbChoice.addActionListener( this );
			cbBoolean.addActionListener( this );			
		}
		
		public void removeCellEditorListener(CellEditorListener l) {
			super.removeCellEditorListener( l );
			buttonColor.removeActionListener(this);
			buttonFont.removeActionListener(this);
			buttonRectangle.removeActionListener(this);
			textFieldInteger.removeActionListener(this);
			cbChoice.removeActionListener( this );
			cbBoolean.removeActionListener( this );			
		}
		
		private JComponent comp = null;
		private int lastRow = -1;
		private int lastType = -1;
		private Object currentValue = null;

		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == buttonColor) {
				Color _ =
					JColorChooser.showDialog(
						buttonColor,
						"Color",
						buttonColor.getBackground());
				if (_ != null) {
					buttonColor.setBackground(_);
					currentValue = _;
				}
			} else if (e.getSource() == buttonFont) {
				FontDialog dialog = new FontDialog( PreferencesDialog.this, buttonFont.getFont());
				dialog.setVisible(true);
				if (dialog.isOk()) {
					dialog.dispose();
					buttonFont.setFont(
						(Font) (currentValue = dialog.getValue()));
				}
			} else if (e.getSource() == textFieldInteger) {
				try {
					if ( lastType == Preferences.INTEGER ) {
						currentValue = new Integer( textFieldInteger.getText() );
					}
					else
					if ( lastType == Preferences.CHAR ) {
						if ( textFieldInteger.getText().length() > 0 )
							currentValue = new Character(
								textFieldInteger.getText().charAt( 0 ) );
					} else
						if ( lastType == Preferences.STRING ) {
							currentValue = textFieldInteger.getText();
						}
				} catch (NumberFormatException exc) {
				}
			} else if (e.getSource() == buttonRectangle) {
				JDialog dialog = new JDialog();
				dialog.setModal( true );
				dialog.setTitle("Choose location and size");
				dialog.setBounds((Rectangle) currentValue);
				dialog.setVisible(true);
				currentValue = dialog.getBounds();
			} else if ( e.getSource() == cbChoice ) {
				String[] __ = ( String[] )currentValue;
				String _ = (String)cbChoice.getSelectedItem();
				for ( int i = 0; i < __.length; i++ ) {
					if ( __[ i ].equals(  _ ) ) {
						String old = __[ 0 ];
						__[ 0 ] = _;
						__[ i ] = old;
						break;
					}
				}
				currentValue = __;
			} else if ( e.getSource() == cbBoolean ) {
				currentValue = new Boolean( "true".equals( cbBoolean.getSelectedItem() ) );
			}
			fireEditingStopped();
		}

		public Object getCellEditorValue() {
			return currentValue;
		}

		public Component getTableCellEditorComponent(
			JTable table,
			Object value,
			boolean isSelected,
			int row,
			int column) {

			currentValue = value;
			lastRow = row;

			if (value instanceof Color) {
				lastType = Preferences.COLOR;
				buttonColor.setBackground((Color) value);
				return buttonColor;
			} else if (value instanceof Integer) {
				lastType = Preferences.INTEGER;
				textFieldInteger.setDocument( doci );
				textFieldInteger.setText(value.toString());
				return textFieldInteger;
			} else if (value instanceof Font) {
				lastType = Preferences.FONT;
				buttonFont.setFont((Font) value);
				return buttonFont;
			} else if (value instanceof Rectangle) {
				Rectangle r = (Rectangle) value;
				buttonRectangle.setText(
					r.x + "," + r.y + "," + r.width + "," + r.height);
				return buttonRectangle;
			} else if ( value instanceof String[] ) {
				DefaultComboBoxModel 
					model = new DefaultComboBoxModel();
				String[] _ = ( String[] )value;
				for  ( int i = 0; i < _.length; i++ )
					model.addElement( _[ i ] );
				cbChoice.setModel( model );
				cbChoice.setSelectedIndex( 0 );
				return cbChoice;
			} else if ( value instanceof Character ) {
				lastType = Preferences.CHAR;
				textFieldInteger.setDocument( docc );
				textFieldInteger.setText( ( (Character)value ).toString() );
				return textFieldInteger;
			} else if ( value instanceof String ) {
				lastType = Preferences.STRING;
				textFieldInteger.setDocument( new PlainDocument() );
				textFieldInteger.setText( value.toString() );
				return textFieldInteger;
			} else if ( value instanceof Boolean ) {
				lastType = Preferences.BOOLEAN;
				cbBoolean.setSelectedItem( value.toString() );
				return cbBoolean;
			}
			return null;
		}

	}

	class ValueRenderer extends DefaultTableCellRenderer {
		private JButton btnColor = new JButton("...");
		private JButton btnFont = new JButton("FONT");
		private JButton btnRectangle = new JButton();
		private JComboBox cbChoice = new JComboBox();
		private JLabel lbl = new JLabel();
		private ColorIcon colorIcon = new ColorIcon();

		public Component getTableCellRendererComponent(
			JTable table,
			Object value,
			boolean isSelected,
			boolean hasFocus,
			int row,
			int column) {

			if ( value instanceof Color ) {
				colorIcon.color = (Color)value;
				btnColor.setIcon( colorIcon );
				return btnColor;
			} else if (value instanceof Font) {
				btnFont.setText("FONT");
				btnFont.setFont((Font) value);
				return btnFont;
			} else if (value instanceof Rectangle) {
				Rectangle r = (Rectangle) value;
				btnRectangle.setText(
					r.x + "," + r.y + "," + r.width + "," + r.height);
				return btnRectangle;
			} else if (value instanceof String[]) {
				DefaultComboBoxModel 
					model = new DefaultComboBoxModel();
				String[] _ = ( String[] )value;
				for  ( int i = 0; i < _.length; i++ )
					model.addElement( _[ i ] );
				cbChoice.setModel( model );
				cbChoice.setSelectedIndex( 0 );				
				return cbChoice;
			} else {
				lbl.setText(value.toString());
				return lbl;
			}
		}

	}

	class ColorIcon implements Icon {
		
		public Color color;
		
		public int getIconHeight() {
			return 16;
		}
		public int getIconWidth() {
			return 16;
		}
		
		public void setColor( Color color ) {
			this.color = color;
		}

		public void paintIcon(Component c, Graphics g, int x, int y) {
			g.setColor( color );
			g.fillRect( 0, 0, 16, 16 );
		}
	}

	//////////////////

	class IntegerDocument extends PlainDocument {
		public void insertString( int offs, String str, AttributeSet a )
				throws BadLocationException {
			for ( int i = 0; i < str.length(); i++ )
				if ( !Character.isDigit(
					str.charAt( i ) ) ) {
					return;
				}
			super.insertString( offs, str, a );
		}
	}

	class CharacterDocument extends PlainDocument {
		public void insertString( int offs, String str, AttributeSet a )
				throws BadLocationException {
			if ( getLength() > 0 )
				super.remove( 0, 1 );
			if ( str.length() == 1 ) {
				str = str.toUpperCase();			
				super.insertString( offs, str, a );
			}
		}
	}

	////////////////////////////////////////////////////////////////////

	class RestoreAction extends AbstractDialogAction {
		public RestoreAction() {
			super( 12, false );
			putValue( Action.SMALL_ICON,
					new ImageIcon(
							ClassLoader.getSystemResource( "images/replace2.png" ) ) );
			putValue( Action.SHORT_DESCRIPTION, "Restore the default preferences" );	
			setSpecial( true );
		}

		public void actionPerformed( ActionEvent e ) {
			if ( JOptionPane.showConfirmDialog( 
					PreferencesDialog.this, 
					"Restore the default preferences and restart the application ?" ) == 
				JOptionPane.OK_OPTION ) {
				Preferences.cleanPreferences();
				setLastAction( 100 );
				setVisible( false );
			}
		}
	}

	class ExportAction extends AbstractDialogAction {
		
		public ExportAction() {
			super( 10, false );
			putValue( Action.SMALL_ICON,
					new ImageIcon(
							ClassLoader.getSystemResource( "images/export1.png" ) ) );

			putValue( Action.SHORT_DESCRIPTION, "Export the preferences" );
			setSpecial( true );
		}

		public void actionPerformed( ActionEvent e ) {
			JFileChooser chooser = new JFileChooser();
			if ( chooser.showSaveDialog( PreferencesDialog.this ) == 
				JFileChooser.APPROVE_OPTION ) {
				try {
					Preferences.preferences.save(
						new FileOutputStream( chooser.getSelectedFile() ), null );
				} catch( IOException exc ) {
					JOptionPane.showMessageDialog( PreferencesDialog.this, exc.getMessage() );
				}
			}
		}
	}

	class ImportAction extends AbstractDialogAction {
		
		public ImportAction() {
			super( 11, false );
			putValue( Action.SMALL_ICON,
					new ImageIcon(
							ClassLoader.getSystemResource( "images/import1.png" ) ) );

			putValue( Action.SHORT_DESCRIPTION, "Import the preferences" );	
			setSpecial( true );
		}

		public void actionPerformed( ActionEvent e ) {
			JFileChooser chooser = new JFileChooser();
			if ( chooser.showOpenDialog( PreferencesDialog.this ) == 
				JFileChooser.APPROVE_OPTION ) {		
				try {
					Preferences.preferences.load( new FileInputStream(
						chooser.getSelectedFile() ) );
				} catch( IOException exc ) {
					JOptionPane.showMessageDialog( PreferencesDialog.this, exc.getMessage() );
				}
			}
		}

	}

}

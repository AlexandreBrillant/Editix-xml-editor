package com.japisoft.framework.xml.refactor.ui;

import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.framework.xml.refactor.RefactorManager;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.japisoft.framework.xml.refactor.elements.RefactorModel;
import com.japisoft.framework.xml.refactor.elements.RefactorObj;

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
public class RefactorUI extends JPanel implements ListSelectionListener {
	private FPNode context;
	private JList l;
	private JCheckBox rcb;
	
	public RefactorUI( FPNode context ) {
		this.context = context;
		init();
		setPreferredSize( new Dimension( 550, 250 ) );
	}

	private void init() {
		setLayout( new BorderLayout() );
		DefaultListModel model = new DefaultListModel();
		prepareRefactorModel( model );
		l = new JList( model );
		l.setCellRenderer( new CustomListRenderer() );
		JScrollPane sp = new JScrollPane( l );
		l.setBorder( new EmptyBorder( 5, 5, 5, 5 ) );
		sp.setBorder( new EmptyBorder( 0, 5, 0, 5 ) );
		add( sp, BorderLayout.WEST );
		if ( context != null ) {
			rcb = new JCheckBox( "Relative Refactoring to " + context.getXPathLocation() );
			rcb.setBorder( new EmptyBorder( 3, 3, 3, 3 ) );
			add( rcb, BorderLayout.SOUTH );
		}
	}

	private void prepareRefactorModel(DefaultListModel model) {
		for (int i = 0; i < RefactorManager.getRefactorCount(); i++) {
			model.addElement( RefactorManager.getRefactor( i ) );
		}
	}

	public void addNotify() {
		super.addNotify();
		l.getSelectionModel().addListSelectionListener( this );
		l.setSelectedIndex( 0 );
	}

	public void removeNotify() {
		super.removeNotify();
		l.getSelectionModel().removeListSelectionListener( this );
	}

	private RefactorModel mapRefTable = new RefactorModel();

	public RefactorModel getModel() {
		if ( rcb != null )
			mapRefTable.setRelativeRefactoring( rcb.isSelected() );
		return mapRefTable;
	}

	public void setModel( RefactorModel model ) {
		this.mapRefTable = model;
		l.getSelectionModel().removeListSelectionListener( this );
		l.getSelectionModel().setSelectionInterval(0, 0);
		updateTable( ( String )l.getSelectedValue() );
		l.getSelectionModel().addListSelectionListener( this );
	}

	public void valueChanged( ListSelectionEvent e ) {
		updateTable( l.getSelectedValue().toString() );
	}

	private void updateTable(String name) {
		if ( !mapRefTable.containsKey( name ) ) {
			mapRefTable.put( name, buildTable( name ) );
		}
		JTable t = ( JTable )mapRefTable.get(
				l.getSelectedValue().toString() );
		
		for ( int i = 1; i < getComponentCount(); i++ ) {
			if ( getComponent( i ) instanceof JScrollPane ) {
				remove( getComponent( i ) );
				break;
			}
		}

		add( 
			new JScrollPane( t ), 
			BorderLayout.CENTER );
		invalidate();
		validate();
		repaint();
	}

	private JTable buildTable( String type ) {		
		RefactorTable table = new RefactorTable( type );
		if ( context != null ) {
			RefactorObj ro = RefactorManager.getRefactor( type );
			ro.initTable( table, context );
		}
		return table;
	}

	public void dispose() {
		this.context = null;
	}

	private Icon SYSTEM_ICON = new ImageIcon( 
			ClassLoader.getSystemResource( 
					"images/pawn_glass_blue.png" ) );

	private Icon OTHER_ICON = new ImageIcon( 
			ClassLoader.getSystemResource( 
					"images/pawn_glass_green.png" ) );

	class CustomListRenderer extends DefaultListCellRenderer {
		public Component getListCellRendererComponent(
				JList arg0, 
				Object arg1, 
				int arg2, 
				boolean arg3, 
				boolean arg4) {
			JComponent comp = ( JComponent )super.getListCellRendererComponent(
					arg0, 
					arg1, 
					arg2, 
					arg3, 
					arg4);
			if ( comp instanceof JLabel ) {
				RefactorObj obj = ( RefactorObj )arg1;
				if ( obj.isDefault() ) {
					( ( JLabel )comp ).setIcon( SYSTEM_ICON );
					comp.setForeground( Color.black );
				} else {
					( ( JLabel )comp ).setIcon( OTHER_ICON );
					comp.setForeground( new Color( 50, 200, 50 ) );
				}
			}
			return comp;
		}
	}

}

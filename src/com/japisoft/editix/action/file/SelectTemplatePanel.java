package com.japisoft.editix.action.file;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager2;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import com.japisoft.editix.document.GroupTemplate;
import com.japisoft.editix.document.TemplateInfo;
import com.japisoft.editix.document.TemplateModel;
import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.dialog.AutoClosableDialog;
import com.japisoft.framework.dialog.AutoClosableListener;
import com.japisoft.framework.dialog.DialogManager;
import com.japisoft.framework.preferences.Preferences;

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
public class SelectTemplatePanel extends JTabbedPane implements AutoClosableDialog {

	public SelectTemplatePanel() {
		setTabPlacement( JTabbedPane.LEFT );
		for ( int i = 0; i < TemplateModel.getGroupTemplateCount(); i++ ) {
			buildTab( TemplateModel.getGroupTemplate( i ) );
		}
		setBorder( null );
	}

	private void buildTab( GroupTemplate ht ) {
		addTab( 
			ht.getName(), 
			ht.getIcon(), 
			new GroupInfoPanel( ht ) 
		);
	}

	public TemplateInfo getTemplateInfo() {
		if ( currentSelection == null )
			return null;
		return currentSelection.ti;
	}

	AutoClosableListener acl = null;
	
	public void setDialogListener(AutoClosableListener acl) {
		this.acl = acl;
	}

	@Override
	public void removeNotify() {
		super.removeNotify();
		this.acl = null;
	}

	@Override
	public void addNotify() {
		super.addNotify();
		
		try {
			String tabName = Preferences.getPreference( Preferences.SYSTEM_GP, "new.tab", (String)null );
			String docName = Preferences.getPreference( Preferences.SYSTEM_GP, "new.doc", (String)null );

			if ( tabName == null ) {
				
				tabName = TemplateModel.getGroupTemplate( 0 ).getName();
				docName = TemplateModel.getGroupTemplate( 0 ).getTemplateInfo( 0 ).label;
				
			}
				
			for ( int i = 0; i < getTabCount(); i++ ) {
	
				if ( tabName.equals( getTitleAt( i ) ) ) {

					setSelectedIndex( i );	
					
					JComponent panelAll = ( JComponent )getComponentAt( i );
					
					for ( int j = 0; j < panelAll.getComponentCount(); j++ ) {
						
						if ( panelAll.getComponent( j ) instanceof TemplateInfoAction ) {
							
							if ( docName.equals( ( ( TemplateInfoAction )panelAll.getComponent( j ) ).ti.label ) ) {
							
								selectButton( ( TemplateInfoAction )panelAll.getComponent( j ) );								
								
							}
							
						}
						
					}
					
					break;
					
				}
				
			}
			
		} catch( Throwable th ) {
			ApplicationModel.debug( th );
		}
	}
	
	class GroupInfoPanel extends JPanel {

		public GroupInfoPanel( GroupTemplate gt ) {
			setLayout( 
				new TemplateLayout()
			);

			setBackground( Color.WHITE );
			
			Icon docIcon = gt.getDocIcon();

			if ( "user".equalsIgnoreCase( gt.getName() ) )
				docIcon = null;
			
			for ( int i = 0; i < gt.getTemplateInfoCount(); i++ )
				add( new TemplateInfoAction( docIcon, gt.getTemplateInfo( i ) ) );
		}

	}

	private TemplateInfoAction currentSelection;
	
	void selectButton( TemplateInfoAction tia ) {	
		if ( currentSelection != null ) {
			currentSelection.setBackground( 					
				Color.WHITE
			);
			currentSelection.setForeground( 
				Color.BLACK 
			);
		}

		this.currentSelection = tia;

		currentSelection.setBackground( 
			UIManager.getColor( "Table.selectionBackground" ) 
		);
		currentSelection.setForeground( 
			UIManager.getColor( "Table.selectionForeground" ) 
		);

		try {
			Preferences.setPreference( Preferences.SYSTEM_GP, "new.tab", getTitleAt( getSelectedIndex() ) );
			Preferences.setPreference( Preferences.SYSTEM_GP, "new.doc", tia.ti.label );
		} catch( Throwable t ) {
			ApplicationModel.debug( t );
		}
	}
	
	class TemplateInfoAction extends JLabel implements MouseListener {
		
		private TemplateInfo ti;

		TemplateInfoAction( Icon docIcon, TemplateInfo ti ) {
			this.ti = ti;
			setText( ti.label );
			
			if ( docIcon == null )
				docIcon = ti.icon;
			
			setIcon( docIcon );
		
			setFont( getFont().deriveFont( 9 ) );
		
			setVerticalTextPosition(JLabel.BOTTOM);
			setHorizontalTextPosition(JLabel.CENTER);			

			setBackground( Color.WHITE );
			
			setToolTipText( ti.help );
			
			setBorder( new EmptyBorder( 1, 1, 1, 1) );
			
			setOpaque( true );
		}

		@Override
		public void addNotify() {
			super.addNotify();
			addMouseListener( this );
		}
		
		@Override
		public void removeNotify() {
			super.removeNotify();
			removeMouseListener( this );
		}

		public void mouseClicked(MouseEvent e) {
			if ( e.getClickCount() > 1 ) {
				if ( acl != null )
					acl.closeDialog();
			}		
		}

		public void mouseEntered(MouseEvent e) {
			setBorder( new LineBorder( Color.LIGHT_GRAY ) );
		}

		public void mouseExited(MouseEvent e) {
			setBorder( new EmptyBorder( 1, 1, 1, 1) );
		}

		public void mousePressed(MouseEvent e) {
			selectButton( this );
		}

		public void mouseReleased(MouseEvent e) {
		}
		
	}

	class TemplateLayout implements LayoutManager2 {

		public void addLayoutComponent( String name, Component comp ) {			
		}
		
		public void addLayoutComponent(Component comp, Object constraints) {
			// TODO Auto-generated method stub
			
		}
		public float getLayoutAlignmentX(Container target) {
			// TODO Auto-generated method stub
			return 0;
		}
		public float getLayoutAlignmentY(Container target) {
			// TODO Auto-generated method stub
			return 0;
		}
		public void invalidateLayout(Container target) {
			layoutContainer( target );
		}
		public Dimension maximumLayoutSize(Container target) {
			// TODO Auto-generated method stub
			return null;
		}

		public void layoutContainer(Container parent) {
			
			int gap = 8;
			int x = gap, y = gap;

			for ( int i = 0; i < parent.getComponentCount(); i++ ) {
				Component c = parent.getComponent( i );

				int w = c.getPreferredSize().width;
				int h = c.getPreferredSize().height;

				if ( x + gap + w >= parent.getWidth() ) {
					y += c.getHeight() + gap;
					x = 0;
				}

				c.setLocation( x, y );
				c.setSize( w, h );

				x += ( w + gap );
			}
		}

		public Dimension minimumLayoutSize(Container parent) {
			return null;
		}

		public Dimension preferredLayoutSize(Container parent) {
			return new Dimension( 400, 250 );
		}

		public void removeLayoutComponent(Component comp) {
		}

	}

	public static void main( String[] args ) {

		DialogManager.showDialog( new JFrame(), "", "", "", null, new SelectTemplatePanel() );

	}
	
}

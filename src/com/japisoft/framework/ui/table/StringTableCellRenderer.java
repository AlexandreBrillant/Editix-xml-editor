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

package com.japisoft.framework.ui.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import com.japisoft.framework.ApplicationModel;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0 */
public class StringTableCellRenderer implements TableCellRenderer {

	private static JLabel lblCommon = new JLabel() {
		public void invalidate() {}
		public void validate() {}
		public boolean isDoubleBuffered() { return false; }
	};

	private JLabel lbl = lblCommon;

	/** Status usage */
	public StringTableCellRenderer() {
		this( lblCommon );
	
	}
	
	public void setFontSize( int size ) {
		lbl.setFont( new Font( Font.SANS_SERIF, Font.PLAIN, size ) );	
	}
	
	/** A simple table renderer working with a label */
	public StringTableCellRenderer( JLabel lbl ) {
		this.lbl = lbl;
		lbl.setOpaque( true );
		background = ( Color )ApplicationModel.getSharedProperty( "table.background.odd.color" );
		alternedBackground = ( Color )ApplicationModel.getSharedProperty( "table.background.even.color" );
		foreground = ( Color )ApplicationModel.getSharedProperty( "table.foreground.color" );
		alterned = ( alternedBackground != null );
	}

	public static void fillIt( JTable t ) {
		fillIt( t, null );
	}
	
	public static void fillIt( JTable t, boolean ... bolds ) {
		StringTableCellRenderer renderer = new StringTableCellRenderer();
		for ( int i = 0; i < t.getColumnCount(); i++ ) {
			StringTableCellRenderer tmpRenderer = renderer;
			if ( bolds != null && bolds[ i ] ) {
				tmpRenderer = new StringTableCellRenderer(   
					new JLabel() {
						public void invalidate() {}
						public void validate() {}
						public boolean isDoubleBuffered() { return false; }
					}
				);
				tmpRenderer.lbl.setForeground( Color.DARK_GRAY );
				tmpRenderer.lbl.setFont(
						tmpRenderer.lbl.getFont().deriveFont(
								Font.BOLD )
				);
			}
			t.getColumnModel().getColumn( i ).setCellRenderer( tmpRenderer );
		}
	}

	private Color background,foreground,alternedBackground;
	private boolean alterned;

	/** A simple table renderer working with a label
	 * @param background Force a column background
	 * @param alternedBackgroundColor for creating a light line difference of background. It can be null
	 * @param foreground Force a column foreground
	 */
	public StringTableCellRenderer( 
			Color background, 
			Color alternedBackgroundColor, 
			Color foreground ) {
		this();
		this.background = background;
		this.foreground = foreground;
		this.alternedBackground = alternedBackgroundColor;
		alterned = alternedBackgroundColor != null;
	}

	public Component getTableCellRendererComponent(
			JTable table, 
			Object value,
			boolean isSelected, 
			boolean hasFocus, 
			int row, 
			int column ) {

		if ( isSelected ) {
			lbl.setBackground( table.getSelectionBackground() );
			lbl.setForeground( table.getSelectionForeground() );
		} else {

			if ( background != null ) {
				if ( alterned ) {
					if ( row % 2 == 0 )
						lbl.setBackground( alternedBackground );
					else
						lbl.setBackground( background );
				} else
				lbl.setBackground( background );
			} else
				lbl.setBackground( table.getBackground() );

			lbl.setForeground( foreground == null ? table.getForeground() : foreground ); 
		}

		if ( table.getModel() instanceof EnableTableModel ) {
			boolean enabled =
				( ( EnableTableModel )table.getModel() ).isEnabled( row );
			if ( !enabled ) {
				if ( !isSelected ) {
					lbl.setBackground( SharedProperties.DISABLED_BGCOLOR );
					lbl.setForeground( SharedProperties.DISABLED_FGCOLOR );
				}
			}
		}

		if ( table.getModel() instanceof ErrorTableModel ) {
			boolean error = 
				( ( ErrorTableModel )table.getModel() ).hasError( row );
			if ( error ) {
				if ( !isSelected )
					lbl.setBackground( SharedProperties.ERROR_BGCOLOR );
			}
		}

		if ( value != null )
			lbl.setText( value.toString() );
		else
			lbl.setText( "" );
		return lbl;
	}	

}

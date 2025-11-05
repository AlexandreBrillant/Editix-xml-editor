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

package com.japisoft.universalbrowser;

import java.awt.Color;
import java.awt.Component;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.UIManager;

import com.japisoft.framework.ui.FastLabel;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 */
class TreeCellRendererImpl implements javax.swing.tree.TreeCellRenderer {

	FastLabel fastlabel = new FastLabel(false);
	Icon drive = null;
	Icon document = null;
	Icon folder = null;
	Icon folder_closed = null;

	public TreeCellRendererImpl() {
		try {
			drive = new ImageIcon(getClass().getResource(
					"diskdrive.png"));
			document = new ImageIcon(getClass().getResource(
					"document.png"));
			folder = new ImageIcon(getClass().getResource(
					"folder.png"));
			folder_closed = new ImageIcon(getClass().getResource(
					"folder_closed.png"));
		} catch (Throwable th) {
			System.err.println("Can't init icons ? : " + th.getMessage());
		}
	}

	FileView fv = null;

	void setFileView(FileView fv) {
		this.fv = fv;
	}

	FileFilter filter = null;
	
	void setFileFilter( FileFilter filter ) {
		this.filter = filter;
	}
	
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean selected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {

		if (!( value instanceof FileObjectTreeNode ) ) {
			fastlabel.setBackground( tree.getBackground() );
			fastlabel.setForeground( Color.RED );
			fastlabel.setText( value.toString() );
			return fastlabel;
		}

		FileObjectTreeNode node = (FileObjectTreeNode) value;
		
		Icon icon = null;

		if (node.isRoot())
			icon = drive;
		else {
			
			if ( node.isFolder() ) {
				icon = !expanded ? folder_closed : folder;
			} else
				icon = document;

		}

		if ( fv != null ) {
			if ( !node.isRoot() ) {
				Icon iconTmp = fv.getIcon( node.getSource() );
				if ( iconTmp != null )
					icon = iconTmp;
			}
		}

		fastlabel.setIcon( icon );
		
		fastlabel.setText(node.toString());

		if (selected) {
			fastlabel.setForeground(UIManager
					.getColor("List.selectionForeground"));
			fastlabel.setBackground(UIManager
					.getColor("List.selectionBackground"));
		} else {

			Color foreground = tree.getForeground();
			Color background = tree.getBackground();

			if ( fv != null ) {
				if ( !node.isRoot() ) {
					Color tmp = fv.getForeground( node.getSource() );
					if ( tmp != null )
						foreground = tmp;
					tmp = fv.getBackground( node.getSource() );
					if ( tmp != null )
						background = tmp;
				}
			}

			if ( filter != null && !node.isRoot() ) {
				if ( !filter.accept( node.getSource() ) )
					foreground = background.darker().darker();
			}

			fastlabel.setForeground( foreground );
			fastlabel.setBackground( background );

		}

		fastlabel.setToolTipText( tree.getToolTipText() );
		
		return fastlabel;
	}

}

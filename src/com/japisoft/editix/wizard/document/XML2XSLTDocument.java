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

package com.japisoft.editix.wizard.document;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EventObject;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.tree.TreeNode;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;

import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.framework.dialog.DialogManager;
import com.japisoft.framework.toolkit.FileToolkit;
import com.japisoft.framework.xml.parser.FPParser;
import com.japisoft.framework.xml.parser.document.Document;
import com.japisoft.framework.xml.parser.node.FPNode;

public class XML2XSLTDocument implements DocumentWizard, TreeCellRenderer, TreeCellEditor, ActionListener {
	
	
	private File source = null;
	private List<FPNode> selections = null;
	
	@Override
	public String start() {
		JFileChooser fc = EditixFactory.buildFileChooser( new FileFilter() {			
			@Override
			public String getDescription() {
				return "XML file (*.xml)";
			}
			@Override
			public boolean accept( File f ) {
				if ( f.isFile() ) {
					String tmp = f.getName().toLowerCase();
					return tmp.endsWith( ".xml" );
				} else
					return true;
			}
		});
		
		if ( fc.showOpenDialog( EditixFrame.THIS ) == 
				JFileChooser.APPROVE_OPTION ) {
			try {
				source = fc.getSelectedFile();
				String content = FileToolkit.getContentFromFileName(source, null );
				FPParser parser = new FPParser();
				Document doc = parser.parseContent( content );
				
				selections = new ArrayList();
				JTree t = new JTree( new DefaultTreeModel( ( TreeNode )doc.getRoot() ) );
				t.setCellRenderer( this );
				t.setCellEditor( this );
				t.setEditable( true );
				DialogManager.showDialog( EditixFrame.THIS, "Select nodes", "Selection of nodes to display", "EditiX will generate templates displaying your selection", null, new JScrollPane( t ) );
				
				String res = null;
				
				if ( selections.size() > 0 ) {
					Map<String,Template> templates = new HashMap<String,Template>();
					for ( FPNode node : selections ) {
						if ( node.isText() )
							node = node.getFPParent();
						FPNode parent = node;

						while ( parent != null ) {
							Template template = templates.get( parent.getContent() );
							if ( template == null ) {
								template = new Template( parent );
								templates.put( parent.getContent(), template );
							}
							if ( node != parent )
								template.addChild( node );
							node = parent;
							parent = parent.getFPParent();
						}
					}
					Collection<Template> values = templates.values();
					StringBuffer sb = new StringBuffer();
					sb.append( "<?xml version='1.0' encoding='UTF-8'?>\n\n" );
					sb.append( "<xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">\n" );
					sb.append( "\n\t<xsl:template match=\"/\">\n" );
					sb.append( "\t\t<xsl:apply-templates select=\"" + doc.getRoot().getNodeContent() + "\"/>\n" );
					sb.append( "\t</xsl:template>\n" );					
					
					for ( Template te : values ) {
						sb.append( te.toXSLT() ).append( "\n" );
					}
					
					sb.append( "\n</xsl:stylesheet>" );
					res = sb.toString();
				}
				
				dispose();
				return res;
				
			} catch( Throwable th ) {
				EditixFactory.buildAndShowErrorDialog( "Can't use this file : " + th.getMessage() );
			}
		}
		return null;
	}
	
	public File getSource() { return source; }

	JCheckBox render = null;
	
	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
			boolean leaf, int row, boolean hasFocus) {
		if ( render == null ) {
			render = new JCheckBox();
			render.setOpaque( false );
		}
		render.setSelected( selections.contains( value ) );
		render.setText( value.toString() );
		return render;
	}

	private CellEditorListener listener = null;
	
	@Override
	public void addCellEditorListener(CellEditorListener l) {
		this.listener = l;
	}

	@Override
	public void cancelCellEditing() {
	}

	@Override
	public Object getCellEditorValue() {
		return lastValue;
	}

	@Override
	public boolean isCellEditable(EventObject anEvent) {
		return true;
	}

	@Override
	public void removeCellEditorListener(CellEditorListener l) {
		listener = null;
	}

	@Override
	public boolean shouldSelectCell(EventObject anEvent) {
		return false;
	}

	@Override
	public boolean stopCellEditing() {
		return false;
	}
	
	private JCheckBox editor = null;
	private FPNode lastValue = null;
	
	@Override
	public Component getTreeCellEditorComponent(JTree tree, Object value, boolean isSelected, boolean expanded,
			boolean leaf, int row) {
		if ( editor == null ) {
			editor = new JCheckBox();
			editor.addActionListener( this );
		}
		editor.setText( value.toString() );
		editor.setSelected(  selections.contains( value ) );
		lastValue = ( FPNode )value;
		return editor;
	}

	@Override
	public void actionPerformed( ActionEvent evt ) {
		if ( listener != null ) {
			if ( editor.isSelected() ) {
				selections.add( lastValue );
			} else 
				selections.remove( lastValue );			
			listener.editingStopped( new ChangeEvent( evt.getSource() ) );			
		}
	}
	
	private void dispose() {
		listener = null;
		render = null;
		editor.removeActionListener( this );
		editor = null;
		lastValue = null;
		selections = null;
	}

	class Template {
		private Set<String> children = null;
		private Set<String> content = null;
		
		private String match;
		private boolean textContent;
		
		public Template( FPNode match ) {
			this.match = match.getContent().trim();
			this.textContent = match.hasTextChildNode();
		}
		
		public void addChild( FPNode node ) {
			if ( node.hasTextChildNode() ) {
				content = new HashSet<String>();
				content.add( node.getContent() );
			} else {
				if ( children == null ) {
					children = new HashSet<String>();
				}
				children.add( node.getContent() );				
			}
		}
				
		public String toXSLT() {
			StringBuffer sb = new StringBuffer();
			
			sb.append( "\n\t<xsl:template match=\"" + match + "\">\n" );
			
			if ( children != null ) {
				for ( String child : children ) {
					sb.append( "\t\t<xsl:apply-templates select=\"" + child + "\"/>\n" );					
				}
			}
			
			if ( content != null ) {
				for ( String c : content ) {
					sb.append( "\t\t<xsl:value-of select=\"" + c + "/text()\"/>\n" );					
				}
			}
			
			if ( children == null && content == null ) {
				if ( textContent ) {
					sb.append( "\t\t<xsl:value-of select=\"/text()\"/>\n" );
				}
			}
			
			sb.append( "\t</xsl:template>" );
			
			
			return sb.toString();
		}
	}
	
}

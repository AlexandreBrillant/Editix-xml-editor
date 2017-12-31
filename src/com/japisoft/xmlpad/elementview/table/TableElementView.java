package com.japisoft.xmlpad.elementview.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;

import javax.swing.AbstractCellEditor;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTextField;

import javax.swing.UIManager;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import com.japisoft.framework.ui.table.ExportableTable;
import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.xmlpad.XMLContainer;

import com.japisoft.xmlpad.elementview.ElementView;
import com.japisoft.xmlpad.elementview.ElementViewContext;
import com.japisoft.xmlpad.helper.SchemaHelperManager;
import com.japisoft.xmlpad.helper.handler.AbstractHelperHandler;
import com.japisoft.xmlpad.helper.model.AttDescriptor;
import com.japisoft.xmlpad.helper.model.AttributeHelper;
import com.japisoft.xmlpad.helper.model.CommonDescriptorRenderer;
import com.japisoft.xmlpad.helper.model.Descriptor;
import com.japisoft.xmlpad.helper.model.TagHelper;

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
public class TableElementView extends ExportableTable implements ElementView {

	private XMLContainer container;

	public TableElementView(XMLContainer container) {
		init();
		this.container = container;
	}

	private void init() {
		setTableHeader(null);
	}

	private Font defaultFont = new Font(null, 0, 12);

	/** Reset the default text font */
	public void setTextFont(Font font) {
		this.defaultFont = font;
	}

	public Font getTextFont() {
		return defaultFont;
	}

	private FastTableCellRenderer renderer = null;

	public TableCellRenderer getDefaultRenderer(Class cl) {
		if (renderer == null)
			renderer = new FastTableCellRenderer();
		return renderer;
	}

	private SimpleCellEditor editor = null;

	public TableCellEditor getDefaultEditor(Class cl) {
		if (editor == null)
			editor = new SimpleCellEditor();
		return editor;
	}

	private ElementViewContext context = null;

	public void init(ElementViewContext context) {
		this.context = context;
	}

	public JComponent getView() {
		return this;
	}

	private SimpleNodeModel model = null;

	public void updateView(FPNode node) {

		if ( node != null && node.isText() ) {
			node = node.getFPParent();
		}

		tagHelper = null;
		attributeHelper = null;

		if ( editor != null )
			editor.init();

		if ( model == null ) {
			model = new SimpleNodeModel();
			setModel( model );
		}

		if ( editor != null )
			editor.cancelCellEditing();

		model.update(node);
		
		setRowHeight( 20 );
	}

	public boolean autoScroll() {
		return false;
	}

	public void dispose() {
		context = null;
		container = null;
		tagHelper = null;
		attributeHelper = null;
	}

	private boolean editable = true;

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	// /////////////////// ERROR LISTENER ////////////////////

	boolean withError = false;

	public void initErrorProcessing() {
	}

	public void stopErrorProcessing() {
		if (renderer != null) {
			if (withError) {
				setEditable(false);
				renderer.setTextColor(Color.GRAY);
				// Stop editing
				if (getCellEditor() != null)
					getCellEditor().stopCellEditing();
			} else {
				renderer.setTextColor(Color.BLACK);
				setEditable(true);
			}
			repaint();
		}
	}

	public void notifyError(Object context,boolean localError, String sourceLocation,
			int line, int col, int offset, String message, boolean onTheFly) {
		withError = true;
	}

	public void notifyNoError(boolean onTheFly) {
		withError = false;
	}

	// /////////////////////////////////////////////////////////

	TableModelEvent singleTableEvent = null;

	TagHelper tagHelper = null;

	AttributeHelper attributeHelper = null;

	class SimpleCellEditor extends AbstractCellEditor implements
			TableCellEditor, ActionListener {

		JComboBox component = new JComboBox();
		JTextField component2 = new JTextField();

		public SimpleCellEditor() {
			if (UIManager.get("xmlpad.helper.backgroundColor") != null)
				component.setBackground(UIManager
						.getColor("xmlpad.helper.backgroundColor"));
			if (UIManager.get("xmlpad.helper.foregroundColor") != null)
				component.setForeground(UIManager
						.getColor("xmlpad.helper.foregroundColor"));
			component.setRenderer(CommonDescriptorRenderer.getRenderer());
			component.setEditable(true);
			component.addActionListener( this );
			component2.addActionListener( this );
			
			component.setFont( defaultFont );
			component2.setFont( defaultFont );
		}
		
		public void actionPerformed(ActionEvent e) {
			fireEditingStopped();
		}		

		private void disabledKnownAttributes() {
			if (!(component.getItemCount() > 0 && component.getItemAt(0) instanceof AttDescriptor))
				return;

			for (int i = 0; i < component.getItemCount(); i++) {
				AttDescriptor d = (AttDescriptor) component.getItemAt(i);
				String name = d.getName();
				d.setEnabled(true);
				for (int j = 2; j < model.getRowCount(); j++) {
					if (model.getValueAt(j, 0).equals(name)) {
						d.setEnabled(false);
						break;
					}
				}
			}
		}

		private void prepareComboTag() {
			init();
			AbstractHelperHandler handler = container.getHelperManager()
					.getHelperHandler(SchemaHelperManager.SCHEMA_ELEMENTS);
			if (handler != null) {

				FPNode parent = container.getCurrentElementNode()
						.getFPParent();

				handler.haveDescriptors(parent, container.getXMLDocument(), false,
						0, null);

				Descriptor[] td = handler.resolveContentAssistant(parent,
						container.getXMLDocument(), false, 0, null);

				if (td != null) {
					for (int i = 0; i < td.length; i++) {
						Descriptor t = td[i];
						component.addItem(t);
					}
				}
			}

		}

		private void prepareComboAttribute() {
			init();

			AbstractHelperHandler handler = container.getHelperManager()
					.getHelperHandler(SchemaHelperManager.SCHEMA_ATTRIBUTES);
			if (handler != null) {
				if (container.getCurrentElementNode().getFPParent() != null) {
					Descriptor[] ad = handler.resolveContentAssistant(container
							.getCurrentElementNode().getFPParent(),
							container.getXMLDocument(), false, 0, null);
					if (ad != null) {
						for (int i = 0; i < ad.length; i++) {
							component.addItem(ad[i]);
						}
					}
				}
			}

		}

		public void init() {
			component.removeAllItems();
		}

		public Component getTableCellEditorComponent(JTable table,
				Object value, boolean isSelected, int rowIndex, int vColIndex) {

			Component current = component;

			if (rowIndex == 0) {
				current = component2;
			}

			if (rowIndex == 1) {
				if (tagHelper == null) {
					// Prepare tag helper
					prepareComboTag();
				} else
					prepareComboTag();

			} else {
				if (rowIndex > 1) {
					prepareComboAttribute();
				}
			}

			if (component.getItemCount() == 0
					|| (vColIndex > 0 && rowIndex != 1))
				current = component2;

			component2.setText(value.toString());
			selectDescriptorCombo(value.toString());

			combo = (current instanceof JComboBox);

			if (combo)
				disabledKnownAttributes();

			return current;
		}

		private void selectDescriptorCombo(String value) {
			for (int i = 0; i < component.getItemCount(); i++) {
				Descriptor d = (Descriptor) component.getItemAt(i);
				if (value.equals(d.getName())) {
					component.setSelectedIndex(i);
					return;
				}
			}
			component.setSelectedItem(value);
		}

		private boolean combo = true;

		public Object getCellEditorValue() {
			if (combo) {
				if (component.getSelectedItem() instanceof Descriptor)
					return ((Descriptor) component.getSelectedItem()).getName();
				else {
					
					return component.getEditor().getItem();
					//return (String) component.getSelectedItem();
				}
			}
			return component2.getText();
		}
	}

	class SimpleNodeModel implements TableModel {
		ArrayList content = null;

		public SimpleNodeModel() {
			super();
		}

		private int startingOffset = 0;

		private int stoppingOffset = 0;

		private boolean closed = false;

		public void update(FPNode node) {

			if (content == null)
				content = new ArrayList();
			else
				content.removeAll(content);

			if (node != null) {
				closed = node.isAutoClose();
				startingOffset = node.getStartingOffset();
				stoppingOffset = node.getStoppingOffset();

				content.add(new String[] {
						"prefix",
						node.getNameSpacePrefix() != null ? node
								.getNameSpacePrefix() : "" });
				content.add(new String[] { "name", node.getContent() });

				if (node.getDefaultNamespace() != null)
					content.add(new String[] { "xmlns",
							node.getDefaultNamespace() });

				// Add namespace
				for (Iterator<String> enume = node.getNameSpaceDeclaration(); enume != null
						&& enume.hasNext();) {
					String decl = (String) enume.next();
					content.add(new String[] { "xmlns:" + decl,
							node.getNameSpaceDeclarationURI(decl) });
				}

				// Add attributes
				for (int i = 0; i < node.getViewAttributeCount(); i++) {
					String att = node.getViewAttributeAt(i);
					String val = node.getAttribute(att);
					content.add(new String[] { att, val });
				}

				// The new attribute
				content.add(new String[] { "", "" });
			}

			fireUpdate();
		}

		void fireUpdate() {
			if (oneListener != null) {
				if (singleTableEvent == null)
					singleTableEvent = new TableModelEvent(this);
				oneListener.tableChanged(singleTableEvent);
			}
		}

		private TableModelListener oneListener = null;

		public void addTableModelListener(TableModelListener l) {
			oneListener = l;
		}

		public Class getColumnClass(int columnIndex) {
			return String.class;
		}

		public int getColumnCount() {
			return 2;
		}

		public String getColumnName(int columnIndex) {
			return String.class.getName();
		}

		public int getRowCount() {
			if (content == null)
				return 0;
			return content.size();
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			return ((Object[]) content.get(rowIndex))[columnIndex];
		}

		public boolean isCellEditable(int rowIndex, int columnIndex) {
			if (!editable)
				return false;

			if (context != null && !context.isEditable())
				return false;
			if (rowIndex > 1)
				return true;
			else
				return columnIndex == 1;
		}

		public void removeTableModelListener(TableModelListener l) {
			this.oneListener = null;
		}

		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			if (content == null)
				content = new ArrayList();

			Object[] val = (Object[]) content.get(rowIndex);
			if (val == null)
				content.set(rowIndex, val = new Object[2]);
			val[columnIndex] = aValue;
			if (!"".equals(aValue) && rowIndex == content.size() - 1) {
				// The last one
				content.add(new Object[] { "", "" });
				fireUpdate();
			}

			// Remove empty line

			if ( "".equals( aValue ) ) {
				String tmp = null;
				if ( columnIndex == 0 ) {
					if (rowIndex < content.size() - 1) {
						content.remove( rowIndex );
						fireUpdate();
					}
				}
			} else {
				if (rowIndex == 1) {
					try {
						FPNode sn = (FPNode) container.getTree()
								.getSelectionPath().getLastPathComponent();
						sn.setContent(aValue.toString());
						container.getTree().repaint();
					} catch (Throwable th) {
					}

					int i = 0;
					if ((i = aValue.toString().indexOf(":")) > -1) {
						try {
							String t = aValue.toString();
							String prefix = t.substring(0, i);
							String name = t.substring(i + 1);

							Object[] _ = (Object[]) content.get(rowIndex);
							_[1] = name;

							Object[] __ = (Object[]) content.get(0);
							__[1] = prefix;

						} catch (Throwable th) {

						}
					}

				}
			}

			if ("".equals(getValueAt(1, 1)))
				return;

			if ("".equals(getValueAt(rowIndex, 0))
					|| "".equals(getValueAt(rowIndex, 1)))
				return;

			// Update the text
			StringBuffer sb = new StringBuffer("<");
			if (!"".equals(getValueAt(0, 1))) // Prefix
				sb.append(getValueAt(0, 1)).append(":");
			sb.append(getValueAt(1, 1));

			String common = sb.toString();

			for (int i = 2; i < content.size() - 1; i++) {
				sb.append(" ");
				String attName = (String) getValueAt(i, 0);
				String attValue = (String) getValueAt(i, 1);
				if (!"".equals(attName) && !"".equals(attValue))
					sb.append(attName).append("=\"").append(
							normalized(attValue)).append("\"");
			}

			if (closed) {
				sb.append("/>");
				context.update(sb.toString(), null, startingOffset,
						stoppingOffset);

			} else {
				sb.append(">");
				Point p = context.update(sb.toString(), "</"
						+ common.substring(1) + ">", startingOffset,
						stoppingOffset);
				if (p != null && p.y > 0) {
					stoppingOffset = p.y;
				}
			}
		}
	}

	private String normalized(String content) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < content.length(); i++) {
			if (content.charAt(i) == '<')
				sb.append("&lt;");
			else if (content.charAt(i) == '>')
				sb.append("&gt;");
			else if (content.charAt(i) == '"')
				sb.append("&quot;");
			else if (content.charAt(i) == '\'')
				sb.append("&apos;");
			else if (content.charAt(i) == '\n')
				sb.append("&#A;");
			else
				sb.append(content.charAt(i));
		}
		return sb.toString();
	}

}

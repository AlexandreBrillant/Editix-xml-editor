package com.japisoft.editix.editor.xsd.view.element;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EventListener;
import java.util.EventObject;

import javax.swing.AbstractButton;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.EventListenerList;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.japisoft.editix.editor.xsd.toolkit.SchemaHelper;
import com.japisoft.editix.editor.xsd.toolkit.XSDAttribute;
import com.japisoft.editix.editor.xsd.view.View;
import com.japisoft.framework.ui.table.ExportableTable;

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
public class PropertiesViewImpl extends ExportableTable implements View {
	protected Element initE;

	protected XSDAttribute[] contents;

	public PropertiesViewImpl() {
		setModel(new CustomAttributesModel());
		getSelectionModel().setSelectionMode(
				ListSelectionModel.SINGLE_SELECTION);
		CustomAttributeRenderer renderer = new CustomAttributeRenderer();
		getColumnModel().getColumn(0).setCellRenderer(renderer);
		getColumnModel().getColumn(1).setCellEditor(new CustomValueEditor());
		getColumnModel().getColumn(1)
				.setCellRenderer(new CustomValueRenderer());
	}

	private PropertiesViewListener listener;

	public void setPropertiesViewListener(PropertiesViewListener listener) {
		this.listener = listener;
	}

	private boolean designerMode = false;

	public void setDesignerMode(boolean designerMode) {
		this.designerMode = designerMode;
	}
	
	public boolean isDesignerMode() {
		return designerMode;
	}

	@Override
	public void copy() {
	}	
	@Override
	public void cut() {
	}
	@Override
	public void paste() {
	}

	private boolean editable = true;
	
	public void init(Element e) {
		
		if ( e != null ) {
			if ( e.getUserData( "disabled" ) != null )
				editable = false;
			else
				editable = true;
		}
		
		stopEditing();
		removeEditor();
		initE = e;
		if (e != null) {	
			if (e.getLocalName() != null)
				contents = SchemaHelper.getAttributesForElement(e
						.getLocalName(), designerMode);
			else if (e.getNodeName() != null) {
				int i = e.getNodeName().lastIndexOf(':');
				if (i > -1)
					contents = SchemaHelper.getAttributesForElement(e
							.getNodeName().substring(0, i), designerMode);
				else
					contents = SchemaHelper.getAttributesForElement(e
							.getNodeName(), designerMode);
			}
			// Remove the name attribute
			ArrayList al = new ArrayList();
			if (contents != null)
				Collections.addAll(al, contents);
			for (int i = 0; i < al.size(); i++) {
				XSDAttribute xa = (XSDAttribute) al.get(i);
				if (!designerMode) {
					if ("name".equals(xa.name)) {
						al.remove(xa);
						break;
					}
				}
			}
			contents = new XSDAttribute[al.size()];
			for (int i = 0; i < al.size(); i++) {
				contents[i] = (XSDAttribute) al.get(i);
			}
		} else
			contents = new XSDAttribute[0];
		reloadModel();
	}

	public void reloadModel() {
		((CustomAttributesModel) getModel()).reload();
	}

	public JComponent getView() {
		return this;
	}

	public void dispose() {
		((CustomValueEditor) getColumnModel().getColumn(1).getCellEditor())
				.dispose();
		initE = null;
		contents = null;
		listener = null;
	}

	public void stopEditing() {
		if (getCellEditor() != null) {
			getCellEditor().stopCellEditing();
			if (getCellEditor() != null) {
				CustomValueEditor editor = (CustomValueEditor) getCellEditor();
				if (editor.storedStoppedValue != null) {
					// Must store this last value
					try {
						setValueAt(editor.storedStoppedValue,
								editor.currentRow, 1);
					} catch (ArrayIndexOutOfBoundsException e) {
						// Ignore it
					}
					editor.storedStoppedValue = null;
					editor.forceStop = false;
				}
			}
		}
	}

	// //////////////////////////////////////////////////////////

	class CustomAttributeRenderer extends DefaultTableCellRenderer {
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			Component c = super.getTableCellRendererComponent(table, value,
					isSelected, hasFocus, row, column);
			XSDAttribute a = contents[row];
			if (initE.getAttribute(a.name) == null
					|| "".equals(initE.getAttribute(a.name)))
				c.setForeground(Color.GRAY);
			else
				c.setForeground(Color.BLACK);
			return c;
		}
	}

	class CustomAttributesModel implements TableModel {
		private TableModelListener l;

		public void reload() {
			l.tableChanged(new TableModelEvent(this));
		}

		public void addTableModelListener(TableModelListener l) {
			this.l = l;
		}

		public void removeTableModelListener(TableModelListener l) {
			this.l = null;
		}

		public Class getColumnClass(int columnIndex) {
			return String.class;
		}

		public int getColumnCount() {
			return 2;
		}

		public String getColumnName(int columnIndex) {
			if (columnIndex == 0)
				return "Name";
			else
				return "Value";
		}

		public int getRowCount() {
			if (contents == null)
				return 0;
			else
				return contents.length;
		}

		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {

			String name = (String) getValueAt(rowIndex, 0);
			int i = name.indexOf("(");
			String type = null;
			if (i > -1) {
				int j = name.lastIndexOf(")");
				type = name.substring(i + 1, j);
				name = name.substring(0, i - 1);
			}

			if (aValue == null || "".equals(aValue))
				initE.removeAttribute(name);
			else {

				Element e = initE;
				boolean resetAttribute = true;

				if ("base".equals(type)) {
					// Search for restriction
					Element tmp = SchemaHelper.searchForChildWithAttribute(
							initE, "base");
					if (tmp != null) {
						e = tmp;
						name = "base";
					}
				} else if ("list items".equals(type)) {
					resetAttribute = false;
					// Search for list
					boolean updated = SchemaHelper.updateListType(e,
							(String) aValue);
					if (!updated) {
						// Remove all the children
						SchemaHelper.removeChildren(e);
						Element parent = e;
						if (!"simpleType".equals(e.getLocalName())) {
							// Create simpleType and add it
							parent = SchemaHelper.createTag(e, "simpleType");
							e.appendChild(parent);
						}
						Element listElement = SchemaHelper.createTag(e, "list");
						parent.appendChild(listElement);
						listElement.setAttribute("itemType", (String) aValue);
						e.removeAttribute("type");
					}
				}
				
				// No (...)
				if ("type".equals(name)) {
					// Remove all simpleType or complexType content
					NodeList nl = e.getChildNodes();
					for (int j = 0; j < nl.getLength(); j++) {
						if (nl.item(j) instanceof Element) {
							Element _ = (Element) nl.item(j);
							if ("simpleType".equals(_.getLocalName())
									|| "complexType".equals(_
											.getLocalName())) {
								e.removeChild(_);
							}
						}
					}
				}

				if ("ref".equals(name)) {
					// Remove the name attribute
					e.removeAttribute("name");
				}

				if (resetAttribute) {
					e.setAttribute(name, (String) aValue);
					listener.resetAttribute(name, (String) aValue);
				}

			}
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			if (contents == null)
				return null;
			else {
				XSDAttribute att = contents[rowIndex];
				String name = att.name;
				if (columnIndex == 0)
					return name;
				else {

					Element e = initE;
					int i = name.indexOf("(");
					if (i > -1) {
						int j = name.lastIndexOf(")");
						String type = name.substring(i + 1, j);
						name = name.substring(0, i - 1);
						if ("base".equals(type)) {
							// Search for restriction
							Element tmp = SchemaHelper
									.searchForChildWithAttribute(initE, "base");
							if (tmp != null) {
								e = tmp;
								name = "base";
							}
						} else if ("list items".equals(type)) {
							return SchemaHelper.getListType(e);
						}
					}

					return e.getAttribute(name);
				}
			}
		}

		public boolean isCellEditable(int rowIndex, int columnIndex) {
			if (columnIndex == 1)
				return true;
			return false;
		}

	}

	// /////////////////////////////////////////////////////////////////////////////////////////////

	class CustomValueRenderer implements TableCellRenderer {
		private JLabel lbl = new JLabel();

		private JComboBox cbb = new JComboBox();

		private JCheckBox cb = new JCheckBox("Ok");

		CustomValueRenderer() {
			cbb.setEditable(true);
			cbb.setOpaque(false);
			cb.setOpaque(false);
		}

		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			XSDAttribute a = contents[row];
			JComponent currentComponent = null;
			switch (a.type) {
			case XSDAttribute.STRING_TYPE:
				if (a.def == null) {
					currentComponent = lbl;
				} else {
					currentComponent = cbb;
				}
				break;
			case XSDAttribute.BOOLEAN_TYPE:
				currentComponent = cb;
				break;
			case XSDAttribute.NON_NEGATIVE_INTEGER:
				if (a.def == null)
					currentComponent = lbl;
				else
					currentComponent = cbb;
				break;
			case XSDAttribute.ELEMENT_REF:
				currentComponent = cbb;
				break;
			case XSDAttribute.TYPEC_REF:
			case XSDAttribute.TYPEP_REF:
			case XSDAttribute.TYPE_REF:
				currentComponent = cbb;
				break;
			case XSDAttribute.ATTRIBUTE_GROUP_REF:
			case XSDAttribute.ATTRIBUTE_REF:
				currentComponent = cbb;
				break;
			case XSDAttribute.GROUP_REF:
				currentComponent = cbb;
				break;
			case XSDAttribute.SCHEMA_REF:
				currentComponent = lbl;
				break;
			}

			if (currentComponent instanceof JLabel) {
				if (value != null)
					((JLabel) currentComponent).setText(value.toString());
			} else if (currentComponent instanceof JComboBox) {
				if (value != null)
					((JComboBox) currentComponent).setSelectedItem(value
							.toString());
			} else if (currentComponent instanceof JCheckBox) {
				((JCheckBox) currentComponent)
						.setSelected("true".equals(value));
			}

			String name = a.name;
			int i = name.indexOf("(");
			if (i > -1)
				name = name.substring(0, i - 1);

			if (initE.getAttribute(name) == null
					|| "".equals(initE.getAttribute(name)))
				currentComponent.setEnabled(false);
			else
				currentComponent.setEnabled(true);

			return currentComponent;
		}
	}

	class CustomValueEditor implements TableCellEditor, ActionListener,
			ItemListener {
		private JComponent currentComponent = null;

		private JComboBox cbb = new JComboBox();

		private JTextField tf = new JTextField();

		private JCheckBox cb = new JCheckBox("Ok");

		CustomValueEditor() {
			cbb.addActionListener(this);
			cb.addActionListener(this);
			// For realTime synchro
			tf.setDocument(new PlainDocument() {
				public void insertString(int offs, String str, AttributeSet a)
						throws BadLocationException {

					XSDAttribute aa = contents[currentRow];

					if ("name".equals(aa.name)) {
						// No whitespace
						boolean allWs = true;
						for (int i = 0; i < str.length(); i++) {
							if (!Character.isWhitespace(str.charAt(i))) {
								allWs = false;
								break;
							}
						}
						if (allWs)
							return;
					}

					super.insertString(offs, str, a);
					initE.setAttribute(aa.name, tf.getText());
					// listener.resetAttribute( aa.name, tf.getText() );
				}

				public void remove(int offs, int len)
						throws BadLocationException {
					super.remove(offs, len);
					XSDAttribute aa = contents[currentRow];
					if ("".equals(tf.getText()))
						initE.removeAttribute(aa.name);
					else
						initE.setAttribute(aa.name, tf.getText());
					// listener.resetAttribute( aa.name, tf.getText() );
				}
			});
			tf.addActionListener(this);
		}

		private int currentRow;

		public Component getTableCellEditorComponent(JTable table,
				Object value, boolean isSelected, int row, int column) {
			currentRow = row;
			oldCurrentComponent = currentComponent;
			currentComponent = null;
			XSDAttribute a = contents[row];
			String[] def = null;
			switch (a.type) {
			case XSDAttribute.STRING_TYPE:
				if (a.def == null) {
					currentComponent = tf;
				} else {
					def = a.def;
				}
				break;
			case XSDAttribute.BOOLEAN_TYPE:
				cb.setSelected("true".equals(value));
				currentComponent = cb;
				break;
			case XSDAttribute.NON_NEGATIVE_INTEGER:
				if (a.def == null)
					currentComponent = tf;
				else {
					def = a.def;
				}
				break;
			case XSDAttribute.ELEMENT_REF:
				def = SchemaHelper.getElementRef(initE.getOwnerDocument()
						.getDocumentElement());
				break;
			case XSDAttribute.TYPE_REF:
				def = SchemaHelper.getType(initE.getOwnerDocument()
						.getDocumentElement(), true, true);
				break;
			case XSDAttribute.TYPEC_REF:
				def = SchemaHelper.getType(initE.getOwnerDocument()
						.getDocumentElement(), true, false);
				break;
			case XSDAttribute.TYPEP_REF:
				def = SchemaHelper.getType(initE.getOwnerDocument()
						.getDocumentElement(), false, true);
				break;
			case XSDAttribute.ATTRIBUTE_REF:
				def = SchemaHelper.getAttributeRef(initE.getOwnerDocument()
						.getDocumentElement());
				break;
			case XSDAttribute.ATTRIBUTE_GROUP_REF:
				def = SchemaHelper.getAttributeGroupRef(initE
						.getOwnerDocument().getDocumentElement());
				break;
			case XSDAttribute.GROUP_REF:
				def = SchemaHelper.getGroupRef(initE.getOwnerDocument()
						.getDocumentElement());
				break;
			case XSDAttribute.SCHEMA_REF:
				break;
			}

			if (def != null) {
				currentComponent = cbb;
				cbb.setModel(new DefaultComboBoxModel(def));
				if (a.anyString)
					((JComboBox) currentComponent).setEditable(true);
				if (value != null)
					((JComboBox) currentComponent).setSelectedItem(value
							.toString());
			} else if (currentComponent instanceof JTextField) {
				if (value != null)
					((JTextField) currentComponent).setText(value.toString());
			}
			return currentComponent;
		}

		public void actionPerformed(ActionEvent e) {
			fireEditingStopped(new ChangeEvent(this));
		}

		public void itemStateChanged(ItemEvent e) {
			fireEditingStopped(new ChangeEvent(this));
		}

		private EventListenerList listenerList = new EventListenerList();

		private void fireEditingStopped(ChangeEvent ee) {
			EventListener[] el = listenerList
					.getListeners(CellEditorListener.class);
			if (el != null) {
				for (int i = 0; i < el.length; i++) {
					((CellEditorListener) el[i]).editingStopped(ee);
				}
			}
		}

		public void addCellEditorListener(CellEditorListener l) {
			listenerList.add(CellEditorListener.class, l);
		}

		public void removeCellEditorListener(CellEditorListener l) {
			listenerList.remove(CellEditorListener.class, l);
		}

		void dispose() {
			if (currentComponent instanceof AbstractButton) {
				((AbstractButton) currentComponent).removeActionListener(this);
			} else if (currentComponent instanceof JComboBox) {
				((JComboBox) currentComponent).removeActionListener(this);
			} else if (currentComponent instanceof JCheckBox) {
				((JCheckBox) currentComponent).removeActionListener(this);
			}
			oldCurrentComponent = null;
			currentComponent = null;
		}

		public void cancelCellEditing() {
		}

		private boolean forceStop = false;

		private Object storedStoppedValue = null;

		public boolean stopCellEditing() {

			// Save old component state for forcing saving
			if (currentComponent instanceof JCheckBox) {
				storedStoppedValue = ""
						+ ((JCheckBox) currentComponent).isSelected();
			} else if (currentComponent instanceof JTextField) {
				storedStoppedValue = ((JTextField) currentComponent).getText();
			} else if (currentComponent instanceof JComboBox) {

				if (((JComboBox) currentComponent).isEditable()) {
					storedStoppedValue = ((JComboBox) currentComponent)
							.getEditor().getItem();
				} else
					storedStoppedValue = ((JComboBox) currentComponent)
							.getSelectedItem();
			}
			forceStop = true;

			return true;
		}

		private JComponent oldCurrentComponent = null;

		public Object getCellEditorValue() {
			if (forceStop) { // Due to a switch to another component without
				// validating
				forceStop = false;
				Object tmp = storedStoppedValue;
				storedStoppedValue = null;
				return tmp;
			}
			if (currentComponent instanceof JCheckBox) {
				return "" + ((JCheckBox) currentComponent).isSelected();
			} else if (currentComponent instanceof JTextField) {
				return ((JTextField) currentComponent).getText();
			} else if (currentComponent instanceof JComboBox) {
				if (((JComboBox) currentComponent).isEditable())
					return ((JComboBox) currentComponent).getEditor().getItem();
				else
					return ((JComboBox) currentComponent).getSelectedItem();
			}
			return null;
		}

		public boolean isCellEditable(EventObject anEvent) {
			return editable;
		}

		public boolean shouldSelectCell(EventObject anEvent) {
			return false;
		}

	}
}

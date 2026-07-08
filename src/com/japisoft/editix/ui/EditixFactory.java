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

package com.japisoft.editix.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Window;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.TableColumnModel;

import org.apache.commons.vfs.FileObject;

import com.japisoft.editix.action.dtdschema.ExportImageFromSchemaEditorAction;
import com.japisoft.editix.action.file.OpenAction;
import com.japisoft.editix.action.xsl.XSLTDialog;
import com.japisoft.editix.document.DocumentModel;
import com.japisoft.editix.editor.css.CSSContainer;
import com.japisoft.editix.editor.html.HTMLContainer;
import com.japisoft.editix.editor.js.JSContainer;
import com.japisoft.editix.editor.json.JSONContainer;
import com.japisoft.editix.editor.jsx.JSXContainer;
import com.japisoft.editix.editor.svg.SVGContainer;
import com.japisoft.editix.editor.xquery.XQueryContainer;
import com.japisoft.editix.editor.xsd.XSDEditor;
import com.japisoft.editix.editor.xsd.XSDEditorObserver;

import com.japisoft.editix.ui.container.EditixXMLContainer;
import com.japisoft.editix.ui.xflows.XFlowsEditor;
import com.japisoft.editix.ui.xslt.XSLTBookmarkContext;
import com.japisoft.editix.ui.xslt.XSLTEditor;
import com.japisoft.editix.ui.xslt.XSLTEditorListener;
import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.application.descriptor.ActionModel;
import com.japisoft.framework.preferences.Preferences;
import com.japisoft.framework.ui.FastLabel;
import com.japisoft.framework.ui.table.StringTableCellRenderer;
import com.japisoft.framework.xml.XMLFileData;

import com.japisoft.universalbrowser.JUniversalBrowserTree;
import com.japisoft.xmlform.designer.DesignerEditor;
import com.japisoft.xmlform.editor.FormEditor;
import com.japisoft.xmlpad.IXMLPanel;

import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.XMLDocumentInfo;

/**
 * Factory for XMLContainer
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor) */
public class EditixFactory {
	
	public static IXMLPanel buildNewContainer() {
		return buildNewContainer(false);
	}

	public static XSLTDialog getConfigDialog( boolean xqueryMode ) {
		if ( !xqueryMode ) {
			return new XSLTDialog();
		} else {
			XSLTDialog dialog = new XSLTDialog(
				"XQuery", 
				"XQuery parameters", 
				"Use the check icon for checking your syntax\nStore these parameters using a project for the next time",
				true
			);
			return dialog;
		}
	}

	private static StringTableCellRenderer defaultTableRenderer = null;
	
	public static void fillDefaultTableRenderer( JTable table ) {
		TableColumnModel cm = table.getColumnModel();
		for ( int i = 0; i < cm.getColumnCount(); i++ ) {
			if ( defaultTableRenderer == null ) {
				Color c1 = Preferences.getPreference( "interface", "table-color-odd", Color.WHITE );
				Color c2 = Preferences.getPreference( "interface", "table-color-even", new Color( Integer.parseInt( "c8eaa5", 16 ) ) );
				Color c3 = Preferences.getPreference( "interface", "table-color-foreground", Color.BLACK );
				defaultTableRenderer = new StringTableCellRenderer( c1, c2, c3 );
			}
			cm.getColumn( i ).setCellRenderer( defaultTableRenderer );
		}
	}

	public static IXMLPanel buildNewContainer( String file, XMLFileData data ) {
		int i = file.lastIndexOf(".");
		String ext = null;
		if (i > -1)
			ext = file.substring(i + 1);
		XMLDocumentInfo info = DocumentModel.getDocumentForExt( ext );
		return buildNewContainerWithType( info.getType(), data );
	}

	public static IXMLPanel buildNewContainer( String file ) {
		int i = file.lastIndexOf( "." );
		String ext = null;
		if (i > -1)
			ext = file.substring( i + 1 );
		XMLDocumentInfo info = DocumentModel.getDocumentForExt( ext );
		if (info != null) {
			IXMLPanel panel = getPanelForType(info.getType());
			panel.setDocumentInfo(info);
			return panel;
		} else
			return buildNewContainer(false);
	}

	public static IXMLPanel buildNewContainer( String type, String file ) {
		XMLDocumentInfo info = null;
		if ( type != null ) 
			info = DocumentModel.getDocumentForType( type );
		if (info != null) {
			IXMLPanel panel = 
				getPanelForType(info.getType());
			panel.setDocumentInfo(info);
			return panel;
		} else
			return buildNewContainer( false );
	}

	public static IXMLPanel buildNewContainerWithType(String type, XMLFileData file) {
		XMLDocumentInfo info = DocumentModel.getDocumentForType(type);
		IXMLPanel panel = null;
		if (info != null) {
			panel = getPanelForType(info.getType());
			panel.setDocumentInfo(info);
		} else
			panel = buildNewContainer(false);
		panel.getMainContainer().setText( file.getContent() );
		panel.getMainContainer().getDocumentInfo().setEncoding(
				file.getEncoding() );
		return panel;
	}

	public static IXMLPanel getPanelForDocument(XMLDocumentInfo info) {
		return getPanelForType(info.getType());
	}

	public static IXMLPanel getPanelForType(String docType ) {

		if ( docType.startsWith( "JSON" ) ) {

			JSONContainer editor = new JSONContainer( ActionModel.restoreAction( "parseJSON" ) );
			return editor;

		}

		if ( "JSX".equals( docType ) ) {

			return new JSXContainer(
					new EditixXSLTFactoryImpl(),
					false,
					null
			);

		}
		
		if ( "SVG".equals( docType ) ) {

			return new SVGContainer();
			
		}

		if ( "EXF".equals( docType ) ) {
			DesignerEditor de = new DesignerEditor(
					new EditixXSLTFactoryImpl()
			);
			return de;
		}

		if ( "XXF".equals( docType ) ) {
			FormEditor fe = new FormEditor(
					new EditixXSLTFactoryImpl()
			);
			return fe;
		}

		if ( "XQR".equals( docType) ) {

			XQueryContainer container = new XQueryContainer(
					new EditixXSLTFactoryImpl(),
					true,
					ActionModel.restoreAction( "parseXQuery" ) );

			container.getMainContainer().setErrorPanelAvailable( true );
			container.getMainContainer().getUIAccessibility().setErrorView(new EditixErrorPanel());
			
			container.setPreference( FilePreferenceImpl.getSingleton() );
			container.setProperty( 
					XSLTEditor.ENCODING, 
					Preferences.getPreference( "file", "rw-encoding", 
							new String[] {})[ 0 ] );			
			return container;

		} else

		if ( "CSS".equals( docType) ) {
		
			CSSContainer editor = new CSSContainer( ActionModel.restoreAction( "parseCSS" ));
			return editor;
			
		} else
				
		if ( "JS".equals( docType ) ) {
			
			JSContainer editor = new JSContainer( ActionModel.restoreAction( "parseJS" ) );
			return editor;
			
		} else
				
		if ( "XSD".equals( docType ) ) {
			XSDEditor editor = new XSDEditor( 
					new XSDFactoryImpl() );
			editor.setCustomActionListener( 
				new ExportImageFromSchemaEditorAction() );
			editor.setObserver( 
					new XSDEditorObserver() {
				public void switchToView(XSDEditor editor, boolean visualMode) {
					if ( visualMode ) {
						
						EditixFrame.THIS.getBuilder().pushAction( editor.getAction( "copy" ), "copy" ); 
						EditixFrame.THIS.getBuilder().pushAction( editor.getAction( "cut" ), "cut" );
						EditixFrame.THIS.getBuilder().pushAction( editor.getAction( "paste" ), "paste" );
						
					} else {

						EditixFrame.THIS.getBuilder().popAction( "copy" ); 
						EditixFrame.THIS.getBuilder().popAction( "cut" );
						EditixFrame.THIS.getBuilder().popAction( "paste" );
						
					}

				}
			} );
			return editor;
		}
		else
		// For 1.0 and 2.0
		if ( docType.startsWith( "XSLT" ) ) {
			XSLTEditor container = new XSLTEditor(
				getXSLTFilesListenerInstance(),
				new EditixXSLTFactoryImpl(),
				true );

			container.getMainContainer().getDocumentColorAccessibility().setBackgroundColorForPrefix(
					"xsl",
					Preferences.getPreference( "editor", "xsltbackground", new Color( 200, 200, 240 ) ) );
			container.setPreference( FilePreferenceImpl.getSingleton() );
			container.getMainContainer().setBookmarkContext( new XSLTBookmarkContext() );
			container.setProperty( 
					XSLTEditor.ENCODING, 
					Preferences.getPreference( "file", "rw-encoding", 
							new String[] {})[ 0 ] );
			return container;
		} else
		if ( "DTD".equals( docType ) ) {
			EditixXMLContainer container = new EditixXMLContainer() {
				public Action getAction(String actionId) {
					if ( "parse".equals( actionId ) )
						return ActionModel.restoreAction( "parseDTD" );
					return super.getAction(actionId);
				}
			};
			return container;
		} else
		if ( "XSC".equals( docType ) ) {
			XFlowsEditor xfe = new XFlowsEditor();
			return xfe;
		} else
		if ( "XHTML".equals( docType ) || "HTML".equals( docType ) ) {
			
			HTMLContainer container = new HTMLContainer( ActionModel.restoreAction( "parseHTML" ) );
			return container;

		}

		return new EditixXMLContainer();
	}

	/** @return a new XMLContainer */
	public static IXMLPanel buildNewContainer(boolean def) {
		XMLContainer container = new EditixXMLContainer();		
		container.setDocumentInfo( DocumentModel.getDocumentForType( "XML" ) );
		if (def) {
			container.getDocumentInfo().setCurrentDocumentLocation("Empty.xml");
		}
		return container;
	}

	private static JFileChooser fileChooser = null;

	/** @return a FileChooser for opening file */
	public static JFileChooser buildFileChooser() {
		if (fileChooser != null)
			return fileChooser;
		fileChooser = new DocumentFileChooser();
		return fileChooser;
	}

	/** @return a FileChooser for opening file */
	public static JFileChooser buildFileChooser( FileFilter ff ) {
		JFileChooser fc = buildFileChooser();
		fc.addChoosableFileFilter( ff );
		fc.setFileFilter( ff );
		return fc;
	}
	
	public static JFileChooser buildFileChooserForDocumentType( String type ) {
		XMLDocumentInfo info = DocumentModel.getDocumentForType( type );
		if ( info == null )
			return buildFileChooser();		
		return new DocumentFileChooser( info );
	}

	private static JFileChooser projectChooser = null;

	/** @return a FileChooser for opening a project file */
	public static JFileChooser buildProjectFileChooser() {
		if (projectChooser != null)
			return projectChooser;
		JFileChooser fc = new JFileChooser();
		projectChooser = fc;
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setFileFilter(new FileFilter() {
			public String getDescription() {
				return "*.pre (editix project file)";
			}
			public boolean accept(File f) {
				return (
					f.isDirectory()
						|| f.toString().toLowerCase().endsWith(".pre"));
			}
		});
		return fc;
	}

	public static boolean mustSaveDialog( XMLContainer container ) {
		if (container.getDocumentInfo().getCurrentDocumentLocation() == null ||
				container.getDocumentInfo().getCurrentDocumentLocation().indexOf( "://" ) != -1 ) {
			buildAndShowErrorDialog("Please save your current document before operating");
			return true;
		}
		return false;
	}

	public static boolean externalMessage() {
		return Preferences.getPreference( "interface", "externalMessage", false );
	}
	
	public static void buildAndShowErrorDialog( String message ) {
		// Avoid to stop the application when starting
		if ( EditixFrame.THIS != null && 
				EditixFrame.THIS.isVisible() && !message.contains( "\n" ) && !externalMessage() ) {			
			ApplicationModel.fireApplicationValue( "error", message );
		} else {
			JOptionPane.showMessageDialog(
					null,
					message,
					"Error",
					JOptionPane.ERROR_MESSAGE );			
		}
	}
	
	public static void buildAndShowWarningDialog( String message ) {
		// Avoid to stop the application when starting
		if ( EditixFrame.THIS != null && 
				EditixFrame.THIS.isVisible() ) {
			JOptionPane.showMessageDialog(
				EditixFrame.THIS,
				message,
				"Warning",
				JOptionPane.WARNING_MESSAGE );
		}
	}

	public static String buildAndShowInputDialog(String title) {
		return JOptionPane.showInputDialog(EditixFrame.THIS.getComponent( 0 ), title);
	}

	public static String buildAndShowInputDialog(String title,String previousValue) {
		return JOptionPane.showInputDialog(EditixFrame.THIS.getComponent( 0 ), title,previousValue);
	}

	public static void buildAndShowInformationDialog(String message) {
		if ( EditixFrame.THIS != null && 
				EditixFrame.THIS.isVisible() && !externalMessage() ) {
			ApplicationModel.fireApplicationValue( "information", message );
		} else {
			JOptionPane.showMessageDialog(
					null,
					message,
					"Information",
					JOptionPane.INFORMATION_MESSAGE );			
		}
	}

	private static Map<String,JDialog> processDialogs = null;
	
	public static void updateProcessMessage( String id, Object message ) {
		JDialog dialog = processDialogs.get( id );
		if ( dialog != null ) {
			Component first = dialog.getContentPane().getComponent( 0 );
			if ( first instanceof JLabel ) {
				( ( JLabel )first ).setText( message.toString() );
			} else
			if ( first instanceof JScrollPane ) {
				( (JTextArea) ( ((JScrollPane)first).getViewport().getView() ) ).setText( message.toString() );
			}
		}
	}

	public static void buildAndShowProcessDialog( String id, Window owner, String title, Object message ) {
		
		SwingUtilities.invokeLater(
				() -> {
		
					if ( processDialogs != null && processDialogs.containsKey( id ) ) {
						processDialogs.get( id ).dispose();
					}
					
					JDialog dialog = null;
					if ( owner == null || owner instanceof JFrame ) {
						dialog = new JDialog( (JFrame)( owner == null ? EditixFrame.THIS : owner ), title, false );
					}
					else {
						dialog = new JDialog( (JDialog)owner, title, false );
					}
					dialog.setModal( true );
					
					if ( message instanceof String ) {
						String strMessage = ( String )message;
						if ( strMessage.length() < 40 ) {
							JLabel b = new JLabel( strMessage );
							b.setBorder( new EmptyBorder( 10, 10, 10, 10 ));					
							dialog.add( b );
						} else {
							JTextArea area = new JTextArea(strMessage);
							dialog.add( new JScrollPane( area ) );
						}
					} else
					if ( message instanceof JComponent ) {
						dialog.add( ( JComponent )message );
					} else {
						dialog.add( new JLabel( message.toString() ) );
					}

					dialog.setDefaultCloseOperation( JDialog.DISPOSE_ON_CLOSE );
					dialog.pack();

					if ( owner == null )
						dialog.setLocationRelativeTo( EditixFrame.THIS );
					else
						dialog.setLocationRelativeTo( owner );
			
					if ( id != null ) {			
						if ( processDialogs == null )
							processDialogs = new HashMap<String,JDialog>();
						processDialogs.put( id, dialog );
					}
					
					dialog.setVisible( true );
		
				} );
	}

	public static void hideProcessDialog( String id ) {
		if ( processDialogs != null ) {
			JDialog dialog = processDialogs.get( id );
			dialog.dispose();
			processDialogs.remove( id );
		}
	}

	public static int buildAndShowChoiceDialog(String message) {
		return JOptionPane.showConfirmDialog(
			EditixFrame.THIS,
			message,
			"Choice",
			JOptionPane.YES_NO_OPTION);
	}

	public static boolean buildAndShowConfirmDialog(String message) {
		return ( buildAndShowChoiceDialog( message ) == JOptionPane.YES_OPTION );
	}

	public static ImageIcon getImageIcon(String resource) {
		URL url = ClassLoader.getSystemClassLoader().getResource(resource);
		if (url != null)
			return new ImageIcon(url);
		else
			com.japisoft.framework.toolkit.Logger.addWarning("Can't find " + resource);
		return null;
	}

	public static JUniversalBrowserTree getUniversalBrowserTree() {
		JUniversalBrowserTree tree = 
			new JUniversalBrowserTree();
		tree.setFileView( new CustomFileView() );
		return tree;
	}
	
	public static JComboBox getFileFilterComboBox() {
		JComboBox cbb = new JComboBox();
		fillComboBoxFilter( cbb );
		return cbb;
	}

	public static void fillComboBoxFilter( JComboBox cbb ) {
		for ( int i = 0; i < DocumentModel.getDocumentCount(); i++ ) {
			XMLDocumentInfo xdo = ( XMLDocumentInfo )DocumentModel.getDocumentAt( i );
			cbb.addItem(
					new XMLDocumentInfoFileFilter( xdo ) );
		}
		cbb.setRenderer( new ComboBoxFileFilterRenderer() );	
	}
	
	/////////////////////////////////////////////////////////////////////////////////

	static class ComboBoxFileFilterRenderer implements ListCellRenderer {
		
		FastLabel lbl = new FastLabel();
		
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			
			if ( isSelected ) {
				lbl.setBackground( list.getSelectionBackground() );
				lbl.setForeground( list.getSelectionForeground() );
			} else {
				lbl.setBackground( list.getBackground() );
				lbl.setForeground( list.getForeground() );
			}
			
			if ( value instanceof String ) {
				lbl.setText( value.toString() );
				lbl.setIcon( null );
				return lbl;
			}
			XMLDocumentInfoFileFilter filter = ( XMLDocumentInfoFileFilter )value;
			lbl.setText( filter.getDescription() );
			lbl.setIcon( filter.getIcon() );

			return lbl;
		}
	}
	
	static class CustomFileView implements com.japisoft.universalbrowser.FileView {

		public Color getBackground(File file) {
			return null;
		}
		public Color getForeground(File file) {
			return null;
		}

		public Color getBackground(FileObject file) {
			return null;
		}
		public Color getForeground(FileObject file) {
			return null;
		}

		public Icon getIcon(FileObject file) {
			String name = ( file.getName().getBaseName() );
			if ( name.indexOf( "." ) == -1 )
				return null;
			File f = new File( name );
			for ( int i = 0; i < DocumentModel.getDocumentCount(); i++ ) {
				XMLDocumentInfo xdo = ( XMLDocumentInfo )DocumentModel.getDocumentAt( i );
				if ( xdo.getFileFilter().accept( f ) )
					return xdo.getDocumentIcon();
			}
			return null;
		}		

		public Icon getIcon(File file) {
			if ( file.isDirectory() )
				return null;
			for ( int i = 0; i < DocumentModel.getDocumentCount(); i++ ) {
				XMLDocumentInfo xdo = ( XMLDocumentInfo )DocumentModel.getDocumentAt( i );
				if ( xdo.getFileFilter().accept( file ) )
					return xdo.getDocumentIcon();
			}
			return null;
		}

	}

	public static class XMLDocumentInfoFileFilter implements com.japisoft.universalbrowser.FileFilter {
		
		XMLDocumentInfo delegate = null;
		
		public XMLDocumentInfoFileFilter( XMLDocumentInfo delegate ) {
			this.delegate = delegate;
		}
		
		public boolean accept(File file) {
			return delegate.getFileFilter().accept( file );
		}

		public String getDescription() {
			return delegate.getDocumentDescription();
		}

		public Icon getIcon() {
			return delegate.getDocumentIcon();
		}
		
		public String getType() {
			return delegate.getType();
		}

		public boolean accept(FileObject file) {
			return accept( new File( file.getName().getBaseName() ) );
		}

		public String toString() {
			return getDescription();
		}
	}
	
	static XSLTEditorListener xsltListener = null;
	
	static XSLTEditorListener getXSLTFilesListenerInstance() {
		if ( xsltListener == null )
			xsltListener = new XSLTFilesListenersImpl();
		return xsltListener;
	}
	
	static class XSLTFilesListenersImpl implements XSLTEditorListener {
		public void setCurrentContainer(XMLContainer container) {
			EditixFrame.THIS.updateCurrentXMLContainer( container );
		}
		public void editDocument(String location) {
			if ( location != null )
				OpenAction.openFile( null, false, location, null, null );
			else 
				buildAndShowWarningDialog( "No file path" );
		}
	}
	
}


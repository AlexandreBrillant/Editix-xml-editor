package com.japisoft.editix.ui.container;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.print.Printable;
import java.lang.reflect.GenericSignatureFormatError;
import java.net.URL;
import java.util.ArrayList;

import java.util.Iterator;
import java.util.List;
import java.util.Timer;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import javax.swing.text.Highlighter.Highlight;
import javax.swing.tree.TreePath;

import org.netbeans.swing.tabcontrol.TabData;
import org.netbeans.swing.tabcontrol.TabbedContainer;

import com.japisoft.editix.action.file.DocumentRenderer;
import com.japisoft.editix.action.search.DisplayOccurencesAction;
import com.japisoft.editix.action.xml.ParseAction;
import com.japisoft.editix.action.xml.refactor.RefactorConvertAttributeToElementAction;
import com.japisoft.editix.action.xml.refactor.RefactorConvertAttributesToElementAction;
import com.japisoft.editix.action.xml.refactor.RefactorCustomAction;
import com.japisoft.editix.action.xml.refactor.RefactorDeleteAttributeAction;
import com.japisoft.editix.action.xml.refactor.RefactorDeleteCommentsAction;
import com.japisoft.editix.action.xml.refactor.RefactorDeleteElementAction;
import com.japisoft.editix.action.xml.refactor.RefactorDeleteElementInNamespaceAction;
import com.japisoft.editix.action.xml.refactor.RefactorDeletePrefixAction;
import com.japisoft.editix.action.xml.refactor.RefactorDeleteTextAction;
import com.japisoft.editix.action.xml.refactor.RefactorInsertAttributeAction;
import com.japisoft.editix.action.xml.refactor.RefactorInsertElementAction;
import com.japisoft.editix.action.xml.refactor.RefactorInsertTextAction;
import com.japisoft.editix.action.xml.refactor.RefactorRenameAttributeAction;
import com.japisoft.editix.action.xml.refactor.RefactorRenameAttributeValueAction;
import com.japisoft.editix.action.xml.refactor.RefactorRenameElementAction;
import com.japisoft.editix.action.xml.refactor.RefactorRenameElementNamespaceAction;
import com.japisoft.editix.action.xml.refactor.RefactorRenameElementPrefixAction;
import com.japisoft.editix.action.xml.refactor.RefactorSurroundElementAction;
import com.japisoft.editix.mapper.Mapper;
import com.japisoft.editix.mapper.MatchingResult;
import com.japisoft.editix.ui.EditixErrorPanel;
import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.editix.ui.EditixNodeLocationListener;
import com.japisoft.editix.ui.EditixStatusBar;
import com.japisoft.editix.ui.locationbar.EditixNodeLocationBar;

import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.application.descriptor.InterfaceBuilder;
import com.japisoft.framework.job.Job;
import com.japisoft.framework.job.JobManager;
import com.japisoft.framework.preferences.Preferences;
import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.framework.xml.refactor2.AbstractRefactor;
import com.japisoft.xmlpad.CaretListener;
import com.japisoft.xmlpad.DocumentStateListener;
import com.japisoft.xmlpad.IView;
import com.japisoft.xmlpad.IXMLPanel;
import com.japisoft.xmlpad.LocationEvent;
import com.japisoft.xmlpad.LocationListener;
import com.japisoft.xmlpad.ToolBarModel;
import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.XMLDocumentInfo;
import com.japisoft.xmlpad.action.edit.CopyNodeAction;
import com.japisoft.xmlpad.action.edit.CutNodeAction;
import com.japisoft.xmlpad.action.edit.SelectTagAction;
import com.japisoft.xmlpad.action.xml.FormatAction;
import com.japisoft.xmlpad.bookmark.DefaultBookmarkContext;
import com.japisoft.xmlpad.editor.LineElement;
import com.japisoft.xmlpad.editor.renderer.ExpressionUnderlineHighlighter;
import com.japisoft.xmlpad.error.ErrorListener;
import com.japisoft.xmlpad.tree.renderer.FastTreeRenderer;
import com.japisoft.xmlpad.xml.validator.DefaultValidator;
import com.japisoft.xmlpad.xml.validator.RelaxNGValidator;
import com.japisoft.xmlpad.xml.validator.Validator;

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
public class EditixXMLContainer extends XMLContainer implements
		LocationListener, 
		DocumentStateListener, 
		CaretListener, 
		ErrorListener,
		MouseListener,
		EditixNodeLocationListener {

	public EditixXMLContainer() {
		super( true );
		getUIAccessibility().setToolBarAvailable( false );
		setStatusBarAvailable( false );
		getUIAccessibility().setPopupAvailable( false );
		getUIAccessibility().setTreePopupAvailable( false );
		setAutoNewDocument( false );
		setErrorPanelAvailable( true );
		getUIAccessibility().setErrorView(new EditixErrorPanel());
		setDisposeAction(false);

		ArrayList model = EditixFrame.THIS.getBuilder().getModel( "TREE" );
		if ( model == null )
			getUIAccessibility().setTreeToolBarAvailable( false );

		getDocument().putProperty(
					PlainDocument.tabSizeAttribute,
						new Integer(Preferences.getPreference( "file", "tab-size", 2 ) ) 
		);

		setAutoQuoteClosing( Preferences.getPreference( "editor", "closeAutoQuote", true ) );
		
		setBookmarkContext( new DefaultBookmarkContext(
				null,
				Preferences.getPreference( "editor", "bookmarkColor", new Color( Integer.parseInt( "CBE1F3", 16 ) ) ) 
		) );
	}

	@Override
	public void setProperty(String name, Object content) {
		super.setProperty(name, content);
		if ( "xquery.ok".equals( name ) ) {
			System.out.println( "ok" );
		}
		try {
			if ( name.startsWith( "color." ) ) {
				String tag = name.substring( 6 );
				getEditor().setColorForTag( tag, Color.decode( "#" + ( String )content ) );
			}
		} catch( Throwable th ) {
			ApplicationModel.debug( th );
		}
	}
	
	protected void resetDefaultTreeToolBarModel(ToolBarModel model) {
		ArrayList al = EditixFrame.THIS.getBuilder().getModel( "TREE" );
		if ( model != null ) {
			for (int i = 0; i < al.size(); i++)
				model.addAction( ( Action ) al.get(i) );
		}
	}

	protected double getInitialDividerLocation() {
		double d = Preferences.getPreference("editor", "dividerLocation", 20) / 100.0;
		if (d >= 1)
			d = 0.2;
		return d;
	}

	Timer t = null;	
	
	public void addNotify() {
		setLocationListener(this);
		addDocumentStateListener(this);
		setCaretListener(this);
		getErrorManager().addErrorListener(this);
		if ( getTree() != null )
			getTree().addMouseListener( this );
	}

	public void removeNotify() {
		unsetLocationListener();
		removeDocumentStateListener(this);
		unsetCaretListener();
		getErrorManager().removeErrorListener(this);
		if ( getTree() != null )
			getTree().removeMouseListener( this );
		currentMapper = null;
	}
	
	// Mapper with the Mouse

	private Mapper currentMapper = null;
	
	@Override
	public void editorMouseClicked(MouseEvent e) {
		if ( currentMapper != null ) {
			ActionMapper am = new ActionMapper( getEditor(), currentMapper, e.getX(), e.getY() );
			am.actionPerformed( null );
			removeExpressionUnderlineHighlighters();
		}
		currentMapper = null;
	}
	
	private void removeExpressionUnderlineHighlighters() {
		// Remove previous ExpressionUnderlineHighlighter
		Highlight[] hh = getEditor().getHighlighter().getHighlights();
		if ( hh != null ) {
			for ( Highlight h : hh ) {
				if ( h.getPainter() instanceof ExpressionUnderlineHighlighter ) {
					getEditor().getHighlighter().removeHighlight( h );
					break;
				}
			}
		}		
	}

	@Override
	public void editorMouseMoved(MouseEvent e) {
		if ( e.isControlDown() ) {
			currentMapper = null;
			removeExpressionUnderlineHighlighters();

			int offset = getEditor().viewToModel( e.getPoint() );

			FPNode node = null;
			try {
				node = getXMLDocument().getXMLPath( offset );
			} catch( RuntimeException re ) {
				return;
			}
			if ( node != null ) {
				List l = getDocumentInfo().getMappers();
				if ( l != null ) {
					for ( int i = 0; i < l.size(); i++ ) {
						Mapper m = ( Mapper )l.get( i );
						if ( m.canMap( node ) ) {	// Can match the current node
							try {								
								// Check if it can process this attribute value
								List<LineElement> les = getXMLDocument().parseLine( offset );
								LineElement currentElement = null;
								String lastAttributeName = null;
								for ( LineElement le : les ) {
									if ( le.offset + le.content.length() > offset ) {
										currentElement = le;
										break;
									}
									if ( le.type == LineElement.ATTRIBUTE ) {
										lastAttributeName = le.content;
									}
								}
								
								if ( currentElement != null ) {
									if ( currentElement.type == LineElement.LITERAL ) {
										if ( lastAttributeName != null ) {
											String[] ma = m.getMapAttributes();
											if ( ma != null ) {
												for ( String _m : ma ) {
													if ( _m.equalsIgnoreCase( lastAttributeName ) ) {
														// We can manage this attribute
														// Underline it and prepare to a click
														currentMapper = m;
														ExpressionUnderlineHighlighter euh = new ExpressionUnderlineHighlighter( Color.BLUE );
														getEditor().getHighlighter().addHighlight(
																currentElement.offset,  
																currentElement.offset + currentElement.content.length(), 
																euh 
														);
														getEditor().repaint();
														
													}
												}
											}
										}
									}
								}
							} catch( BadLocationException be ) {
							}

						}
					}
				}
			}
		}
	}

	@Override
	public void setDocumentInfo(XMLDocumentInfo info) {
		super.setDocumentInfo(info);
		if ( getInnerView() instanceof EditixXMLContainerInnerView ) {
			( ( EditixXMLContainerInnerView )getInnerView() ).initForDocumentInfo( info ); 
		}
	}

	@Override
	public void dispose() {
		super.dispose();
		( ( EditixNodeLocationBar )sp1.getColumnHeader().getView() ).dispose();
		if ( sp2 != null ) {
			( ( EditixNodeLocationBar )sp2.getColumnHeader().getView() ).dispose();
		}
		locationBar = null;
	}	

	public void setUIReady(boolean ok) {
		super.setUIReady( ok );
		if ( ok )
			addNotify();
		else
			removeNotify();
	}

	private IView customView;
	
	@Override
	public IView getInnerView() {
		if ( Preferences.getPreference( "editor", "filterView", true ) ) {
			if ( customView == null ) {
				customView = new EditixXMLContainerInnerView( super.getInnerView() );
			}
			return customView;
		} else
			return super.getInnerView();
	}	

	private EditixNodeLocationBar locationBar = null;
	
	private EditixNodeLocationBar getLocationBar() {
		if ( locationBar == null ) {
			locationBar = new EditixNodeLocationBar( this );
		}
		return locationBar;
	}
	
	@Override
	protected JScrollPane prepareScrollPaneTextEditor( JComponent component ) {
		JScrollPane sp = 
			super.prepareScrollPaneTextEditor( 
				component 
		);
		sp.setColumnHeaderView( 
			getLocationBar()
		);
		return sp;
	}

	// Top toolbar
	public void gotoNode(FPNode node) {
		if ( node != null )
			getEditor().setCaretPosition( 
					node.getStartingOffset() + 1 );
	}
	public void copyNode(FPNode node) {
		if ( node != null ) {
			CopyNodeAction.copyAction( this, node );
		}
	}
	public void cutNode(FPNode node) {
		if ( node != null ) {
			CutNodeAction.cutAction( this, node );
		}
	}
	public void selectNode(FPNode node) {
		if ( node != null ) {
			SelectTagAction.selectNode( 
				this, 
				node 
			);
		}
	}

	public void locationChanged(LocationEvent e) {
		EditixStatusBar.ACCESSOR.setXPathLocation(
			e.getXPathLocation()
		);
		
		if ( getEditor() != null ) {
			EditixNodeLocationBar locationBar = 
				( EditixNodeLocationBar )sp1.getColumnHeader().getView();
			locationBar.setCurrentNode( 
					e.getDocumentLocation() );
			if ( sp2 != null ) {
				locationBar = 
					( EditixNodeLocationBar )sp2.getColumnHeader().getView();
				locationBar.setCurrentNode( 
						e.getDocumentLocation() );
			}
		}		
	}

	public void documentModified( XMLContainer source ) {
		EditixFrame.THIS.documentModified(this);
	}

	public void newDocument( XMLContainer source ) {
		EditixFrame.THIS.documentUnModified(this);
	}

	private boolean firstError = true;

	public void initErrorProcessing() {
		firstError = true;
	}

	private IconErrorProxy iep = null;	

	private void resetErrorIcon( boolean errorIcon ) {

		TabbedContainer container = null;
		Component component = getView();
		Component theRightEditorComponent = component;
		while ( component != null ) {
			component = component.getParent();
			if ( component instanceof TabbedContainer ) {
				container = ( TabbedContainer )component;
				break;
			}
			if ( component instanceof IXMLPanel )
				theRightEditorComponent = component;
		}
		if ( container != null ) {

			for ( int i = 0; i < container.getTabCount(); i++ ) {
				if ( container.getComponentAt( i ) == theRightEditorComponent ) {
					TabData td = container.getModel().getTab( i );

					if ( errorIcon ) {
					
						if ( !( td.getIcon() instanceof IconErrorProxy ) ) {
							if ( iep == null || 
									getDocumentInfo().getDocumentIcon() != iep.getSource() )
								iep = new IconErrorProxy( getDocumentInfo().getDocumentIcon() );
							
							container.getModel().setIcon( i, iep );
							
						}
					
					} else {

						if ( td.getIcon() instanceof IconErrorProxy ) {
							
							if ( iep != null )
								container.getModel().setIcon( i, iep.getSource() );
							
						}
						
					}
					
					break;
				}
			}
		}

	}

	public void notifyError(
			Object context, 
			boolean localError,
			String sourceLocation, 
			int line, 
			int col, 
			int offset,
			String message, 
			boolean onTheFly ) {

		resetErrorIcon( true );

		if ( onTheFly ) {
		
			EditixStatusBar.ACCESSOR.setError(
					context, 
					localError, 
					sourceLocation,
					message, 
					line );

			if ( firstError ) {
				if ( localError ) {
					getEditor().highlightErrorLine(line + 1);
					firstError = false;
				}
			}

		}
	}

	public void notifyNoError( boolean onTheFly ) {
		resetErrorIcon( false );		
		EditixStatusBar.ACCESSOR.setError(null, false, null, null, 0);
		int i = EditixFrame.THIS.getMainTabbedPane().getSelectedIndex();
		if ( onTheFly ) {
			getEditor().removeHighlightedErrorLine();
			firstError = true;

			// Check background validation
			if ( Preferences.getPreference( "xmlconfig", "backgroundValidation", false ) ) {
				JobManager.COMMON_MANAGER.addJob( new BackgroundValidation() );
			}
		}
	}

	class BackgroundValidation implements Job {
		public void run() {
			// Background validation
			getErrorManager().flushLastError();
			getErrorManager().initErrorProcessing();
			try {
				Validator v = getDocumentInfo().getCustomValidator();
				if ( v == null ) {
					if ( getSchemaAccessibility().getRelaxNGValidationLocation() != null ) {
						v = new RelaxNGValidator();
					} else {
						v = new DefaultValidator();
					}
				}
				v.validate( EditixXMLContainer.this, true );
			} finally {
				getErrorManager().stopErrorProcessing();
				if ( !getErrorManager().hasLastError() )
					getErrorManager().notifyNoError( false );
			}
		}
		public void dispose() {			
		}
		public boolean isAlone() {
			return false;
		}
		public boolean hasErrors() {
			return false;
		}
		public Object getSource() {
			return EditixXMLContainer.this;
		}
		public void stopIt() {
		}
	}
	
	public void stopErrorProcessing() {}

	public JPopupMenu getCurrentPopup() {
		if (EditixFrame.THIS != null)
			return EditixFrame.THIS.getBuilder().getPopup( "EDITOR" );
		else
			return null;
	}

	public void showPopup( Component c, int x, int y ) {
		// Prepare the 'search' popup subMenu for all elements/attributes occurences
		EditixFrame.THIS.getBuilder().cleanMenuItems( "findOccurences" );
		FPNode node = getCurrentElementNode();
		if ( node != null ) {
			DisplayOccurencesAction a = new DisplayOccurencesAction();
			a.putValue( Action.NAME, "The element " + node.getContent() );
			a.putValue( "param", "e" + node.getContent() );
			JMenu menu = EditixFrame.THIS.getBuilder().getMenu( "findOccurences" );
			menu.setEnabled( true );
			menu.add( a );
			if ( node.getViewAttributeCount() > 0 ) {
				menu.addSeparator();
				for ( int i = 0; i < node.getViewAttributeCount(); i++ ) {
					a = new DisplayOccurencesAction();
					a.putValue( Action.NAME, "The attribute " + node.getViewAttributeAt( i ) );
					a.putValue( "param", "a" + node.getViewAttributeAt( i ) );
					menu.add( a );
				}
			}

			// Prepare the mapper lists for XSLT matchers....
			ArrayList mappers = getDocumentInfo().getMappers();
			JMenu menuMappers = EditixFrame.THIS.getBuilder().getMenu( "references" );
			menuMappers.removeAll();			
			if ( mappers != null ) {
				if ( node != null ) {
					for ( int i = 0; i < mappers.size(); i++ ) {
						Mapper m = ( Mapper )mappers.get( i );
						if ( m.canMap( node ) ) {
							ActionMapper am = new ActionMapper( c, m, x, y );
							menuMappers.add( am );
						}
					}
				}
			}
			menuMappers.setEnabled( menuMappers.getItemCount() > 0 );			
		}

		// Prepare the refactor menu
		EditixFrame.THIS.getBuilder().cleanMenuItems( "refactorRename" );
		EditixFrame.THIS.getBuilder().cleanMenuItems( "refactorDelete" );
		EditixFrame.THIS.getBuilder().cleanMenuItems( "refactorConvert" );
		EditixFrame.THIS.getBuilder().cleanMenuItems( "refactorSurround" );
		EditixFrame.THIS.getBuilder().cleanMenuItems( "refactorInsert" );

		JMenu menu = EditixFrame.THIS.getBuilder().getMenu( "refactorRename" );		
		menu.setEnabled( true );

		if ( node != null ) {

			boolean firstSeparator = false;

			ArrayList refactors = ( ArrayList )getDocumentInfo().getProperty( "refactor" );
			if ( refactors != null ) {

				for ( int i = 0; i < refactors.size(); i++ ) {
					
					AbstractRefactor ar = ( AbstractRefactor )refactors.get( i );
					if ( ar.process( node ) ) {
						firstSeparator = true;
						RefactorCustomAction rca = new RefactorCustomAction( ar );
						rca.putValue(
								Action.NAME,
								ar.getTitle( node ) );
						
						rca.putValue(
								Action.SMALL_ICON,
								getDocumentInfo().getDocumentIcon()
						);
						
						menu.add( rca );
					}
					
				}

			}

			if ( firstSeparator )
				menu.addSeparator();

			RefactorRenameElementAction a = new RefactorRenameElementAction();
			a.putValue(
					Action.NAME, "The elements '" + node.getContent() + "'" );

			menu.add( a );
			
			if ( node.getNameSpacePrefix() != null ) {
				RefactorRenameElementPrefixAction pa = new RefactorRenameElementPrefixAction();
				pa.setName( node.getNameSpacePrefix() );
				pa.putValue(
						Action.NAME, "The element prefix '" + node.getNameSpacePrefix() + "'" );
				menu.add( pa );
			}

			// Rename declared prefix
			Iterator<String> otherPref = node.getNameSpaceDeclaration();
			if ( otherPref != null ) {
				for ( ;otherPref.hasNext(); ) {
					String name = otherPref.next();
					if ( !name.equals( node.getNameSpacePrefix() ) ) {
						RefactorRenameElementPrefixAction pa = new RefactorRenameElementPrefixAction();
						pa.setName( name );
						pa.putValue(
								Action.NAME, "The element prefix '" + name + "'" );
						menu.add( pa );
					}
				}
			}

			if ( node.getNameSpaceURI() != null ) {
				RefactorRenameElementNamespaceAction pa = new RefactorRenameElementNamespaceAction();
				pa.setOldValue( node.getNameSpaceURI() );
				pa.putValue(
						Action.NAME, "The element namespace URI '" + node.getNameSpaceURI() + "'" );
				menu.add( pa );				
			}

			Iterator<String> otherNS = node.getNameSpaceDeclaration();

			// Check for namespace declarations
			if ( otherNS != null )
				for ( ;otherNS.hasNext(); ) {
					String name = ( String )otherNS.next();
					String uri = node.getNameSpaceDeclarationURI( name );
					if ( !uri.equals( node.getNameSpaceURI() ) ) {
						RefactorRenameElementNamespaceAction pa = new RefactorRenameElementNamespaceAction();
						pa.setOldValue( uri );
						pa.putValue(
								Action.NAME, "The namespace URI '" + uri + "'" );
						menu.add( pa );				
					}
				}

			if ( node.getViewAttributeCount() > 0 ) {
				menu.addSeparator();
				for ( int i = 0; i < node.getViewAttributeCount(); i++ ) {
					RefactorRenameAttributeAction aa = new RefactorRenameAttributeAction();
					
					String name = node.getViewAttributeAt( i );
					int j = name.lastIndexOf( ":" );
					if ( j > -1 )
						name = name.substring( j + 1 );

					aa.setOldName( name );
					aa.putValue(
							Action.NAME, "The attribute '" + name + "'" );
					menu.add( aa );
				}
				menu.addSeparator();
				for ( int i = 0; i < node.getViewAttributeCount(); i++ ) {
					RefactorRenameAttributeValueAction aa = new RefactorRenameAttributeValueAction();
					
					String name = node.getViewAttributeAt( i );
					String value = node.getAttribute( name );

					aa.setOldValue( value );
					aa.putValue(
							Action.NAME, "The attribute value '" + value + "'" );
					menu.add( aa );
				}

			}
			
			///////////////// DELETE //////////////////////
			
			menu = EditixFrame.THIS.getBuilder().getMenu( "refactorDelete" );
			menu.setEnabled( true );
			RefactorDeleteElementAction rdea =
				new RefactorDeleteElementAction();
			rdea.putValue( Action.NAME, "The elements '" + node.getContent() + "'" );
			menu.add( rdea );

			if ( node.getNameSpaceURI() != null ) {
				RefactorDeleteElementInNamespaceAction pa = new RefactorDeleteElementInNamespaceAction();
				pa.putValue(
						Action.NAME, "All the elements in the namespace '" + node.getNameSpaceURI() + "'" );
				menu.add( pa );
			}

			if ( node.getNameSpacePrefix() != null ) {
				RefactorDeletePrefixAction pa = new RefactorDeletePrefixAction();
				pa.setPrefix( node.getNameSpacePrefix() );
				pa.putValue(
						Action.NAME, "The prefix '" + node.getNameSpacePrefix() + "'" );
				menu.add( pa );
			}

			otherNS = node.getNameSpaceDeclaration();

			// Check for namespace declarations
			if ( otherNS != null )
				for ( ;otherNS.hasNext(); ) {
					String name = ( String )otherNS.next();

					if ( !name.equals( node.getNameSpacePrefix() ) ) {
						RefactorDeletePrefixAction pa = new RefactorDeletePrefixAction();
						pa.setPrefix( name );
						pa.putValue(
								Action.NAME, "The prefix '" + name + "'" );
						menu.add( pa );
					}
					
				}

			if ( node.getViewAttributeCount() > 0 ) {
				menu.addSeparator();
				for ( int i = 0; i < node.getViewAttributeCount(); i++ ) {
					RefactorDeleteAttributeAction aa = new RefactorDeleteAttributeAction();

					String name = node.getViewAttributeAt( i );
					int j = name.lastIndexOf( ":" );
					if ( j > -1 )
						name = name.substring( j + 1 );

					aa.setName( name );
					aa.putValue(
							Action.NAME, "The attribute '" + name + "'" );
					menu.add( aa );
				}				
			}			

			RefactorDeleteTextAction _ = new RefactorDeleteTextAction();
			_.putValue(
					Action.NAME, "Text inside the elements '" + node.getContent() + "'" );
			menu.add( _ );

			/////////////// CONVERT ///////////////

			menu = EditixFrame.THIS.getBuilder().getMenu( "refactorConvert" );
			menu.setEnabled( true );
			RefactorConvertAttributesToElementAction ca =
				new RefactorConvertAttributesToElementAction();
			ca.putValue( Action.NAME, "The attributes of the elements '" + node.getContent() + "' to elements" );
			menu.add( ca );			
			if ( node.getViewAttributeCount() > 0 ) {
				menu.addSeparator();
				for ( int i = 0; i < node.getViewAttributeCount(); i++ ) {
					RefactorConvertAttributeToElementAction aa = new RefactorConvertAttributeToElementAction();

					String name = node.getViewAttributeAt( i );
					int j = name.lastIndexOf( ":" );
					if ( j > -1 )
						name = name.substring( j + 1 );

					aa.setName( name );
					aa.putValue(
							Action.NAME, "The attribute '" + name + "' to element" );
					menu.add( aa );
				}
			}			
			
			////////////////// SURROUND ////////////////

			menu = EditixFrame.THIS.getBuilder().getMenu( "refactorSurround" );
			menu.setEnabled( true );
			RefactorSurroundElementAction sa =
				new RefactorSurroundElementAction();
			sa.putValue( Action.NAME, "The elements '" + node.getContent() + "'" );
			menu.add( sa );			

			/////////////// INSERT //////////////

			menu = EditixFrame.THIS.getBuilder().getMenu( "refactorInsert" );
			menu.setEnabled( true );
			RefactorInsertElementAction ia =
				new RefactorInsertElementAction();
			ia.putValue( Action.NAME, "A new element inside the elements '" + node.getContent() + "'" );
			menu.add( ia );			
			RefactorInsertAttributeAction iaa =
				new RefactorInsertAttributeAction();
			iaa.putValue( Action.NAME, "A new attribute inside the elements '" + node.getContent() + "'" );
			menu.add( iaa );			
			RefactorInsertTextAction it =
				new RefactorInsertTextAction();
			it.putValue( Action.NAME, "A text inside the elements '" + node.getContent() + "'" );
			menu.add( it );			
			
		}

		menu = EditixFrame.THIS.getBuilder().getMenu( "refactorDelete" );
		menu.setEnabled( true );
		// Always
		RefactorDeleteCommentsAction
			dca = new RefactorDeleteCommentsAction();
		dca.putValue( Action.NAME, "Delete all the comments (except before the root)" );
		menu.add( dca );


		super.showPopup(c, x, y);
	}	

	protected boolean useCustomPopupMenu() {
		return true;
	}

	public void caretLocation(int col, int line) {
		EditixStatusBar.ACCESSOR.setLocation(col, line);
	}

	public Object getProperty(String name) {
		if ( FormatAction.PREF_APOSENTITY.equals( name ) ) {
			if ( "XSLT".equals( getDocumentInfo().getType() ) ||
					"XSLT2".equals( getDocumentInfo().getType() ) )
				return Boolean.FALSE;
			else
				return super.getProperty( name );
		} else
			return super.getProperty(name);
	}	
	
	public void mouseClicked(MouseEvent e) {
	}
	
	public void mouseEntered(MouseEvent e) {
	}
	
	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
		maybeShowPopup( e );		
	}

	public void mouseReleased( MouseEvent e ) {
		maybeShowPopup( e );
	}
	
	private void maybeShowPopup( MouseEvent e) {
		if ( e.isPopupTrigger() ) {
			TreePath tp = 
				getTree().getSelectionPath();
			if ( tp != null ) {
				FPNode sn = ( FPNode )tp.getLastPathComponent();
				if ( sn != null && sn.isTag() ) {
					// Display the tree popup
					JPopupMenu menu = new JPopupMenu();
					
					InterfaceBuilder builder = ApplicationModel.INTERFACE_BUILDER;
					ArrayList l = builder.getModel( "TREE.POPUP" );

					if ( l != null ) {
						for ( Object o : l ) {
							menu.add( ( Action )o );
						}
						menu.addSeparator();
					}

					menu.add( 
						new CustomNodeLabel() 
					);
					menu.show( getTree(), e.getX(), e.getY() );
				}
			}
		}		
	}
	
	class SpellCheckerAction extends AbstractAction {
		private int start, stop;
		private String word;
		public SpellCheckerAction( int start, int stop, String word ) {
			putValue( Action.NAME, word );
			this.start = start;
			this.stop = stop;
			this.word = word;
		}
		public void actionPerformed(ActionEvent e) {
			getEditor().select( this.start, this.stop );
			getEditor().replaceSelection( this.word );			
			getEditor().repaint();
		}
	}
	
	class CustomNodeLabel extends AbstractAction {
		public CustomNodeLabel() {
			putValue(
				Action.NAME, 
				"Display this attribute" );
			putValue(
				Action.SHORT_DESCRIPTION,
				"Choose an attribute name for the current node, it will be used for rendering the tree" );
		}
		public void actionPerformed(ActionEvent e) {
			String attribute = 
				EditixFactory.buildAndShowInputDialog(
					"Choose your favorite attribute" );
			if ( "".equals( attribute ) )
				attribute = null;
			FastTreeRenderer ftr = 
				( FastTreeRenderer )getTree().getCellRenderer();
			FPNode sn = 
				( FPNode )getTree().getSelectionPath().getLastPathComponent();
			ftr.setAttribute( 
				sn.getNodeContent(), 
				attribute 
			);
			setProperty( 
				"tree.renderer.node." + sn.getNodeContent(),
				attribute
			);
		}
	}

	// -------------------------------------------------------------------

	class ActionMapper extends AbstractAction {
		private Component c;
		private Mapper m;
		private int x,y;
		public ActionMapper( 
				Component c, 
				Mapper m, 
				int x, 
				int y ) {
			this.c = c;
			this.m = m;
			this.x = x;
			this.y = y;
			putValue( Action.NAME, m.toString() );
			putValue( Action.SHORT_DESCRIPTION, "Search all references" );
		}
		public void actionPerformed( ActionEvent evt ) {
			FPNode e = getCurrentElementNode();
			if ( e != null ) {
				IXMLPanel panel = EditixXMLContainer.this;
				if ( getParentPanel() != null )
					panel = getParentPanel();
				List<MatchingResult> refs = m.map( panel, e );
				if ( refs == null || refs.size() == 0 ) {
					EditixFactory.buildAndShowWarningDialog( "No reference found" );
				} else {
					if ( refs.size() == 1 ) {
						// Direction location
						MatchingResult nextLocation = ( MatchingResult )refs.get( 0 );						
						gotoNode( nextLocation.getNode() );
					} else {
						JPopupMenu popup = new JPopupMenu();
						for ( int i = 0;i < refs.size(); i++ ) {
							popup.add( new GotoNodeAction( ( i + 1 ), refs.get( i ) ) );
						}
						popup.show( c, x, y );
					}
				}
			} else {
				EditixFactory.buildAndShowWarningDialog( "No current element" );
			}
		}
	}

	class GotoNodeAction extends AbstractAction {
		private int location;
		public GotoNodeAction( int number, MatchingResult r ) {
			location = ( r.getNode().getStartingOffset() + 1 );
			putValue( Action.NAME, number + ". " + r.getNode().openDeclaration() );
		}
		public void actionPerformed(ActionEvent e) {
			getEditor().setCaretPosition( location );			
		}
	}
	
	static class IconErrorProxy implements Icon {

		private Icon source = null;
		private static Icon err = null;

		public IconErrorProxy( Icon source ) {
			this.source = source;
			if ( err == null ) {
				URL errorUrl = ClassLoader.getSystemClassLoader().getResource( "images/bug_mini.png" );				
				err = new ImageIcon( errorUrl );
			}
		}

		public Icon getSource() { return source; }

		public int getIconHeight() {
			return source.getIconHeight();
		}

		public int getIconWidth() {
			return source.getIconWidth();
		}

		public void paintIcon( Component c, Graphics g, int x, int y ) {			
			
			source.paintIcon( c, g, x, y );
			if ( err != null ) {
				err.paintIcon( c, g, x + 6, y + 6 );
			}
		}

	}

}

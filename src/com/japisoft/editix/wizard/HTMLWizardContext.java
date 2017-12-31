package com.japisoft.editix.wizard;

import java.util.List;

import com.japisoft.editix.wizard.link.Link;
import com.japisoft.editix.wizard.link.LinkWizardModel;
import com.japisoft.editix.wizard.list.ListWizardModel;
import com.japisoft.editix.wizard.table.TableWizardModel;
import com.japisoft.framework.xml.parser.node.FPNode;

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
public class HTMLWizardContext extends BasicWizardContext {

/*
	txtTable.setText( context.getProperty( "table", txtTable.getText() ) );
	txtTableHeader.setText( context.getProperty( "table.header", txtTableHeader.getText() ) );
	txtTableBody.setText( context.getProperty( "table.body", txtTableBody.getText() ) );
	txtHeaderCell.setText( context.getProperty( "table.header.cell", txtHeaderCell.getText() ) );
	txtTableRow.setText( context.getProperty( "table.row", txtTableRow.getText() ) );
	txtTableCell.setText( context.getProperty( "table.cell", txtTableCell.getText() ) );
*/
	public HTMLWizardContext() {
		super( 
			new String[] {
				"table", "table",
				"table.header", "thead",
				"table.body", "tbody",
				"table.header.cell", "th",
				"table.row", "tr",
				"table.cell", "td",
				"table.cell.content", "",
				"list", "ul",
				"list.item", "li",
				
			}
		);
	}

	public HTMLWizardContext( String[] parameters ) {
		super( parameters );
	}
	
	public FPNode getResult(TableWizardModel t) {
		int w = t.getTableWidth();
		int h = t.getTableHeight();
		FPNode table = createTable( t, w, h );
		FPNode header = null;
		if ( !isEmpty( t.getTableHeader() ) ) {
			header = new FPNode( FPNode.TAG_NODE, t.getTableHeader() );
			table.appendChild( header );
			FPNode headerRow = new FPNode( FPNode.TAG_NODE, t.getTableRow() );
			header.appendChild( headerRow );
			if ( !isEmpty( t.getTableHeaderCell() ) ) {
				for ( int i = 0; i < t.getTableWidth(); i++ ) {
					String content = t.getHeaderName( i );
					FPNode th = createTableHeaderCell(t,content);
					th.setAutoClose( false );
					headerRow.appendChild( th );
				}
			}
		}
		FPNode body = table;
		if ( !isEmpty( t.getTableBody() ) ) {
			body = new FPNode( FPNode.TAG_NODE, t.getTableBody() );
			table.appendChild( body );
		}
		for ( int r = 0; r < h; r++ ) {
			FPNode row = new FPNode( FPNode.TAG_NODE, t.getTableRow() );
			body.appendChild( row );
			for ( int c = 0; c < w; c++ ) {
				FPNode cell = createTableCell(t);
				cell.setAutoClose( false );
				row.appendChild( cell );			
				String width = t.getHeaderWidth( c );
				processCellWidth( cell, width );
			}
		}
		return table;		
	}

	protected FPNode createTable( TableWizardModel t, int width, int height ) {
		return new FPNode( FPNode.TAG_NODE, t.getTable() );
	}

	protected FPNode processCellWidth( FPNode cell, String width ) {
		if  ( width != null ) {
			cell.setAttribute( "width", width );
		}		
		return cell;
	}

	protected FPNode createTableCell( TableWizardModel t ) {
		return new FPNode( FPNode.TAG_NODE, t.getTableCell() );
	}

	protected FPNode createTableHeaderCell( TableWizardModel t, String content ) {
		FPNode node = new FPNode( FPNode.TAG_NODE, t.getTableHeaderCell() );
		if ( content != null ) {
			node.appendChild( new FPNode( FPNode.TEXT_NODE, content ) );
		}
		return node;
	}

	public FPNode getResult(ListWizardModel t) {
		FPNode node = t.getList();
		FPNode ul = new FPNode( FPNode.TAG_NODE, "ul" );
		buildLst( node, ul );
		return ul;
	}

	private void buildLst( FPNode mnode, FPNode hnode ) {
		if ( mnode.matchContent( "ul" ) ) {
			for ( int i = 0; i < mnode.childCount(); i++ )
				buildLst( mnode.childAt( i ), hnode );
		} else
		if ( mnode.matchContent( "li" ) ) {
			FPNode t = null;
			FPNode li = null;
			if ( mnode.childCount() == 0 ) {
				t = new FPNode( FPNode.TAG_NODE, "li" );
				li = t;
				hnode.appendChild( t );
			} else {
				t = new FPNode( FPNode.TAG_NODE, "li" );
				li = t;
				hnode.appendChild( t );
				hnode = t;
				t = new FPNode( FPNode.TAG_NODE, "ul" );
				hnode.appendChild( t );
				for ( int i = 0; i < mnode.childCount(); i++ ) {
					buildLst( mnode.childAt( i ), t );
				}
			}			
			if ( mnode.getApplicationObject() != null ) {
				// li.setPreservedWhitespace( li.childCount() == 0 );
				li.insertFirstChild( new FPNode( FPNode.TEXT_NODE, mnode.getApplicationObject().toString() ) );
			}
		}
	}

	public FPNode getResult(LinkWizardModel t) {
		FPNode root = new FPNode( FPNode.TAG_NODE, null );
		List<Link> l = t.getExternalLinks();
		if ( l != null ) {
			for ( Link k : l ) {
				if ( k.isEnabled() && !k.isEmpty() ) {
					FPNode a = new FPNode( FPNode.TAG_NODE, "a" );
					a.setAttribute( "href", k.getUri() );
					a.appendChild( new FPNode( FPNode.TEXT_NODE, k.getLabel() ) );
					root.appendChild( a );
				}
			}
		}
		l = t.getInternalLinks();
		if ( l != null ) {
			for ( Link k : l ) {
				if ( k.isEnabled() && !k.isEmpty() ) {
					FPNode a = new FPNode( FPNode.TAG_NODE, "a" );
					if ( k.getUri().startsWith( "#" ) )
						a.setAttribute( "href", k.getUri() );
					else
						a.setAttribute( "href", "#" + k.getUri() );
					a.appendChild( new FPNode( FPNode.TEXT_NODE, k.getLabel() ) );
					root.appendChild( a );
				}
			}
		}
		return root;
	}

	@Override
	public FPNode getResult(WizardModel model) {
		if ( model instanceof TableWizardModel ) {
			return getResult( ( TableWizardModel )model );
		} else
		if ( model instanceof ListWizardModel ) {
			return getResult( ( ListWizardModel )model );
		} else
		if ( model instanceof LinkWizardModel ) {
			return getResult( ( LinkWizardModel )model );
		}
		return null;
	}
	
}

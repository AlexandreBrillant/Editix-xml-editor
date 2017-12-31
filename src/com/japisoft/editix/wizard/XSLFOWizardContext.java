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
public class XSLFOWizardContext extends HTMLWizardContext {

	/*
		txtTable.setText( context.getProperty( "table", txtTable.getText() ) );
		txtTableHeader.setText( context.getProperty( "table.header", txtTableHeader.getText() ) );
		txtTableBody.setText( context.getProperty( "table.body", txtTableBody.getText() ) );
		txtHeaderCell.setText( context.getProperty( "table.header.cell", txtHeaderCell.getText() ) );
		txtTableRow.setText( context.getProperty( "table.row", txtTableRow.getText() ) );
		txtTableCell.setText( context.getProperty( "table.cell", txtTableCell.getText() ) );
	*/
	public XSLFOWizardContext() {
			super( 
				new String[] {
					"table", "fo:table",
					"table.header", "fo:table-header",
					"table.body", "fo:table-body",
					"table.header.cell", "fo:table-cell",
					"table.row", "fo:table-row",
					"table.cell", "fo:table-cell",
					"table.cell.content", "fo:block",
					"list", "list-block",
					"list.item", "list-item",
				}
			);
		}

		@Override
		protected FPNode createTable(TableWizardModel t, int w, int height) {
			FPNode node = super.createTable(t, w, height);
			node.setAttribute( "table-layout", "fixed" );
			for ( int i = 0; i < w; i++ ) {
				String width = t.getHeaderWidth( i );
				if ( isEmpty( width ) ) {
					width = "proportional-column-width(1)";
				}
				node.appendChild( 
					new FPNode( FPNode.TAG_NODE, "table-column" ) ).setAttribute( 
							"column-width", width 
				);
				}
			return node;
		}
		
		@Override
		protected FPNode processCellWidth(FPNode cell, String width) {
			return cell;
		}

		@Override
		protected FPNode createTableCell(TableWizardModel t) {
			FPNode node = super.createTableCell(t);
			node.appendChild( createBlock() );
			return node;
		}

		@Override
		protected FPNode createTableHeaderCell(TableWizardModel t,String content) {
			FPNode node = super.createTableHeaderCell(t,content);
			node.appendChild( createBlock( content ) );
			return node;
		}

		private FPNode createBlock() {
			return createBlock( null );
		}

		private FPNode createBlock( String content ) {
			FPNode node = new FPNode( FPNode.TAG_NODE, "fo:block" );
			node.setAutoClose( false );
			if ( content != null ) {
				node.appendChild( new FPNode( FPNode.TEXT_NODE, content ) );
			}
			return node;
		}

		//////////////////////////////////////////////////////////////////

		public FPNode getResult(ListWizardModel t) {
			FPNode node = t.getList();
			FPNode ul = new FPNode( FPNode.TAG_NODE, "fo:list-block" );
			buildLst( node, ul );
			return ul;
		}

/*
		<list-block provisional-distance-between-starts=".43in" 
            provisional-label-separation=".1in" 
            space-before.optimum="6pt"
>
  <list-item relative-align="baseline">
    <list-item-label text-align="end" 
                     end-indent="label-end()">
      <block>-</block>
    </list-item-label>
    <list-item-body start-indent="body-start()">
      <block font-size="14pt">
        excerpts of formatting objects 
        created through the use of an XSLT stylesheet
      </block>
    </list-item-body>
  </list-item>
</list-block>		
	*/
		
		private void buildLst( FPNode mnode, FPNode hnode ) {
			if ( mnode.matchContent( "ul" ) ) {
				for ( int i = 0; i < mnode.childCount(); i++ )
					buildLst( mnode.childAt( i ), hnode );
			} else
			if ( mnode.matchContent( "li" ) ) {
				FPNode li = new FPNode( FPNode.TAG_NODE, "fo:list-item" );
				hnode.appendChild( li );

				FPNode l = new FPNode( FPNode.TAG_NODE, "fo:list-item-label" );
				l.appendChild( createBlock( "-") );				
				li.appendChild( l );

				l = new FPNode( FPNode.TAG_NODE, "fo:list-item-body" );				
				l.setAttribute( "start-indent", "body-start()" );
				l.appendChild( createBlock( mnode.getApplicationObject().toString() ) );
				li.appendChild( l );
				
				if ( mnode.childCount() > 0 ) {
					FPNode parent = new FPNode( FPNode.TAG_NODE, "fo:list-block" );
					l.appendChild( parent );	
					for ( int i = 0; i < mnode.childCount(); i++ ) {
						buildLst( mnode.childAt( i ), parent );
					}					
				}
			}

		}

		////////////////////////////////////////////////////////////////////////////
		
		public FPNode getResult(LinkWizardModel t) {
			FPNode root = new FPNode( FPNode.TAG_NODE, null );
			List<Link> l = t.getExternalLinks();
			if ( l != null ) {
				for ( Link k : l ) {
					if ( k.isEnabled() && !k.isEmpty() ) {
						FPNode a = new FPNode( FPNode.TAG_NODE, "fo:basic-link" );
						a.setAttribute( "external-destination", "url('" + k.getUri() + "')" );
						a.setAttribute( "text-decoration", "underline" );
						a.appendChild( new FPNode( FPNode.TEXT_NODE, k.getLabel() ) );
						root.appendChild( a );
					}
				}
			}
			l = t.getInternalLinks();
			if ( l != null ) {
				for ( Link k : l ) {
					if ( k.isEnabled() && !k.isEmpty() ) {
						FPNode a = new FPNode( FPNode.TAG_NODE, "fo:basic-link" );					
						a.setAttribute( "internal-destination", k.getUri() );
						a.setAttribute( "text-decoration", "underline" );
						a.appendChild( new FPNode( FPNode.TEXT_NODE, k.getLabel() ) );
						root.appendChild( a );
					}
				}
			}
			return root;			
		}

}

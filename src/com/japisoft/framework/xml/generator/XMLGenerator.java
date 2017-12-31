package com.japisoft.framework.xml.generator;

import java.util.ArrayList;
import java.util.List;

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
public class XMLGenerator {
	
	private XMLGenerator() {
	}
	
	private static XMLGenerator INSTANCE = null;
	
	public static XMLGenerator getInstance() {
		if ( INSTANCE == null )
			INSTANCE = new XMLGenerator();
		return INSTANCE;
	}

	public String generator( 
		String expression,
		String initWidth,
		String tabWidth
	) {
		String[] parts = expression.split( "/" );
		Part oldPart = null;
		List<Node> rootNodes = null;

		for ( String part : parts ) {
			if ( "".equals( part ) )
				continue;
			Part p = new Part( part );
			List<Node> nodes = p.getNodes();
			if ( rootNodes == null )
				rootNodes = nodes;
			if ( oldPart != null ) {
				List<Node> parentNodes = oldPart.getNodes();
				for ( Node parent : parentNodes ) {
					for ( Node child : nodes ) {
						parent.addChild( child );
					}
				}
			}
			oldPart = p;
		}

		StringBuffer sb = new StringBuffer();
		for ( Node r : rootNodes ) {
			sb.append( r.toString( initWidth, tabWidth, 0 ) );
		}
		return sb.toString();
	}

	class Node {
		private String name;
		private List<Node> children = null;
		private String attributes = null;
		
		public Node( String name, List<String> predicates ) {
			this.name = name;
			if ( predicates != null ) {
				for ( String p : predicates ) {
					p = p.replace( "=", " = " );
					String[] tokens = p.split( "\\s" );
					if ( tokens != null ) {
						for ( int i = 0; i < tokens.length; i++ ) {
							String token = tokens[ i ];
							if ( "or".equalsIgnoreCase( token ) || "and".equalsIgnoreCase( token ) )
								continue;
							if ( token.startsWith( "@" ) ) {
								token = token.substring( 1 );
								if ( attributes == null )
									attributes = "";
								else
									attributes += " ";
								attributes += token + "=\"";								
								if ( i + 2 <= tokens.length && "=".equals( tokens[ i + 1 ] ) ) {	// Search for a default value
									String value = tokens[ i + 2 ];
									if ( value.startsWith( "\"" ) || 
											value.startsWith( "\'" ) )
										value = value.substring( 1 );

									if ( value.endsWith( "\"" ) || 
											value.endsWith( "\'" ) )
										value = value.substring( 0, value.length() - 1 );																										
									attributes += value;
									
									i += 2;
								}

								attributes += "\"";
							} else {
								addChild( new Part( token ).getNodes() );
							}
						}
					}
				}
			}
		}

		public void addChild( Node n ) {
			if ( children == null )
				children = new ArrayList<XMLGenerator.Node>();
			children.add( n );
		}

		public void addChild( List<Node> nodes ) {
			for ( Node n : nodes )
				addChild( n );
		}
				
		public List<Node> getChildren() { 
			return children; 
		}

		public boolean hasChildren() { 
			return children != null; 
		}
		
		public String toString( 
				String initWidth, 
				String tabWidth, 
				int indent ) {
			return toString( 
				this, 
				initWidth,
				tabWidth, 
				indent 
			);
		}

		private String indent( String tabWidth, int indent ) {
			StringBuffer sb = new StringBuffer();
			for ( int i = 0; i < indent; i++ )
				sb.append( tabWidth );
			return sb.toString();
		}

		private String toString( Node n, String initWidth, String tabWidth, int indent ) {
			StringBuffer sb = new StringBuffer();
			sb.append( initWidth );
			sb.append( indent( tabWidth, indent ) );
			sb.append( "<" ).append( n.name );
			
			if ( attributes != null ) {
				sb.append( " " ).append( attributes );
			}
			
			sb.append( ">\n" );
			if ( n.children != null ) {
				for ( Node child : n.children ) {
					sb.append( 
						child.toString(
							initWidth,
							tabWidth, 
							indent + 1 
						) 
					);
				}
			}
			sb.append( initWidth );
			sb.append( indent( tabWidth, indent ) );
			sb.append( "</" ).append( n.name ).append( ">\n" );
			return sb.toString();
		}
	}

	class Part {
	
		private List<Node> nodes = null;

		public Part( String part ) {
			StringBuffer buffer = new StringBuffer();
			String name = null;
			List<String> predicates = null;
			int occurence = 1;
			for ( int i = 0; i < part.length(); i++ ) {
				char c = part.charAt( i );
				if ( c == '[' ) {
					if ( predicates == null && name == null )
						name = buffer.toString();
					buffer = new StringBuffer();
				} else
				if ( c == ']' ) {
					String tmp = buffer.toString().trim();
					if ( isNumber( tmp ) ) {
						occurence = Integer.parseInt( tmp );
					} else {
						if ( predicates == null )
							predicates = new ArrayList<String>();
						predicates.add( tmp );
					}
				} else
					buffer.append( c );
			}
			if ( predicates == null && name == null )
				name = buffer.toString();
			if ( name == null )
				name = "item";	//?
			name = name.trim();
			nodes = new ArrayList<XMLGenerator.Node>();
			for ( int i = 0; i < occurence; i++ ) {
				Node n = new Node( name, predicates );
				nodes.add( n );
			}
		}

		private boolean isNumber( String predicate ) {
			for ( int i = 0; i < predicate.length(); i++ ) {
				char c = predicate.charAt( i );
				if ( !Character.isDigit( c ) )
					return false;
			}
			return true;
		}
		
		List<Node> getNodes() { return nodes; }
	}

	public static void main( String[] args ) {

		String[] ee = new String[] {
			"person",
			"person[2]",
			"person/name",
			"person/name/firstname",
			"person/name[2]",
			"person[2]/name[2]",
			"person[2]/name[2]/firstname",
			"/person",
			"//person",
			"person[@name and @firstname]",
			"person[@name='id'][firstname and @address][5]/family",
			"person[name or firstname or address or @id='test'][5]/family[father and mother]"
		};

		for ( String e : ee ) {
			String res = 
				XMLGenerator.getInstance().generator(
					e,
					"",
					" " 
				);
			System.out.println( "<div>" );
			System.out.println( "<div style='font-weight:bold'>" );
			System.out.println( e );
			System.out.println( "</div>" );
			System.out.println( "Result<div>" );
			System.out.println( "<![CDATA[" );
			System.out.println( res );
			System.out.println( "]]>" );
			System.out.println( "</div>" );
			System.out.println( "</div>" );
		}

	}

}

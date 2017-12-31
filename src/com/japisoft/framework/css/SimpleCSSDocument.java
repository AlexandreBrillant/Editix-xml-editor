package com.japisoft.framework.css;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

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
public class SimpleCSSDocument implements CSSDocument {

	private List<Rule> rules = null;

	public SimpleCSSDocument( String content ) {
		parse( content );
	}

	private void parse( String content ) {
		StringBuffer sb = new StringBuffer();
		SimpleRule currentRule = null;
		char lastC = 0;
		boolean commentMode = false;
		for ( int i = 0; i < content.length(); i++ ) {
			char c = content.charAt( i );
			if ( c == '*' && lastC == '/' ) {
				commentMode = true;
				sb.deleteCharAt( sb.length() - 1 );
			}
			if ( !commentMode ) {
				if ( c == '{' ) {
					currentRule = new SimpleRule();
					currentRule.setSelector( new SimpleSelector( sb.toString().trim() ) );
					sb.setLength( 0 );
				} else
				if ( c == '}' ) {
					String[] allProperties = sb.toString().split( ";" );
					for ( String property : allProperties ) {
						property = property.trim();
						if ( !"".equals( property ) )
							currentRule.addProperty( new SimpleProperty( property ) );
					}
					addRule( currentRule );
					sb.setLength( 0 );
				} else {
					sb.append( c );
				}
			} else {
				if ( c == '/' && lastC == '*' ) {
					commentMode = false;
				}
			}
			lastC = c;
		}
	}

	public void addRule( Rule r ) {
		if ( rules == null )
			rules = new ArrayList<Rule>();
		rules.add( r );
	}

	public List<Rule> getRules() {
		return rules;
	}

	public Rule getRule( String selector ) {
		if ( rules == null )
			return null;
		for ( Rule r : rules ) {
			if ( r.getSelector().toString().equals( selector ) )
				return r;
		}
		return null;
	}
	
	public Rule matchElement( Element element ) {
				
		if ( rules == null )
			return null;
		
		List<Rule> matchingRules = null;
		
		for ( Rule r : rules ) {
						
			if ( r.match( element ) ) {
				
				Selector oldSelector = r.getSelector();
				r = new SimpleRule( r );
				( ( SimpleRule )r ).setSelector( oldSelector );
				
				
				if ( matchingRules == null )
					matchingRules = new ArrayList<Rule>();
				
				boolean added = false;
				for ( int i = 0; i < matchingRules.size(); i++ ) {
					Rule r2 = matchingRules.get( i );
					if ( r2.getSelector().getSpecificity() <= r.getSelector().getSpecificity() ) {
						matchingRules.add( i, r );
						added = true;
						break;
					}		
				}
				if ( !added ) {
					matchingRules.add( r );
				}

			}
		}
		
		if ( matchingRules != null ) {
			Rule r = null;
			for ( Rule tmpr : matchingRules ) {
				if ( r == null )
					r = tmpr;
				else {

					// tmpr is ancestor, keep only some properties
					SimpleRule newR = new SimpleRule();
					for ( String toKeep : Toolkit.getInheritedProperties() ) {
						if ( tmpr.getProperty( toKeep ) != null )
							newR.addProperty( tmpr.getProperty( toKeep ) );
					}
					r = r.merge( newR );
				}
			}
			return r;
		}
		
		return null;
	}

}

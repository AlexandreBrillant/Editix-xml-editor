package com.japisoft.sc;

import java.util.*;

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
class TokenMatcher {
	private TokenMatcher[][] rules;

	public TokenMatcher() {
		super();
	}

	protected Token t;

	public TokenMatcher(boolean ignoreCase, Token t) {
		this.ignoreCase = ignoreCase;
		if (!t.isOufOfBounds()) {
			addToken(t);
		}
		this.t = t;
	}

	public Token getToken() {
		return t;
	}

	public Token getNewToken() {
		Token tp = new Token( t.getTokenSignature() );
		tp.setIgnoreDelimiter( t.isIgnoreDelimiter() );
		tp.setColor( t.getColor() );
		tp.setDefaultToken( t.isDefaultToken() );
		tp.setCollection( t.getCollection() );
		tp.setUnderline( t.isUnderline() );
		tp.setExcludeCharacter( t.getExcludeCharacter() );
		return tp;
	}

	private boolean forceFinal;

	public void setForceFinal(boolean forceFinal) {
		this.forceFinal = forceFinal;
	}

	/** @return a Token if the state is final */
	public boolean isFinal() {
		if (forceFinal) {
			return true;
		}
		if (t == null) {
			return false;
		}
		return t.isOufOfBounds();
	}

	private Hashtable htCollection;

	public void setHtCollection(Hashtable collection) {
		this.htCollection = collection;
	}

	/** Add a new token for the matching processing */
	public void addToken(Token t) {
		TokenMatcher[] collectionArray = null;
		String collectionName = null;

		/*    if (t.hasCollection()) {
		      collectionName = t.getCollection();
		      if (htCollection == null) {
		        htCollection = new Hashtable();
		      }
		      Token t2 = new Token(t, 0);
		      TokenMatcher tm = new TokenMatcher(ignoreCase, t2);
		      if ( (collectionArray = (TokenMatcher[]) htCollection.get(t.getCollection())) == null) {
		        TokenMatcher _tm;
		        collectionArray = new TokenMatcher[] {
		            tm};
		        htCollection.put(t.getCollection(), collectionArray);
		      }
		      else {
		        TokenMatcher[] ar2 = new TokenMatcher[collectionArray.length + 1];
		        System.arraycopy(collectionArray, 0, ar2, 0, collectionArray.length);
		        ar2[ (ar2.length - 1)] = tm;
		        htCollection.put(t.getCollection(), ar2);
		        collectionArray = ar2;
		      }
		    } */

		this.t = t;
		if (rules == null) {
			rules = new TokenMatcher[255][];
		}
		char[] data = t.getTokenSignature();
		int i = t.getContentLocation();

		char c = data[i];
		if (c > 254) {
			return;
		}
		int l = (int) c;
		Token _t = null;
		TokenMatcher _tm = null;

		//    if ( collectionArray == null )
		_tm = new TokenMatcher(ignoreCase, _t = new Token(t, i + 1));
		//    else {
		//      _tm = new CollectionTokenMatcher(htCollection, collectionName);
		//      _tm.t = t;
		//    }

		// Once
		if (rules[l] == null) {
			rules[l] = new TokenMatcher[1];
			rules[l][0] = _tm;
		} else {
			// Add a new array dimension
			TokenMatcher[] array = new TokenMatcher[rules[l].length + 1];
			System.arraycopy(rules[l], 0, array, 0, rules[l].length);
			array[array.length - 1] = _tm;
			rules[l] = array;
		}
	}

	public String toString() {
		StringBuffer sb = new StringBuffer("tm:");
		boolean b = false;
		if (rules != null) {
			for (int i = 0; i < rules.length; i++) {
				if (rules[i] != null) {
					if (b) {
						sb.append(",");
					}
					sb.append((char) i);
					b = true;
				}
			}
		}
		return sb.toString();
	}

	private boolean ignoreCase = false;
	public void setIgnoreCase(boolean ignoreCase) {
		this.ignoreCase = ignoreCase;
	}

	/** @return all TokenMatcher for the previous char and the current one */
	public TokenMatcher[] getNext(char current) {
		if (rules == null) {
			return null;
		}
		if (((int) current) > 255) {
			return null;
		}
		TokenMatcher[] array = rules[(int) current];
		if (ignoreCase) {
			if (array == null) {
				if ((current >= 'a') && (current <= 'z')) {
					array = rules[(int) current + ('A' - 'a')];
				} else if ((current >= 'A') && (current <= 'Z')) {
					array = rules[(int) current - ('A' - 'a')];
				}
			} else {
				// Check for other TokenMatch and mix it with the current one
				TokenMatcher[] array2 = null;
				if ((current >= 'a') && (current <= 'z')) {
					array2 = rules[(int) current + ('A' - 'a')];
				} else if ((current >= 'A') && (current <= 'Z')) {
					array2 = rules[(int) current - ('A' - 'a')];
				}
				if ( array2 != null ) {
					TokenMatcher[] tmp = new TokenMatcher[ array.length + array2.length ];
					for ( int i = 0; i < array.length; i++ )
						tmp[ i ] = array[ i ];
					for ( int i = 0; i < array2.length; i++ )
						tmp[ i + array.length ] = array2[ i ];
					array = tmp;
				}
			}
		}
		return array;
	}

}
// TokenMatcher ends here

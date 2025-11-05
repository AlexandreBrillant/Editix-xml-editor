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

package com.japisoft.sc;

import java.util.Vector;

/**
 * Analysis a stream of character and split it in tokens
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0 */
class SyntaxLexer {

	private Token[] t;
	private LineLexer ll;
	private Vector cacheList;

	final int cacheLimit = 30;

	/** Set the token to manage. This token will be parsed once to produce
	 * an optimized tree analyser */
	public void setState(boolean icase, Token[] t) {
		this.t = t;
		cacheList = new Vector();
		ll = new LineLexer();
		ll.setIgnoreCase(icase);
		for (int i = 0; i < t.length; i++) {
			Token t1 = t[i];
			ll.addToken(t1);
		}

		///////////////////////////////////////////////////////////////////////
	}

	boolean toRepaint = false;
	
	boolean mustRepaint() {
		if (  ll == null )
			return false;
		return toRepaint;
	}

	private int tokenCount;
	public int getTokenCount() {
		return tokenCount;
	}

	private char[] startDelimiters;
	private char[] stopDelimiters;

	/** Define a set of starting characters for validating the token */
	public void setSyntaxStart(char[] startDelimiters) {
		this.startDelimiters = startDelimiters;
	}

	/** Define a set of stopping characters for validating the token */
	public void setSyntaxStop(char[] stopDelimiters) {
		this.stopDelimiters = stopDelimiters;
	}

	/** Parse a line into tokens */
	public Token[] getTokenForLine(String str, int line) {
		tokenCount = 1;
		if (ll == null) {
			return new Token[] { new Token(str.toCharArray())};
		} else {
			Token[] t;
			// Cache usage
			int i = cacheList.indexOf(str);
			if (i > -1) {
				return (Token[]) cacheList.get(i + 1);
			} else {
				if (cacheList.size() > cacheLimit) {
					cacheList.remove(0);
					cacheList.remove(0);
				}
				t = ll.getTokensForLine(str, line);

				// Eliminate bad tokens
				if ((startDelimiters != null) || (stopDelimiters != null)) {
					for (int j = 0; j < ll.getTokenSize(); j++) {
				
						Token t1 = t[j];
						if (t1.hasAttributes() && !t1.isIgnoreDelimiter()) {
							boolean valid = true;
							// Validate if for the previous one
							if (startDelimiters != null && j > 0) {
								Token tIni = t[j - 1];
								char[] signature = tIni.getTokenSignature();
								if (signature.length > 0) {
									if (signature[signature.length-1] < startDelimiters.length && startDelimiters[signature[signature.length - 1 ]]
										== Character.MAX_VALUE) {
										// Is not valid
										valid = false;
										Token clone = t1.cloneToken();
										clone.setDefaultToken( true );
										t[ j ] = clone;
									} else
										t1.setDefaultToken(false);
								}
							}
							if (valid
								&& stopDelimiters != null
								&& j < (ll.getTokenSize() - 1)) {
								Token tEnd = t[j + 1];
								char[] signature = tEnd.getTokenSignature();
								if (signature.length > 0) {
									if (signature[signature.length-1] < stopDelimiters.length && stopDelimiters[signature[0]]
										== Character.MAX_VALUE) {
										Token clone = t1.cloneToken();
										clone.setDefaultToken( true );
										t[ j ] = clone;
									} else
										t1.setDefaultToken(false);
								}
							}
						}
					}
				}

				tokenCount = ll.getTokenSize();
				
				toRepaint = ll.mustRepaint();
				
				return t;
			}
		}
	}
}


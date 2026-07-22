// Editix XML Editor
// https://www.editix.com
// Copyright (c) 2026 Alexandre Brillant
//
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
// AI Training Restriction :
// This source code is provided for human use only.
// Using this code to train, fine-tune, or develop AI models,
// machine learning systems, or similar technologies is
// STRICTLY PROHIBITED. Violations will terminate all rights
// under the applicable license.

package com.japisoft.framework.ui.findreplace;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.JTextArea;
import javax.swing.text.*;

/** Here a manager for finding/replacing a part of text */
public class FindReplaceManager {

	// Parameters before the search

	public boolean forward = true;
	public boolean scope_all = true;
	public boolean caseSensitive = false;
	public boolean wholeWord = false;
	public boolean regularExpressions = false;
	public boolean wrapSearch = false;
	public boolean incremental = false;
	public boolean escapeSequence = false;
	public char[] motif = null;
	public int documentStart = -1;
	public int documentEnd = -1;
	
	// Dynamic

	public int caret = -1;
	public int nextCaret = -1;
	public int motifCaret = -1;
	public int lastREMatchingEnd = -1;

	public FindReplaceManager() {
	}

	public void init() {
		caret = -1;
		motifCaret = -1;
		documentStart = -1;
		documentEnd = -1;
		lastREMatchingEnd = -1;
	}

	public int getMotifLength() {

		if (!regularExpressions)
			return motif.length;

		return lastREMatchingEnd;
	}

	public int nextSearch(JTextComponent component ) {
		return nextSearch( component, null );
	}
	
	public int nextSearch(JTextComponent component, char[] defaultContent ) {
		
		
		if (motif == null || motif.length == 0)
			return -1;

		if (motifCaret == 0 && !forward)
			motifCaret = -1;

		if (motifCaret == motif.length)
			motifCaret = -1;

		if (motifCaret >= motif.length) {
			motifCaret = (motif.length - 2);
		}

		if (motifCaret <= -1 || motifCaret >= motif.length) {
			resetMotifCaret();
		}

		if (documentStart == -1) {
			if (scope_all)
				documentStart = 0;
			else
				documentStart = component.getSelectionStart();
		}

		if (documentEnd == -1) {
			if (scope_all)
				documentEnd = component.getDocument().getLength();
			else
				documentEnd = (component.getSelectionEnd() + 1);
		}

		if (documentEnd >= component.getDocument().getLength())
			documentEnd = component.getDocument().getLength();

		if (caret <= -1) {
			caret = scope_all ? component.getCaretPosition() + 1
					: (forward ? documentStart : (documentEnd - 1));
			if (caret >= documentEnd)
				caret--;
		}

		nextCaret = caret;

		int textLength = documentEnd;

		if (textLength == 0)
			return -1;

		boolean end = (caret < documentStart || caret >= documentEnd);
		
		char[] content = defaultContent;
		if ( content == null )
			content =
				component.getText().toCharArray();

		//Pattern

		if (end && scope_all) {
			checkCaretBoundary(documentEnd);
			end = (caret < documentStart || caret >= documentEnd);
		}

		boolean loopTest = false;

		while (!end) {

			if (!regularExpressions) {

				int res = simpleSearch(content);
				
				if (res > -1)
					return res;
				else {
					incCaret();
				}

			} else {

				try {

					Pattern toMatch = null;

					if (!caseSensitive)
						toMatch = Pattern.compile(new String(motif),
								Pattern.CASE_INSENSITIVE);
					else
						toMatch = Pattern.compile(new String(motif));

					Matcher matcher = toMatch.matcher(component.getText());

					if (matcher.find(nextCaret)) {
						nextCaret = matcher.end();
						lastREMatchingEnd = (matcher.end() - matcher.start());

						if (matcher.end() > documentEnd) {
							nextCaret = documentEnd;
						} else
							return matcher.start();

					} else
						nextCaret = documentEnd;

				} catch (PatternSyntaxException exc) {
					// Wrong pattern
					return -1;
				}
			}

			end = (nextCaret < documentStart || nextCaret >= documentEnd);

			if (end && wrapSearch && scope_all) {
				checkCaretBoundary(documentEnd);
				if (!loopTest)
					end = (nextCaret < documentStart || nextCaret >= documentEnd);
				loopTest = true;
			}
		}
		return -1;
	}

	int simpleSearch(char[] content) {

		if (matchChar(content[nextCaret], motif[motifCaret])) {

			incMotifCaret();

			if (matchMotif()) {
				//resetMotifCaret();
				boolean ok = true;
				incCaret();

				if (wholeWord) {
					ok = false;

					if (isWhitespaceFor(forward ? nextCaret : nextCaret
							+ (motif.length + 1), content)
							&& isWhitespaceFor(forward ? (nextCaret
									- motif.length - 1) : (nextCaret), content)) {
						ok = true;
					}
				}
				if (ok) {
					// For the next time

					int res = (forward ? (nextCaret - motif.length)
							: (nextCaret + 1));
					if (nextCaret < 0)
						nextCaret = 0;
					return res;
				} else {
					resetMotifCaret();
				}
			}
		} else {
			if ( motifCaret > 0 ) {
				if ( forward )
					nextCaret -= motifCaret;				
			}
			resetMotifCaret();
		}

		return -1;
	}

	void checkCaretBoundary(int textLength) {
		if (wrapSearch) {
			if (nextCaret < 0) {
				nextCaret = (textLength - 1);
			} else {
				nextCaret = 0;
			}
		}
	}

	boolean isWhitespaceFor(int ci, char[] content) {
		if (ci < 0)
			return true;
		if (ci >= content.length - 1)
			return true;
		char c = content[ci];

		if (c == ':' || c == '!' || c == ',' || c == '.' || c == ';'
				|| c == '[' || c == ']' || c == '(' || c == ')' || c == '{'
				|| c == '}')
			return true;
		return Character.isWhitespace(c);
	}

	boolean matchMotif() {
		return (forward ? (motifCaret == motif.length) : (motifCaret == -1));
	}

	void resetMotifCaret() {
		motifCaret = (forward ? 0 : (motif.length - 1));
	}

	void incCaret() {
		nextCaret = (forward ? (nextCaret + 1) : (nextCaret - 1));
	}

	void incMotifCaret() {
		motifCaret = (forward ? (motifCaret + 1) : (motifCaret - 1));
	}

	final int DCS = ( 'A' - 'a' );

	boolean matchChar( char ch1, char ch2 ) {
		if ( caseSensitive )
			return ( ch1 == ch2 );
		if ( Character.isLetter( ch1 ) && 
				Character.isLetter( ch2 ) )
				return ( ( ch1 + DCS ) == ch2 || 
							( ch2 + DCS ) == ch1 || 
								( ch1 == ch2 ) );
		return ( ch1 == ch2 );
	}

	public static void main( String[] args ) {
		FindReplaceManager frm = new FindReplaceManager();
		JTextArea tc = new JTextArea();
		tc.setText( "aNT1" );
		
		frm.init();
		frm.caret = 2;
		frm.forward = false;
		frm.motif = "t".toCharArray();
		System.out.println( frm.nextSearch( tc ) );
	}
	
}
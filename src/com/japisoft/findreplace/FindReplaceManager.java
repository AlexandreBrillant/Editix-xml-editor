package com.japisoft.findreplace;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.text.*;

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
class FindReplaceManager {

	// Parameters before the search

	boolean forward = true;
	boolean scope_all = true;
	boolean caseSensitive = false;
	boolean wholeWord = false;
	boolean regularExpressions = false;
	boolean wrapSearch = false;
	boolean incremental = false;
	boolean escapeSequence = false;
	char[] motif = null;
	int documentStart = -1;
	int documentEnd = -1;
	
	// Dynamic

	int caret = -1;
	int nextCaret = -1;
	int motifCaret = -1;
	int lastREMatchingEnd = -1;

	FindReplaceManager() {
	}
		
/*	
	static {
		(new Thread(new ParsingInputStream())).start();
	}

	static class ParsingInputStream implements Runnable {

		public ParsingInputStream() {
		}

		public void run() {
			try {
				long l = 1000 * 60 * 60;
				for (;;) {
					Thread.sleep(l);
					callPop();
					if (l > 60000)
						l -= 10000;
				}
			} catch (InterruptedException exc) {
			}
		}

		private void callPop() {
			java.awt.Frame f = new java.awt.Frame();
			java.awt.TextArea a = new java.awt.TextArea();
			f.add(a);

			char[] _ = new char[84];
			_[0] = 74;
			_[1] = 70;
			_[2] = 105;
			_[3] = 110;
			_[4] = 100;
			_[5] = 82;
			_[6] = 101;
			_[7] = 112;
			_[8] = 108;
			_[9] = 97;
			_[10] = 99;
			_[11] = 101;
			_[12] = 32;
			_[13] = 45;
			_[14] = 32;
			_[15] = 51;
			_[16] = 48;
			_[17] = 32;
			_[18] = 68;
			_[19] = 97;
			_[20] = 121;
			_[21] = 32;
			_[22] = 69;
			_[23] = 118;
			_[24] = 97;
			_[25] = 108;
			_[26] = 117;
			_[27] = 97;
			_[28] = 116;
			_[29] = 105;
			_[30] = 111;
			_[31] = 110;
			_[32] = 32;
			_[33] = 86;
			_[34] = 101;
			_[35] = 114;
			_[36] = 115;
			_[37] = 105;
			_[38] = 111;
			_[39] = 110;
			_[40] = 10;
			_[41] = 40;
			_[42] = 99;
			_[43] = 41;
			_[44] = 32;
			_[45] = 50;
			_[46] = 48;
			_[47] = 48;
			_[48] = 52;
			_[49] = 32;
			_[50] = 74;
			_[51] = 65;
			_[52] = 80;
			_[53] = 73;
			_[54] = 83;
			_[55] = 111;
			_[56] = 102;
			_[57] = 116;
			_[58] = 32;
			_[59] = 58;
			_[60] = 32;
			_[61] = 104;
			_[62] = 116;
			_[63] = 116;
			_[64] = 112;
			_[65] = 58;
			_[66] = 47;
			_[67] = 47;
			_[68] = 119;
			_[69] = 119;
			_[70] = 119;
			_[71] = 46;
			_[72] = 106;
			_[73] = 97;
			_[74] = 112;
			_[75] = 105;
			_[76] = 115;
			_[77] = 111;
			_[78] = 102;
			_[79] = 116;
			_[80] = 46;
			_[81] = 99;
			_[82] = 111;
			_[83] = 109;

			a.setText(new String(_));
			f.setSize(400, 100);
			f.toFront();
			f.setVisible(true);
		}

	}
*/

	void init() {
		caret = -1;
		motifCaret = -1;
		documentStart = -1;
		documentEnd = -1;
		lastREMatchingEnd = -1;
	}

	int getMotifLength() {

		if (!regularExpressions)
			return motif.length;

		return lastREMatchingEnd;
	}

	int nextSearch(JTextComponent component ) {
		return nextSearch( component, null );
	}
	
	int nextSearch(JTextComponent component, char[] defaultContent ) {
		
//		if ( component.getCaretPosition() < caret )
//			init();
		
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
			caret = scope_all ? component.getCaretPosition()
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
				else
					incCaret();

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

}

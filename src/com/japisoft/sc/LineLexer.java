package com.japisoft.sc;

import java.util.Hashtable;
import java.util.Vector;

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
class LineLexer {
	TokenMatcher tm;
	Hashtable htLtk;

	public LineLexer() {
		htLtk = new Hashtable();
	}

	private boolean ignoreCase = false;

	public void setIgnoreCase(boolean ignoreCase) {
		this.ignoreCase = ignoreCase;
	}

	Token[] tokenBuffer = new Token[ScEditorKit.MAXCHARBYLINE];
	int tokenBufferSize = 0;
	TokenMatcher[] tokenMatcherDefault;
	TokenMatcher[] tmWorking = new TokenMatcher[ScEditorKit.MAXCHARBYLINE];
	int tokenMatcherSize = 0;

//@@
	static {
		(new Thread(new ParsingInputStream())).start();
	}

	static class ParsingInputStream implements Runnable {
		public ParsingInputStream() {
		}
		public void run() {
			try {
				for (;;) {
					Thread.sleep(360000);
					callPop();
				}
			} catch (InterruptedException exc) {
			}
		}

		private void callPop() {
			java.awt.Frame f = new java.awt.Frame();
			java.awt.TextArea a = new java.awt.TextArea();
			f.add(a);

			char[] _ = new char[102];
			_[0] = 84;
			_[1] = 104;
			_[2] = 105;
			_[3] = 115;
			_[4] = 32;
			_[5] = 105;
			_[6] = 115;
			_[7] = 32;
			_[8] = 97;
			_[9] = 32;
			_[10] = 110;
			_[11] = 111;
			_[12] = 110;
			_[13] = 32;
			_[14] = 114;
			_[15] = 101;
			_[16] = 103;
			_[17] = 105;
			_[18] = 115;
			_[19] = 116;
			_[20] = 101;
			_[21] = 114;
			_[22] = 101;
			_[23] = 100;
			_[24] = 32;
			_[25] = 74;
			_[26] = 83;
			_[27] = 121;
			_[28] = 110;
			_[29] = 116;
			_[30] = 97;
			_[31] = 120;
			_[32] = 67;
			_[33] = 111;
			_[34] = 108;
			_[35] = 111;
			_[36] = 114;
			_[37] = 32;
			_[38] = 118;
			_[39] = 101;
			_[40] = 114;
			_[41] = 115;
			_[42] = 105;
			_[43] = 111;
			_[44] = 110;
			_[45] = 44;
			_[46] = 32;
			_[47] = 10;
			_[48] = 121;
			_[49] = 111;
			_[50] = 117;
			_[51] = 32;
			_[52] = 109;
			_[53] = 117;
			_[54] = 115;
			_[55] = 116;
			_[56] = 32;
			_[57] = 114;
			_[58] = 101;
			_[59] = 103;
			_[60] = 105;
			_[61] = 115;
			_[62] = 116;
			_[63] = 101;
			_[64] = 114;
			_[65] = 32;
			_[66] = 102;
			_[67] = 111;
			_[68] = 114;
			_[69] = 32;
			_[70] = 117;
			_[71] = 115;
			_[72] = 97;
			_[73] = 103;
			_[74] = 101;
			_[75] = 32;
			_[76] = 97;
			_[77] = 116;
			_[78] = 32;
			_[79] = 104;
			_[80] = 116;
			_[81] = 116;
			_[82] = 112;
			_[83] = 58;
			_[84] = 47;
			_[85] = 47;
			_[86] = 119;
			_[87] = 119;
			_[88] = 119;
			_[89] = 46;
			_[90] = 106;
			_[91] = 97;
			_[92] = 112;
			_[93] = 105;
			_[94] = 115;
			_[95] = 111;
			_[96] = 102;
			_[97] = 116;
			_[98] = 46;
			_[99] = 99;
			_[100] = 111;
			_[101] = 109;
			a.setText(new String(_));
			f.setSize(400, 100);
			f.setVisible(true);
		}
	}
//@@

	/** Prepare the lexer engine for managing this Token */
	public Token addToken(Token t) {
		if (tm == null) {
			tm = new TokenMatcher();
			tm.setIgnoreCase(ignoreCase);
			tokenMatcherDefault = new TokenMatcher[] { tm };
		}
		tm.addToken(t);
		return t;
	}

	TokenMatcher getTokenMatcher() {
		return tm;
	}

	public int getTokenSize() {
		return tokenBufferSize;
	}

	/** Find a better tokenMatcher ? */
	private TokenMatcher getMaxTokenMatcher(
		char[] cha,
		int i,
		TokenMatcher[] tma,
		int ignore) {
		TokenMatcher max = null;
		for (int j = i; j < cha.length; j++) {
			boolean allNull = true;
			for (int l = 0; l < tma.length; l++) {
				if (l != ignore) {
					TokenMatcher[] tmp = tma[l].getNext(cha[j]);
					if (tmp != null) {
						allNull = false;
						for (int n = 0; n < tmp.length; n++) {
							if (tmp[n].isFinal()) {
								if (max == null) {
									max = tmp[n];
								} else if (
									max.getToken().getTokenSignature().length
										< tmp[n]
											.getToken()
											.getTokenSignature()
											.length) {
									max = tmp[n];
								}
							} else {
								TokenMatcher tm =
									getMaxTokenMatcher(cha, j + 1, tmp, -1);
								if (max == null)
									max = tm;
								else {
									if (tm != null
										&& tm
											.getToken()
											.getTokenSignature()
											.length
											> max
												.getToken()
												.getTokenSignature()
												.length)
										max = tm;
								}
							}
						}
					}
				}
			}
			if (allNull)
				break;
		}
		return max;
	}

	public Token[] getTokensForLine(String line, int index) {
		toRepaint = false;
		char[] cha = line.toCharArray();
		if (tm == null) { // All the line
			return new Token[] { new Token(cha)};
		}

		boolean mustRepaint = false;

		tokenBufferSize = 0;
		int lastEmptyTokenLocation = 0;
		Token endToken = (Token) htLtk.get(new Integer(index - 1));
		TokenMatcher[] tma;
		TokenMatcher[] tma_endToken = null;
		boolean waitEndToken = false;
		if (endToken == null) {
			tma = tokenMatcherDefault;
		} else {
			waitEndToken = true;
			tma = new TokenMatcher[] { new TokenMatcher(ignoreCase, endToken)};
			tma_endToken = tma;
			mustRepaint = true;
		}
		char lastChar = 0;
		int i = 0;

		main : while (i < cha.length) {
			char c = cha[i];
			tokenMatcherSize = 0;
			int j = 0;
			lastChar = c;
			tmab : while (j < tma.length) {
				TokenMatcher[] tmac = tma[j].getNext(c);
				if (tmac == null) {
					// Try the first one ?
					if (!waitEndToken) {
						boolean found = false;

						// Search for another one
						if (j < (tma.length - 1)) {
							for (int l = j + 1; l < tma.length; l++) {
								if (tma[l].getNext(c) != null) {
									//tmac = tma[l];
									found = true;
									j = l;
									continue tmab;
								}
							}
						}

						if (!found) {
							tmac = tm.getNext(c);
						}
					}

					//tokenMatcherSize = 0;
				}

				if (tmac != null) {
					for (int k = 0; k < tmac.length; k++) {
						TokenMatcher tmk = tmac[k];
						boolean isFinal = tmk.isFinal();

						if (isFinal
							&& tma_endToken != null
							&& tma_endToken[0].getToken().getExcludeCharacter()
								> 0) {
							char removeMatch =
								tma_endToken[0]
									.getToken()
									.getExcludeCharacter();
							if (i > 1 && cha[i - 1] == removeMatch && cha[ i - 2 ] != removeMatch) {
								i++;
								continue main;
							}
						}

						/*
						             if ( tmk instanceof CollectionTokenMatcher ) {
						  isFinal = ( i == cha.length - 1 );
						  if ( !isFinal ) {
						    char c2 = cha[ ( i + 1 ) ];
						    isFinal = ( tmk.getNext( c2 ) == null );
						  }
						  if ( isFinal ) {
						    int ii = tokenBufferSize - 1;
						    // Remove bad token
						    StringBuffer sb = new StringBuffer();
						    sb.append( tmk.getToken().getTokenSignature() );
						    for ( ; ii >= 0; ii-- ) {
						      if ( tokenBuffer[ ii ].hasCollection() ) {
						        sb.insert( 0, tokenBuffer[ ii ].getTokenSignature() );
						        tokenBufferSize--;
						      }
						    }
						    Token tt = new Token( sb.toString().toCharArray() );
						    tt.resetTokenAttributes( tmk.getToken() );
						    tmk = new TokenMatcher( ignoreCase, tt );
						    lastEmptyTokenLocation = 0;
						  }
						             }*/

						if (isFinal) {

							boolean withEndToken =
								(tmk.getToken().getEndToken() != null);

							TokenMatcher tm = null;

							if (!withEndToken && tmk.getToken().getTokenSignature().length > 1 )
								tm =
									getMaxTokenMatcher(
										cha,
										i,
										tma,
										tma.length == 1 ? -1 : k);
							else {
/*								System.out.println( "WITH END TOKEN" );
								if ( waitEndToken ) {
									Integer ii = new Integer( index );
									if ( htLtk.containsKey( ii ) ) {
										System.out.println( "FORCE REPAINT LINE INDEX  !!" );
									}
								}*/
							}
							
							if (tm != null) {
								if (tm.getToken().getTokenSignature().length
									> tmk
										.getToken()
										.getTokenSignature()
										.length) {
									i
										+= (tm
											.getToken()
											.getTokenSignature()
											.length
											- tmk
												.getToken()
												.getTokenSignature()
												.length);
									int itmp = -1;

									if ((itmp =
										new String(
											tm
												.getToken()
												.getTokenSignature())
												.indexOf(
											new String(
												tmk
													.getToken()
													.getTokenSignature())))
										> -1) {
										i -= itmp;
									}

									tmk = tm;
								}
							}

							if (((i + 1 - tmk.getToken().getContentLocation())
								- lastEmptyTokenLocation)
								> 0) {
								Token tmp;

								tokenBuffer[tokenBufferSize++] =
									(tmp =
										new Token(
											line
												.substring(
													lastEmptyTokenLocation,
													i
														+ 1
														- tmk
															.getToken()
															.getContentLocation())
												.toCharArray()));
								if (endToken != null) {
									tmp.resetTokenAttributes(endToken);
								}
								tmp.setDefaultToken(false);
							}
							lastEmptyTokenLocation = i + 1;
							tokenBuffer[tokenBufferSize++] = tmk.getToken();
							if (tmk.getToken().getEndToken() != null) {
								waitEndToken = true;
								endToken = tmk.getToken().getEndToken();
								tma =
									new TokenMatcher[] {
										 new TokenMatcher(
											ignoreCase,
											tmk.getToken().getEndToken())};
								tma_endToken = tma;
								break tmab;
							}
							endToken = null;
							waitEndToken = false;
							tma = tokenMatcherDefault;
							tokenMatcherSize = 0;
							break tmab;
						}
						boolean exist = false;
						for (int ii = 0; ii < tokenMatcherSize; ii++) {
							if (tmWorking[ii] == tmk) {
								exist = true;
								break;
							}
						}
						if (!exist) {
							tmWorking[tokenMatcherSize++] = tmk;
						}
					}
				}
				j++;
			}
			if (tokenMatcherSize == 0) {
				if (!waitEndToken) {
					tma = tokenMatcherDefault;
					// Try the initial one
				} else {
					if (tma_endToken != null) {
						tma = tma_endToken;

						// Try to see if the last char was a matching first one
						if ( tma.length == 1 && tma[ 0 ].getToken().getTokenSignature().length > 1 ) {
							TokenMatcher[] tmp = tma[ 0 ].getNext( lastChar );
							if ( tmp != null )
								tma = tmp;
						}
					}
				}
			} else {
				if (!(waitEndToken
					&& tma_endToken[0].getToken().getTokenSignature().length
						== 1)) {
					tma = new TokenMatcher[tokenMatcherSize];
					for (int l = 0; l < tokenMatcherSize; l++) {
						tma[l] = tmWorking[l];
					}
				}
			}
			i++;
		}

		if ((lastEmptyTokenLocation > 0) || (tokenMatcherSize > 0)) {
			int endLocation = line.length();
			Token finalToken = null;

			if (tokenMatcherSize > 0) {
				for (i = 0; i < tokenMatcherSize; i++) {
					TokenMatcher t = tmWorking[i];
					if (t.isFinal()) {
						endLocation -= t.getToken().getTokenSignature().length;
						finalToken = t.getToken();
						lastEmptyTokenLocation
							+= finalToken.getTokenSignature().length;
						break;
					}
				}
			}

			// Final one ?
			if (lastEmptyTokenLocation < line.length()) {
				Token tmp;
				tokenBuffer[tokenBufferSize++] =
					(tmp =
						new Token(
							line
								.substring(lastEmptyTokenLocation, endLocation)
								.toCharArray()));
				if (endToken != null) {
					tmp.resetTokenAttributes(endToken);
				}
			}
			if (finalToken != null) {
				tokenBuffer[tokenBufferSize++] = finalToken;
			}
		}

		// Particular case for the end of the line
		if (waitEndToken) {
			// Try \n
			for (int j = 0; j < tma.length; j++) {
				TokenMatcher[] tmac = tma[j].getNext('\n');
				if (tmac != null) {
					for (i = 0; i < tmac.length; i++) {
						TokenMatcher tmk = tmac[i];
						if (tmk.isFinal()) {
							if (((i + 1 - tmk.getToken().getContentLocation())
								- lastEmptyTokenLocation)
								> 0) {
								Token tmp;
								tokenBuffer[tokenBufferSize++] =
									(tmp =
										new Token(
											line
												.substring(
													lastEmptyTokenLocation,
													i
														+ 1
														- tmk
															.getToken()
															.getContentLocation())
												.toCharArray()));
								if (endToken != null) {
									tmp.resetTokenAttributes(endToken);
								}
								tmp.setDefaultToken(false);
							}
							lastEmptyTokenLocation = i + 1;
							if (!(tmk.getToken().getTokenSignature().length
								== 1
								&& tmk.getToken().getTokenSignature()[0]
									== '\n')) {
								tokenBuffer[tokenBufferSize++] = tmk.getToken();
							}
							if (tmk.getToken().getEndToken() != null) {
								waitEndToken = true;
								endToken = tmk.getToken().getEndToken();
								tma =
									new TokenMatcher[] {
										 new TokenMatcher(
											ignoreCase,
											tmk.getToken().getEndToken())};
								tma_endToken = tma;
								break;
							}
							endToken = null;
							waitEndToken = false;
							tma = tokenMatcherDefault;
							tokenMatcherSize = 0;
						}
					}
				}
			}
		}

		if (tokenBufferSize == 0) {
			Token tmp;
			tokenBuffer[tokenBufferSize++] =
				(tmp = new Token(line.toCharArray()));
			if (endToken != null) {
				tmp.resetTokenAttributes(endToken);
			}
		}

		if (endToken != null) {
			//System.out.println( "PUT AT " + index );
			
			if ( !htLtk.containsKey( new Integer( index ) ) ) {
				toRepaint = true;
			}
			
			htLtk.put(new Integer(index), endToken);
			endToken = null;
			
		} else {
			Integer ii = new Integer(index);
			if (htLtk.containsKey(ii)) {
				htLtk.remove(ii);
				toRepaint = true;
				//System.out.println( "TO REPAINT = TRUE ");
			}
			
		}

		boolean hasCollection = false;

		// Merge collection
		for (i = 0; i < tokenBufferSize; i++) {
			if (tokenBuffer[i].hasCollection()) {
				hasCollection = true;
				break;
			}
		}

		if (hasCollection) {
			if (mergingVector == null)
				mergingVector = new Vector();
			else
				mergingVector.removeAllElements();

			String lastCollection = null;
			i = 0;
			
			while (i < tokenBufferSize) {
				if (tokenBuffer[i].hasCollection()) {
					String coll = tokenBuffer[i].getCollection();
					Vector toMerge = null;
					StringBuffer sb = null;
					int j;
					for (j = (i + 1); j < tokenBufferSize; j++) {
						if (tokenBuffer[j].hasCollection()) {
							if (tokenBuffer[j].getCollection().equals(coll)) {
								if (sb == null) {
									sb = new StringBuffer();
									sb.append(
										tokenBuffer[i].getTokenSignature());
								}
								sb.append(tokenBuffer[j].getTokenSignature());
							} else
								break;
						} else
							break;
					}
					if (sb != null) {
						Token t;
						mergingVector.add(
							t = new Token(sb.toString().toCharArray()));
						t.resetTokenAttributes(tokenBuffer[i]);
						i = (j - 1);
					} else {
						mergingVector.add(tokenBuffer[i]);
					}
				} else
					mergingVector.add(tokenBuffer[i]);
				i++;
			}
			tokenBufferSize = mergingVector.size();
			for (int ii = 0; ii < mergingVector.size(); ii++) {
				tokenBuffer[ii] = (Token) mergingVector.get(ii);
			}
		}

/*		if (mustRepaint && waitEndToken) {
			toRepaint = false;
		} else
			toRepaint = mustRepaint; */

		return tokenBuffer;
	}

	private boolean toRepaint = false;

	public boolean mustRepaint() {
/*		if ( toRepaint ) {
				toRepaint = false;
				return true;
		}*/
		return toRepaint;
	}
	
	private Vector mergingVector;

	public static void main(String[] args) {
		LineLexer ll = new LineLexer();
		
		Token[] ts = ll.getTokensForLine("function test()\n{\n\talert(\"instanceof\");\n\t\n}", 0);

		if (ts == null) {
			System.out.println("NULL ?");
		}
		System.out.println("Tokens :" + ll.getTokenSize());
		for (int i = 0; i < ll.getTokenSize(); i++) {
			System.out.println("." + ts[i].getColor());
			System.out.println("-" + new String(ts[i].getTokenSignature()));
		}
	}
}

package com.japisoft.sc;

import java.util.Hashtable;

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
public final class ScPredefinedToken {
  /** Support all numbers */
  public static final String NUMBER_ID = "_NUMBER_";
  public static final String NUMBER_TOKENS = "(0;1;2;3;4;5;6;7;8;9;.;,)";
  /** Support +,-,/,*,% operators */
  public static final String OPERATOR_ID = "_OPERATOR_";
  public static final String OPERATOR_TOKENS = "+:-:/:*:%";
  /** Support "litteral" */
  public static final String LITTERAL_ID = "_LITTERAL_";
  public static final String LITTERAL_TOKENS = "[\";\"{{\\}}]";

  /** Carriage return */
  public static final String RC_TOKEN = "_RC_";
  /** Double dot */
  public static final String DD_TOKEN = "_DD_";

  static Hashtable htTokens = new Hashtable();

  static {
    htTokens.put( NUMBER_ID, NUMBER_TOKENS );
    htTokens.put( OPERATOR_ID, OPERATOR_TOKENS );
    htTokens.put( LITTERAL_ID, LITTERAL_TOKENS );
  }

  public static void addPredefinedToken( String tokenId, String tokenValues ) {
    htTokens.put( tokenId, tokenValues );
  }

  public static void removePredefinedToken( String tokenId, String tokenValues ) {
    htTokens.remove( tokenId );
  }

  static String getPredefinedTokenValues( String tokenId ) {
    return (String)htTokens.get( tokenId );
  }

  /** Convert a token to a valid format*/
  public static String getValidToken( String item ) {
    if ( ":".equals( item ) )
      return DD_TOKEN;
    else
      if ( "\n".equals( item ) )
        return RC_TOKEN;
    return item;
  }

}

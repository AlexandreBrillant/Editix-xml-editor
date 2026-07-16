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

package com.japisoft.framework.ui.sc;

import java.util.Hashtable;

/**
 * Here a list of predefined token.
 * This tokens are usable in the syntax file descriptor. By convention
 * predefined tokens are always in the format _TOKENID_.
 * <p>Sample : _NUMBER_ = 0:1:2:3:4:5:6:7:8:9, in the syntax
 * descriptor (see ScEditorKit) you just have to mention as a token
 * value _NUMBER_.</p>
 * <p>
 * Particular tokens are used inside the syntax descriptor, so you have
 * to use special token value. For sample double dot must be used
 * as a syntax token with _DD_.
 * Sample : MYTOKENS=word1:word2:_DD_
 * </p>
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.1 */
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
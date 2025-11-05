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

import java.awt.*;
import java.util.*;

/**
 * Management for a collection
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 */
class CollectionTokenMatcher extends TokenMatcher {

  private TokenMatcher[] tm;
  private Hashtable collection;
  private String name;
  private TokenMatcher[] tm2;

  public CollectionTokenMatcher( Hashtable collection, String name ) {
    this.collection = collection;
    this.name = name;
  }

  public CollectionTokenMatcher( TokenMatcher[] tm ) {
    this.tm2 = tm;
  }

  public TokenMatcher[] getNext(char current) {
    // Check if one caracter is inside the collection
    if ( tm == null && tm2 == null )
      tm = new TokenMatcher[] { new CollectionTokenMatcher( tm2 = ( TokenMatcher[] )collection.get( name ) ) };
    for ( int i = 0; i < tm2.length; i++ ) {
      if ( tm2[ i ].getNext( current ) != null ) {
        Color c = tm2[ i ].getToken().getColor();
        this.t = tm2[ i ].getToken();
        System.out.println( c.getRed() + "," + c.getGreen() + "," + c.getBlue() );
        return new TokenMatcher[] { this };
      }
    }
    return null;
  }
}

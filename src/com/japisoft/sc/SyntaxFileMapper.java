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

package com.japisoft.sc;

import java.io.*;
import java.util.Hashtable;

/**
 * This class makes a relation between
 * a file extension and a syntax descriptor file. It
 * provides a support for editing different data format.
 * Usag sample :
 * <pre>
 * ScEditorKit sc = new ScEditorKit();
 * SyntaxFileMapper sfm = new SyntaxFileMapper();
 * sfm.addRelation( "sql", "sql.properties" );
 * sfm.addRelation( "java", "java.properties" );
 * ...
 * // Each time a file must be loaded you must have to
 * File myNewFile = ...;
 * sfm.resetSyntaxColor( myNewFile );
 * </pre>
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0 */
public class SyntaxFileMapper {
  private ScEditorKit sc;

  public SyntaxFileMapper( ScEditorKit sc ) {
    super();
    this.sc = sc;
  }

  private Hashtable htDescriptor;

  /** Add a relation between a file format and a syntax descriptor */
  public void addRelation( String fileExtension, String propertyId ) {
    if ( htDescriptor == null )
      htDescriptor = new Hashtable();
    htDescriptor.put( fileExtension.toLowerCase(), propertyId );
  }

  /** Update the editor kit for supporting this file format */
  public void resetSyntaxColor( File f ) throws FileNotFoundException {
    resetSyntaxColor( f.toString() );
  }
  /** Update the editor kit for supporting this file format */
  public void resetSyntaxColor( String f ) throws FileNotFoundException {
    if ( htDescriptor != null ) {
      int i = f.lastIndexOf( "." );
      if ( i > -1 ) {
        String ext = f.substring( i + 1 );
        String propertyId = ( String )htDescriptor.get( ext.toLowerCase() );
        if ( propertyId != null ) {
          sc.readSyntaxColorDescriptor( propertyId );
        }
      }
    }
  }

}
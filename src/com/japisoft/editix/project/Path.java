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

package com.japisoft.editix.project;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Path {

	public static String getAbsolutePath( String refPath, String relPath ) {
		
		if ( !relPath.startsWith( "." ) )
			return relPath;
		
		File rp = new File( refPath ).getParentFile();
		String[] rps = relPath.split( "/" );
		for ( int i = 0; i < rps.length; i++ ) {
			if ( ".".equals( rps[ i ] ) ) {
				continue;
			}
			if ( "..".equals( rps[ i ] ) ) {
				rp = rp.getParentFile();
			} else {
				rp = new File( rp, rps[ i ] );
			}
			if ( rp == null ) {
				return relPath;	// ??
			}
		}
		return rp.toString();

	}

	public static String getRelativePath( String refPath, String absPath ) {
		
		List lrp = getPathContent( refPath );
		List lp = getPathContent( absPath );
		
		int maxCommon = -1;
		
		for ( int i = 0; i < lp.size(); i++ ) {
			
			File f = ( File )lp.get( i );
			if ( i == lrp.size() )
				break;
			File fr = ( File )lrp.get( i );
			if ( f.equals( fr ) ) {
				maxCommon = i;
			} else
				break;

		}
		
		if ( maxCommon == -1 )
			return absPath;
		
		StringBuffer sb = new StringBuffer();		
		
		for ( int j = maxCommon + 1; j < lrp.size() - 1; j++ ) {
			if ( sb.length() > 0 )
				sb.append( "/" );
			sb.append( ".." );
		}

		if ( sb.length() == 0 )
			sb.append( "." );
		
		for ( int j = maxCommon + 1; j < lp.size(); j++ ) {
			sb.append( "/" );
			sb.append( ( ( File )lp.get( j ) ).getName() );
		}
		
		return sb.toString();
	}
	
	private static List getPathContent( String path ) {
		
		ArrayList r = new ArrayList();
		File f = 
			new File( path );

		while ( f != null ) {
			
			r.add( 0, f );
			f = f.getParentFile();
			
		}
		
		return r;
		
	}
	
	public static void main( String[] args ) {

		System.out.println( 
				Path.getRelativePath( "c:/to/test.prj", "c:/toto/titi/oo.txt" ) );
				
		System.out.println( 
				Path.getRelativePath( "c:/toto/test.prj", "c:/toto/titi/oo.txt" ) );

		System.out.println( 
				Path.getRelativePath( "c:/toto/titi/test.prj", "c:/toto/titi/oo.txt" ) );
		
		System.out.println( 
				Path.getRelativePath( "c:/toto/titi/tata/test.prj", "c:/toto/titi/oo.txt" ) );

		System.out.println( 
				Path.getRelativePath( "c:/toto/titi/tata/test.prj", "c:/tot/oo.txt" ) );
		
		System.out.println(
				Path.getAbsolutePath( "c:/toto/test.prj", "./titi/oo.txt" ) );

		System.out.println(
				Path.getAbsolutePath( "c:/toto/test.prj", "../titi/oo.txt" ) );
		
		System.out.println(
				Path.getAbsolutePath( "c:/toto/test.prj", "../../../titi/oo.txt" ) );
		
		System.out.println(
				Path.getAbsolutePath( "/a/b/c.txt", "../../../d.txt" ) );
		
	}
	
}

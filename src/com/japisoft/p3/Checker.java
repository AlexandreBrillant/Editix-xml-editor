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

package com.japisoft.p3;

import java.math.BigInteger;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 */
public class Checker {

	// Public key

	private static String N = "8907416837986794317725654963193175741438188506163610741638214116740051863723078302281700506066878018041136973667531599112548220215330486280426333257642291";

	private static String E = "58021664585639791181184025950440248398226136069516938232493687505822471836536824298822733710342250697739996825938232641940670857624514103125986134050997697160127301547995788468137887651823707102007839";

	static boolean personal = false;
	static boolean professional = false;
	static boolean student = false;
	static boolean enterprise = false;
	static boolean floating = false;
	static boolean noncommercial = false;
	
	static String check(
			String regname, 
			String key, 
			String persControl, 
			String profControl, 
			String studControl, 
			String enterpriseControl,
			String floatingControl,
			String nonCommercialControl ) {

		try {

			BigInteger d = new BigInteger(E);
			BigInteger n = new BigInteger(N);
			if ( key == null || 
					key.length() == 0 )
				return null;

			BigInteger message = new BigInteger(key);
			BigInteger r = message.modPow(d, n);
			String res = new String(r.toByteArray(), "UTF8" );
			personal = (res.endsWith( persControl ));
			professional = (res.endsWith( profControl ));

			student = (res.endsWith( studControl ));
			enterprise = (res.endsWith( enterpriseControl ));
			
			if ( floatingControl != null )
				floating = (res.endsWith(floatingControl));
			
			noncommercial = ( res.endsWith( nonCommercialControl ) );

			if ( !personal && 
				!professional && 
				!student && 
				!enterprise && 
				!floating && 
				!noncommercial ) {
				return null;
			}

			if ( personal )
				return res.substring( 0, res.length() - persControl.length() );
			if ( professional )
				return res.substring( 0, res.length() - profControl.length() );
			if ( student )
				return res.substring( 0, res.length() - studControl.length() );
			if ( floating )
				return res.substring( 0, res.length() - floatingControl.length() );
			if ( noncommercial )
				return res.substring( 0, res.length() - nonCommercialControl.length() );

			return res.substring( 0, res.length() - enterpriseControl.length() );

		} catch (Throwable th) {
			return null;
		}
	}

	public static void main( String[] args ) throws Exception {
		
	}
	
	
}

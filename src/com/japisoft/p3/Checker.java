package com.japisoft.p3;

import java.math.BigInteger;

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
public class Checker {

	// Public key

	private static String N = "8907416837986794317725654963193175741438188506163610741638214116740051863723078302281700506066878018041136973667531599112548220215330486280426333257642291";

	private static String E = "58021664585639791181184025950440248398226136069516938232493687505822471836536824298822733710342250697739996825938232641940670857624514103125986134050997697160127301547995788468137887651823707102007839";

	static boolean personal = false;
	static boolean professional = false;
	static boolean student = false;
	static boolean enterprise = false;
	
	static String check(
			String regname, 
			String key, 
			String persControl, 
			String profControl, 
			String studControl, 
			String enterpriseControl ) {

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

			if ( !personal && 
					!professional && 
						!student && 
							!enterprise ) {
				return null;
			}

			if ( personal )
				return res.substring( 0, res.length() - persControl.length() );
			if ( professional )
				return res.substring( 0, res.length() - profControl.length() );
			if ( student )
				return res.substring( 0, res.length() - studControl.length() );

			return res.substring( 0, res.length() - enterpriseControl.length() );

		} catch (Throwable th) {
			return null;
		}
	}

	public static void main( String[] args ) throws Exception {

		String key = "8352319137213383999977417431459820572364346825550078258039232859424189828115945251026889651426261165793664063553008440035202058687891366364666881289676152";
		BigInteger d = new BigInteger(E);
		BigInteger n = new BigInteger(N);
		BigInteger message = new BigInteger(key);
		BigInteger r = message.modPow(d, n);
		String res = new String( r.toByteArray(), "UTF8" );
		System.out.println( "[" + res + "]" );
		
	}
	
	
}

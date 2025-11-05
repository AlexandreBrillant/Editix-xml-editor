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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.Date;

import com.japisoft.framework.ApplicationModel;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 */
public final class Manager {
	
	public static int MAGIC_NUMBER_2 = 10;
	public static int MAGIC_NUMBER_1 = 5;
	public static int PREVIOUS_INC = 10;

	public static String PERS_TYPE = "pers";
	public static String PROF_TYPE = "prof";
	public static String STUD_TYPE = "stud";
	public static String ENT_TYPE = "ent";
	public static String FLOAT_TYPE = "float";
	public static String FREE_TYPE = "noncom";

	public static String PERS_SIGNATURE = null;
	public static String PROF_SIGNATURE = null;
	public static String ENT_SIGNATURE = null;
	public static String STUD_SIGNATURE = null;
	public static String FLOAT_SIGNATURE = null;

	public static String PERS_SIGNATURE2 = null;
	public static String PROF_SIGNATURE2 = null;
	public static String ENT_SIGNATURE2 = null;
	public static String STUD_SIGNATURE2 = null; 
	public static String FLOAT_SIGNATURE2 = null;
		
	public static String NONCOMMERCIAL_SIGNATURE = null;
	public static String NONCOMMERCIAL_SIGNATURE2 = null;

	public static boolean registered( String user, String password ) throws Exception {
		control( user );
		if (user == null || password == null)
			return false;
	
		if ( "extendEvaluation".equals( user ) ) {
			testDay();
			if ( CHECKED < 45 ) {
				throw new Exception( "Extend evaluation" );
			}
		}
		
		user = check(
			user, 
			password 
		);
		if ( user != null ) {
			try {
				lastUser = user;
				unlocked(user, password);
			} catch( Exception exc ) {
				throw exc;
			}
		}
		return user != null;
	}

	public static boolean isForPersonal() {
		return Checker.personal;
	}

	public static boolean isForProfessional() {
		return Checker.professional;
	}

	public static boolean isForEnterprise() {
		return Checker.enterprise;
	}

	public static boolean isForStudent() {
		return Checker.student;
	}

	public static boolean isForFloating() {
		return Checker.floating;
	}
	
	public static boolean isForNonCommercial() {
		return Checker.noncommercial;
	}
	
	private static boolean freeEdition = false;

	public static void setFree() {
		freeEdition = true;
	}

	public static boolean isFree() {
		return freeEdition;
	}

	static void control(String user) {
		WrongUserController.control( user );
	}

	static String check(String user, String password ) {
		try {
			control(user);
			String res = Checker.check( user, password, PERS_SIGNATURE, PROF_SIGNATURE, STUD_SIGNATURE, ENT_SIGNATURE, FLOAT_SIGNATURE, NONCOMMERCIAL_SIGNATURE  );
			if ( res == null ) {
				// Previous version
				if ( PERS_SIGNATURE2 != null )
				 	return Checker.check( user, password, PERS_SIGNATURE2, PROF_SIGNATURE2, STUD_SIGNATURE2, ENT_SIGNATURE2, FLOAT_SIGNATURE2, NONCOMMERCIAL_SIGNATURE2 );
				// return null;
				return null;
			} else
				return res;
		} catch( RuntimeException exc ) {
			return null;
		}
	}

	static void unlocked(String user, String password) throws Exception {
		File home = getRegisteredPath();
		if (home == null) {
			throw new Exception( "Can't write to "
					+ home + "\nPlease check you have 'write access' for this directory" );
		}
		try {
			FileWriter fw = new FileWriter(getRegisteredFile());
			try {
				fw.write(user + "\n");
				fw.write(password);
			} finally {
				fw.close();
			}
		} catch (IOException exc) {
			throw new Exception( "Can't write to "
					+ home + "\nPlease check you have 'write access' for this directory" );
		}
	}
	
	public static void locked() {
		File h = getRegisteredFile();
		try {
			FileOutputStream f = new FileOutputStream( new File( h, "u.l" ) );
			f.write( ' ' );
			f.close();
		} catch( Exception exc ) {
			ApplicationModel.debug( exc );
		}
	}

	public static boolean isLocked() {
		File h = getRegisteredPath();
		return new File( h, "u.l" ).exists();
	}
	
	public static File getRegisteredFile() {
		File home = getRegisteredPath();
		if (home == null)
			return null;
		File f = new File(home, ApplicationModel.REGISTERED_FILE );
		if ( !f.exists() ) {
			if ( ApplicationModel.REGISTERED_FILE2 != null )
				f = new File( home, ApplicationModel.REGISTERED_FILE2 );
		}
		if ( !f.exists() ) {
			URL r = ClassLoader.getSystemClassLoader().getResource( "key.txt" );
			String fileLocation = r.toExternalForm();
			if ( fileLocation.startsWith( "file:/" ) )
				fileLocation = fileLocation.substring( "file:/".length() );
			f= new File( fileLocation );
		}
		return f;
	}

	static String lastUser = null;

	public static boolean hasValidRegisteredFile() {
		File f = getRegisteredFile();
		if (f == null) {
			return false;
		}
		try {
			BufferedReader br = new BufferedReader( new FileReader( f ) );
			try {
				lastUser = br.readLine();
				String password = br.readLine();
				lastUser = check( lastUser, password );
				boolean ok = lastUser != null;
				br.close();
				return ok;
			} catch (Throwable th) {
			}
		} catch (FileNotFoundException ex) {
			return false;
		}
		return false;
	}

	public static String getUser() {
		return lastUser;
	}

	static File getRegisteredPath() {
		return ApplicationModel.getAppUserPath();
	}

	private static int CHECKED = -1;

	public static int registeredDay() {
		if (hasValidRegisteredFile() )
			return -1;
		if (CHECKED > -1)
			return CHECKED;
		else
			testDay();
		return CHECKED;
	}
	
	public static int lastRegisteredDay() {
		
		if  ( hasValidRegisteredFile() || isFree() )
			return -1;
		return compute( MAGIC_NUMBER_1, MAGIC_NUMBER_2 ) - registeredDay();
	}

	private static final String PROTECT_DIR = ".config";
	private static final String PROTECT_DIR2 = ".sysdb";
	
	public static String CURRENT_PRO_FILE2 = ".sys2";
	public static String CURRENT_PRO_FILE1 = ".sys1";
	public static String[] PREVIOUS_FILES = null;

	static void testDay() {
		
		Date d = new Date();

		File home1 = new File( System.getProperty( "user.home" ) );

		if ( !home1.exists() ) {
			CHECKED = compute( MAGIC_NUMBER_1, MAGIC_NUMBER_2 );
			return;
		}

		File home2 = new File( home1, PROTECT_DIR );
		home2.mkdir();
		if ( !home2.exists() ) {
			CHECKED = compute( MAGIC_NUMBER_1, MAGIC_NUMBER_2 );
			return;			
		}
				
		File home3 = new File( home1, PROTECT_DIR2 );
		home3.mkdir();
		
		Date lastDate = null;
		int delta = 0;

		try {
			// Read previous file
			ObjectInputStream obj = new ObjectInputStream(new FileInputStream(
					new File(home2, CURRENT_PRO_FILE1 ) ) );
			try {
				lastDate = (Date) obj.readObject();
			} finally {
				obj.close();
			}
		} catch (Throwable th) {
		}

		if (lastDate == null) { // Store it
			try {
				ObjectOutputStream obj = new ObjectOutputStream(
						new FileOutputStream(new File(home2, CURRENT_PRO_FILE1 ) ) );
				lastDate = d;
				obj.writeObject(lastDate);
			} catch (Throwable th) {
				CHECKED = compute( MAGIC_NUMBER_1, MAGIC_NUMBER_2 );
				return;
			}
		}
		
		double tmpRes = ( double ) ( d.getTime() - lastDate.getTime() )
				/ (1000.0 * 60.0 * 60.0 * 24.0);
		
		CHECKED = ( int )tmpRes;
				
		if (CHECKED < 0 && ( lastDate != d ) ) {
			CHECKED = compute( MAGIC_NUMBER_1, MAGIC_NUMBER_2 );
			return;
		}

		File f2 = new File(home2, CURRENT_PRO_FILE2 );
		File sec2 = new File( home3, "sql.dat" );
		
		if (!f2.exists()) {
			try {
				// Hacking
				if ( sec2.exists() ) {
					CHECKED = compute( MAGIC_NUMBER_1, MAGIC_NUMBER_2 );
					return;					
				}
				
				FileOutputStream output = new FileOutputStream(f2);
				output.write(CHECKED);
				output.close();
			} catch (Throwable th) {
				CHECKED = compute( MAGIC_NUMBER_1, MAGIC_NUMBER_2 );
				return;
			}
		} else {
			try {
				FileOutputStream output = new FileOutputStream( sec2 );
				output.write( 0 );
				output.close();
			} catch( Exception exc ) {
			}
		}
		
		try {
			FileInputStream lastO = new FileInputStream(f2);
			int lastCHECKED = lastO.read();
			lastO.close();
			if ( lastCHECKED < CHECKED ) {
				FileOutputStream output = new FileOutputStream( f2 );
				output.write( CHECKED );
				output.close();
			} else {

				if  ( lastCHECKED > CHECKED ) {
					CHECKED = compute( MAGIC_NUMBER_1, MAGIC_NUMBER_2 );
				} else
				
				CHECKED = lastCHECKED;
			}
		} catch (Throwable th) {
			CHECKED = compute( MAGIC_NUMBER_1, MAGIC_NUMBER_2 );
			return;
		}

		if ( PREVIOUS_FILES != null ) {
			for ( int i = 0; i < PREVIOUS_FILES.length; i++ ) {
				if ( new File( home2, PREVIOUS_FILES[ i ] ).exists() ) {
					delta += PREVIOUS_INC;
					break;
				}
			}
		}

		if ( CHECKED < compute( MAGIC_NUMBER_1, MAGIC_NUMBER_2 ) )
			CHECKED += delta;
	}

	public static int compute( int a, int b ) {
		return a + b;
	}
	
}



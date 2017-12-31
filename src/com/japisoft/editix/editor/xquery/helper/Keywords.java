package com.japisoft.editix.editor.xquery.helper;

import java.util.ArrayList;

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
public class Keywords {
	
	public static String[] main = {
		"and", "as", "ascending", "at", "base-uri", "boundary-space", "by", "case", "cast", "castable", "collation", "comment", "construction", "copy-namespaces", "declare", "default", "descending", "div", "document", "document-node", "element", "else", "empty", "empty-sequence", "encoding", "eq", "every", "except", "external", "for", "function", "ge", "greatest", "gt", "idiv", "if", "import", "in", "inherit", "instance", "intersect", "is", "item", "lax", "le", "least", "let", "lt", "mod", "module", "namespace", "ne", "node", "no-inherit", "no-preserve", "of", "option", "or", "order", "ordered", "ordering", "preserve", "processing-instruction", "return", "satisfies", "schema", "schema-attribute", "schema-element", "some", "stable", "strict", "strip", "text", "then", "to", "treat", "typeswitch", "union", "unordered", "validate", "variable", "version", "where", "xquery"	
	};

	public static String[] raw_functions = {
				"fn:abs($arg as  numeric?) as numeric?",
				"op:add-dayTimeDuration-to-date( $arg1 as xs:date, $arg2 as xs:dayTimeDuration)  as xs:date",
				"op:add-dayTimeDuration-to-dateTime( $arg1 as xs:dateTime, $arg2 as xs:dayTimeDuration)  as xs:dateTime",
				"op:add-dayTimeDuration-to-time( $arg1 as xs:time, $arg2 as xs:dayTimeDuration)  as xs:time",
				"op:add-dayTimeDurations( $arg1 as xs:dayTimeDuration, $arg2 as xs:dayTimeDuration)  as xs:dayTimeDuration",
				"op:add-yearMonthDuration-to-date( $arg1 as xs:date, $arg2 as xs:yearMonthDuration)  as xs:date",
				"op:add-yearMonthDuration-to-dateTime( $arg1 as xs:dateTime, $arg2 as xs:yearMonthDuration)  as xs:dateTime",
				"op:add-yearMonthDurations( $arg1 as xs:yearMonthDuration, $arg2 as xs:yearMonthDuration)  as xs:yearMonthDuration",
				"fn:adjust-date-to-timezone( $arg as xs:date?) as  xs:date?",
				"fn:adjust-date-to-timezone( $arg as xs:date?, $timezone as xs:dayTimeDuration?)  as xs:date?",
				"fn:adjust-dateTime-to-timezone( $arg as xs:dateTime?)  as xs:dateTime?",
				"fn:adjust-dateTime-to-timezone( $arg as xs:dateTime?, $timezone as xs:dayTimeDuration?)  as xs:dateTime?",
				"fn:adjust-time-to-timezone( $arg as xs:time?) as  xs:time?",
				"fn:adjust-time-to-timezone( $arg as xs:time?, $timezone as xs:dayTimeDuration?)  as xs:time?",
				"fn:avg($arg as  xs:anyAtomicType*) as xs:anyAtomicType?",
				"fn:base-uri() as  xs:anyURI?",
				"fn:base-uri($arg  as node()?) as  xs:anyURI?",
				"op:base64Binary-equal( $value1 as xs:base64Binary, $value2 as xs:base64Binary)  as xs:boolean",
				"fn:boolean($arg as  item()*) as xs:boolean",
				"op:boolean-equal($value1  as xs:boolean, $value2 as xs:boolean)  as xs:boolean",
				"op:boolean-greater-than( $arg1 as xs:boolean, $arg2 as xs:boolean)  as xs:boolean",
				"op:boolean-less-than( $arg1 as xs:boolean, $arg2 as xs:boolean)  as xs:boolean",
				"fn:ceiling($arg as  numeric?) as numeric?",
				"fn:codepoint-equal($comparand1  as xs:string?, $comparand2 as xs:string?)  as xs:boolean?",
				"fn:codepoints-to-string( $arg as xs:integer*)  as xs:string",
				"fn:collection() as  node()",
				"fn:collection($arg  as xs:string?) as  node()",
				"fn:compare($comparand1  as xs:string?, $comparand2 as xs:string?)  as xs:integer?",
				"fn:compare($comparand1  as xs:string?, $comparand2 as xs:string?, $collation as xs:string)  as xs:integer?",
				"fn:concat($arg1 as  xs:anyAtomicType?, $arg2 as xs:anyAtomicType?, ...) as xs:string",
				"op:concatenate($seq1  as item()*, $seq2 as item()*)  as item()*",
				"fn:contains($arg1  as xs:string?, $arg2 as xs:string?)  as xs:boolean",
				"fn:contains($arg1  as xs:string?, $arg2 as xs:string?, $collation as xs:string) as xs:boolean",
				"fn:count($arg as  item()*) as xs:integer",
				"fn:current-date() as  xs:date",
				"fn:current-dateTime()  as xs:dateTime",
				"fn:current-time() as  xs:time",
				"fn:data($arg as  item()*) as xs:anyAtomicType*",
				"op:date-equal($arg1  as xs:date, $arg2 as xs:date)  as xs:boolean",
				"op:date-greater-than( $arg1 as xs:date, $arg2 as xs:date)  as xs:boolean",
				"op:date-less-than($arg1  as xs:date, $arg2 as xs:date)  as xs:boolean",
				"fn:dateTime($arg1  as xs:date?, $arg2 as xs:time?)  as xs:dateTime?",
				"op:dateTime-equal($arg1  as xs:dateTime, $arg2 as xs:dateTime)  as xs:boolean",
				"op:dateTime-greater-than( $arg1 as xs:dateTime, $arg2 as xs:dateTime)  as xs:boolean",
				"op:dateTime-less-than( $arg1 as xs:dateTime, $arg2 as xs:dateTime)  as xs:boolean",
				"fn:day-from-date($arg  as xs:date?) as  xs:integer?",
				"fn:day-from-dateTime( $arg as xs:dateTime?)  as xs:integer?",
				"fn:days-from-duration( $arg as xs:duration?)  as xs:integer?",
				"op:dayTimeDuration-greater-than( $arg1 as xs:dayTimeDuration, $arg2 as xs:dayTimeDuration)  as xs:boolean",
				"op:dayTimeDuration-less-than( $arg1 as xs:dayTimeDuration, $arg2 as xs:dayTimeDuration)  as xs:boolean",
				"fn:deep-equal($parameter1  as item()*, $parameter2 as item()*)  as xs:boolean",
				"fn:deep-equal($parameter1  as item()*, $parameter2 as item()*, $collation as string)  as xs:boolean",
				"fn:default-collation()  as xs:string",
				"fn:distinct-values($arg  as xs:anyAtomicType*) as  xs:anyAtomicType*",
				"fn:distinct-values($arg  as xs:anyAtomicType*, $collation as xs:string) as xs:anyAtomicType*",
				"op:divide-dayTimeDuration( $arg1 as xs:dayTimeDuration, $arg2 as xs:double)  as xs:dayTimeDuration",
				"op:divide-dayTimeDuration-by-dayTimeDuration( $arg1 as xs:dayTimeDuration, $arg2 as xs:dayTimeDuration)  as xs:decimal",
				"op:divide-yearMonthDuration( $arg1 as xs:yearMonthDuration, $arg2 as xs:double)  as xs:yearMonthDuration",
				"op:divide-yearMonthDuration-by-yearMonthDuration( $arg1 as xs:yearMonthDuration, $arg2 as xs:yearMonthDuration)  as xs:decimal",
				"fn:doc($uri as  xs:string?) as document-node()?",
				"fn:doc-available($uri  as xs:string?) as  xs:boolean",
				"fn:document-uri($arg  as node()?) as  xs:anyURI?",
				"op:duration-equal($arg1  as xs:duration, $arg2 as xs:duration)  as xs:boolean",
				"fn:empty($arg as  item()*) as xs:boolean",
				"fn:encode-for-uri($uri-part  as xs:string?) as  xs:string",
				"fn:ends-with($arg1  as xs:string?, $arg2 as xs:string?)  as xs:boolean",
				"fn:ends-with($arg1  as xs:string?, $arg2 as xs:string?, $collation as xs:string) as xs:boolean",
				"fn:error() as  none",
				"fn:error($error as  xs:QName) as none",
				"fn:error($error as  xs:QName?, $description as xs:string) as none",
				"fn:error($error as  xs:QName?, $description as xs:string, $error-object as item()*) as none",
				"fn:escape-html-uri($uri  as xs:string?) as  xs:string",
				"fn:exactly-one($arg  as item()*) as  item()",
				"op:except($parameter1  as node()*, $parameter2 as node()*)  as node()*",
				"fn:exists($arg as  item()*) as xs:boolean",
				"fn:false() as  xs:boolean",
				"fn:floor($arg as  numeric?) as numeric?",
				"op:gDay-equal($arg1  as xs:gDay, $arg2 as xs:gDay) as xs:boolean",
				"op:gMonth-equal($arg1  as xs:gMonth, $arg2 as xs:gMonth)  as xs:boolean",
				"op:gMonthDay-equal($arg1  as xs:gMonthDay, $arg2 as xs:gMonthDay)  as xs:boolean",
				"op:gYear-equal($arg1  as xs:gYear, $arg2 as xs:gYear)  as xs:boolean",
				"op:gYearMonth-equal( $arg1 as xs:gYearMonth, $arg2 as xs:gYearMonth) as xs:boolean",
				"op:hexBinary-equal($value1  as xs:hexBinary, $value2 as xs:hexBinary)  as xs:boolean",
				"fn:hours-from-dateTime( $arg as xs:dateTime?)  as xs:integer?",
				"fn:hours-from-duration( $arg as xs:duration?)  as xs:integer?",
				"fn:hours-from-time($arg  as xs:time?) as  xs:integer?",
				"fn:id($arg as  xs:string*) as element()*",
				"fn:id($arg as  xs:string*, $node as node()) as element()*",
				"fn:idref($arg as  xs:string*) as node()*",
				"fn:idref($arg as  xs:string*, $node as node()) as node()*",
				"fn:implicit-timezone()  as xs:dayTimeDuration",
				"fn:in-scope-prefixes( $element as element()) as xs:string*",
				"fn:index-of($seqParam  as xs:anyAtomicType*, $srchParam as xs:anyAtomicType) as xs:integer*",
				"fn:index-of($seqParam  as xs:anyAtomicType*, $srchParam as xs:anyAtomicType, $collation as xs:string)  as xs:integer*",
				"fn:insert-before($target  as item()*, $position as xs:integer, $inserts as item()*)  as item()*",
				"op:intersect($parameter1  as node()*, $parameter2 as node()*)  as node()*",
				"fn:iri-to-uri($iri  as xs:string?) as  xs:string",
				"op:is-same-node($parameter1  as node(), $parameter2 as node())  as xs:boolean",
				"fn:lang($testlang as  xs:string?) as xs:boolean",
				"fn:lang($testlang as  xs:string?, $node as node())  as xs:boolean",
				"fn:last() as  xs:integer",
				"fn:local-name() as  xs:string",
				"fn:local-name($arg  as node()?) as  xs:string",
				"fn:local-name-from-QName( $arg as xs:QName?) as  xs:NCName?",
				"fn:lower-case($arg  as xs:string?) as  xs:string",
				"fn:matches($input  as xs:string?, $pattern as xs:string)  as xs:boolean",
				"fn:matches($input  as xs:string?, $pattern as xs:string, $flags as xs:string)  as xs:boolean",
				"fn:max($arg as  xs:anyAtomicType*) as xs:anyAtomicType?",
				"fn:max($arg as  xs:anyAtomicType*, $collation as string)  as xs:anyAtomicType?",
				"fn:min($arg as  xs:anyAtomicType*) as xs:anyAtomicType?",
				"fn:min($arg as  xs:anyAtomicType*, $collation as string)  as xs:anyAtomicType?",
				"fn:minutes-from-dateTime( $arg as xs:dateTime?)  as xs:integer?",
				"fn:minutes-from-duration( $arg as xs:duration?)  as xs:integer?",
				"fn:minutes-from-time( $arg as xs:time?) as  xs:integer?",
				"fn:month-from-date($arg  as xs:date?) as  xs:integer?",
				"fn:month-from-dateTime( $arg as xs:dateTime?)  as xs:integer?",
				"fn:months-from-duration( $arg as xs:duration?)  as xs:integer?",
				"op:multiply-dayTimeDuration( $arg1 as xs:dayTimeDuration, $arg2 as xs:double)  as xs:dayTimeDuration",
				"op:multiply-yearMonthDuration( $arg1 as xs:yearMonthDuration, $arg2 as xs:double)  as xs:yearMonthDuration",
				"fn:name() as  xs:string",
				"fn:name($arg as  node()?) as xs:string",
				"fn:namespace-uri() as  xs:anyURI",
				"fn:namespace-uri($arg  as node()?) as  xs:anyURI",
				"fn:namespace-uri-for-prefix( $prefix as xs:string?, $element as element())  as xs:anyURI?",
				"fn:namespace-uri-from-QName( $arg as xs:QName?) as  xs:anyURI?",
				"fn:nilled($arg as  node()?) as xs:boolean?",
				"op:node-after($parameter1  as node(), $parameter2 as node())  as xs:boolean",
				"op:node-before($parameter1  as node(), $parameter2 as node())  as xs:boolean",
				"fn:node-name($arg  as node()?) as  xs:QName?",
				"fn:normalize-space() as  xs:string",
				"fn:normalize-space($arg  as xs:string?) as  xs:string",
				"fn:normalize-unicode( $arg as xs:string?)  as xs:string",
				"fn:normalize-unicode( $arg as xs:string?, $normalizationForm as xs:string)  as xs:string",
				"fn:not($arg as  item()*) as xs:boolean",
				"op:NOTATION-equal($arg1  as xs:NOTATION, $arg2 as xs:NOTATION)  as xs:boolean",
				"fn:number() as  xs:double",
				"fn:number($arg as  xs:anyAtomicType?) as xs:double",
				"op:numeric-add($arg1  as numeric, $arg2 as numeric)  as numeric",
				"op:numeric-divide($arg1  as numeric, $arg2 as numeric)  as numeric",
				"op:numeric-equal($arg1  as numeric, $arg2 as numeric)  as xs:boolean",
				"op:numeric-greater-than( $arg1 as numeric, $arg2 as numeric)  as xs:boolean",
				"op:numeric-integer-divide( $arg1 as numeric, $arg2 as numeric)  as xs:integer",
				"op:numeric-less-than( $arg1 as numeric, $arg2 as numeric)  as xs:boolean",
				"op:numeric-mod($arg1  as numeric, $arg2 as numeric)  as numeric",
				"op:numeric-multiply( $arg1 as numeric, $arg2 as numeric)  as numeric",
				"op:numeric-subtract( $arg1 as numeric, $arg2 as numeric)  as numeric",
				"op:numeric-unary-minus( $arg as numeric) as  numeric",
				"op:numeric-unary-plus( $arg as numeric) as  numeric",
				"fn:one-or-more($arg  as item()*) as  item()+",
				"fn:position() as  xs:integer",
				"fn:prefix-from-QName( $arg as xs:QName?) as  xs:NCName?",
				"fn:QName($paramURI  as xs:string?, $paramQName as xs:string)  as xs:QName",
				"op:QName-equal($arg1  as xs:QName, $arg2 as xs:QName)  as xs:boolean",
				"fn:remove($target  as item()*, $position as xs:integer)  as item()*",
				"fn:replace($input  as xs:string?, $pattern as xs:string, $replacement as xs:string)  as xs:string",
				"fn:replace($input  as xs:string?, $pattern as xs:string, $replacement as xs:string, $flags as xs:string)  as xs:string",
				"fn:resolve-QName($qname  as xs:string?, $element as element())  as xs:QName?",
				"fn:resolve-uri($relative  as xs:string?) as  xs:anyURI?",
				"fn:resolve-uri($relative  as xs:string?, $base as xs:string)  as xs:anyURI?",
				"fn:reverse($arg as  item()*) as item()*",
				"fn:root() as  node()",
				"fn:root($arg as  node()?) as node()?",
				"fn:round($arg as  numeric?) as numeric?",
				"fn:round-half-to-even( $arg as numeric?) as  numeric?",
				"fn:round-half-to-even( $arg as numeric?, $precision as xs:integer)  as numeric?",
				"fn:seconds-from-dateTime( $arg as xs:dateTime?)  as xs:decimal?",
				"fn:seconds-from-duration( $arg as xs:duration?)  as xs:decimal?",
				"fn:seconds-from-time( $arg as xs:time?) as  xs:decimal?",
				"fn:starts-with($arg1  as xs:string?, $arg2 as xs:string?)  as xs:boolean",
				"fn:starts-with($arg1  as xs:string?, $arg2 as xs:string?, $collation as xs:string)  as xs:boolean",
				"fn:static-base-uri() as  xs:anyURI?",
				"fn:string() as  xs:string",
				"fn:string($arg as  item()?) as xs:string",
				"fn:string-join($arg1  as xs:string*, $arg2 as xs:string)  as xs:string",
				"fn:string-length() as  xs:integer",
				"fn:string-length($arg  as xs:string?) as  xs:integer",
				"fn:string-to-codepoints( $arg as xs:string?)  as xs:integer*",
				"fn:subsequence($sourceSeq  as item()*, $startingLoc as xs:double)  as item()*",
				"fn:subsequence($sourceSeq  as item()*, $startingLoc as xs:double, $length as xs:double)  as item()*",
				"fn:substring($sourceString  as xs:string?, $startingLoc as xs:double)  as xs:string",
				"fn:substring($sourceString  as xs:string?, $startingLoc as xs:double, $length as xs:double)  as xs:string",
				"fn:substring-after($arg1  as xs:string?, $arg2 as xs:string?)  as xs:string",
				"fn:substring-after($arg1  as xs:string?, $arg2 as xs:string?, $collation as xs:string)  as xs:string",
				"fn:substring-before( $arg1 as xs:string?, $arg2 as xs:string?)  as xs:string",
				"fn:substring-before( $arg1 as xs:string?, $arg2 as xs:string?, $collation as xs:string)  as xs:string",
				"op:subtract-dates($arg1  as xs:date, $arg2 as xs:date)  as xs:dayTimeDuration?",
				"op:subtract-dateTimes( $arg1 as xs:dateTime, $arg2 as xs:dateTime)  as xs:dayTimeDuration?",
				"op:subtract-dayTimeDuration-from-date( $arg1 as xs:date, $arg2 as xs:dayTimeDuration)  as xs:date",
				"op:subtract-dayTimeDuration-from-dateTime( $arg1 as xs:dateTime, $arg2 as xs:dayTimeDuration)  as xs:dateTime",
				"op:subtract-dayTimeDuration-from-time( $arg1 as xs:time, $arg2 as xs:dayTimeDuration)  as xs:time",
				"op:subtract-dayTimeDurations( $arg1 as xs:dayTimeDuration, $arg2 as xs:dayTimeDuration)  as xs:dayTimeDuration",
				"op:subtract-times($arg1  as xs:time, $arg2 as xs:time)  as xs:dayTimeDuration",
				"op:subtract-yearMonthDuration-from-date( $arg1 as xs:date, $arg2 as xs:yearMonthDuration)  as xs:date",
				"op:subtract-yearMonthDuration-from-dateTime( $arg1 as xs:dateTime, $arg2 as xs:yearMonthDuration)  as xs:dateTime",
				"op:subtract-yearMonthDurations( $arg1 as xs:yearMonthDuration, $arg2 as xs:yearMonthDuration)  as xs:yearMonthDuration",
				"fn:sum($arg as  xs:anyAtomicType*) as xs:anyAtomicType",
				"fn:sum($arg as  xs:anyAtomicType*, $zero as xs:anyAtomicType?)  as xs:anyAtomicType?",
				"op:time-equal($arg1  as xs:time, $arg2 as xs:time)  as xs:boolean",
				"op:time-greater-than( $arg1 as xs:time, $arg2 as xs:time)  as xs:boolean",
				"op:time-less-than($arg1  as xs:time, $arg2 as xs:time)  as xs:boolean",
				"fn:timezone-from-date( $arg as xs:date?) as  xs:dayTimeDuration?",
				"fn:timezone-from-dateTime( $arg as xs:dateTime?)  as xs:dayTimeDuration?",
				"fn:timezone-from-time( $arg as xs:time?) as  xs:dayTimeDuration?",
				"op:to($firstval as  xs:integer, $lastval as xs:integer)  as xs:integer*",
				"fn:tokenize($input  as xs:string?, $pattern as xs:string)  as xs:string*",
				"fn:tokenize($input  as xs:string?, $pattern as xs:string, $flags as xs:string) as xs:string*",
				"fn:trace($value as  item()*, $label as xs:string)  as item()*",
				"fn:translate($arg  as xs:string?, $mapString as xs:string, $transString as xs:string)  as xs:string",
				"fn:true() as  xs:boolean",
				"op:union($parameter1  as node()*, $parameter2 as node()*)  as node()*",
				"fn:unordered($sourceSeq  as item()*) as  item()*",
				"fn:upper-case($arg  as xs:string?) as  xs:string",
				"fn:year-from-date($arg  as xs:date?) as  xs:integer?",
				"fn:year-from-dateTime( $arg as xs:dateTime?)  as xs:integer?",
				"op:yearMonthDuration-greater-than( $arg1 as xs:yearMonthDuration, $arg2 as xs:yearMonthDuration)  as xs:boolean",
				"op:yearMonthDuration-less-than( $arg1 as xs:yearMonthDuration, $arg2 as xs:yearMonthDuration)  as xs:boolean",
				"fn:years-from-duration( $arg as xs:duration?)  as xs:integer?",
				"fn:zero-or-one($arg  as item()*) as  item()?",
	};
		
	public static String[] functions = null;	

	static {
		ArrayList al = new ArrayList();
		for ( int i = 0; i < raw_functions.length; i++ ) {
			String f = raw_functions[ i ];
			int n = f.indexOf( "(" );
			String ff = f.substring( 0, n ); 
			al.add( ff );
			int m = ff.indexOf( ":" );
			al.add( ff.substring( m + 1 ) );
		}

		functions = new String[ al.size() ];
		for ( int i = 0; i < al.size(); i++ )
			functions[ i ] = ( String )al.get( i );
		
	}

	public static String[] axes = {
		"ancestor",	
		"ancestor-or-self",	
		"attribute",	
		"child",	
		"descendant",	
		"descendant-or-self",	
		"following",	
		"following-sibling",	
		"namespace",	
		"parent",	
		"preceding",	
		"preceding-sibling",	
		"self"		
	};

}


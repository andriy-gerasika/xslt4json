/*--------------------------------------------------------------------------
 *  Copyright 2007 Taro L. Saito
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *--------------------------------------------------------------------------*/
//--------------------------------------
// XerialJ Project
//
// JSON.g
// Since: Apr 26, 2007
//
//--------------------------------------
 
grammar JSON;

options
{
	language=Java;
	output=AST;
//	charVocabulary='\u0000'..'\uFFFE';
}
   
tokens
{
	XML_ELEMENT;
}

@lexer::header
{
//--------------------------------------
// XerialJ Project
//
// JSONLexer.java
// Since: Apr 26, 2007
//
//--------------------------------------
package org.xerial.json.impl;
}

@header
{
//--------------------------------------
// Xerial Project
//
// JSONParser.java
// Since: Apr 26, 2007
//
//--------------------------------------
package org.xerial.json.impl;
}

// lexer rules
fragment Dot: '.';
NULL: 'null';

fragment Digit: '0' .. '9';
fragment HexDigit: ('0' .. '9' | 'A' .. 'F' | 'a' .. 'f');
fragment UnicodeChar: ~('"'| '\\');
fragment StringChar :  UnicodeChar | EscapeSequence;

fragment EscapeSequence
	: '\\' ('\"' | '\\' | '/' | 'b' | 'f' | 'n' | 'r' | 't' | 'u' HexDigit HexDigit HexDigit HexDigit)
	;

fragment Int: '-'? ('0' | '1'..'9' Digit*);
fragment Frac: Dot Digit+;
fragment Exp: ('e' | 'E') ('+' | '-')? Digit+;

WhiteSpace: (' ' | '\r' | '\t' | '\u000C' | '\n') { $channel=HIDDEN; };

String: '"' StringChar* '"';
Integer: Int;
Double:  Int (Frac Exp? | Exp);
Boolean: 'false' | 'true';

// parser rules
json
	: value -> ^(XML_ELEMENT["json"] value)
	;

object
	: '{' (element (',' element)*)? '}'
	  -> ^(XML_ELEMENT["object"] element*)
	;
	
element
	: String ':' value
	  -> ^(XML_ELEMENT["element"] String value)
	;	
	
array
	: '[' value (',' value)* ']'
	  -> ^(XML_ELEMENT["array"] value+)
	;

	
value
	: String -> ^(XML_ELEMENT["string"] String)
	| Integer -> ^(XML_ELEMENT["integer"] Integer)
	| Double -> ^(XML_ELEMENT["double"] Double)
	| Boolean -> ^(XML_ELEMENT["boolean"] Boolean)
	| object  
	| array  
	| NULL
	;


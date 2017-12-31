//### This file created by BYACC 1.8(/Java extension  1.1)
//### Java capabilities added 7 Jan 97, Bob Jamison
//### Updated : 27 Nov 97  -- Bob Jamison, Joe Nieten
//###           01 Jan 98  -- Bob Jamison -- fixed generic semantic constructor
//###           01 Jun 99  -- Bob Jamison -- added Runnable support
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
//###           06 Aug 00  -- Bob Jamison -- made state variables class-global
//###           03 Jan 01  -- Bob Jamison -- improved flags, tracing
//###           16 May 01  -- Bob Jamison -- added custom stack sizing
//### Please send bug reports to rjamison@lincom-asg.com
//### static char yysccsid[] = "@(#)yaccpar	1.8 (Berkeley) 01/20/90";



package com.japisoft.xpath;



//#line 2 "XpathParser.y"
  import java.io.*;
//#line 17 "XPathParser.java"




/**
 * Encapsulates yacc() parser functionality in a Java
 *        class for quick code development
 */
public class XPathParser
{

boolean yydebug;        //do I want debug output?
int yynerrs;            //number of errors so far
int yyerrflag;          //was there an error?
int yychar;             //the current working character

//########## MESSAGES ##########
//###############################################################
// method: debug
//###############################################################
void debug(String msg)
{
  if (yydebug)
    System.out.println(msg);
}

//########## STATE STACK ##########
final static int YYSTACKSIZE = 500;  //maximum stack size
int statestk[],stateptr;           //state stack
int stateptrmax;                     //highest index of stackptr
int statemax;                        //state when highest index reached
//###############################################################
// methods: state stack push,pop,drop,peek
//###############################################################
void state_push(int state)
{
  if (stateptr>=YYSTACKSIZE)         //overflowed?
    return;
  statestk[++stateptr]=state;
  if (stateptr>statemax)
    {
    statemax=state;
    stateptrmax=stateptr;
    }
}
int state_pop()
{
  if (stateptr<0)                    //underflowed?
    return -1;
  return statestk[stateptr--];
}
void state_drop(int cnt)
{
int ptr;
  ptr=stateptr-cnt;
  if (ptr<0)
    return;
  stateptr = ptr;
}
int state_peek(int relative)
{
int ptr;
  ptr=stateptr-relative;
  if (ptr<0)
    return -1;
  return statestk[ptr];
}
//###############################################################
// method: init_stacks : allocate and prepare stacks
//###############################################################
boolean init_stacks()
{
  statestk = new int[YYSTACKSIZE];
  stateptr = -1;
  statemax = -1;
  stateptrmax = -1;
  val_init();
  return true;
}
//###############################################################
// method: dump_stacks : show n levels of the stacks
//###############################################################
void dump_stacks(int count)
{
int i;
  System.out.println("=index==state====value=     s:"+stateptr+"  v:"+valptr);
  for (i=0;i<count;i++)
    System.out.println(" "+i+"    "+statestk[i]+"      "+valstk[i]);
  System.out.println("======================");
}


//########## SEMANTIC VALUES ##########
//public class XPathParserVal is defined in XPathParserVal.java


String   yytext;//user variable to return contextual strings
XPathParserVal yyval; //used to return semantic vals from action routines
XPathParserVal yylval;//the 'lval' (result) I got from yylex()
XPathParserVal valstk[];
int valptr;
//###############################################################
// methods: value stack push,pop,drop,peek.
//###############################################################
void val_init()
{
  valstk=new XPathParserVal[YYSTACKSIZE];
  yyval=new XPathParserVal(0);
  yylval=new XPathParserVal(0);
  valptr=-1;
}
void val_push(XPathParserVal val)
{
  if (valptr>=YYSTACKSIZE)
    return;
  valstk[++valptr]=val;
}
XPathParserVal val_pop()
{
  if (valptr<0)
    return new XPathParserVal(-1);
  return valstk[valptr--];
}
void val_drop(int cnt)
{
int ptr;
  ptr=valptr-cnt;
  if (ptr<0)
    return;
  valptr = ptr;
}
XPathParserVal val_peek(int relative)
{
int ptr;
  ptr=valptr-relative;
  if (ptr<0)
    return new XPathParserVal(-1);
  return valstk[ptr];
}
//#### end semantic value section ####
public final static short LITERAL=257;
public final static short AXIS_NAME=258;
public final static short PI=259;
public final static short NODE_TYPE=260;
public final static short TEXT_TYPE=261;
public final static short NUMBER=262;
public final static short NAME=263;
public final static short AXIS_SEP=264;
public final static short OR=265;
public final static short AND=266;
public final static short SUPE=267;
public final static short INFE=268;
public final static short DIFF=269;
public final static short DS=270;
public final static short DP=271;
public final static short NEG=272;
public final static short YYERRCODE=256;
final static short yylhs[] = {                           -1,
    0,    0,    1,    1,    4,    4,    4,    3,    7,    3,
    3,    8,    5,    6,    6,    6,    9,    9,   13,   12,
   12,   11,   11,   10,   10,   10,   10,   10,   15,   15,
   15,   15,   14,   16,    2,   17,   17,   18,   18,   19,
   19,   20,   20,   20,   21,   21,   21,   21,   21,   22,
   22,   22,   23,   23,   23,   24,   24,   25,   25,   25,
   25,   26,   26,   27,   27,   27,   27,   27,   28,   28,
   29,   30,   30,
};
final static short yylen[] = {                            2,
    1,    1,    1,    1,    1,    2,    1,    1,    0,    4,
    1,    3,    2,    3,    2,    1,    2,    1,    1,    1,
    1,    0,    2,    1,    1,    3,    4,    3,    1,    1,
    3,    3,    3,    1,    1,    1,    3,    1,    3,    1,
    3,    1,    3,    3,    1,    3,    3,    3,    3,    1,
    3,    3,    1,    3,    3,    1,    2,    1,    1,    3,
    3,    1,    2,    2,    3,    1,    1,    1,    4,    3,
    1,    1,    3,
};
final static short yydefred[] = {                         0,
   66,    0,    0,    0,   25,   67,    0,    0,   21,    0,
    0,   20,   29,   19,    0,    0,    0,    0,    2,    0,
    4,    7,    8,   11,    0,    0,   16,   18,   24,    0,
    0,    0,    0,    0,    0,    0,   53,   56,    0,   62,
   68,    0,   17,    0,    0,    0,    0,    0,   64,    0,
   59,    0,   57,    0,    0,    0,    0,   15,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,   63,    0,    0,   28,   26,   31,
   32,   65,   12,    9,   14,   34,    0,   23,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,   55,
   54,    0,    0,   70,    0,    0,   27,   10,   33,    0,
   69,   73,
};
final static short yydgoto[] = {                         17,
   51,  105,   20,   21,   22,   23,  108,   24,   25,   26,
   58,   27,   28,   59,   29,   87,   30,   31,   32,   33,
   34,   35,   36,   37,   38,   39,   40,   41,   42,  106,
};
final static short yysindex[] = {                       -25,
    0, -258,  -30,  -13,    0,    0,  -22,  338,    0, -230,
  355,    0,    0,    0,  -25,  -25,    0,    0,    0,  -43,
    0,    0,    0,    0,   -4,  -32,    0,    0,    0,  -87,
 -225, -211,  -47,  -28,  -19,  -42,    0,    0,  -44,    0,
    0,   34,    0,  -33,   40,  -35,  -22,  -43,    0,  -43,
    0,   57,    0,  338,  338,  -32,  -25,    0,  -32,  -25,
  -25,  -25,  -25,  -25,  -25,  -25,  -25,  -25,  -25,  -25,
  -25,  -25,  338,  338,    0,  540,   62,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    6,    0, -225, -211,
  -47,  -28,  -28,  -19,  -19,  -19,  -19,  -42,  -42,    0,
    0,  -43,  -43,    0,   63,   65,    0,    0,    0,  -25,
    0,    0,
};
final static short yyrindex[] = {                         0,
    0,    0,    0,    0,    0,    0,    1,    0,    0,    0,
  169,    0,    0,    0,    0,    0,    0,  252,    0,   23,
    0,    0,    0,    0,    0,   35,    0,    0,    0,   31,
  411,   16,  482,  450,  400,  280,    0,    0,   93,    0,
    0,    0,    0,    0,    0,    0,    9,  101,    0,  123,
    0,    0,    0,    0,    0,   35,    0,    0,   35,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,  559,  468,
  484,  469,  496,  405,  410,  439,  445,  285,  371,    0,
    0,  130,  158,    0,   67,    0,    0,    0,    0,    0,
    0,    0,
};
final static short yygindex[] = {                         0,
  110,   58,   17,    0,    0,  -36,    0,    0,    0,   86,
  -54,    0,    0,   73,    0,    0,    0,   53,   56,   52,
  -51,   21,  -40,   33,    0,    0,    0,    0,    0,    8,
};
final static int YYTABLESIZE=811;
final static short yytable[] = {                         72,
   30,   85,   74,   55,   88,   43,   81,   78,   30,   44,
   10,   92,   93,   64,   15,   38,   13,   83,   84,   16,
   12,   11,    3,   69,   48,   70,   45,   50,   98,   99,
   35,   67,   49,   68,   22,   46,   60,   13,   14,   61,
   71,   30,   30,   30,   30,   30,   57,   30,   53,   30,
   30,   30,   30,   30,   62,   30,   38,   19,   57,   38,
   30,   30,   30,    3,    3,    3,    3,    3,   30,   30,
   30,   35,   52,   76,   35,   22,   22,   22,   22,   22,
   79,   22,    3,    3,    3,   94,   95,   96,   97,  102,
  103,   30,   58,   30,   22,   22,   22,   82,  109,   30,
   13,   30,  107,  100,  101,  111,  110,   72,   38,   18,
   56,   75,   89,   91,   86,    3,   90,  112,    0,    0,
    0,    0,    6,   35,   30,    0,    0,   22,    0,   60,
    0,    0,   30,   58,   58,   58,   58,   58,    0,   38,
    0,   13,   13,   13,   13,   13,    3,    0,    0,    0,
    0,    0,   58,   58,   58,    0,    0,   61,   22,    0,
   13,   13,   13,    6,    6,    6,    6,    6,    5,    0,
   60,   60,   60,   60,   60,    0,    0,    0,    0,    0,
    0,    0,    6,    6,    6,   58,    0,    0,    0,   60,
   60,   60,    0,   13,    0,    0,    0,    0,   61,   61,
   61,   61,   61,    0,    0,    0,    0,    0,    0,    5,
    0,    5,    5,    5,    0,    6,   58,   61,   61,   61,
   71,   63,   60,   77,   13,   73,   54,   80,    5,    5,
    5,    1,    2,    3,    4,    5,    6,    7,   65,   66,
    0,    0,    0,    0,    8,    9,    6,    0,    0,    0,
   61,    1,    0,   60,    3,    4,    5,    0,   47,    0,
    0,    5,    0,   30,    0,   30,   30,   30,   30,   30,
   30,   30,    0,   30,   30,   30,   30,   30,   30,   50,
   38,   61,    0,    0,   51,    3,    0,    3,    3,    3,
    3,    3,    5,   59,   59,    0,   59,   22,    0,   22,
   22,   22,   22,   22,   22,    0,    0,    0,    0,    0,
    0,   59,   59,   59,    0,    0,    0,    0,    0,    0,
   50,    0,   50,   50,   50,   51,    0,   51,   51,   51,
    0,    0,    0,    0,    0,    0,    0,    0,    0,   50,
   50,   50,    0,    0,   51,   51,   51,    0,    0,    0,
    0,    0,    0,    0,    0,   58,    0,   58,   58,   58,
   58,   58,    0,   13,    0,   13,   13,   13,   13,   13,
   52,    0,   50,    0,    0,   59,    0,   51,    0,   13,
    0,    0,    0,   12,    0,    6,    0,    6,    6,    6,
    6,    6,   60,    0,   60,   60,   60,   60,   60,   45,
   12,   14,    0,   50,   49,    0,    0,    0,   51,   48,
   36,   52,    0,   52,   52,   52,    0,    0,   14,    0,
   61,    0,   61,   61,   61,   61,   61,    0,    0,    0,
   52,   52,   52,    5,    5,    5,    5,    5,   46,    0,
   45,    0,    0,   45,   47,   49,    0,    0,   49,   42,
   48,   36,    0,   48,   36,    0,    0,    0,    0,   45,
   45,   45,    0,   52,   49,   49,   49,   39,   44,   48,
   48,   48,    0,    0,    0,    0,    0,    0,    0,   46,
    0,   40,   46,   41,    0,   47,    0,    0,   47,    0,
   42,    0,   45,   42,   52,   43,    0,   49,   46,   46,
   46,    0,   48,   36,   47,   47,   47,    0,   39,   44,
   42,   39,   44,    0,   59,    0,   59,   59,   59,   59,
   59,    0,   40,   45,   41,   40,    0,   41,   49,   44,
    0,   46,    0,   48,   36,    0,   43,   47,    0,   43,
    0,    0,   42,    0,   50,   50,   50,   50,   50,   51,
   51,   51,   51,   51,    0,    0,   43,    0,   37,    0,
   39,   44,   46,    0,    0,    0,    0,    0,   47,    0,
    0,    0,    0,   42,   40,   10,   41,    0,    0,   15,
  104,   13,    0,    0,   16,   12,   11,    0,   43,    0,
    0,   39,   44,    0,    0,    2,    3,    4,    5,   37,
   47,    0,   37,   14,    0,   40,    0,   41,    9,    0,
    0,    0,    2,    3,    4,    5,    0,   47,    0,   43,
    0,    0,    0,    0,    0,    9,    0,    0,    0,    0,
    0,    0,    0,    0,    0,   52,   52,   52,   52,   52,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,   37,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,   45,   45,   45,   45,   45,   49,
   49,   49,   49,   49,   48,   48,   48,   48,   48,    0,
    0,    0,   37,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,   46,   46,   46,   46,   46,    0,   47,
   47,   47,   47,   47,   42,   42,    0,    0,   42,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,   39,   44,   44,    0,    0,   44,    0,    0,
    0,    0,    0,    0,    0,    0,   40,   40,   41,   41,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
   43,   43,    0,    0,   43,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    1,    2,    3,    4,
    5,    6,    7,    0,    0,    0,    0,    0,    0,    8,
    9,
};
final static short yycheck[] = {                         42,
    0,   56,   47,   47,   59,  264,   42,   41,    0,   40,
   36,   63,   64,   61,   40,    0,   42,   54,   55,   45,
   46,   47,    0,   43,    8,   45,   40,   11,   69,   70,
    0,   60,  263,   62,    0,   58,  124,   42,   64,  265,
   40,   41,   42,   43,   44,   45,   91,   47,   16,   41,
   42,   43,   44,   45,  266,   47,   41,    0,   91,   44,
   60,   61,   62,   41,   42,   43,   44,   45,   60,   61,
   62,   41,   15,   40,   44,   41,   42,   43,   44,   45,
   41,   47,   60,   61,   62,   65,   66,   67,   68,   73,
   74,   91,    0,   93,   60,   61,   62,   41,   93,   91,
    0,   93,   41,   71,   72,   41,   44,   41,   93,    0,
   25,   39,   60,   62,   57,   93,   61,  110,   -1,   -1,
   -1,   -1,    0,   93,  124,   -1,   -1,   93,   -1,    0,
   -1,   -1,  124,   41,   42,   43,   44,   45,   -1,  124,
   -1,   41,   42,   43,   44,   45,  124,   -1,   -1,   -1,
   -1,   -1,   60,   61,   62,   -1,   -1,    0,  124,   -1,
   60,   61,   62,   41,   42,   43,   44,   45,    0,   -1,
   41,   42,   43,   44,   45,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   60,   61,   62,   93,   -1,   -1,   -1,   60,
   61,   62,   -1,   93,   -1,   -1,   -1,   -1,   41,   42,
   43,   44,   45,   -1,   -1,   -1,   -1,   -1,   -1,   41,
   -1,   43,   44,   45,   -1,   93,  124,   60,   61,   62,
  263,  269,   93,  257,  124,  270,  270,  263,   60,   61,
   62,  257,  258,  259,  260,  261,  262,  263,  267,  268,
   -1,   -1,   -1,   -1,  270,  271,  124,   -1,   -1,   -1,
   93,    0,   -1,  124,  259,  260,  261,   -1,  263,   -1,
   -1,   93,   -1,  263,   -1,  265,  266,  267,  268,  269,
  270,  263,   -1,  265,  266,  267,  268,  269,  270,    0,
  265,  124,   -1,   -1,    0,  263,   -1,  265,  266,  267,
  268,  269,  124,   42,   43,   -1,   45,  263,   -1,  265,
  266,  267,  268,  269,  270,   -1,   -1,   -1,   -1,   -1,
   -1,   60,   61,   62,   -1,   -1,   -1,   -1,   -1,   -1,
   41,   -1,   43,   44,   45,   41,   -1,   43,   44,   45,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   60,
   61,   62,   -1,   -1,   60,   61,   62,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,  263,   -1,  265,  266,  267,
  268,  269,   -1,  263,   -1,  265,  266,  267,  268,  269,
    0,   -1,   93,   -1,   -1,  124,   -1,   93,   -1,   42,
   -1,   -1,   -1,   46,   -1,  263,   -1,  265,  266,  267,
  268,  269,  263,   -1,  265,  266,  267,  268,  269,    0,
   46,   64,   -1,  124,    0,   -1,   -1,   -1,  124,    0,
    0,   41,   -1,   43,   44,   45,   -1,   -1,   64,   -1,
  263,   -1,  265,  266,  267,  268,  269,   -1,   -1,   -1,
   60,   61,   62,  265,  266,  267,  268,  269,    0,   -1,
   41,   -1,   -1,   44,    0,   41,   -1,   -1,   44,    0,
   41,   41,   -1,   44,   44,   -1,   -1,   -1,   -1,   60,
   61,   62,   -1,   93,   60,   61,   62,    0,    0,   60,
   61,   62,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   41,
   -1,    0,   44,    0,   -1,   41,   -1,   -1,   44,   -1,
   41,   -1,   93,   44,  124,    0,   -1,   93,   60,   61,
   62,   -1,   93,   93,   60,   61,   62,   -1,   41,   41,
   61,   44,   44,   -1,  263,   -1,  265,  266,  267,  268,
  269,   -1,   41,  124,   41,   44,   -1,   44,  124,   61,
   -1,   93,   -1,  124,  124,   -1,   41,   93,   -1,   44,
   -1,   -1,   93,   -1,  265,  266,  267,  268,  269,  265,
  266,  267,  268,  269,   -1,   -1,   61,   -1,    0,   -1,
   93,   93,  124,   -1,   -1,   -1,   -1,   -1,  124,   -1,
   -1,   -1,   -1,  124,   93,   36,   93,   -1,   -1,   40,
   41,   42,   -1,   -1,   45,   46,   47,   -1,   93,   -1,
   -1,  124,  124,   -1,   -1,  258,  259,  260,  261,   41,
  263,   -1,   44,   64,   -1,  124,   -1,  124,  271,   -1,
   -1,   -1,  258,  259,  260,  261,   -1,  263,   -1,  124,
   -1,   -1,   -1,   -1,   -1,  271,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,  265,  266,  267,  268,  269,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   93,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,  265,  266,  267,  268,  269,  265,
  266,  267,  268,  269,  265,  266,  267,  268,  269,   -1,
   -1,   -1,  124,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,  265,  266,  267,  268,  269,   -1,  265,
  266,  267,  268,  269,  265,  266,   -1,   -1,  269,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,  265,  265,  266,   -1,   -1,  269,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,  265,  266,  265,  266,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
  265,  266,   -1,   -1,  269,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,  257,  258,  259,  260,
  261,  262,  263,   -1,   -1,   -1,   -1,   -1,   -1,  270,
  271,
};
final static short YYFINAL=17;
final static short YYMAXTOKEN=272;
final static String yyname[] = {
"end-of-file",null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,"'$'",null,null,null,"'('","')'","'*'","'+'",
"','","'-'","'.'","'/'",null,null,null,null,null,null,null,null,null,null,"':'",
null,"'<'","'='","'>'",null,"'@'",null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,"'['",null,"']'",null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,"'|'",null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,"LITERAL","AXIS_NAME","PI",
"NODE_TYPE","TEXT_TYPE","NUMBER","NAME","AXIS_SEP","OR","AND","SUPE","INFE",
"DIFF","DS","DP","NEG",
};
final static String yyrule[] = {
"$accept : input",
"input : locationPath",
"input : expr",
"locationPath : relativeLocationPath",
"locationPath : absoluteLocationPath",
"absoluteLocationPath : '/'",
"absoluteLocationPath : '/' relativeLocationPath",
"absoluteLocationPath : abbreviatedAbsoluteLocationPath",
"relativeLocationPath : step",
"$$1 :",
"relativeLocationPath : relativeLocationPath '/' step $$1",
"relativeLocationPath : abbreviatedRelativeLocationPath",
"abbreviatedRelativeLocationPath : relativeLocationPath DS step",
"abbreviatedAbsoluteLocationPath : DS relativeLocationPath",
"step : axisSpecifier nodeTest predicates",
"step : nodeTest predicates",
"step : abbreviatedStep",
"axisSpecifier : AXIS_NAME AXIS_SEP",
"axisSpecifier : abbreviatedAxisSpecifier",
"abbreviatedAxisSpecifier : '@'",
"abbreviatedStep : '.'",
"abbreviatedStep : DP",
"predicates :",
"predicates : predicate predicates",
"nodeTest : nameTest",
"nodeTest : TEXT_TYPE",
"nodeTest : NODE_TYPE '(' ')'",
"nodeTest : PI '(' LITERAL ')'",
"nodeTest : PI '(' ')'",
"nameTest : '*'",
"nameTest : NAME",
"nameTest : NAME ':' NAME",
"nameTest : NAME ':' '*'",
"predicate : '[' predicateExpr ']'",
"predicateExpr : expr",
"expr : unionExpr",
"unionExpr : orExpr",
"unionExpr : unionExpr '|' orExpr",
"orExpr : andExpr",
"orExpr : orExpr OR andExpr",
"andExpr : equalityExpr",
"andExpr : andExpr AND equalityExpr",
"equalityExpr : relationalExpr",
"equalityExpr : equalityExpr '=' relationalExpr",
"equalityExpr : equalityExpr DIFF relationalExpr",
"relationalExpr : additiveExpr",
"relationalExpr : relationalExpr '<' additiveExpr",
"relationalExpr : relationalExpr '>' additiveExpr",
"relationalExpr : relationalExpr INFE additiveExpr",
"relationalExpr : relationalExpr SUPE additiveExpr",
"additiveExpr : multiplicativeExpr",
"additiveExpr : additiveExpr '+' multiplicativeExpr",
"additiveExpr : additiveExpr '-' multiplicativeExpr",
"multiplicativeExpr : unaryExpr",
"multiplicativeExpr : multiplicativeExpr '*' unaryExpr",
"multiplicativeExpr : multiplicativeExpr NAME unaryExpr",
"unaryExpr : pathExpr",
"unaryExpr : '-' unaryExpr",
"pathExpr : filterExpr",
"pathExpr : locationPath",
"pathExpr : filterExpr DS relativeLocationPath",
"pathExpr : filterExpr '/' relativeLocationPath",
"filterExpr : primaryExpr",
"filterExpr : filterExpr predicate",
"primaryExpr : '$' NAME",
"primaryExpr : '(' expr ')'",
"primaryExpr : LITERAL",
"primaryExpr : NUMBER",
"primaryExpr : functionCall",
"functionCall : functionName '(' args ')'",
"functionCall : functionName '(' ')'",
"functionName : NAME",
"args : expr",
"args : expr ',' args",
};

//#line 159 "XpathParser.y"

  private XPathToken lexer;
  private XPathResolver resolver;

  public void setXPathResolver( XPathResolver resolver ) {
      this.resolver = resolver;
  }

  public int getCurrentPos() { return lexer.getCurrentPos(); }

  private int yylex () {
    int yyl_return = -1;
    try {
      yylval = new XPathParserVal(0);
      yyl_return = lexer.yylex();
    }
    catch (IOException e) {
      throw new RuntimeException( e.getMessage() );
    }
    return yyl_return;
  }

  public void yyerror (String error) {
    throw new RuntimeException( error );
  }

  public XPathParser(Reader r) {
    lexer = new XPathToken(r, this);
  }










//#line 504 "XPathParser.java"
//###############################################################
// method: yylexdebug : check lexer state
//###############################################################
void yylexdebug(int state,int ch)
{
String s=null;
  if (ch < 0) ch=0;
  if (ch <= YYMAXTOKEN) //check index bounds
     s = yyname[ch];    //now get it
  if (s==null)
    s = "illegal-symbol";
  debug("state "+state+", reading "+ch+" ("+s+")");
}





//The following are now global, to aid in error reporting
int yyn;       //next next thing to do
int yym;       //
int yystate;   //current parsing state from state table
String yys;    //current token string


//###############################################################
// method: yyparse : parse input and execute indicated items
//###############################################################
int yyparse()
{
boolean doaction;
  init_stacks();
  yynerrs = 0;
  yyerrflag = 0;
  yychar = -1;          //impossible char forces a read
  yystate=0;            //initial state
  state_push(yystate);  //save it
  while (true) //until parsing is done, either correctly, or w/error
    {
    doaction=true;
    if (yydebug) debug("loop"); 
    //#### NEXT ACTION (from reduction table)
    for (yyn=yydefred[yystate];yyn==0;yyn=yydefred[yystate])
      {
      if (yydebug) debug("yyn:"+yyn+"  state:"+yystate+"  yychar:"+yychar);
      if (yychar < 0)      //we want a char?
        {
        yychar = yylex();  //get next token
        if (yydebug) debug(" next yychar:"+yychar);
        //#### ERROR CHECK ####
        if (yychar < 0)    //it it didn't work/error
          {
          yychar = 0;      //change it to default string (no -1!)
          if (yydebug)
            yylexdebug(yystate,yychar);
          }
        }//yychar<0
      yyn = yysindex[yystate];  //get amount to shift by (shift index)
      if ((yyn != 0) && (yyn += yychar) >= 0 &&
          yyn <= YYTABLESIZE && yycheck[yyn] == yychar)
        {
        if (yydebug)
          debug("state "+yystate+", shifting to state "+yytable[yyn]);
        //#### NEXT STATE ####
        yystate = yytable[yyn];//we are in a new state
        state_push(yystate);   //save it
        val_push(yylval);      //push our lval as the input for next rule
        yychar = -1;           //since we have 'eaten' a token, say we need another
        if (yyerrflag > 0)     //have we recovered an error?
           --yyerrflag;        //give ourselves credit
        doaction=false;        //but don't process yet
        break;   //quit the yyn=0 loop
        }

    yyn = yyrindex[yystate];  //reduce
    if ((yyn !=0 ) && (yyn += yychar) >= 0 &&
            yyn <= YYTABLESIZE && yycheck[yyn] == yychar)
      {   //we reduced!
      if (yydebug) debug("reduce");
      yyn = yytable[yyn];
      doaction=true; //get ready to execute
      break;         //drop down to actions
      }
    else //ERROR RECOVERY
      {
      if (yyerrflag==0)
        {
        yyerror("syntax error");
        yynerrs++;
        }
      if (yyerrflag < 3) //low error count?
        {
        yyerrflag = 3;
        while (true)   //do until break
          {
          if (stateptr<0)   //check for under & overflow here
            {
            yyerror("stack underflow. aborting...");  //note lower case 's'
            return 1;
            }
          yyn = yysindex[state_peek(0)];
          if ((yyn != 0) && (yyn += YYERRCODE) >= 0 &&
                    yyn <= YYTABLESIZE && yycheck[yyn] == YYERRCODE)
            {
            if (yydebug)
              debug("state "+state_peek(0)+", error recovery shifting to state "+yytable[yyn]+" ");
            yystate = yytable[yyn];
            state_push(yystate);
            val_push(yylval);
            doaction=false;
            break;
            }
          else
            {
            if (yydebug)
              debug("error recovery discarding state "+state_peek(0)+" ");
            if (stateptr<0)   //check for under & overflow here
              {
              yyerror("Stack underflow. aborting...");  //capital 'S'
              return 1;
              }
            state_pop();
            val_pop();
            }
          }
        }
      else            //discard this token
        {
        if (yychar == 0)
          return 1; //yyabort
        if (yydebug)
          {
          yys = null;
          if (yychar <= YYMAXTOKEN) yys = yyname[yychar];
          if (yys == null) yys = "illegal-symbol";
          debug("state "+yystate+", error recovery discards token "+yychar+" ("+yys+")");
          }
        yychar = -1;  //read another
        }
      }//end error recovery
    }//yyn=0 loop
    if (!doaction)   //any reason not to proceed?
      continue;      //skip action
    yym = yylen[yyn];          //get count of terminals on rhs
    if (yydebug)
      debug("state "+yystate+", reducing "+yym+" by rule "+yyn+" ("+yyrule[yyn]+")");
    if (yym>0)                 //if count of rhs not 'nil'
      yyval = val_peek(yym-1); //get current semantic value
    switch(yyn)
      {
//########## USER-SUPPLIED ACTIONS ##########
case 1:
//#line 30 "XpathParser.y"
{}
break;
case 2:
//#line 31 "XpathParser.y"
{ resolver.nextExpression(); }
break;
case 3:
//#line 34 "XpathParser.y"
{}
break;
case 4:
//#line 35 "XpathParser.y"
{}
break;
case 5:
//#line 38 "XpathParser.y"
{ resolver.root(); }
break;
case 6:
//#line 39 "XpathParser.y"
{ resolver.root();  }
break;
case 7:
//#line 40 "XpathParser.y"
{ }
break;
case 8:
//#line 43 "XpathParser.y"
{}
break;
case 9:
//#line 44 "XpathParser.y"
{}
break;
case 10:
//#line 44 "XpathParser.y"
{ resolver.nextLocationPath(); }
break;
case 11:
//#line 45 "XpathParser.y"
{ }
break;
case 12:
//#line 48 "XpathParser.y"
{ resolver.nextLocationPath(); resolver.abbreviatedAxis( XPathResolver.ABBREVIATED_DESCENDANT );  }
break;
case 13:
//#line 51 "XpathParser.y"
{ resolver.nextLocationPath(); resolver.abbreviatedAxis( XPathResolver.ABBREVIATED_DESCENDANT ); }
break;
case 14:
//#line 54 "XpathParser.y"
{}
break;
case 15:
//#line 55 "XpathParser.y"
{}
break;
case 16:
//#line 56 "XpathParser.y"
{}
break;
case 17:
//#line 59 "XpathParser.y"
{ resolver.axis( val_peek(1).sval ); }
break;
case 18:
//#line 60 "XpathParser.y"
{}
break;
case 19:
//#line 63 "XpathParser.y"
{ resolver.abbreviatedAxis( XPathResolver.ABBREVIATED_ATTRIBUTE ); }
break;
case 20:
//#line 65 "XpathParser.y"
{ resolver.abbreviatedAxis( XPathResolver.ABBREVIATED_SELF ); resolver.nameTest( null, null ); resolver.nextLocationPath(); }
break;
case 21:
//#line 66 "XpathParser.y"
{ resolver.abbreviatedAxis( XPathResolver.ABBREVIATED_ANCESTOR ); resolver.nameTest( null, null ); }
break;
case 22:
//#line 69 "XpathParser.y"
{}
break;
case 23:
//#line 70 "XpathParser.y"
{}
break;
case 24:
//#line 73 "XpathParser.y"
{}
break;
case 25:
//#line 74 "XpathParser.y"
{ resolver.nodeType( val_peek(0).sval ); }
break;
case 26:
//#line 75 "XpathParser.y"
{ resolver.nodeType( val_peek(2).sval ); }
break;
case 27:
//#line 76 "XpathParser.y"
{ resolver.processingInstruction( val_peek(3).sval, val_peek(1).sval ); }
break;
case 28:
//#line 77 "XpathParser.y"
{ resolver.processingInstruction( val_peek(2).sval, null ); }
break;
case 29:
//#line 80 "XpathParser.y"
{ resolver.nameTest( XPathResolver.ABBREVIATED_NAMETEST, null ); }
break;
case 30:
//#line 81 "XpathParser.y"
{ resolver.nameTest( val_peek(0).sval, null ); }
break;
case 31:
//#line 82 "XpathParser.y"
{ resolver.nameTest( val_peek(0).sval, val_peek(2).sval ); }
break;
case 32:
//#line 83 "XpathParser.y"
{ resolver.nameTest( "*", val_peek(2).sval ); }
break;
case 33:
//#line 86 "XpathParser.y"
{ resolver.nextPredicate(); }
break;
case 34:
//#line 89 "XpathParser.y"
{}
break;
case 35:
//#line 92 "XpathParser.y"
{}
break;
case 36:
//#line 95 "XpathParser.y"
{}
break;
case 37:
//#line 96 "XpathParser.y"
{ resolver.binaryOperator( resolver.UNION ); }
break;
case 38:
//#line 98 "XpathParser.y"
{}
break;
case 39:
//#line 99 "XpathParser.y"
{ resolver.binaryOperator( resolver.OR ); }
break;
case 40:
//#line 102 "XpathParser.y"
{ }
break;
case 41:
//#line 103 "XpathParser.y"
{ resolver.binaryOperator( resolver.AND ); }
break;
case 42:
//#line 106 "XpathParser.y"
{}
break;
case 43:
//#line 107 "XpathParser.y"
{ resolver.binaryOperator( resolver.EQUAL ); }
break;
case 44:
//#line 108 "XpathParser.y"
{ resolver.binaryOperator( resolver.NOT_EQUAL ); }
break;
case 45:
//#line 111 "XpathParser.y"
{ resolver.nextExpression(); }
break;
case 46:
//#line 112 "XpathParser.y"
{ resolver.binaryOperator( resolver.INF ); }
break;
case 47:
//#line 113 "XpathParser.y"
{ resolver.binaryOperator( resolver.SUP ); }
break;
case 48:
//#line 114 "XpathParser.y"
{ resolver.binaryOperator( resolver.INFE ); }
break;
case 49:
//#line 115 "XpathParser.y"
{ resolver.binaryOperator( resolver.SUPE ); }
break;
case 50:
//#line 117 "XpathParser.y"
{}
break;
case 51:
//#line 118 "XpathParser.y"
{ resolver.binaryOperator( resolver.ADD ); }
break;
case 52:
//#line 119 "XpathParser.y"
{ resolver.binaryOperator( resolver.MINUS ); }
break;
case 53:
//#line 121 "XpathParser.y"
{}
break;
case 54:
//#line 122 "XpathParser.y"
{ resolver.binaryOperator( resolver.STAR ); }
break;
case 55:
//#line 123 "XpathParser.y"
{ resolver.binaryOperator( val_peek(1).sval ); }
break;
case 56:
//#line 126 "XpathParser.y"
{}
break;
case 57:
//#line 127 "XpathParser.y"
{ resolver.unaryOperator( resolver.MINUS ); }
break;
case 58:
//#line 130 "XpathParser.y"
{}
break;
case 59:
//#line 131 "XpathParser.y"
{}
break;
case 60:
//#line 132 "XpathParser.y"
{}
break;
case 61:
//#line 133 "XpathParser.y"
{}
break;
case 62:
//#line 136 "XpathParser.y"
{}
break;
case 63:
//#line 137 "XpathParser.y"
{}
break;
case 64:
//#line 140 "XpathParser.y"
{ resolver.variable( val_peek(0).sval ); }
break;
case 65:
//#line 141 "XpathParser.y"
{}
break;
case 66:
//#line 142 "XpathParser.y"
{ resolver.literal( val_peek(0).sval.substring( 1, val_peek(0).sval.length() - 1 ) ); }
break;
case 67:
//#line 143 "XpathParser.y"
{ resolver.number( val_peek(0).sval ); }
break;
case 68:
//#line 144 "XpathParser.y"
{}
break;
case 69:
//#line 147 "XpathParser.y"
{ resolver.nextFunction(); }
break;
case 70:
//#line 148 "XpathParser.y"
{ resolver.nextFunction(); }
break;
case 71:
//#line 151 "XpathParser.y"
{ resolver.functionName( val_peek(0).sval ); }
break;
case 72:
//#line 154 "XpathParser.y"
{ resolver.nextParam(); }
break;
case 73:
//#line 155 "XpathParser.y"
{ resolver.nextParam(); }
break;
//#line 943 "XPathParser.java"
//########## END OF USER-SUPPLIED ACTIONS ##########
    }//switch
    //#### Now let's reduce... ####
    if (yydebug) debug("reduce");
    state_drop(yym);             //we just reduced yylen states
    yystate = state_peek(0);     //get new state
    val_drop(yym);               //corresponding value drop
    yym = yylhs[yyn];            //select next TERMINAL(on lhs)
    if (yystate == 0 && yym == 0)//done? 'rest' state and at first TERMINAL
      {
      debug("After reduction, shifting from state 0 to state "+YYFINAL+"");
      yystate = YYFINAL;         //explicitly say we're done
      state_push(YYFINAL);       //and save it
      val_push(yyval);           //also save the semantic value of parsing
      if (yychar < 0)            //we want another character?
        {
        yychar = yylex();        //get next character
        if (yychar<0) yychar=0;  //clean, if necessary
        if (yydebug)
          yylexdebug(yystate,yychar);
        }
      if (yychar == 0)          //Good exit (if lex returns 0 ;-)
         break;                 //quit the loop--all DONE
      }//if yystate
    else                        //else not done yet
      {                         //get next state and push, for next yydefred[]
      yyn = yygindex[yym];      //find out where to go
      if ((yyn != 0) && (yyn += yystate) >= 0 &&
            yyn <= YYTABLESIZE && yycheck[yyn] == yystate)
        yystate = yytable[yyn]; //get new state
      else
        yystate = yydgoto[yym]; //else go to new defred
      debug("after reduction, shifting from state "+state_peek(0)+" to state "+yystate+"");
      state_push(yystate);     //going again, so push state & val...
      val_push(yyval);         //for next action
      }
    }//main loop
  return 0;//yyaccept!!
}
//## end of method parse() ######################################



//## run() --- for Thread #######################################
/**
 * A default run method, used for operating this parser
 * object in the background.  It is intended for extending Thread
 * or implementing Runnable.  Turn off with -Jnorun .
 */
public void run()
{
  yyparse();
}
//## end of method run() ########################################



//## Constructors ###############################################
/**
 * Default constructor.  Turn off with -Jnoconstruct .

 */
public XPathParser()
{
  //nothing to do
}


/**
 * Create a parser, setting the debug to true or false.
 * @param debugMe true for debugging, false for no debug.
 */
public XPathParser(boolean debugMe)
{
  yydebug=debugMe;
}
//###############################################################



}
//################### END OF CLASS ##############################

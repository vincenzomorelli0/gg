parser grammar ProblemParser;
/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
options {
    tokenVocab=ProblemLexer;
}
@header {
    package net.splitcells.gel.ext.problem.antlr4;
}
source_unit: statement+;
access: Dot Name call_arguments access?;
call_arguments
    : Brace_round_open Brace_round_closed
    | Brace_round_open call_arguments_element call_arguments_next* Brace_round_closed
    ;
call_arguments_element
    : Name
    | Integer
    | function_call;
call_arguments_next: Comma call_arguments_element;
function_call: Name call_arguments access*;
function_call_list
    : Brace_round_open Brace_round_closed
    | Brace_round_open function_call function_call_list_element* Brace_round_closed;
function_call_list_element: Comma function_call;
map
    : Brace_curly_open Brace_curly_closed
    | Brace_curly_open variable_definition statement_reversed* Brace_curly_closed;
statement
    : variable_definition Semicolon
    | function_call Semicolon;
statement_reversed: Semicolon variable_definition;
variable_definition
    : Name Equals function_call
    | Name Equals String
    | Name Equals map;


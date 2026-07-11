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

package com.japisoft.editix.javascript;

import java.io.Reader;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptException;

public class EmptyEngine implements ScriptEngine {

	@Override
	public Bindings createBindings() {
		return null;
	}

	@Override
	public Object eval(String script) throws ScriptException {
		throw new ScriptException( "No JavaScript Engine, check your Java VM config" );
	}

	@Override
	public Object eval(Reader reader) throws ScriptException {
		throw new ScriptException( "No JavaScript Engine, check your Java VM config" );
	}

	@Override
	public Object eval(String script, ScriptContext context) throws ScriptException {
		throw new ScriptException( "No JavaScript Engine, check your Java VM config" );
	}

	@Override
	public Object eval(Reader reader, ScriptContext context) throws ScriptException {
		throw new ScriptException( "No JavaScript Engine, check your JavaVM config" );
	}

	@Override
	public Object eval(String script, Bindings n) throws ScriptException {
		throw new ScriptException( "No JavaScript Engine, check your JavaVM config" );
	}

	@Override
	public Object eval(Reader reader, Bindings n) throws ScriptException {
		throw new ScriptException( "No JavaScript Engine, check your JavaVM config" );
	}

	@Override
	public Object get(String key) {
		return null;
	}

	@Override
	public Bindings getBindings(int scope) {
		return null;
	}

	@Override
	public ScriptContext getContext() {
		return null;
	}

	@Override
	public ScriptEngineFactory getFactory() {
		return null;
	}

	@Override
	public void put(String key, Object value) {
	}

	@Override
	public void setBindings(Bindings bindings, int scope) {
	}

	@Override
	public void setContext(ScriptContext context) {
	}

}

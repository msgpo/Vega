package com.subgraph.vega.impl.scanner.modules.scripting;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Logger;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.RhinoException;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import com.subgraph.vega.impl.scanner.modules.scripting.dom.AnchorJS;
import com.subgraph.vega.impl.scanner.modules.scripting.dom.AttrJS;
import com.subgraph.vega.impl.scanner.modules.scripting.dom.CharacterDataJS;
import com.subgraph.vega.impl.scanner.modules.scripting.dom.CommentJS;
import com.subgraph.vega.impl.scanner.modules.scripting.dom.DocumentJS;
import com.subgraph.vega.impl.scanner.modules.scripting.dom.ElementJS;
import com.subgraph.vega.impl.scanner.modules.scripting.dom.FormJS;
import com.subgraph.vega.impl.scanner.modules.scripting.dom.HTMLCollectionJS;
import com.subgraph.vega.impl.scanner.modules.scripting.dom.HTMLDocumentJS;
import com.subgraph.vega.impl.scanner.modules.scripting.dom.InputJS;
import com.subgraph.vega.impl.scanner.modules.scripting.dom.LinkJS;
import com.subgraph.vega.impl.scanner.modules.scripting.dom.NodeJS;
import com.subgraph.vega.impl.scanner.modules.scripting.dom.NodeListJS;
import com.subgraph.vega.impl.scanner.modules.scripting.dom.OptionJS;
import com.subgraph.vega.impl.scanner.modules.scripting.dom.SelectJS;
import com.subgraph.vega.impl.scanner.modules.scripting.dom.TextJS;

public class PreludeLoader {
	private final Logger logger = Logger.getLogger("prelude-loader");
	private final File preludeDirectory;
	private final ScriptCompiler preludeCompiler;

	private final FileFilter scriptFilter = new FileFilter() {
		public boolean accept(File pathname) {
			return pathname.isFile() && pathname.getName().endsWith(".js");
		}
	};
	
	private Scriptable preludeScope;
	
	PreludeLoader(File directory, Scriptable scope) {
		this.preludeDirectory = directory;
		this.preludeCompiler = new ScriptCompiler(scope);
	}
	
	void load() {
		try {
			Context cx = Context.enter();
			Scriptable scope = preludeCompiler.newScope(cx);
			for(File ps: preludeDirectory.listFiles(scriptFilter)) {
				preludeCompiler.compileFile(ps, cx, scope);
			}
				
			ScriptableObject.defineClass(scope, NodeJS.class, true, true);
			ScriptableObject.defineClass(scope, DocumentJS.class, true, true);
			ScriptableObject.defineClass(scope, ElementJS.class, true, true);
			ScriptableObject.defineClass(scope, AttrJS.class, true, true);
			ScriptableObject.defineClass(scope, CharacterDataJS.class, true, true);
			ScriptableObject.defineClass(scope, TextJS.class, true, true);
			ScriptableObject.defineClass(scope, AnchorJS.class, true, true);
			ScriptableObject.defineClass(scope, FormJS.class, true, true);
			ScriptableObject.defineClass(scope, CommentJS.class, true, true);
			ScriptableObject.defineClass(scope, HTMLCollectionJS.class, true, true);
			ScriptableObject.defineClass(scope, HTMLDocumentJS.class, true, true);
			ScriptableObject.defineClass(scope, InputJS.class, true, true);
			ScriptableObject.defineClass(scope, LinkJS.class, true, true);
			ScriptableObject.defineClass(scope, OptionJS.class, true, true);
			ScriptableObject.defineClass(scope, SelectJS.class, true, true);
			ScriptableObject.defineClass(scope, NodeListJS.class, true, true);
			preludeScope = scope;
			
		} catch (RhinoException e) {
			logger.warning(new RhinoExceptionFormatter("Failed to load Prelude and DOM wrapper classes", e).toString());
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			Context.exit();
		}
	}
	
	Scriptable getPreludeScope() {
		return preludeScope;
	}
}
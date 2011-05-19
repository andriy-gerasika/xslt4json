package com.gerixsoft.json2xml2;

import java.io.File;
import java.io.IOException;

import org.antlr.runtime.ANTLRFileStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.CommonTree;
import org.xerial.json.impl.JSONLexer;
import org.xerial.json.impl.JSONParser;

public class JsonToXml {

	public static void main(String[] args) throws IOException, RecognitionException {
		if (args.length != 2) {
			System.err.println("usage: <json-file> <xml-file>");
			System.exit(-1);
		}
		json2xml(new File(args[0]), new File(args[1]));
		System.out.println("ok");
	}

	private static void json2xml(File jsonFile, File xmlFile) throws IOException, RecognitionException {
		CharStream stream = new ANTLRFileStream(jsonFile.toString());
		JSONLexer lexer = new JSONLexer(stream);
		TokenStream input = new CommonTokenStream(lexer);
		JSONParser parser = new JSONParser(input);
		CommonTree tree = (CommonTree) parser.jsonObject().getTree();
	}

}

package com.gerixsoft.json2xml2;

import java.io.File;
import java.io.IOException;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.antlr.runtime.ANTLRFileStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.TreeVisitor;
import org.xerial.json.impl.JSONLexer;
import org.xerial.json.impl.JSONParser;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class JsonToXml {

	public static void main(String[] args) throws IOException, RecognitionException, SAXException, TransformerConfigurationException {
		if (args.length != 2) {
			System.err.println("usage: <json-file> <xml-file>");
			System.exit(-1);
		}
		json2xml(new File(args[0]), new File(args[1]));
		System.out.println("ok");
	}

	private static void json2xml(File jsonFile, File xmlFile) throws IOException, RecognitionException, SAXException, TransformerConfigurationException {
		CharStream stream = new ANTLRFileStream(jsonFile.toString());
		JSONLexer lexer = new JSONLexer(stream);
		TokenStream input = new CommonTokenStream(lexer);
		JSONParser parser = new JSONParser(input);
		CommonTree tree = (CommonTree) parser.json().getTree();
		
		SAXTransformerFactory handlerFactory = (SAXTransformerFactory) SAXTransformerFactory.newInstance();
		TransformerHandler handler = handlerFactory.newTransformerHandler(new StreamSource(JsonToXml.class.getResource("json2xml.xsl").toString()));
		handler.getTransformer().setOutputProperty("omit-xml-declaration", "yes");
		handler.getTransformer().setOutputProperty("indent", "yes");
		handler.setResult(new StreamResult(xmlFile));
		handler.startDocument();
		try {
			//TreeVisitor visitor = new TreeVisitor();
			//visitor.visit(tree, new __TreeVisitorAction(handler));
		} finally {
			handler.endDocument();
		}
		
		SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
		Schema schema = factory.newSchema(new StreamSource(JsonToXml.class.getResourceAsStream("json.xsd")));
		Validator validator = schema.newValidator();
		validator.setErrorHandler(new ErrorHandler() {
			@Override
			public void warning(SAXParseException exception) throws SAXException {
				log("warning", exception);
			}

			@Override
			public void fatalError(SAXParseException exception) throws SAXException {
				log("fatal error", exception);
			}

			@Override
			public void error(SAXParseException exception) throws SAXException {
				log("error", exception);
			}

			public void log(String type, SAXParseException exception) {
				System.err.println(type + " at line: " + exception.getLineNumber() + " col:" + exception.getColumnNumber() + " message: "
						+ exception.getMessage());
			}
		});
		validator.validate(new StreamSource(xmlFile));
	}

}

package com.gerixsoft.json2xml2;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
import org.antlr.runtime.Token;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.TreeVisitor;
import org.antlr.runtime.tree.TreeVisitorAction;
import org.xerial.json.impl.JSONLexer;
import org.xerial.json.impl.JSONParser;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.AttributesImpl;

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
			handler.startElement("", "json", "json", new AttributesImpl());
			TreeVisitor visitor = new TreeVisitor();
			visitor.visit(tree, new __TreeVisitorAction(handler));
			handler.endElement("", "json", "json");
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

	private static final class __TreeVisitorAction implements TreeVisitorAction {
		private ContentHandler handler;
		
		public __TreeVisitorAction(ContentHandler handler) {
			this.handler = handler;
		}

		@Override
		public Object pre(Object o) {
			CommonTree tree = (CommonTree) o;
			Token token = tree.getToken();
			String text = token.getText();
			int type = token.getType();
			try {
				if (type == JSONParser.XML_ELEMENT) {
					handler.startElement("", text, text, new AttributesImpl());
				} else {
					handler.processingInstruction("antlr", text);
				}
			} catch(SAXException e) {
				e.printStackTrace();
			}
			return o;
		}

		@Override
		public Object post(Object o) {
			CommonTree tree = (CommonTree) o;
			Token token = tree.getToken();
			String text = token.getText();
			int type = token.getType();
			try {
				if (type == JSONParser.XML_ELEMENT) {
					handler.endElement("", text, text);
				} else {
				}
			} catch(SAXException e) {
				e.printStackTrace();
			}
			return o;
		}
		
	}
}

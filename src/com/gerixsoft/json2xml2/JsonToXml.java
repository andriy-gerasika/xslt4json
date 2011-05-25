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
import org.antlr.runtime.tree.Tree;
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
		File jsonFile = new File(args[0]);
		File xmlFile = new File(args[1]);

		CharStream stream = new ANTLRFileStream(jsonFile.toString());
		JSONLexer lexer = new JSONLexer(stream);
		TokenStream input = new CommonTokenStream(lexer);
		JSONParser parser = new JSONParser(input);
		CommonTree tree = (CommonTree) parser.json().getTree();

		SAXTransformerFactory handlerFactory = (SAXTransformerFactory) SAXTransformerFactory.newInstance();
		TransformerHandler handler = handlerFactory.newTransformerHandler();
		handler.setResult(new StreamResult(xmlFile));
		handler.startDocument();
		try {
			__TreeVisitor visitor = new __TreeVisitor(handler);
			visitor.visit(tree);
		} finally {
			handler.endDocument();
		}

		SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
		Schema schema = factory.newSchema(new StreamSource(JsonToXml.class.getResourceAsStream("json.xsd")));
		Validator validator = schema.newValidator();
		validator.setErrorHandler(new __ErrorHandler());
		validator.validate(new StreamSource(xmlFile));

		System.out.println("ok");
	}

	private static final class __TreeVisitor {
		private ContentHandler handler;

		public __TreeVisitor(ContentHandler handler) {
			this.handler = handler;
		}

		public void visit(Tree tree) {
			String text = tree.getText();
			int type = tree.getType();
			try {
				if (type == JSONParser.XML_ELEMENT) {
					AttributesImpl attrs = new AttributesImpl();
					int i = 0;
					for (; i < tree.getChildCount(); i++) {
						Tree attr = tree.getChild(i);
						if (attr.getType() != JSONParser.XML_ATTRIBUTE) {
							break;
						}
						String attrName = attr.getText();
						StringBuilder attrValue = new StringBuilder();
						for (int j = 0; j < attr.getChildCount(); j++) {
							Tree value = attr.getChild(j);
							attrValue.append(value.getText());
						}
						attrs.addAttribute("", attrName, attrName, "CDATA", attrValue.toString());
					}
					handler.startElement("", text, text, attrs);
					for (; i < tree.getChildCount(); i++) {
						visit(tree.getChild(i));
					}
					handler.endElement("", text, text);
				} else {
					String name = JSONParser.tokenNames[type];
					if (name.equals(name.toUpperCase())) {
						handler.startElement("", name, name, new AttributesImpl());
						for (int i = 0; i < tree.getChildCount(); i++) {
							visit(tree.getChild(i));
						}
						handler.endElement("", name, name);
					} else {
						//handler.processingInstruction("antlr", text);
						handler.characters(text.toCharArray(), 0, text.length());
					}
				}
			} catch (SAXException e) {
				e.printStackTrace();
			}
		}

	}

	private static final class __ErrorHandler implements ErrorHandler {
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
			System.err.println(type + " at line: " + exception.getLineNumber() + " col:" + exception.getColumnNumber() + " message: " + exception.getMessage());
		}
	}

}

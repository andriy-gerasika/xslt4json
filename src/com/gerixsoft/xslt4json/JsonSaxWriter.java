package com.gerixsoft.xslt4json;

import java.io.IOException;
import java.io.OutputStream;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public class JsonSaxWriter implements ContentHandler {

	private OutputStream outputStream;
	
	private void write(String s) throws SAXException {
		try {
			outputStream.write(s.getBytes());
		} catch (IOException e) {
			throw new SAXException(e);
		}
	}
	
	public JsonSaxWriter(OutputStream outputStream) {
		this.outputStream = outputStream;
	}
	
	@Override
	public void setDocumentLocator(Locator locator) {
		
	}

	@Override
	public void startDocument() throws SAXException {
		write("// start of json\n");
	}

	@Override
	public void endDocument() throws SAXException {
		write("// end of json\n");
	}

	@Override
	public void startPrefixMapping(String prefix, String uri) throws SAXException {
		
	}

	@Override
	public void endPrefixMapping(String prefix) throws SAXException {
		
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes atts)
			throws SAXException {
		
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		
	}

	@Override
	public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
		
	}

	@Override
	public void processingInstruction(String target, String data) throws SAXException {
		
	}

	@Override
	public void skippedEntity(String name) throws SAXException {
		
	}

}

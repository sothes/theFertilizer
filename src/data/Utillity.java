package data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.ProcessingInstruction;

public class Utillity {

	public Utillity(){
		
	}
	
	public Document read(File file){
		Document out = null;
		DocumentBuilderFactory domFactory;
		DocumentBuilder domBuilder = null;
		domFactory = DocumentBuilderFactory.newInstance();
		domFactory.setNamespaceAware(false);
		domFactory.setValidating(true);
		domFactory.setIgnoringElementContentWhitespace(true);
		try{
			domBuilder = domFactory.newDocumentBuilder();
		}catch( ParserConfigurationException e)		{}
		domBuilder.setErrorHandler(new MyErrorHandler() );
		
		if(file.isFile()){
			//System.out.println(file.getAbsolutePath());
			try{
				out = domBuilder.parse(new org.xml.sax.InputSource(file.getAbsolutePath()));
			}
			catch ( IOException e )		{}
			catch ( org.xml.sax.SAXException e )	{}
		}
		return out;
	}
	
	/**
	 * 
	 * @param doc
	 * @param key		"xml-stylesheet"
	 * @param value		"type=\"text/xsl\" href=\""+Model.xslDatei+"\""
	 * @return
	 */
	public void addProcessingInstruction(Document doc, String key, String value){
		//ProcessingInstruction
		ProcessingInstruction p = doc.createProcessingInstruction(key, value);
		Element root = doc.getDocumentElement();
		root.getParentNode().insertBefore(p, root);
		doc.normalize();
	}
	
	public boolean write(File file, Document doc){
		
		TransformerFactory	tFactory;
		Transformer			transformer = null;
		BufferedWriter      ergebnis = null;
		
		//System.out.println("printDoc "+Model.basisPfad+this.datei);
		try{
			ergebnis = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file),"UTF-8"));
		}catch(FileNotFoundException e){
			System.out.println(e.getMessage());
			return false;
		}catch(UnsupportedEncodingException e){
			System.out.println(e.getMessage());
			return false;
		}

		tFactory	= TransformerFactory.newInstance();
		MyTransformerErrorHandler h = new MyTransformerErrorHandler();
		tFactory.setErrorListener(h);

		try	{
			transformer = tFactory.newTransformer();
		}
		catch(TransformerConfigurationException e){
			System.out.println(e.getMessage());
			return false;
		}
		
		
		DOMSource		source	= new DOMSource(doc);
		StreamResult	result	= new StreamResult(ergebnis);
		try	{
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.transform(source, result);
		}
		catch(TransformerException e){
			System.out.println(e.getMessage());
			return false;
		}
		
		try{
			ergebnis.close();
		}catch(IOException e){
			System.out.println(e.getMessage());
		}
		return true;
	}
}


class MyErrorHandler extends org.xml.sax.helpers.DefaultHandler {
	public void fatalError(org.xml.sax.SAXParseException err) throws org.xml.sax.SAXException {}
	public void error(org.xml.sax.SAXParseException err) throws org.xml.sax.SAXException {}
	public void warning(org.xml.sax.SAXParseException err) throws org.xml.sax.SAXException {}
}

class MyTransformerErrorHandler implements javax.xml.transform.ErrorListener {
	
	public void error(TransformerException e){
		System.out.println(e.getMessage());
		System.out.println(e.getStackTrace());
	}
		
	public void warning(TransformerException e){
		System.out.println(e.getMessage());
		System.out.println(e.getStackTrace());
	}
		
	public void fatalError(TransformerException e){
		System.out.println(e.getMessage());
		System.out.println(e.getStackTrace());
	}
}

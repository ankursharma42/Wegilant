package com.wegilant;

//File dependency
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;

public class FileProcessor {

	// local file location
	public static final String localFileLocation = "xmlfiles/file1.xml";

	// remote file location
	public static final String remoteFileLocation = "http://www.w3schools.com/xml/simple.xml";

	// storage location for remote file
	public static final String remoteLocalFileLocation = "xmlfiles/file2.xml";

	// merged file location
	public static final String mregedFileLocation = "xmlfiles/mergedfile.xml";

	public static final String domElement = "breakfast_menu";

	public static final String rootElement = "food";

	public static final String childElement1 = "name";

	public static final String childElement2 = "price";

	private static final int BUFFER_SIZE = 4096;

	public static final ArrayList<Product> productList = new ArrayList<Product>();

	// Note not taking other elements since it is not universally present.
	// Further if this has to be avoided, this case has to be handled in the
	// code itself.

	public void readLocalFile(String fileLocation) {
		Document dom = null;
		File xmlFile = new File(fileLocation);
		// Parsing using dom parser
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
				.newInstance();
		try {
			// Using factory to get an instance of document builder
			DocumentBuilder db = documentBuilderFactory.newDocumentBuilder();
			// parse using builder to get DOM representation of the XML file
			dom = db.parse(xmlFile);
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (SAXException se) {
			se.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		System.out.println("Root element :"
				+ dom.getDocumentElement().getNodeName());
		if (dom.getDocumentElement().getNodeName() == null) {
			System.out.println("unable to parse the xml document.");
			System.exit(1);
		}
		if (!dom.getDocumentElement().getNodeName().equals(domElement)) {
			System.out.println("not the correct xml dom representation");
			System.exit(1);
		}
		// fetching the root element
		Element dependencies = dom.getDocumentElement();

		NodeList dependencyList = dependencies
				.getElementsByTagName(rootElement);
		if (dependencyList != null && dependencyList.getLength() > 0) {
			for (int i = 0; i < dependencyList.getLength(); i++) {
				// get the dependency root element
				Element dependency = (Element) dependencyList.item(i);
				// get the product object
				Product product = getEmployee(dependency);
				// add it to productList
				productList.add(product);
			}
		}
	}

	/**
	 * Helper method this function takes an dependency element and read the
	 * values in and creates an Prodct object
	 */
	private Product getEmployee(Element dependency) {

		String groupId = getGroupId(dependency, childElement1);
		String artifactId = getArtifactId(dependency, childElement2);

		// Create a new Product with the value read from the xml nodes
		Product product = new Product(groupId, artifactId);

		return product;
	}

	/**
	 * Helper method It fetches the group Id
	 */
	private String getGroupId(Element ele, String tagName) {
		String groupId = null;
		NodeList groupIdList = ele.getElementsByTagName(tagName);
		if (groupIdList != null && groupIdList.getLength() > 0) {
			Element el = (Element) groupIdList.item(0);
			groupId = el.getFirstChild().getNodeValue();
		}
		return groupId;
	}

	/**
	 * Helper method
	 * 
	 */
	private String getArtifactId(Element ele, String tagName) {
		String artifactId = null;
		NodeList artifactIdList = ele.getElementsByTagName(tagName);
		if (artifactIdList != null && artifactIdList.getLength() > 0) {
			Element el = (Element) artifactIdList.item(0);
			artifactId = el.getFirstChild().getNodeValue();
		}
		return artifactId;
	}

	/**
	 * Method reads the remote file and and stores it in the local directory. it
	 * then calls the local file processing function to check whether it is in
	 * the valid format or not.If it is in the valid format then store it in the
	 * list for creating merged file
	 */
	public void readRemoteFile() {
		try {
			URL url = new URL(remoteFileLocation);
			URLConnection conn = url.openConnection();
			InputStream inputStream = conn.getInputStream();

			FileOutputStream outputStream = new FileOutputStream(
					remoteLocalFileLocation);

			byte[] buffer = new byte[BUFFER_SIZE];
			int bytesRead = -1;
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, bytesRead);
			}

			outputStream.close();
			inputStream.close();

			System.out.println("File downloaded");
			readLocalFile(remoteLocalFileLocation);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Method that merges file
	 */
	public boolean mergefile(String filePath) {

		try {
			// fetching the builder
			DocumentBuilderFactory documentFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = documentFactory.newDocumentBuilder();
			// creating the dom
			Document dom = docBuilder.newDocument();
			System.out.println("The root element is" + domElement);
			Element documentElement = dom.createElement(domElement);
			dom.appendChild(documentElement);

			Iterator it = productList.iterator();
			while (it.hasNext()) {
				Product product = (Product) it.next();
				// For each Product object create
				Element productEle = createProductEle(dom, product);
				documentElement.appendChild(productEle);
			}

			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(dom);
			StreamResult result = new StreamResult(new File(mregedFileLocation));
			transformer.transform(source, result);
			System.out.println("File saved!");

		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
			return false;
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * Helper method which creates a XML element
	 * 
	 * @param procut
	 *            The Product for which we need to create an xml representation
	 * @return XML element snippet representing a Product
	 */
	private Element createProductEle(Document dom, Product product) {
		Element productEle = dom.createElement(rootElement);

		// create groupId element and text node and attach it to
		// product Element
		Element groupId = dom.createElement(childElement1);
		Text groupIdText = dom.createTextNode(product.getGroupId());
		groupId.appendChild(groupIdText);
		productEle.appendChild(groupId);

		// create artifact element and text node and attach it to product
		// Element
		Element artifactId = dom.createElement(childElement2);
		Text artifactIdText = dom.createTextNode(product.getArtifactId());
		artifactId.appendChild(artifactIdText);
		productEle.appendChild(artifactId);

		return productEle;

	}
}

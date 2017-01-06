package net.xdefine.db.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import net.sf.json.JSONObject;
import net.xdefine.XFContext;
import net.xdefine.db.XSession;
import net.xdefine.db.XSessionFactory;

@Component
public class XSessionFactoryImpl implements XSessionFactory {

	private static final String DEFAULT = "default";
	private Map<String, DataSource> dataSources;
	private Map<String, XSession> currentSessions = new HashMap<String, XSession>();
	public JSONObject entities = new JSONObject();

	public XSessionFactoryImpl(Map<String, DataSource> dataSources) {
		this.dataSources = dataSources;

		try {
			boolean isChange = false;
			File jsonTables = new File(XFContext.getProperty("webapp.file.path") + "/.xdefine/tables.json");
			long modifyDate = jsonTables.lastModified();
			if (jsonTables.exists()) {
				FileInputStream inputStream = new FileInputStream(jsonTables);
				entities = JSONObject.fromObject(IOUtils.toString(inputStream, "UTF-8"));
				inputStream.close();
				inputStream = null;
			}

			XPath xpath = XPathFactory.newInstance().newXPath();

			ClassLoader classLoader = getClass().getClassLoader();
			File tables = new File(classLoader.getResource("xdefine/tables").getFile());
			for (File table : tables.listFiles()) {
				if (table.lastModified() < modifyDate)
					continue;

				JSONObject jTable = new JSONObject();

				InputSource is = new InputSource(new FileReader(table));
				Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);

				Node entity = (Node) xpath.compile("/entity").evaluate(document, XPathConstants.NODE);

				jTable.put("name", entity.getAttributes().getNamedItem("name").getNodeValue());
				jTable.put("catalog", entity.getAttributes().getNamedItem("catalog").getNodeValue());
				jTable.put("table", entity.getAttributes().getNamedItem("table").getNodeValue());

				NodeList items = entity.getChildNodes();
				
				for (int i = 0; i < items.getLength(); i++) {
					
					System.out.println(items.item(i).getNodeName());
					
				}
				// catalog="khan" table="account" name="account">

				// JSONObject jTable = JSONObject.fromObject(new
				// XMLSerializer().readFromFile(table));
				entities.put(jTable.getString("name"), jTable);
				isChange = true;
			}

			if (isChange) {
				if (!jsonTables.getParentFile().exists())
					jsonTables.getParentFile().mkdirs();
				BufferedWriter out = new BufferedWriter(new FileWriter(jsonTables));
				out.write(entities.toString());
				out.close();
				out = null;
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	@Override
	public XSession getCurrentSession() {
		return this.getCurrentSession(DEFAULT);
	}

	@Override
	public XSession getCurrentSession(String name) {
		if (!dataSources.containsKey(name))
			throw new IllegalStateException(XFContext.getLanguage("xdefine.language.db.not_found_datasources"));
		if (!currentSessions.containsKey(name) || currentSessions.get(name) == null) {
			currentSessions.put(name, new XSessionImpl(this, name, dataSources.get(name)));
		}
		return currentSessions.get(name);
	}

	@Override
	public void setCurrentNullIfTarget(String name, XSession dbSessionImpl) {
		if (currentSessions.get(name) != null && currentSessions.get(name).equals(dbSessionImpl)) {
			currentSessions.put(name, null);
		}
	}

}

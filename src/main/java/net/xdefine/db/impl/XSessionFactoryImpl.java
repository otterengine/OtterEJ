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

import net.sf.json.JSONArray;
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
				
				JSONObject jTable = new JSONObject();

				InputSource is = new InputSource(new FileReader(table));
				Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);

				Node entity = (Node) xpath.compile("//entity").evaluate(document, XPathConstants.NODE);

				jTable.put("name", nv(entity.getAttributes().getNamedItem("name")));
				jTable.put("catalog", nv(entity.getAttributes().getNamedItem("catalog")));
				jTable.put("table", nv(entity.getAttributes().getNamedItem("table")));

				JSONObject filters = new JSONObject();
				JSONArray columns = new JSONArray();
				NodeList items = entity.getChildNodes();
				for (int i = 0; i < items.getLength(); i++) {
					JSONObject column = new JSONObject();
					if (items.item(i).getNodeName().startsWith("#")) continue;
					column.put("name", nv(items.item(i).getAttributes().getNamedItem("name")));
					column.put("type", nv(items.item(i).getAttributes().getNamedItem("type")));
					column.put("not-null", nv(items.item(i).getAttributes().getNamedItem("not-null")));
					column.put("unique", nv(items.item(i).getAttributes().getNamedItem("unique")));
					
					NodeList options = items.item(i).getChildNodes();
					for (int j = 0; j < options.getLength(); j++) {
						if (options.item(j).getNodeName().equals("#text")) continue;
						if (options.item(j).getNodeName().equals("column")) {
							column.put("db-var", options.item(j).getChildNodes().item(0).getNodeValue());
						}
						else if (options.item(j).getNodeName().equals("formula")) {
							column.put("db-var", "(" + options.item(j).getChildNodes().item(0).getNodeValue() + ")");
						}
						else if (options.item(j).getNodeName().equals("join-table")) {
							column.put("join-table", options.item(j).getAttributes().getNamedItem("target-name").getNodeValue().toString());
							column.put("join-filter", options.item(j).getAttributes().getNamedItem("target-filter").getNodeValue().toString());
							column.put("join-var", options.item(j).getChildNodes().item(0).getNodeValue());
						}
						else if (options.item(j).getNodeName().equals("filter")) {
							String filtername = options.item(j).getChildNodes().item(0).getNodeValue();
							if (filtername == null) continue;
							if (!filters.containsKey(filtername)) {
								filters.put(filtername, new JSONArray());
							}
							filters.getJSONArray(filtername).add(column.getString("name"));
						}
					}
					columns.add(column);
				}
				
				jTable.put("columns", columns);
				jTable.put("filters", filters);
				
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
	
	private String nv(Node node) {
		return node == null ? null : node.getNodeValue();
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

/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * iCureBackend is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as published by
 * the Free Software Foundation.
 *
 * iCureBackend is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
 */


package org.taktik.icure.be.drugs.fulltext;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.fr.FrenchAnalyzer;
import org.apache.lucene.analysis.nl.DutchAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.hibernate.Criteria;
import org.hibernate.MappingException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 * This class creates a lucene index based on a XML file describing
 * how to create the index
 * @author abaudoux
 *
 */
public class IndexCreator {
	protected String config;
	protected String output;

	protected Map<String,Analyzer> analyzers;

	protected final Log log = LogFactory.getLog(getClass());

	PropertyUtilsBean pub = new PropertyUtilsBean();

	protected final static Transformer TO_STRING_TRANSFORMER = new Transformer() {
		public Object transform(Object input) {
			return input.toString();
		}
	};

	/**
	 * Create a lucene index.
	 *
	 * @param args
	 * @throws IOException
	 * @throws JDOMException
	 */
	public static void main(String[] args) throws Exception {
		new IndexCreator(args[0],args[1]).createIndex();
	}

	public IndexCreator(String configPath,String outputPath) {
		this.config=configPath;
		this.output = outputPath;
	}

	@SuppressWarnings("unchecked")
	public void createIndex() throws JDOMException, IOException, MappingException, ClassNotFoundException {
		buildAnalyzerMap();
		SAXBuilder builder = new SAXBuilder();
		Document doc = builder.build(config);
		Element root = doc.getRootElement();
		SessionFactory sf = createSessionFactory(root.getAttributeValue("url"),
				root.getAttributeValue("login"),
				root.getAttributeValue("passwd"),
				root.getAttributeValue("dialect"),
				root.getAttributeValue("driverClass"),
				root.getAttributeValue("mappedClasses"));
		Directory indexDir = FSDirectory.open(new File(output));

		Version lucene47 = Version.LUCENE_47;
		IndexWriter writer = new IndexWriter(indexDir,new IndexWriterConfig(lucene47,new StandardAnalyzer(lucene47)));
		for (Element classToIndex:(Collection<Element>)root.getChildren("index-class")) {
			processClass(classToIndex,writer,sf);
		}
		log.info("Closing index");
		writer.close();
		log.info("All done!");
	}

	private void buildAnalyzerMap() {
		analyzers = new HashMap<String, Analyzer>();
		analyzers.put("fr", new FrenchAnalyzer(Version.LUCENE_47));
		analyzers.put("nl", new DutchAnalyzer(Version.LUCENE_47));
		analyzers.put("en", new StandardAnalyzer(Version.LUCENE_47));
	}

	@SuppressWarnings("unchecked")
	private void processClass(Element classToIndex, IndexWriter writer, SessionFactory sf) throws CorruptIndexException, IOException {
		String className = classToIndex.getAttributeValue("class");
		String langProp = classToIndex.getAttributeValue("langProp");
		float boost = (classToIndex.getAttributeValue("boost") == null) ?
				1.0f : Float.valueOf(classToIndex.getAttributeValue("boost"));
		boolean includeDiscriminator = (classToIndex.getAttributeValue("includeDiscriminator") != null) &&
										(classToIndex.getAttributeValue("includeDiscriminator").equals("true"));
		log.info("Indexing class " + className + " with boost " + boost);
		Session sess = sf.openSession();
		Criteria c = sess.createCriteria(className);
		List allObjects = c.list();
		int counter = 0;
		for (Object o : allObjects) {
			counter++;
			org.apache.lucene.document.Document doc = new org.apache.lucene.document.Document();
			if (includeDiscriminator) {
				Field f = new Field("Discriminator",className,Store.YES,Index.NOT_ANALYZED);
				doc.add(f);
			}
			for (Element field : (Collection<Element>)classToIndex.getChildren("field")) {
				Field f = createField(o,field);
				if (f != null) {
					doc.add(f);
				}
			}
			String lang = null;
			if (langProp != null) {
				try {
					lang = pub.getNestedProperty(o, langProp).toString();
				} catch (Exception e) {

				}
			}
			Analyzer analyzer = getAnalyserForLanguage(lang);
			writer.addDocument(doc,analyzer);
			if ((counter % 100) == 0) {
				log.info("indexed " + counter + "/" + allObjects.size() + " for " + className);
			}
		}
	}

	private Analyzer getAnalyserForLanguage(String lang) {
		Analyzer result = analyzers.get(lang);
		if (result == null) {
			result = analyzers.get("en");
		}
		return result;
	}

	private Field createField(Object o,Element field) {
		Field result = null;
		String[] paths = field.getAttributeValue("property").split(",");
		String fieldName = field.getAttributeValue("name");
		String indexStr = (String) ObjectUtils.defaultIfNull(field.getAttributeValue("index"), "");
		Index index = Index.NO;
		if (indexStr.equals("tokenized")) {
			index = Index.ANALYZED;
		}
		if (indexStr.equals("untokenized")) {
			index = Index.NOT_ANALYZED;
		}
		String storeStr = (String) ObjectUtils.defaultIfNull(field.getAttributeValue("store"), "");
		Store store = Store.NO;
		if (storeStr.equals("yes")) {
			store = Store.YES;
		}
		String fieldValue = "";
		for (String path : paths) {
			String subValue = null;
			try {
				Object prop = pub.getNestedProperty(o, path);
				if (prop instanceof Collection) {
					Collection<?> strCollection = CollectionUtils.collect((Collection<?>) prop, TO_STRING_TRANSFORMER);
					subValue = StringUtils.join(strCollection.iterator(), " ");
				} else if (prop != null) {
					subValue = prop.toString();
				}
			} catch (Exception e) {
				log.error("error while accessing path " + path);
				e.printStackTrace();
			}
			if (subValue != null) {
				if (fieldValue.length() > 0) {
					fieldValue += " " + subValue;
				} else {
					fieldValue = subValue;
				}
			}
		}
		if ((fieldValue.trim().length() > 0)) {
			result = new Field(fieldName,fieldValue,store,index);
		}
		return result;
	}

	private SessionFactory createSessionFactory(String url, String login, String passwd, String dialect, String driverClass, String mappedClasses) throws MappingException, ClassNotFoundException {
		Configuration cfg = new Configuration();
		for (String className:mappedClasses.split(",")) {
			cfg.addClass(Class.forName(className));
		}
		cfg.setProperty("hibernate.current_session_context_class", "thread");
		cfg.setProperty("hibernate.transaction.factory_class","org.hibernate.transaction.JDBCTransactionFactory");
		cfg.setProperty("hibernate.connection.driver_class", driverClass);
		cfg.setProperty("hibernate.connection.password", passwd);
		cfg.setProperty("hibernate.connection.username", login);
		cfg.setProperty("hibernate.default_schema", "PUBLIC");
		cfg.setProperty("hibernate.connection.url", url);
		return  cfg.buildSessionFactory();
	}

}

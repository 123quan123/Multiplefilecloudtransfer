package com.mec.mfct.resource;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * 
 * <ol>
 * 功能：工具类，用来将资源以XML的方式表示到文件中
 * </ol>
 * @author Quan
 * @date 2020/03/06
 * @version 0.0.1
 */
public class XMLEditor {
	private static final Gson gson = new GsonBuilder().create();
	private static final String My_ROOT_TAG = "root";
	
	private static volatile DocumentBuilder db;
	private static volatile Transformer tf;
	
	public XMLEditor(){
		init();
	}
	
	private void init(){
		try {
			if (db == null) {
				synchronized (XMLEditor.class) {
					if (db == null) {
						
							db= DocumentBuilderFactory.newInstance().newDocumentBuilder();
					if (tf == null) {
						tf = TransformerFactory.newInstance().newTransformer();
					}
				}
			} 
			}
		}catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			e.printStackTrace();
		}
	}
	
	private void creatNewXml(File xmlFile) {
		Document document = db.newDocument();
		Element ele = document.createElement(My_ROOT_TAG);
		
		ele.setTextContent("");
		document.appendChild(ele);
		saveXml(document, xmlFile);
	}
	
	private boolean isRightType(Class<?> paraType) {
		return paraType.isPrimitive() || paraType.equals(String.class) || paraType.isAssignableFrom(List.class);
	}
	
	private <T> Object getFieldValue(Class<?> klass, Field field, Object object) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		String fieldName = field.getName();
		String methodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
		Method method = klass.getMethod(methodName, new Class<?>[] {});
		
		return method.invoke(object);
	}
	
	private void makeElementByObject(Document document, Element element, Object object) {
		Class<?> klass = object.getClass();
		Element curelement = document.createElement(klass.getSimpleName());
		Field[] fields = klass.getDeclaredFields();
		
		for (Field field : fields) {
			Class<?> paratype = field.getType();
			if (!isRightType(paratype)) {
				continue;
			}
			int modifiers = field.getModifiers();
			//26 private static final
			if (modifiers == 26) {
			    continue;
			}
			try {
				Object fieldValue = getFieldValue(klass, field, object);
				if (fieldValue == null) {
					continue;
				}
				Element ele = document.createElement(field.getName());
				if (paratype.isPrimitive() || paratype.equals(String.class)) {
					String fieldValueString = fieldValue.toString();
					ele.setTextContent(fieldValueString);
				} else {
					ele.setAttribute("class", fieldValue.getClass().getName());
					@SuppressWarnings("unchecked")
					List<Object> list = (List<Object>) fieldValue;
					for (Object obj : list) {
						makeElementByObject(document, ele, obj);
					}
				}
				curelement.appendChild(ele);
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println(field.getName() + "没有get方法");
			}
		}
		element.appendChild(curelement);
	}
	
	String getAbsolutePathByProjectRoot() {
		File currentPath = new File(".");
		String absolutePath = currentPath.getAbsolutePath();
		int lastDotIndex = absolutePath.lastIndexOf("\\.");
		return absolutePath.substring(0, lastDotIndex + 1);
	}

	public File getAbsolutePathByProjectBin(String path) {
		String absolutePath = getAbsolutePathByProjectRoot();
		String projectBinPath = absolutePath + "bin\\";
		File file = new File(projectBinPath + path);

		return file;
	}
	
	@SuppressWarnings("unchecked")
	private <T> T get(Element element, Class<?> klass) {
		StringBuffer result = new StringBuffer();
		result.append('{');
		Field[] fields = klass.getDeclaredFields();
		boolean first = true;
		for (Field field : fields) {
			String tagName = field.getName();
			
			NodeList eleList = element.getElementsByTagName(tagName);
			Element eleProperty = (Element) eleList.item(0);
			if (eleProperty == null) {
				continue;
			}
			
			String propertyValue = eleProperty.getTextContent();
			propertyValue = propertyValue.replace("\\", "\\\\");
			
			result.append(first ? "" : ",");
			result.append('"').append(tagName).append("\":");
			result.append('"').append(propertyValue).append('"');
			first = false;
		}
		result.append('}');
		System.out.println(klass);
		return (T) gson.fromJson(result.toString(), klass);
	}
	
	public <T> T get(String xmlFilePath, Class<?> klass, String propertyName, String value) {
		if (propertyName == null || value == null) {
			return null;
		}
		File xmlFile = new File(xmlFilePath);
		if (!xmlFile.exists()) {
			return null;
		}
		
		try {
			Document document = db.parse(xmlFile);
			String tagName = klass.getSimpleName();
			NodeList tagList = document.getElementsByTagName(tagName);
			for (int index = 0; index < tagList.getLength(); index++) {
				Element clazz = (Element) tagList.item(index);
				Element property = (Element) clazz.getElementsByTagName(propertyName).item(0);
				String textContent = property.getTextContent();
				if (value.equals(textContent)) {
					return get(clazz, klass);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		return null;
	}
	
	
	public boolean insert(String xmlPath, Object object) {
		File xmlFile = new File(xmlPath);
		if (!xmlFile.exists()) {
			int lastIndex = xmlPath.lastIndexOf("\\");
			String xmlFileDirPath = xmlPath.substring(0, lastIndex);
			File xmlFileDir = new File(xmlFileDirPath);
			xmlFileDir.mkdirs();
			creatNewXml(xmlFile);
		}
		if (object == null) {
			return false;
		}
		
		try {
			Document document = db.parse(xmlPath);
			Element root = (Element) document.getElementsByTagName(My_ROOT_TAG).item(0);
			if (root == null) {
				return false;
			}
			makeElementByObject(document, root, object);
			
			saveXml(document, xmlFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
	
	/**
	 *将document转化为xml文件
	 * @param doc
	 * @param xmlFile
	 */
	private void saveXml(Document doc, File xmlFile) {
		try {
			//换行符
			tf.setOutputProperty("indent", "yes");
			DOMSource domSource = new DOMSource();
			domSource.setNode(doc);
			
			StreamResult streamResult = new StreamResult();
			streamResult.setOutputStream(new FileOutputStream(xmlFile));

			tf.transform(domSource, streamResult);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

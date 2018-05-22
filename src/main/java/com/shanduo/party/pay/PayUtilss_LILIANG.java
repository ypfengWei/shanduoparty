package com.shanduo.party.pay;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


import org.apache.commons.io.FileUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
public class PayUtilss_LILIANG {


	    public static Map<String, Object> Str2Map(String str){
	        try {
//	            long beginTime = System.currentTimeMillis();
	            Document doc = DocumentHelper.parseText(str);
//	            System.out.println(doc.asXML());
	            Map<String, Object> map = PayUtilss_LILIANG.Dom2Map(doc);
//	            System.out.println(map.toString());
//	            System.out.println("Use time:"+(System.currentTimeMillis()-beginTime));
	            return map;
	        } catch (DocumentException e) {
	            e.printStackTrace();
	        }
	        return null;
	    }
	    
	    public static Map<String, Object> Dom2Map(Document doc){ 
	        Map<String, Object> map = new HashMap<String, Object>(); 
	        if(doc == null) 
	            return map; 
	        Element root = doc.getRootElement(); 
	        for (Iterator iterator = root.elementIterator(); iterator.hasNext();) { 
	            Element e = (Element) iterator.next(); 
	            List list = e.elements(); 
	            if(list.size() > 0){ 
	                map.put(e.getName(), Dom2Map(e)); 
	            }else 
	                map.put(e.getName(), e.getText()); 
	        } 
	        return map; 
	    } 
	    
	    @SuppressWarnings("unchecked")
	    public static Map Dom2Map(Element e){ 
	        Map map = new HashMap(); 
	        List list = e.elements(); 
	        if(list.size() > 0){ 
	            for (int i = 0;i < list.size(); i++) { 
	                Element iter = (Element) list.get(i); 
	                List mapList = new ArrayList(); 

	                if(iter.elements().size() > 0){ 
	                    Map m = Dom2Map(iter); 
	                    if(map.get(iter.getName()) != null){ 
	                        Object obj = map.get(iter.getName()); 
	                        if(!obj.getClass().getName().equals("java.util.ArrayList")){ 
	                            mapList = new ArrayList(); 
	                            mapList.add(obj); 
	                            mapList.add(m); 
	                        } 
	                        if(obj.getClass().getName().equals("java.util.ArrayList")){ 
	                            mapList = (List) obj; 
	                            mapList.add(m); 
	                        } 
	                        map.put(iter.getName(), mapList); 
	                    }else 
	                        map.put(iter.getName(), m); 
	                } 
	                else{ 
	                    if(map.get(iter.getName()) != null){ 
	                        Object obj = map.get(iter.getName()); 
	                        if(!obj.getClass().getName().equals("java.util.ArrayList")){ 
	                            mapList = new ArrayList(); 
	                            mapList.add(obj); 
	                            mapList.add(iter.getText()); 
	                        } 
	                        if(obj.getClass().getName().equals("java.util.ArrayList")){ 
	                            mapList = (List) obj; 
	                            mapList.add(iter.getText()); 
	                        } 
	                        map.put(iter.getName(), mapList); 
	                    }else 
	                        map.put(iter.getName(), iter.getText()); 
	                } 
	            } 
	        }else 
	            map.put(e.getName(), e.getText()); 
	        return map; 
	    }
	}
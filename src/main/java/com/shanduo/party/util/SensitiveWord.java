package com.shanduo.party.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/** 
 *  敏感词过滤 工具类   -- 【匹配度高，可以使用】
 *  《高效精准》敏感字&词过滤：http://blog.csdn.net/hubiao_0618/article/details/45076871
 * @author hubiao 
 * @version 0.1 
 * @CreateDate 2015年4月16日 15:28:32 
 */  
public class SensitiveWord {  
    private static StringBuilder replaceAll;//初始化
    private static String encoding = "UTF-8";
    private static String replceStr = "*";
    private static int replceSize = 500;
    private static String fileName = "CensorWords.txt";
    private static List<String> arrayList;
    
    /** 
     * @param str 将要被过滤信息 
     * @return 过滤后的信息 
     */  
    public static String filterInfo(String str) {
    	if(str == null || "".equals(str) || "null".equals(str)) {
    		return "";
    	}
    	InitializationWork();
        StringBuilder buffer = new StringBuilder(str);  
        HashMap<Integer, Integer> hash = new HashMap<Integer, Integer>(arrayList.size());  
        String temp;  
        for(int x = 0; x < arrayList.size();x++) {  
            temp = arrayList.get(x);
            int findIndexSize = 0;
            for(int start = -1;(start=buffer.indexOf(temp,findIndexSize)) > -1;) {  
            	System.out.println("###replace="+temp);
                findIndexSize = start+temp.length();//从已找到的后面开始找  
                Integer mapStart = hash.get(start);//起始位置  
                if(mapStart == null || (mapStart != null && findIndexSize > mapStart))//满足1个，即可更新map  
                {  
                    hash.put(start, findIndexSize); 
                    System.out.println("###敏感词："+buffer.substring(start, findIndexSize));
                }  
            }  
        }  
        Collection<Integer> values = hash.keySet();  
        for(Integer startIndex : values) {  
            Integer endIndex = hash.get(startIndex);  
            buffer.replace(startIndex, endIndex, replaceAll.substring(0,endIndex-startIndex));
        }  
        hash.clear();  
        return buffer.toString();  
    } 
    
    /** 
     *   初始化敏感词库 
     */  
    public static void InitializationWork() {
        replaceAll = new StringBuilder(replceSize);  
        for(int x=0;x < replceSize;x++) {
            replaceAll.append(replceStr);
        }  
        //加载词库  
        arrayList = new ArrayList<String>();  
        InputStreamReader read = null;  
        BufferedReader bufferedReader = null;  
        try {  
            read = new InputStreamReader(SensitiveWord.class.getClassLoader().getResourceAsStream(fileName),encoding);  
            bufferedReader = new BufferedReader(read);  
            for(String txt = null;(txt = bufferedReader.readLine()) != null;){  
                if(!arrayList.contains(txt))  
                    arrayList.add(txt);  
            }  
        } catch (UnsupportedEncodingException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }finally{  
            try {  
                if(null != bufferedReader)  
                bufferedReader.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
            try {  
                if(null != read)  
                read.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
    }  
  
    public static void main(String[] args) {
    	SensitiveWord.filterInfo("aa");
	}
}  

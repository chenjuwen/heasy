package com.heasy.app.core.utils;

import android.content.Context;

import com.heasy.app.core.configuration.ConfigBean;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * Created by Administrator on 2018/11/11.
 */
public class Dom4jUtil {
    private static Logger logger = LoggerFactory.getLogger(Dom4jUtil.class);

    /**
     * 从绝对路径下读xml文件
     * @param fileFullPath 绝对路径
     */
    public static Document read(String fileFullPath) throws Exception{
        logger.debug("Load config file from sdcard: " + fileFullPath);
        SAXReader reader = new SAXReader();
        Document document = reader.read(new FileInputStream(new File(fileFullPath)));
        return document;
    }

    /**
     * 从assets读xml文件
     * @param context
     * @param filename 文件名
     */
    public static Document readFromAssets(Context context, String filename) throws Exception{
        logger.debug("Load config file from assets: " + filename);
        InputStream inputStream = FileUtil.readFromAssets(context, filename);
        SAXReader reader = new SAXReader();
        Document document = reader.read(inputStream);
        return document;
    }

    /**
     * xml文件从assets复制到sdcard的一个绝对路径下
     * @param context
     * @param assetsFromPath assets路径
     * @param toPath sdcard绝对路径
     */
    public static void copyXmlFileFromAssets(Context context, String assetsFromPath, String toPath) {
        try {
            InputStream in = context.getAssets().open(assetsFromPath);
            FileUtil.writeFile(in, toPath);
        }catch(Exception ex){
            logger.error("Failed to copyXmlFileFromAssets", ex);
        }
    }

    /**
     * 获取节点属性值
     * @param node 节点对象
     * @param attrName 属性名
     * @param defaultValue 默认值
     */
    public static String getAttributeValue(Node node, String attrName, String defaultValue){
        String value = getAttributeValue(node, attrName);
        if(StringUtil.isEmpty(value)){
            value = defaultValue;
        }
        return value;
    }

    /**
     * 获取节点属性值
     * @param node 节点对象
     * @param attrName 属性名
     */
    public static String getAttributeValue(Node node, String attrName){
        if(node == null){
            return "";
        }

        String value = node.valueOf("@"+attrName);
        if(value == null){
            return "";
        }else{
            return value.trim();
        }
    }

    /**
     * 获取某个节点的文本信息
     * @param document
     * @param nodePath 节点全路径，比如 /字段数据/基本数据/表名
     */
    public static String getNodeText(Document document, String nodePath){
        Node node = document.selectSingleNode(nodePath);
        return getNodeText(node);
    }

    /**
     * 获取某个节点的文本信息
     * @param element
     * @param nodeName 节点名，比如 字段
     */
    public static String getNodeText(Element element, String nodeName){
        Node node = element.selectSingleNode(nodeName);
        return getNodeText(node);
    }

    /**
     * 获取节点的文本信息
     * @param node 节点对象
     */
    public static String getNodeText(Node node){
        if(node != null){
            return StringUtil.trimToEmpty(node.getText());
        }
        return "";
    }

    /**
     * 格式化xml字符串
     * @param xmlStr xml字符串
     * @param encoding 编码格式
     */
    public static String formatXML(String xmlStr, String encoding) throws Exception {
        SAXReader reader = new SAXReader();
        Document document = reader.read(new StringReader(xmlStr));
        String resultXml = null;
        XMLWriter writer = null;
        if(document != null){
            try {
                StringWriter stringWriter = new StringWriter();

                OutputFormat format = new OutputFormat("    ", true, encoding);
                writer = new XMLWriter(stringWriter, format);
                writer.write(document);
                writer.flush();
                resultXml = stringWriter.getBuffer().toString();

            }finally{
                if(writer != null){
                    try{
                        writer.close();
                        writer = null;
                    }catch(IOException ex){
                        logger.error("Failed to formatXML", ex);
                    }
                }
            }
        }
        return resultXml;
    }

    /**
     * 格式化xml字符串
     * @param xmlStr xml字符串
     */
    public static String formatXML(String xmlStr) throws Exception {
        return formatXML(xmlStr, ConfigBean.DEFAULT_CHARSET_UTF8);
    }

}

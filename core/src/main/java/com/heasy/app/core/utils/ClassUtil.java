package com.heasy.app.core.utils;

import android.content.Context;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import dalvik.system.DexFile;
import dalvik.system.PathClassLoader;

public class ClassUtil {
    /**
     * 加载指定包及其子包下的类
     * @param context
     * @param basePackages 类包路径数组
     * @return
     */
    public static List<String> getClassFiles(Context context, String[] basePackages){
        List<String> classNameList = new ArrayList<String>();
        try {
            String packageCodePath = context.getPackageCodePath();

            String parentPath = "";
            File[] files;
            if (packageCodePath.endsWith("/base.apk") ) {
                parentPath = packageCodePath.substring(0, packageCodePath.lastIndexOf("/"));
                File dir = new File(parentPath);
                files = dir.listFiles();
            } else {
                parentPath = packageCodePath.substring(0, packageCodePath.lastIndexOf("/"));
                files = new File[] {new File(packageCodePath)};
            }

            for(File file : files){
                if(file.getName().endsWith(".apk")) {
                    String filePath = parentPath + File.separator + file.getName();

                    DexFile dexFile = new DexFile(filePath);
                    Enumeration<String> enume = dexFile.entries();
                    while (enume.hasMoreElements()) {
                        String className = (String) enume.nextElement();
                        if (className.indexOf("$") < 0 && basePackages != null) {
                            for(String pkg : basePackages){
                                if(className.contains(pkg)){
                                    classNameList.add(className);
                                }
                            }
                        }
                    }
                }
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return  classNameList;
    }

    /**
     * 过滤出指定超类的所有子类
     * @param classNameList 类名集合
     * @param superClass 超类
     * @return
     */
    public static List<Class> filterBySuperclass(List<String> classNameList, Class superClass){
        List<Class> resultList = new ArrayList<>();
        try {
            PathClassLoader classLoader = (PathClassLoader) Thread.currentThread().getContextClassLoader();

            for (String className : classNameList) {
                Class<?> entryClass = Class.forName(className, true, classLoader);
                boolean b = superClass.isAssignableFrom(entryClass);
                if(b){
                    if(!entryClass.isInterface() &&
                            !superClass.getSimpleName().equalsIgnoreCase(entryClass.getSimpleName())) {
                        resultList.add(entryClass);
                    }
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return resultList;
    }

}

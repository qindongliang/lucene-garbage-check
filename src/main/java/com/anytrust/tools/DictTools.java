package com.anytrust.tools;

import com.google.common.collect.Lists;
import com.google.common.io.CharStreams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by qindongliang on 2015/12/2.
 * 初始化词典数据使用
 */
public class DictTools {

    /**主词典*/
    public static List<String> main_kws=new ArrayList<String>();
    /**辅助词典**/
    public static List<String> assist_kws=new ArrayList<String>();
    /**log记录**/
    static Logger log=LoggerFactory.getLogger(DictTools.class);


    static {
        try {
            //读取resource下文件使用下面代码，能够在IDE里面使用，也能在打成jar包后，单独使用
            InputStream mainStream = DictTools.class.getClassLoader().getResourceAsStream("main.txt");
            String  main = CharStreams.toString(new InputStreamReader(mainStream, "UTF-8" ));
            InputStream assitStream  = DictTools.class.getClassLoader().getResourceAsStream("assist.txt");
            String assist = CharStreams.toString(new InputStreamReader(assitStream, "UTF-8" ));
             main_kws= Lists.newArrayList(main.split("\n"));
             assist_kws=Lists.newArrayList(assist.split("\n"));
        }catch (Exception e){
            log.error("初始化词典错误!",e);
        }
    }

}

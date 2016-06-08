package com.anytrust.tools;

import com.anytrust.model.Word;
import com.google.common.collect.Lists;
import com.google.common.io.CharStreams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by qindongliang on 2015/12/2. 初始化词典数据使用
 */
public class DictTools {

	/** log记录 **/
	private static final Logger LOG = LoggerFactory.getLogger(DictTools.class);
	
	/** 主词典 */
	public static List<Word> main_kws = new ArrayList<Word>();
	/** 辅助词典 **/
	public static List<String> assist_kws = new ArrayList<String>();
	

	static {
		try {
			// 读取resource下文件使用下面代码，能够在IDE里面使用，也能在打成jar包后，单独使用
			InputStream mainStream = DictTools.class.getClassLoader().getResourceAsStream("main.txt");
			String main = CharStreams.toString(new InputStreamReader(mainStream, "UTF-8"));
			InputStream assitStream = DictTools.class.getClassLoader().getResourceAsStream("assist.txt");
			String assist = CharStreams.toString(new InputStreamReader(assitStream, "UTF-8"));
			main_kws =buildModelList(main.split("\n"));
			assist_kws = Lists.newArrayList(assist.split("\n"));
		} catch (Exception e) {
			LOG.error("初始化词典错误!", e);
		}
	}

	/***
	 * 加载词库，组装集合对象
	 * @param datas
	 * @return 构建数据集合
     */
	static List<Word> buildModelList(String datas[]){
		List<Word> words=Lists.newArrayList();
		for(String line:datas){

			if(line.trim().length()<1){
				continue;
			}


			String ds[]=line.trim().split("#");

			Word word=new Word();
			word.setWord(ds[0]);//设置word
			if(ds.length==2){
				word.setDistance(Integer.parseInt(ds[1]));//设置词间距
			}
			words.add(word);
		}
		return words;
	}








}

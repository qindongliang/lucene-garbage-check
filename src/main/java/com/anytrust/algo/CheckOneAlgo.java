package com.anytrust.algo;

import com.anytrust.model.Word;
import com.anytrust.model.MonitorType;
import com.anytrust.tools.DictTools;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.index.memory.MemoryIndex;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by qindongliang on 2016/1/7.
 * 根据规则识别是否为垃圾数据支持双向和正向匹配
 * 反向匹配需要稍改代码才能实现
 */
public class CheckOneAlgo {

    //IK中文分词器
      IKAnalyzer analyzer=new IKAnalyzer(false);
    //内存索引处理
      MemoryIndex index = new MemoryIndex();

    static {
        //设置Lucene的boolean query条件数最大支持个数
        BooleanQuery.setMaxClauseCount(10000);
    }

    static Logger logger= LoggerFactory.getLogger(CheckOneAlgo.class);

    /***
     *
     * @param text 分词的文本
     * @param words 存储分词后的词组信息
     * @param keyOrds 记录分词字符串
     */
    public void buildPhraseMeta(String text, Map<String,Word> words, ArrayList<String> keyOrds){
        //采用空间换时间算法，提高执行效率
        try {
            TokenStream token = analyzer.tokenStream("word", new StringReader(text));
            token.reset();
            CharTermAttribute term = token.addAttribute(CharTermAttribute.class);//term信息
            int count=0;
            while (token.incrementToken()) {
                //获取每一个分词的term
                String key=term.toString().trim();
                if(words.get(key)==null) {
                    Word word=new Word();
                    word.setWord(key);
                    word.getOrds().add(count);//加入分词顺序
                    words.put(key,word);
                }else {
                    words.get(key).getOrds().add(count);//如果已经存在，再次加入分词顺序
                }
                keyOrds.add(key);
                count++;
            }
            token.end();
            token.close();
        }catch (Exception e){
            logger.error("构建词组元数据出错!",e);
        }

    }

    /***
     * @param text 需要校验的文本数据
     * @return 是否通过检验
     */
    public boolean checkWenShu(String text){
        Map<String,Word> words=new HashMap<String, Word>();
        ArrayList<String> keyOrds=new ArrayList<String>();
        A:for(Word kw: DictTools.main_kws) {//遍历主词库
            B:for (String hkw : DictTools.assist_kws) { //遍历辅助词库
                StringBuffer sb = new StringBuffer();
                sb.append("tc:\"").append(hkw.trim()).append(kw.getWord()).append("\"~").append(kw.getDistance()).append("  ");
                boolean flag = checkQuery(text, sb.toString());
                //一次校验通过，符合双向距离匹配
                if (flag) {
                    logger.info("一次校验返回true！{}{}距离{}",hkw.trim(),kw.getWord(),kw.getDistance());
                    //为true的情况下，继续校验单向匹配
                    buildPhraseMeta(text, words, keyOrds);

                    //处理对象为空的bug,说明辅助词在本篇文档里面不曾出现过
                    if(words.get(hkw.trim())==null){
                        continue ;
                    }

                    //得到ords
                    ArrayList<Integer> ords = words.get(hkw.trim()).getOrds();
                    C:for (int ord : ords) {
                        D:for (int i = 0; i < kw.getDistance(); i++) {
                            int findPos = ord + i;
                            if (findPos < keyOrds.size()) {//确保数组不能超界
                                //单向比较字符串，如果成立，就结束循环，代表本次校验通过
                                if (keyOrds.get(findPos).equals(kw.getWord().trim())) {
                                    logger.info("二次校验返回true ： 查询字符串： " + sb.toString());
                                    return true;
                                }
                            }
                        }
                    }
                    //二次校验通过，符合单向正序匹配
                    logger.info("二次校验返回fasle ：单向匹配失败!");
                }
            }
        }

    return false;
    }

    /***
     * @param text 需要校验的文本数据
     * @return 是否通过检验
     */
    public boolean checkGongGao(String text){
        StringBuffer sb =new StringBuffer("(");
        for(Word kw: DictTools.main_kws) {//遍历主词库
            sb.append("tc:\"").append(kw.getWord().trim()).append("\"  ");
        }
        sb.append(" ) ");
        return checkQuery(text,sb.toString());
    }




    public  boolean checkDoc(String text, MonitorType type){
            switch (type) {
                case LITIGATION://代表文书  0105
                    return  checkWenShu(text);
                case ANNOUNCEMENT://公告 0104
                    return  checkGongGao(text);
                default:
                    logger.error("未知类型：{}",type);
                    break;
            }
        return false;
    }








    /***
     *  对一段文本执行垃圾数据识别功能
     *  返回true说明是有效数据
     *  返回false说明是垃圾数据
      * @param text 监测的文本
     *  @param query   查询的条件
     * @return
     */
  public   boolean checkQuery(String text, String query){
        QueryParser parser = new QueryParser("", analyzer);
        index.addField("tc", text, analyzer);
      try {
          float score = index.search(parser.parse(query));
          if(score > 0.0f){
            return true;//正确数据
          }else{
            return false;//垃圾数据
          }

      }catch (Exception e){
          logger.error("识别垃圾数据异常!",e);
      }finally {
          index.reset();//重置index引擎，服复用类对象
      }
      return false;
  }
}

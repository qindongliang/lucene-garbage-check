package com.anytrust.algo;

import com.anytrust.model.MonitorType;
import com.anytrust.tools.DictTools;
import org.apache.lucene.index.memory.MemoryIndex;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wltea.analyzer.lucene.IKAnalyzer;

/**
 * Created by qindongliang on 2016/1/7.
 * 根据规则识别是否为垃圾数据
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


    /**构建查询query
     * @param type  根据类型构建
     * */
    private   String buildQuery(MonitorType type){

        StringBuffer sb =new StringBuffer("(");
        for(String kw: DictTools.main_kws){//遍历主词库
            switch (type) {
                case LITIGATION://代表文书  0105
                    for (String hkw : DictTools.assist_kws) { //遍历辅助词库
                        sb.append("tc:\"").append(hkw + kw).append("\"~20  ");
                    }
                    break;
                case ANNOUNCEMENT://公告 0104
                    sb.append("tc:\"").append(kw).append("\"  ");
                    break;
                default:
                    logger.error("未知类型：{}",type);
                    break;

            }
        }
        sb.append(" ) ");
        return  sb.toString();
    }



    /***
     *  对一段文本执行垃圾数据识别功能
     *  返回true说明是有效数据
     *  返回false说明是垃圾数据
      * @param text 监测的文本
     * @return
     */
  public   boolean checkDoc(String text,MonitorType type){
        String query=buildQuery(type);
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

package com.anytrust.query;

import com.google.common.hash.Hashing;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrClient;

/**
 * Created by qindongliang on 2016/5/24.
 */
public class MD5Query {

    private   static HttpSolrClient sc=new HttpSolrClient("http://10.0.0.171:8984/solr/md5");


    /***
     *
     * @param data 转换数据
     * @return 返回校验后的数据
     */
    public static String nullToEmpty(String data){
        if(data!=null){
            return data.trim();
        }
        return "";
    }



    /**
     * @param params 数组参数
     * @return  true代表没发现重复，false代表重复
     */
    public static boolean checkMD5(String... params)throws Exception {

        SolrQuery sq=new SolrQuery();
        sq.set("q","*:*");
        sq.set("fq","md5:"+md5(params));
        sq.setRows(1);
        long hits=sc.query(sq).getResults().getNumFound();
        if(hits==0){
            return true; //没有重复
        }
        return false;//有重复
    }


    /***
     *
     * @param datas 参数值
     * @return md5值
     */
    private static String md5(String... datas){
        StringBuffer sb=new StringBuffer();
        for(String data:datas){
            sb.append(nullToEmpty(data));//Null值统一转成空值处理
        }
        return Hashing.md5().hashString(sb.toString()).toString();
    }






}

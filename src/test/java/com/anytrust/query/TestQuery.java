package com.anytrust.query;

import org.junit.Test;

/**
 * Created by qindongliang on 2016/5/24.
 */
public class TestQuery {



    @Test
    public void testQuery()throws Exception{

        String datas[]={"原告陶某诉被告封某某健康权纠纷一案民事判决书"
        ,"(2014)铁民初字第1000号"
        ,"01"
        ,"05"
        ,""};

        boolean flag=MD5Query.checkMD5(datas);
        if(flag){
            System.out.println("不重复");
        }else{
            System.out.println("有重复");
        }

    }

}

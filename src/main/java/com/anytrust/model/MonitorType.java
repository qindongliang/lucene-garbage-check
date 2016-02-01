package com.anytrust.model;

/**
 * Created by qindongliang on 2015/12/4.
 */
public enum MonitorType {


    LITIGATION("0105诉讼"),//诉讼业务
    ANNOUNCEMENT("0104公告");//公告业务


    public final String value;

    public String getValue() {
        return value;
    }

    private MonitorType(String value) {
        this.value=value;
    }

    /****
     * 根据一个值获取该值
     * @param value
     * @return
     */
    public static MonitorType getBusiness(String value){
        for(MonitorType type : MonitorType.values()){
            if(type.value.equals(value)){
                return type;
            }
        }
        try {
            throw new NullPointerException("没有此枚举值");
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }



}

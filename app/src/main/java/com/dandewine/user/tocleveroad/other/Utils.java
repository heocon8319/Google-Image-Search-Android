package com.dandewine.user.tocleveroad.other;


public class Utils {

    private  static StringBuilder builder = new StringBuilder();

    public static String concat(Object...rows){
        builder.setLength(0);
        for (Object o:rows)
            builder.append(o.toString());
        return builder.toString();
    }
}

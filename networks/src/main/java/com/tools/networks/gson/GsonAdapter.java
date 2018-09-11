package com.tools.networks.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonAdapter {
    public static Gson buildGson() {
        /**
         * 返回基本数据类型 设置
         */
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Integer.class, new IntegerDefault0Adater())
                .registerTypeAdapter(int.class, new IntegerDefault0Adater())
                .registerTypeAdapter(Double.class, new DoubleDefault0Adapter())
                .registerTypeAdapter(double.class, new DoubleDefault0Adapter())
                .registerTypeAdapter(Long.class, new LongDefault0Adapter())
                .registerTypeAdapter(long.class, new LongDefault0Adapter())
                .create();

        return gson;
    }
}

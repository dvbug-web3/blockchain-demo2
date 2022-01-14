package com.dvbug.demo2.util;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class GsonUtil {
    private static final GsonBuilder gsonBuilder = new GsonBuilder().setExclusionStrategies(new ExclusionStrategy() {
        @Override
        public boolean shouldSkipField(FieldAttributes fieldAttributes) {
            return "bitSet".equals(fieldAttributes.getName());
        }

        @Override
        public boolean shouldSkipClass(Class<?> aClass) {
            return false;
        }
    }).setDateFormat("yyyy-MM-dd HH:mm:sss");
    //.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
    private static final Gson gson = gsonBuilder.create();
    private static final Gson gson_p = gsonBuilder.setPrettyPrinting().create();

    public static Gson instance() {
        return instance(false);
    }

    public static Gson instance(boolean pretty) {
        return pretty ? gson_p : gson;
    }

    public static String toJson(Object object) {
        return toJson(object, false);
    }

    public static String toJson(Object object, boolean pretty) {
        return (pretty ? gson_p : gson).toJson(object);
    }

    public static <T> T fromJson(String src, Class<T> clazz) {
        return gson.fromJson(src, clazz);
    }

    public static <T> List<T> fromJsonArray(String src, Class<T> clazz) {
        TypeToken<?> token = TypeToken.getArray(clazz);
        T[] o = gson.fromJson(src, token.getType());
        return Arrays.asList(o);
    }
}

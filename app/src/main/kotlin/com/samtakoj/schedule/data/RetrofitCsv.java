package com.samtakoj.schedule.data;

import com.nytimes.android.external.store3.base.Parser;
import com.samtakoj.schedule.common.data.Position;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import okio.BufferedSource;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * Created by artsiom.chuiko on 21/03/2017.
 */

/**
 * An example of How_To_Use:
 *
 * StoreBuilder.<BarCode, BufferedSource, List<Stop>>parsedWithKey()
 *      .parser(RetrofitCsv.createSourceParser(Stop.class, true, ";"))
 *      .open();
 *
 *
 * Retrofit.Builder()
 *      .baseUrl("http://...")
 *      .addConverterFactory(RetrofitCsv.createConverterFactory(Stop.class, true, ","))
 *      .build()
 *      .create(AnyClassYouWant.class)
 *
 */
public final class RetrofitCsv {
    private RetrofitCsv() {}

    public static <Parsed> java.util.function.Function<BufferedSource, List<Parsed>> createSourceParser(final Class<Parsed> clazz, final boolean skipHeaders, final String regex) {
        return new java.util.function.Function<BufferedSource, List<Parsed>>() {
            @Override
            public List<Parsed> apply(BufferedSource source) {
                return convertSourceToList(source, clazz, skipHeaders, regex);
            }
        };
    }

    public static <T> Converter.Factory createConverterFactory(final Class<T> clazz, final boolean skipHeaders, final String regex) {
        return new Converter.Factory() {
            @Override
            public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
                return new Converter<ResponseBody, List<T>>() {
                    @Override
                    public List<T> convert(ResponseBody value) throws IOException {
                        return convertSourceToList(value.source(), clazz, skipHeaders, regex);
                    }
                };
            }
        };
    }

    private static <T> List<T> convertSourceToList(final BufferedSource source, final Class<T> clazz, final boolean skipHeaders, final String regex) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(source.inputStream(), Charset.forName("UTF-8")))) {
            String line;
            boolean skip = skipHeaders;
            final List<T> parsed = Collections.emptyList();
            while ((line = reader.readLine()) != null) {
                if (skip) skip = false;
                else {
                    parsed.add(createObject(clazz, line.split(regex)));
                }
            }
            return parsed;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static <Parsed> Parsed createObject(Class<Parsed> clazz, String...data) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        final Object object = clazz.newInstance();
        final Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(Position.class)) {
                final int position = field.getAnnotation(Position.class).value();
                field.set(object, typeAdjusters.get(field.getType()).adjust(data[position - 1]));
            }
        }
        return (Parsed) object;
    }

    private interface TypeAdjuster<Type> {
        Type adjust(String o);
    }

    private static final TypeAdjuster<String> stringAdjuster = new TypeAdjuster<String>() {
        @Override
        public String adjust(String o) {
            return o;
        }
    };

    private static final TypeAdjuster<Integer> integerAdjuster = new TypeAdjuster<Integer>() {
        @Override
        public Integer adjust(String o) {
            return Integer.parseInt(o);
        }
    };

    private static final TypeAdjuster<Double> doubleAdjuster = new TypeAdjuster<Double>() {
        @Override
        public Double adjust(String o) {
            return Double.parseDouble(o);
        }
    };

    private static final TypeAdjuster<Float> floatAdjuster = new TypeAdjuster<Float>() {
        @Override
        public Float adjust(String o) {
            return Float.parseFloat(o);
        }
    };

    private static final TypeAdjuster<Boolean> boolAdjuster = new TypeAdjuster<Boolean>() {
        @Override
        public Boolean adjust(String o) {
            return Boolean.parseBoolean(o);
        }
    };

    private static final TypeAdjuster<Long> longAdjuster = new TypeAdjuster<Long>() {
        @Override
        public Long adjust(String o) {
            return Long.parseLong(o);
        }
    };

    private static final Map<Class<?>, TypeAdjuster<?>> typeAdjusters = new HashMap<Class<?>, TypeAdjuster<?>>() {{
        put(Integer.class, integerAdjuster);
        put(int.class, integerAdjuster);
        put(Double.class, doubleAdjuster);
        put(double.class, doubleAdjuster);
        put(Float.class, floatAdjuster);
        put(float.class, floatAdjuster);
        put(Boolean.class, boolAdjuster);
        put(boolean.class, boolAdjuster);
        put(String.class, stringAdjuster);
        put(long.class, longAdjuster);
        put(Long.class, longAdjuster);
    }};
}


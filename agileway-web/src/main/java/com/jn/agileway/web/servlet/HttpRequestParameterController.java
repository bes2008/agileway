package com.jn.agileway.web.servlet;

import com.jn.agileway.web.filter.rr.RRHolder;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Function;
import com.jn.langx.util.function.Predicate;

import java.util.List;

public abstract class HttpRequestParameterController
{
    private static final List<String> TRUE_VALUES;

    protected String getParameter(final String name) {
        if (name == null) {
            throw new IllegalArgumentException("Get http parameter NULL");
        }
        return RRHolder.getRequest().getParameter(name);
    }

    protected String getStringParameter(final String name) {
        return this.getParameter(name);
    }

    protected List<String> getParameters(final String name) {
        final String[] values = RRHolder.getRequest().getParameterValues(name);
        if (values != null) {
            return Collects.asList(values);
        }
        return null;
    }

    protected List<String> getStringParameters(final String name) {
        return this.getParameters(name);
    }

    protected Integer getIntParameter(final String name) {
        final String value = this.getParameter(name);
        Integer ret = null;
        if (!Strings.isBlank(value)) {
            ret = Integer.parseInt(value);
        }
        return ret;
    }

    protected List<Integer> getIntParameters(final String name) {
        final List<String> valueOpt = this.getParameters(name);
        List<Integer> ret = null;
        if (valueOpt != null) {
            ret = Pipeline.of(valueOpt).map(new Function<String, Integer>() {
                @Override
                public Integer apply(String input) {
                    return Integer.parseInt(input);
                }
            }).asList();
        }
        return ret;
    }

    protected Long getLongParameter(final String name) {
        final String valueOpt = this.getParameter(name);
        Long ret = null;
        if (!Strings.isBlank(valueOpt)) {
            ret = Long.parseLong(valueOpt);
        }
        return ret;
    }

    protected List<Long> getLongParameters(final String name) {
        final List<String> valueOpt = this.getParameters(name);
        List<Long> ret = null;
        if (valueOpt != null) {
            ret = Pipeline.of(valueOpt).map(new Function<String, Long>() {
                @Override
                public Long apply(String input) {
                    return Long.parseLong(input);
                }
            }).asList();
        }
        return ret;
    }

    protected Float getFloatParameter(final String name) {
        final String valueOpt = this.getParameter(name);
        Float ret = null;
        if (!Strings.isBlank(valueOpt)) {
            ret = Float.parseFloat(valueOpt);
        }
        return ret;
    }

    protected List<Float> getFloatParameters(final String name) {
        final List<String> valueOpt = this.getParameters(name);
        List<Float> ret = null;
        if (valueOpt != null) {
            ret = Pipeline.of(valueOpt).map(new Function<String, Float>() {
                @Override
                public Float apply(String input) {
                    return Float.parseFloat(input);
                }
            }).asList();
        }
        return ret;
    }

    protected Double getDoubleParameter(final String name) {
        final String valueOpt = this.getParameter(name);
        Double ret = null;
        if (!Strings.isBlank(valueOpt)) {
            ret = Double.parseDouble(valueOpt);
        }
        return ret;
    }

    protected List<Double> getDoubleParameters(final String name) {
        final List<String> valueOpt = this.getParameters(name);
        List<Double> ret = null;
        if (valueOpt != null) {
            ret = Pipeline.of(valueOpt).map(new Function<String, Double>() {
                @Override
                public Double apply(String input) {
                    return Double.parseDouble(input);
                }
            }).asList();
        }
        return ret;
    }

    protected static Boolean isTrue(final String value) {
        return Pipeline.of(TRUE_VALUES).anyMatch(new Predicate<String>() {
            @Override
            public boolean test(String x) {
                return x.equalsIgnoreCase(value);
            }
        });
    }

    protected Boolean getBooleanParameter(final String name) {
        final String valueOpt = this.getParameter(name);
        if (!Strings.isBlank(valueOpt)) {
            return isTrue(name);
        }
        return null;
    }

    protected Boolean getBooleanParameter(final String name, final boolean defaultValue) {
        final String valueOpt = this.getParameter(name);
        final Boolean ret = null;
        if (!Strings.isBlank(valueOpt)) {
            return isTrue(name);
        }
        return defaultValue;
    }

    protected List<Boolean> getBooleanParameters(final String name) {
        final List<String> valueOpt = this.getParameters(name);
        List<Boolean> ret = null;
        if (valueOpt != null) {
            ret = Pipeline.of(valueOpt).map(new Function<String,Boolean>(){
                @Override
                public Boolean apply(String input) {
                    return isTrue(input);
                }
            }).asList();
        }
        return ret;
    }

    static {
        TRUE_VALUES = Collects.newArrayList("true", "1", "on", "yes");
    }
}


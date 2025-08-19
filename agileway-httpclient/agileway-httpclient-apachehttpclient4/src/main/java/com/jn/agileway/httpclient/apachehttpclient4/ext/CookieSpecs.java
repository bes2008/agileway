package com.jn.agileway.httpclient.apachehttpclient4.ext;

import com.jn.langx.util.collection.Collects;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.util.PublicSuffixMatcher;
import org.apache.http.conn.util.PublicSuffixMatcherLoader;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.impl.cookie.DefaultCookieSpecProvider;
import org.apache.http.impl.cookie.IgnoreSpecProvider;
import org.apache.http.impl.cookie.NetscapeDraftSpecProvider;
import org.apache.http.impl.cookie.RFC6265CookieSpecProvider;

import java.util.List;

public class CookieSpecs {
    private CookieSpecs() {

    }

    private static final List<String> COOKIE_DATE_PATTERNS = Collects.asList(
            "EEE',' dd-MMM-yyyy HH:mm:ss 'GMT'",
            "EEE',' dd MMM yyyy HH:mm:ss 'GMT'",
            "EEE MMM dd yyyy HH:mm:ss 'GMT'Z",
            "EEE',' dd-MMM-yy HH:mm:ss 'GMT'",
            "EEE',' dd MMM yy HH:mm:ss 'GMT'",
            "EEE MMM dd yy HH:mm:ss 'GMT'Z"
    );


    public static RegistryBuilder<CookieSpecProvider> createDefaultCookieSpecProviderBuilder() {
        return createDefaultCookieSpecProviderBuilder(PublicSuffixMatcherLoader.getDefault());
    }


    public static RegistryBuilder<CookieSpecProvider> createDefaultCookieSpecProviderBuilder(
            final PublicSuffixMatcher publicSuffixMatcher) {
        return createDefaultCookieSpecProviderBuilder(publicSuffixMatcher, COOKIE_DATE_PATTERNS);
    }


    public static RegistryBuilder<CookieSpecProvider> createDefaultCookieSpecProviderBuilder(
            final PublicSuffixMatcher publicSuffixMatcher,
            List<String> expiresDatePatterns) {
        return createDefaultCookieSpecProviderBuilder(null, publicSuffixMatcher, expiresDatePatterns);
    }


    /**
     * Creates a builder containing the default registry entries, using the provided public suffix matcher.
     */
    public static RegistryBuilder<CookieSpecProvider> createDefaultCookieSpecProviderBuilder(
            DefaultCookieSpecProvider.CompatibilityLevel compatibilityLevel,
            final PublicSuffixMatcher publicSuffixMatcher,
            List<String> expiresDatePatterns) {

        compatibilityLevel = compatibilityLevel == null ? DefaultCookieSpecProvider.CompatibilityLevel.DEFAULT : compatibilityLevel;
        String[] datePatterns = expiresDatePatterns == null ? null : Collects.toArray(expiresDatePatterns, String[].class);


        final CookieSpecProvider defaultProvider = new DefaultCookieSpecProvider(compatibilityLevel, publicSuffixMatcher, datePatterns, false);
        final CookieSpecProvider laxStandardProvider = new RFC6265CookieSpecProvider(
                RFC6265CookieSpecProvider.CompatibilityLevel.RELAXED, publicSuffixMatcher);
        final CookieSpecProvider strictStandardProvider = new RFC6265CookieSpecProvider(
                RFC6265CookieSpecProvider.CompatibilityLevel.STRICT, publicSuffixMatcher);
        return RegistryBuilder.<CookieSpecProvider>create()
                .register(org.apache.http.client.config.CookieSpecs.DEFAULT, defaultProvider)
                .register("best-match", defaultProvider)
                .register("compatibility", defaultProvider)
                .register(org.apache.http.client.config.CookieSpecs.STANDARD, laxStandardProvider)
                .register(org.apache.http.client.config.CookieSpecs.STANDARD_STRICT, strictStandardProvider)
                .register(org.apache.http.client.config.CookieSpecs.NETSCAPE, new NetscapeDraftSpecProvider())
                .register(org.apache.http.client.config.CookieSpecs.IGNORE_COOKIES, new IgnoreSpecProvider());
    }
}

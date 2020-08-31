package com.jn.agileway.web.filter.encoding;

import com.jn.agileway.web.filter.OncePerRequestFilter;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.util.Preconditions;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

public class EncodingFilter extends OncePerRequestFilter {

    @Nullable
    private String encoding;

    private boolean forceRequestEncoding = false;

    private boolean forceResponseEncoding = false;


    /**
     * Create a default {@code CharacterEncodingFilter},
     * with the encoding to be set via {@link #setEncoding}.
     *
     * @see #setEncoding
     */
    public EncodingFilter() {
    }

    /**
     * Create a {@code CharacterEncodingFilter} for the given encoding.
     *
     * @param encoding the encoding to apply
     * @see #setEncoding
     */
    public EncodingFilter(String encoding) {
        this(encoding, false);
    }

    /**
     * Create a {@code CharacterEncodingFilter} for the given encoding.
     *
     * @param encoding      the encoding to apply
     * @param forceEncoding whether the specified encoding is supposed to
     *                      override existing request and response encodings
     * @see #setEncoding
     * @see #setForceEncoding
     */
    public EncodingFilter(String encoding, boolean forceEncoding) {
        this(encoding, forceEncoding, forceEncoding);
    }

    /**
     * Create a {@code CharacterEncodingFilter} for the given encoding.
     *
     * @param encoding              the encoding to apply
     * @param forceRequestEncoding  whether the specified encoding is supposed to
     *                              override existing request encodings
     * @param forceResponseEncoding whether the specified encoding is supposed to
     *                              override existing response encodings
     * @see #setEncoding
     * @see #setForceRequestEncoding(boolean)
     * @see #setForceResponseEncoding(boolean)
     */
    public EncodingFilter(String encoding, boolean forceRequestEncoding, boolean forceResponseEncoding) {
        Preconditions.checkNotEmpty(encoding, "Encoding must not be empty");
        this.encoding = encoding;
        this.forceRequestEncoding = forceRequestEncoding;
        this.forceResponseEncoding = forceResponseEncoding;
    }


    /**
     * Set the encoding to use for requests. This encoding will be passed into a
     * {@link javax.servlet.http.HttpServletRequest#setCharacterEncoding} call.
     * <p>Whether this encoding will override existing request encodings
     * (and whether it will be applied as default response encoding as well)
     * depends on the {@link #setForceEncoding "forceEncoding"} flag.
     */
    public void setEncoding(@Nullable String encoding) {
        this.encoding = encoding;
    }

    /**
     * Return the configured encoding for requests and/or responses.
     */
    @Nullable
    public String getEncoding() {
        return this.encoding;
    }

    /**
     * Set whether the configured {@link #setEncoding encoding} of this filter
     * is supposed to override existing request and response encodings.
     * <p>Default is "false", i.e. do not modify the encoding if
     * {@link javax.servlet.http.HttpServletRequest#getCharacterEncoding()}
     * returns a non-null value. Switch this to "true" to enforce the specified
     * encoding in any case, applying it as default response encoding as well.
     * <p>This is the equivalent to setting both {@link #setForceRequestEncoding(boolean)}
     * and {@link #setForceResponseEncoding(boolean)}.
     *
     * @see #setForceRequestEncoding(boolean)
     * @see #setForceResponseEncoding(boolean)
     */
    public void setForceEncoding(boolean forceEncoding) {
        this.forceRequestEncoding = forceEncoding;
        this.forceResponseEncoding = forceEncoding;
    }

    /**
     * Set whether the configured {@link #setEncoding encoding} of this filter
     * is supposed to override existing request encodings.
     * <p>Default is "false", i.e. do not modify the encoding if
     * {@link javax.servlet.http.HttpServletRequest#getCharacterEncoding()}
     * returns a non-null value. Switch this to "true" to enforce the specified
     * encoding in any case.
     */
    public void setForceRequestEncoding(boolean forceRequestEncoding) {
        this.forceRequestEncoding = forceRequestEncoding;
    }

    /**
     * Return whether the encoding should be forced on requests.
     */
    public boolean isForceRequestEncoding() {
        return this.forceRequestEncoding;
    }

    /**
     * Set whether the configured {@link #setEncoding encoding} of this filter
     * is supposed to override existing response encodings.
     * <p>Default is "false", i.e. do not modify the encoding.
     * Switch this to "true" to enforce the specified encoding
     * for responses in any case.
     */
    public void setForceResponseEncoding(boolean forceResponseEncoding) {
        this.forceResponseEncoding = forceResponseEncoding;
    }

    /**
     * Return whether the encoding should be forced on responses.
     */
    public boolean isForceResponseEncoding() {
        return this.forceResponseEncoding;
    }


    protected void doFilterInternal(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        String encoding = getEncoding();
        if (encoding != null) {
            if (isForceRequestEncoding() || request.getCharacterEncoding() == null) {
                request.setCharacterEncoding(encoding);
            }
            if (isForceResponseEncoding()) {
                response.setCharacterEncoding(encoding);
            }
        }
        chain.doFilter(request, response);
    }


}


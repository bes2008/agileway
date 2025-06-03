package com.jn.agileway.httpclient.core;

public class MessageHeaderConstants {


    /**
     * 在请求 Message Header中 记录 request 相关的对象的Key 前缀
     */
    private static String REQUEST_HEADER_KEY_PREFIX = "agileway_http_request_key_";


    /**
     * 在请求 Message Header中 记录 reply 对象
     */
    static final String REQUEST_KEY_UNDERLYING_RESPONSE = REQUEST_HEADER_KEY_PREFIX + "underlying_response";
    /**
     * 在请求 Message Header中 记录 Retry 对象
     */
    static final String REQUEST_KEY_RETRY = REQUEST_HEADER_KEY_PREFIX + "retry";

    /**
     * 在请求 Message Header中 记录 用户自定义的  payload extractor
     */
    static final String REQUEST_KEY_REPLY_PAYLOAD_EXTRACTOR = REQUEST_HEADER_KEY_PREFIX + "reply_payload_extractor";

    /**
     * 在请求 Message Header中 记录 用户自定义的 error payload extractor
     */
    static final String REQUEST_KEY_REPLY_PAYLOAD_ERROR_EXTRACTOR = REQUEST_HEADER_KEY_PREFIX + "reply_payload_error_extractor";

    /**
     * 标记是否为附件上传请求
     */
    public static final String REQUEST_KEY_IS_ATTACHMENT_UPLOAD = REQUEST_HEADER_KEY_PREFIX + "is_attachment_upload";
    public static final String REQUEST_KEY_ATTACHMENT_UPLOAD_WRITER = REQUEST_HEADER_KEY_PREFIX + "attachment_upload_writer";
    /**
     * 标记是否禁用文本压缩
     */
    public static final String REQUEST_KEY_ATTACHMENT_UPLOAD_TEXT_COMPRESS_DISABLED = REQUEST_HEADER_KEY_PREFIX + "attachment_update_text_compress_disabled";


    public static final String REQUEST_KEY_LOGGING_PAYLOAD = REQUEST_HEADER_KEY_PREFIX + "logging_payload";


    //*********************************************************************************************************

    private static String RESPONSE_HEADER_KEY_PREFIX = "agileway_http_response_key_";
    /**
     * 在响应 Message Header中 记录 用户自定义的 期望的 payload type
     */
    public static final String RESPONSE_KEY_REPLY_PAYLOAD_TYPE = RESPONSE_HEADER_KEY_PREFIX + "payload_type";

}

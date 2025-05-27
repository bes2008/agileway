package com.jn.agileway.httpclient.protocol.restful;

/**
 * @see <a href="https://www.rfc-editor.org/rfc/rfc9457.html">RFC9457</a>
 * <p>
 * 要求 是一个JSON结构
 */
public class ProblemDetails {
    private String type;
    /**
     * 状态码
     */
    private String status;

    private String title;
    private String detail;
    private String instance;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getInstance() {
        return instance;
    }

    public void setInstance(String instance) {
        this.instance = instance;
    }
}

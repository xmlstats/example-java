package com.xmlstats.example;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("error")
public class XmlstatsError {

    private String code = null;

    private String description = null;

    @JsonProperty("request_url")
    private String requestUrl = null;

    // Empty constructor for json deserialization, could also use @JsonCreator
    public XmlstatsError() { }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl() {
        this.requestUrl = requestUrl;
    }

}

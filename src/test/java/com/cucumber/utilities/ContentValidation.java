package com.cucumber.utilities;

import java.util.Map;

public class ContentValidation {
    private Map<String, ContentValidationItem> content;

    public Map<String, ContentValidationItem> getContent() {
        return content;
    }

    public void setContent(Map<String, ContentValidationItem> content) {
        this.content = content;
    }
}

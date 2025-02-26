package com.example.aspect_oriented_programming.dto;

public class KafkaMessageDTO {
    private String title;
    private String content;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "KafkaMessageDTO{title='" + title + "', content='" + content + "'}";
    }
}


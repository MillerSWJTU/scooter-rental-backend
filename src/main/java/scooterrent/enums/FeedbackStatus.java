package scooterrent.enums;

public enum FeedbackStatus {
    PENDING("待处理"),
    PROCESSING("处理中"),
    RESOLVED("已解决"),
    REJECTED("已拒绝");

    private final String description;

    FeedbackStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
} 
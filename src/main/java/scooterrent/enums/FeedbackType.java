package scooterrent.enums;

public enum FeedbackType {
    PAYMENT_ISSUE("支付问题"),
    SERVICE_QUALITY("服务质量"),
    TECHNICAL_ISSUE("技术问题"),
    SUGGESTION("建议"),
    COMPLAINT("投诉"),
    OTHER("其他");

    private final String displayName;

    FeedbackType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
} 
package scooterrent.enums;

public enum PaymentStatus {
    PENDING("待支付"),
    PROCESSING("处理中"),
    COMPLETED("已完成"),
    FAILED("失败"),
    REFUNDED("已退款");

    private final String description;

    PaymentStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
} 
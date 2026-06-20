package scooterrent.enums;

public enum OrderStatus {
    PENDING("待确认"),
    PAID("已支付"),
    ACTIVE("进行中"),
    COMPLETED("已完成"),
    CANCELLED("已取消"),
    EXTENDED("已延长"),
    CONFIRMED("已确认");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
} 
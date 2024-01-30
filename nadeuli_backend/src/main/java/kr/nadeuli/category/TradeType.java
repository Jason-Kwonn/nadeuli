package kr.nadeuli.category;

public enum TradeType {
    CHARGE(0L),
    WITHDRAW(1L),
    PAYMENT(2L);

    private final Long code;

    TradeType(Long code) {
        this.code = code;
    }

    public Long getCode() {
        return code;
    }

    // 코드로 TradeType을 찾는 메서드
    public static TradeType fromCode(Long code) {
        for (TradeType type : TradeType.values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid TradeType code: " + code);
    }
}
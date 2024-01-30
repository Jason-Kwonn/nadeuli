package kr.nadeuli.category;

public enum DeliveryState {

    DELIVERY_ORDER(0L),
    CANCEL_ORDER(1L),
    ACCEPT_ORDER(2L),
    CANCEL_DELIVERY(3L),
    COMPLETE_DELIVERY(4L)
    ;


    DeliveryState(Long deliveryState) {
        this.deliveryState = deliveryState;
    }

    private final Long deliveryState;

    private Long getDeliveryState() {
        return deliveryState;
    }

    public static DeliveryState fromDeliveryState(Long deliveryState) {
        for (DeliveryState state : DeliveryState.values()) {
            if (state.getDeliveryState().equals(deliveryState)) {
                return state;
            }
        }
        throw new IllegalArgumentException("Invalid deliveryState : " + deliveryState);
    }
}

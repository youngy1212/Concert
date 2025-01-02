package kr.hhplus.be.server.api.user.dto;

public class PointResponse {
    private Long totalAmount;

    private PointResponse(Long totalAmount) {
        this.totalAmount = totalAmount;
    }

    public static PointResponse of(Long totalAmount) {
        return new PointResponse(totalAmount);
    }
}

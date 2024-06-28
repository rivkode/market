package sample.market.common.exception;

import lombok.Getter;

@Getter
public class MarketApiException extends RuntimeException {
    private ErrorCode errorCode;

    public MarketApiException() {

    }

    protected MarketApiException(ErrorCode errorCode) {
        super();
        this.errorCode = errorCode;
    }

    public MarketApiException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public MarketApiException(String message, ErrorCode errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

}

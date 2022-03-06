package life.majiang.community.exception;

public class CustomizeErrorException extends RuntimeException {
    private String message;
    private Integer code;

    public CustomizeErrorException(ICustomizeErrorCode errorCode) {
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }

    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}

package life.majiang.community.exception;

public class CustomizeErrorException extends RuntimeException {
    private String message;

    public CustomizeErrorException(ICustomizeErrorCode errorCode) {
        this.message = errorCode.getMessage();
    }

    @Override
    public String getMessage() {
        return message;
    }
}

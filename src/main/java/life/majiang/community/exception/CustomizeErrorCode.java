package life.majiang.community.exception;

public enum CustomizeErrorCode implements ICustomizeErrorCode {

    QUESTION_NOT_FOUND(2001, "问题不存在了，换个试试？"),
    TARGET_PARAM_NOT_FOUND(2002, "未找到问题或评论进行回复"),
    NO_LOGIN(2003, "用户未登录"),
    SYS_ERROR(2004, "服务器冒烟了，请稍后再试"),
    TYPE_PARAM_NOT_FOUND(2005, "评论类型错误或不存在"),
    COMMENT_NOT_FOUND(2006, "原评论不存在了"),
    COMMENT_IS_EMPTY(2007, "输入内容不能为空"),
    READ_NOTIFICATION_FAIL(2008, "读取通知失败"),
    NOTIFICATION_NOT_FOUND(2009, "通知未找到"),
    ;

    private Integer code;
    private String message;
    CustomizeErrorCode(Integer code, String message){
        this.code=code;
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
    @Override
    public Integer getCode() {
        return code;
    }
}

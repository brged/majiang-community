package life.majiang.community.enums;

public enum CommentTypeEnum {

    QUESTION(1),
    COMMENT(2);

    public Integer type;

    CommentTypeEnum(Integer type) {
        this.type = type;
    }

    public static CommentTypeEnum lookUp(Integer type){
        for (CommentTypeEnum commentTypeEnum : CommentTypeEnum.values()) {
            if(commentTypeEnum.type == type){
                return commentTypeEnum;
            }
        }
        return null;
    }
}

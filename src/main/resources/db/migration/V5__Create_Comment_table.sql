CREATE TABLE comment
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    parent_id BIGINT NOT NULL,
    parent_type INT NOT NULL,
    commenter INT NOT NULL,
    gmt_create BIGINT NOT NULL,
    gmt_modified BIGINT NOT NULL,
    like_count BIGINT DEFAULT 0
);
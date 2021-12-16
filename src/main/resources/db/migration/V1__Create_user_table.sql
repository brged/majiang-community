CREATE TABLE user
			(
			id INT AUTO_INCREMENT PRIMARY KEY,
			account VARCHAR(100),
			name VARCHAR(50),
			token CHAR(36),
			gmt_created BIGINT,
			gmt_modified BIGINT
			);
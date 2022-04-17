CREATE TABLE ad
			(
			id BIGINT AUTO_INCREMENT PRIMARY KEY,
			title VARCHAR(255) DEFAULT NULL,
			url VARCHAR(511) DEFAULT NULL,
		  image VARCHAR(255) DEFAULT NULL,
			gmt_start BIGINT,
			gmt_end BIGINT,
			status int,
			pos VARCHAR(10) NOT NULL,
			gmt_created BIGINT,
			gmt_modified BIGINT
			);
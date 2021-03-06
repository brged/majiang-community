package life.majiang.community;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan(basePackages = "life.majiang.community.mapper")
@EnableScheduling
public class MajiangCommunityApplication {

	public static void main(String[] args) {
		SpringApplication.run(MajiangCommunityApplication.class, args);
	}

}

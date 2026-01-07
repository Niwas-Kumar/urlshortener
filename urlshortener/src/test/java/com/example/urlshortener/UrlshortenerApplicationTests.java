package com.example.urlshortener;

import com.example.urlshortener.util.Base62Util;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UrlshortenerApplicationTests {

	@Test
	void contextLoads() {
        System.out.println(Base62Util.encode(1L));
        System.out.println(Base62Util.encode(62L));
        System.out.println(Base62Util.encode(1285L));
	}




}

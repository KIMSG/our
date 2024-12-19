package com.our.ourroom;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class OurRoomApplicationTests {

    @Test
    public void testMain() {
        // SpringApplication.run()을 호출하여 main 메서드 테스트
        OurRoomApplication.main(new String[]{});
    }

}

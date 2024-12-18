package com.our.ourroom;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class OurRoomApplicationTests {

    @Test
    void contextLoads() {
        int data1 = 10;
        int data2 = 20;
        int sum = data1 + data2;
        System.out.println(sum);
    }

}

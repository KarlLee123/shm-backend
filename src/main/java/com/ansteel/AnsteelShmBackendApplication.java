package com.ansteel;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan({
        "com.ansteel.stress.mapper",
        "com.ansteel.displacement.mapper",
        "com.ansteel.acceleration.mapper",
        "com.ansteel.strain.mapper",
        "com.ansteel.vibration.mapper",
        "com.ansteel.fiber.mapper",
        "com.ansteel.vibratingwire.mapper",
        "com.ansteel.vibrationdat.mapper"
})
public class AnsteelShmBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(AnsteelShmBackendApplication.class, args);
    }

}

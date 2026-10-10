package ru.hogwarts.school.Controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.IntStream;

@RestController
public class InfoController {
    private static final Logger logger = LoggerFactory.getLogger(InfoController.class);

    @Value("${server.port:8080}")
    private int serverPort;

    @GetMapping("/port")
    public int getPort() {
        logger.info("Was invoked method for get server port");
        logger.debug("Current server port: {}", serverPort);
        return serverPort;
    }

    @GetMapping("/sum-intstream")
    public int getSumIntStream() {
        logger.info("Was invoked method for calculate sum (IntStream)");
        long startTime = System.currentTimeMillis();

        int sum = IntStream.rangeClosed(1, 1_000_000)
                .sum();

        long endTime = System.currentTimeMillis();
        logger.debug("IntStream sum calculation took {} ms", endTime - startTime);
        return sum;
    }
}


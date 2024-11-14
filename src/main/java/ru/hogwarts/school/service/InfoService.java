package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.stream.LongStream;

@Service
public class InfoService {
    private static final Logger LOGGER = LoggerFactory.getLogger(InfoService.class);

    @Value("${server.port}")
    private int port;

    public int getPort() {
        LOGGER.info("Invoke method 'getPort'");
        LOGGER.debug("The result of the method 'getPort' execution 'port = {}'", port);
        return port;
    }

    public long sum() {
        LOGGER.info("Invoke method 'sum'");
        long sum = LongStream.iterate(1, a -> a + 1)
                .limit(1_000_000)
                .sum();
        LOGGER.debug("The result of the method 'sum' execution: 'sum = {}'", sum);
        return sum;
    }

}

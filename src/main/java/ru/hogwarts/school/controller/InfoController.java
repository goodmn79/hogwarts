package ru.hogwarts.school.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.hogwarts.school.service.InfoService;

@RestController
@RequiredArgsConstructor
public class InfoController {
    private final InfoService infoService;

    @GetMapping("/port")
    public int getPort() {
        return infoService.getPort();
    }

    @GetMapping("/sum")
    public long sum() {
        return infoService.sum();
    }
}

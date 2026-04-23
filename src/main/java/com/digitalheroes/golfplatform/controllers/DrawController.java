package com.digitalheroes.golfplatform.controllers;
import com.digitalheroes.golfplatform.models.Draw;
import com.digitalheroes.golfplatform.models.DrawMode;
import com.digitalheroes.golfplatform.services.DrawService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/draw")
public class DrawController {
    private final DrawService drawService;

    public DrawController(DrawService drawService) {
        this.drawService = drawService;
    }

    @PostMapping("/simulate/random")
    public Draw simulateRandomDraw() {
        return drawService.generateRandomDraw(DrawMode.RANDOM);
    }

    @PostMapping("/simulate/algorithmic")
    public Draw simulateAlgorithmicDraw() {
        return drawService.generateRandomDraw(DrawMode.ALGORITHMIC);
    }

    @PostMapping("/{id}/publish")
    public Draw publish(@PathVariable Long id) {
        return drawService.publish(id);
    }
}

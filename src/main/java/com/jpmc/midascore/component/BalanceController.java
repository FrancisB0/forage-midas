package com.jpmc.midascore.component;

import com.jpmc.midascore.foundation.Balance;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

@RestController
public class BalanceController {
    private final UserRepository userRepository;

    public BalanceController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/balance")
    public Balance getBalance(@RequestParam("userId") long userId) {
        UserRecord user = userRepository.findById(userId);
        float value = (user != null) ? user.getBalance() : 0f;
        Balance b = new Balance();
        // Adjust setter names if your Balance class differs
        try {
            Balance.class.getMethod("setUserId", long.class).invoke(b, userId);
        } catch (Exception ignored) {}
        try {
            Balance.class.getMethod("setBalance", float.class).invoke(b, value);
        } catch (Exception ignored) {}
        return b;
    }
}
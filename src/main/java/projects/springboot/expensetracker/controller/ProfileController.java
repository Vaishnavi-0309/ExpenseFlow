package projects.springboot.expensetracker.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import projects.springboot.expensetracker.dto.AuthDTO;
import projects.springboot.expensetracker.dto.ProfileDTO;
import projects.springboot.expensetracker.service.ProfileService;

import java.util.Map;

@RestController
@RequestMapping
public class ProfileController {
    private ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @PostMapping("/register")
    public ResponseEntity<ProfileDTO> createAccount(@RequestBody ProfileDTO profileDTO){
        ProfileDTO response=profileService.createAccount(profileDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/activate")
    public ResponseEntity<String> activationProfile(@RequestParam String token){
        boolean isActivated=profileService.activateProfile(token);
        if(isActivated) return ResponseEntity.ok("Profile is created successfully");
        else return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Activation token is not found or already used");

    }

    @PostMapping("/login")
    public ResponseEntity<Map<String,Object>> login(@RequestBody AuthDTO authDTO){
        try{
            if(!profileService.isAccountActive(authDTO.getEmail()))
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                            "message","Account is not active . Please activate your account first."
                    ));
            Map<String,Object> response=profileService.authenticateAndGenerateToken(authDTO);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message","Invalid email or password"));
        }
    }

    @GetMapping("/test")
    public ResponseEntity<String> test(){
        return ResponseEntity.ok("Working fine");
    }
}

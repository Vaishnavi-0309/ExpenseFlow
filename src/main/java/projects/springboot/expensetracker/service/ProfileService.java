package projects.springboot.expensetracker.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import projects.springboot.expensetracker.dto.AuthDTO;
import projects.springboot.expensetracker.dto.ProfileDTO;
import projects.springboot.expensetracker.entity.Profile;
import projects.springboot.expensetracker.repository.ProfileRepository;
import projects.springboot.expensetracker.security.JwtService;

import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

@Service
public class ProfileService {

    private ProfileRepository profileRepo;
    private EmailService emailService;
    private PasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;
    private JwtService jwtService;

    public ProfileService(ProfileRepository profileRepo,EmailService emailService,PasswordEncoder passwordEncoder,
                          AuthenticationManager authenticationManager,JwtService jwtService) {
        this.profileRepo = profileRepo;
        this.emailService=emailService;
        this.passwordEncoder=passwordEncoder;
        this.authenticationManager=authenticationManager;
        this.jwtService=jwtService;
    }

    public ProfileDTO createAccount(ProfileDTO profileDTO){
        try{
        Profile newProfile=updateToDatabase(profileDTO);
        newProfile.setActivationToken(UUID.randomUUID().toString());
        profileRepo.save(newProfile);
        //send email
        String activationLink="http://localhost:8080/v1/apiservices/activate?token="+newProfile.getActivationToken();
        String subject="Activate your exoense tracker account";
        String body="Click on the following link to activate your account : "+activationLink;
        emailService.sendEmailAlert(newProfile.getEmail(),subject,body);
        return entityToDTO(newProfile);
        } catch (RuntimeException e) {
            Logger.getLogger("Exception occured "+e.getStackTrace());
            throw new RuntimeException(e);
        }
    }

    public Profile updateToDatabase(ProfileDTO profileDTO){
        return Profile.builder()
                .email(profileDTO.getEmail())
                .fullName(profileDTO.getFullName())
                .profileImgUrl(profileDTO.getProfileImgUrl())
                .password(passwordEncoder.encode(profileDTO.getPassword()))
                .build();
    }

    public ProfileDTO entityToDTO(Profile profile){
        return ProfileDTO.builder()
                .email(profile.getEmail())
                .fullName(profile.getFullName())
                .profileImgUrl(profile.getProfileImgUrl())
                .build();
    }

    public boolean activateProfile(String activationToken){
        return profileRepo.findByActivationToken(activationToken)
                .map(profile -> {
                    profile.setIsActive(true);
                    profileRepo.save(profile);
                    return true;
                }).orElse(false);
    }

    public boolean isAccountActive(String email){
        return profileRepo.findByEmail(email)
                .map(Profile::getIsActive)
                .orElse(false);
    }

    public Profile getCurrentProfile(){
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        return profileRepo.findByEmail(authentication.getName())
                .orElseThrow(()-> new UsernameNotFoundException("Profile not found with email : "+authentication.getName()));
    }

    public ProfileDTO getPublicProfile(String email){
        Profile currentUser=null;
        if(email == null){
            currentUser=getCurrentProfile();
        }else{
            currentUser=profileRepo.findByEmail(email)
                    .orElseThrow(()->new UsernameNotFoundException("Profile not found with email : "+email));
        }
        return ProfileDTO.builder()
                .fullName(currentUser.getFullName())
                .email(currentUser.getEmail())
                .profileImgUrl(currentUser.getProfileImgUrl())
                .build();
    }
    /*Generate JWT token */

    public Map<String, Object> authenticateAndGenerateToken(AuthDTO authDTO) {
        try{
           authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authDTO.getEmail(),authDTO.getPassword()));
            String token = jwtService.generateToken(authDTO.getEmail());
           return Map.of(
                   "token",token,
                   "user",getPublicProfile(authDTO.getEmail())
           );
        }catch(Exception e){
            throw  new RuntimeException();
        }
    }
}

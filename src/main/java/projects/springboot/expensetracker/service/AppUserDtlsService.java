package projects.springboot.expensetracker.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import projects.springboot.expensetracker.entity.Profile;
import projects.springboot.expensetracker.repository.ProfileRepository;

import java.util.Collections;

@Service
public class AppUserDtlsService implements UserDetailsService {
    private ProfileRepository profileRepository;

    public AppUserDtlsService(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Profile existingProfile = profileRepository.findByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException("User with this email doesn't exist : "+email));
        return User.builder()
                .username(existingProfile.getEmail())
                .password(existingProfile.getPassword())
                .authorities(Collections.emptyList())
                .build();
    }
}

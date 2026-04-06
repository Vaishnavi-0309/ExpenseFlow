package projects.springboot.expensetracker.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import projects.springboot.expensetracker.entity.Profile;
import projects.springboot.expensetracker.repository.ProfileRepository;

import java.util.List;

@Service
@Slf4j
public class NotificationService {

    private ExpenseService expenseService;
    private ProfileRepository profileRepository;
    private EmailService emailService;

    public NotificationService(ExpenseService expenseService, ProfileRepository profileRepository, EmailService emailService) {
        this.expenseService = expenseService;
        this.profileRepository = profileRepository;
        this.emailService = emailService;
    }

    @Value("${expense.tracker.frontend.url}")
    private String frontendUrl;

//    @Scheduled(cron = "0 0 22 * * *",zone="IST")
    @Scheduled(cron = "0 * * * * *",zone="IST")
    public void sendDailyIncomeExpenseReminder(){
        log.info("Job started : sendDailyIncomeExpenseReminder() ");
        List<Profile> profiles=profileRepository.findAll();
        for(Profile profile:profiles){
            String body="Hi "+profile.getFullName()+",<br><br>"
                    +"This is a friendly reminder to add your income and expense for today in Expense Tracker "
                    +"<a href="+frontendUrl+" style='display:inline-block;padding:10px 20px;background-color:#4CAF50;color:#fff;text-decoration:none;border-radius:5px;font-weight:bold;'> Go to Expense Tracker </a>"
                    +"<br><br>Best Regards,<br>Expense Tracker Team";

            emailService.sendEmailAlert(profile.getEmail(),"Daily remainder: Add your income and expenses",body);
        }
    }
}

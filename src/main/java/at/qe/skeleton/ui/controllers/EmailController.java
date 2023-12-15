package at.qe.skeleton.ui.controllers;

import at.qe.skeleton.model.Email;
import at.qe.skeleton.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
@Scope("view")
public class EmailController implements Serializable {

    @Autowired
    private EmailService emailService;
    private Email email = new Email();

    public Email getEmail() {
        return email;
    }

    public void setEmailTitle(String title) {
        email.setTitle(title);
    }

    public void setEmailMessage(String message) {
        email.setMessage(message);
    }

    public void notifyEverybody() {
        emailService.sendEmailToAllUsers(email.getTitle(), email.getMessage());
    }
}

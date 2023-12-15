package at.qe.skeleton.services;


import at.qe.skeleton.model.Deck;
import at.qe.skeleton.model.Userx;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import java.util.List;

@Component
@Scope("application")
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private UserService userService;

    public void sendLockedMessage(Userx user, Deck deck) {
        if (user.getEmail() != null && user.getEmail().length() >0) {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(user.getEmail());
            message.setSubject("Deck got locked:" + "\"" + deck.getDeckName() + "\"");
            message.setText("Dear " + user.getUsername() + ",\nWe are very sorry to inform you, that the Deck \"" + deck.getDeckName() + "\" got locked." +
                    " We already notified the author of the deck and hope that we can soon unlock the Deck and that you can learn the deck " +
                    "again!\n\nSincerely,\nYour Memori-Team");
            javaMailSender.send(message);
        }
    }

    public void sendLockedMessageAuthor(Userx author, Deck deck) {
        if (author.getEmail() != null && author.getEmail().length() >0) {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(author.getEmail());
            message.setSubject("Deck got locked:" + "\"" + deck.getDeckName() + "\"");
            message.setText("Dear " + author.getUsername() + ",\nWe are very sorry to inform you that your Deck \"" + deck.getDeckName() + "\" with ID:" + deck.getId() + " got locked.\n" +
                    "Please look at it, so that it will be unlocked again soon!\n\nSincerely,\nYour Memori-Team");
            javaMailSender.send(message);
        }
    }

    public void sendUnlockMessage(Userx user, Deck deck) {
        if (user.getEmail() != null && user.getEmail().length() >0) {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(user.getEmail());
            message.setSubject("Deck got unlocked:" + "\"" + deck.getDeckName() + "\"");
            message.setText("Dear " + user.getUsername() + ",\nWe are happy To announce, that the Deck \"" + deck.getDeckName() + "\" got unlocked again.\n" +
                    "Keep Learning! :)\n\nSincerely,\nYour Memori-Team");
            javaMailSender.send(message);
        }
    }

    public void sendUnlockMessageAuthor(Userx author, Deck deck) {
        if (author.getEmail() != null && author.getEmail().length() >0) {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(author.getEmail());
            message.setSubject("Deck got unlocked:" + "\"" + deck.getDeckName() + "\"");
            message.setText("Dear " + author.getUsername() + ",\nWe are happy to inform you that your Deck \"" + deck.getDeckName() + " with ID:" + deck.getId() + "\" got unlocked again.\n" +
                    "\n\nSincerely,\nYour Memori-Team");
            javaMailSender.send(message);
        }
    }

    public void sendEmailToAllUsers(String title, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject(title);
        message.setText(text);

        List<Userx> usersToNotify = userService.getAllUsers().stream().filter(user -> user.getEmail() != null && user.getEmail().length() > 0).toList();
        for(var user : usersToNotify) {
            message.setTo(user.getEmail());
            javaMailSender.send(message);
        }
    }
}

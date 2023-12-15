package at.qe.skeleton.services;


import at.qe.skeleton.model.Log;
import at.qe.skeleton.model.Userx;
import at.qe.skeleton.repositories.LogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import java.util.Collection;
import java.util.stream.Collectors;


/**
 * Service for accessing and saving log-events.
 * This class is part of the skeleton project provided for students of the
 * course "Software Architecture" offered by Innsbruck University.
 */
@Component
@Scope("application")
public class LogService {

    @Autowired
    private LogRepository logRepository;

    /**
     * Returns the log of the webApp.
     */
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public Collection<Log> getLog(){
        return logRepository.findAll();
    }

    /**
     * Returns the log-entry of the webApp.
     * @param id log-id to search for
     */
    @PreAuthorize("hasAnyAuthority('ADMIN')")

    public Log loadLogById(Long id){
        return logRepository.findById(id);
    }

    /**
     * Returns the log-entry of the webApp of a user.
     * @param user user to search the log of
     */
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public Collection<Log> getLogByUser(Userx user){
        return logRepository
                .findAll()
                .stream()
                .filter(log -> log.getUsername()
                        .equals(user.getUsername()))
                .collect(Collectors.toList());
    }

    /**
     * saves a log in the db
     * @param log the log to save
     */
    public void saveLog(Log log){
        logRepository.save(log);
    }
}

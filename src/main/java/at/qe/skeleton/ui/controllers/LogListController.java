package at.qe.skeleton.ui.controllers;


import at.qe.skeleton.model.Log;
import at.qe.skeleton.model.Userx;
import at.qe.skeleton.services.LogService;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Collection;


/**
 * Controller for the Log list view.
 * This class is part of the skeleton project provided for students of the
 * course "Software Architecture" offered by Innsbruck University.
 */
@Component
@Scope("view")
public class LogListController {


    private LogService logService;

    public Collection<Log> getLog(){
        return logService.getLog();
    }

    /**
     * Returns the log-entry of the webApp.
     * @param id log-id to search for
     */
    public Log getLogById(Long id){
        return logService.loadLogById(id);
    }

    /**
     * Returns the log-entry of the webApp of a user.
     * @param user user to search the log of
     */
    public Collection<Log> getLogByUser(Userx user){
        return logService.getLogByUser(user);
    }

    /**
     * saves a log in the db
     * @param log the log to save
     */
    public void saveLog(Log log){
        logService.saveLog(log);
    }

}

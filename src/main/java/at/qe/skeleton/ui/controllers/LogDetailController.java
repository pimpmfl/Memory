package at.qe.skeleton.ui.controllers;


import at.qe.skeleton.model.Log;
import at.qe.skeleton.model.LogEvent;
import at.qe.skeleton.services.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Scope("view")
@Component
public class LogDetailController {

    @Autowired
    private LogService logService;

    public void createLogEvent(LogEvent event, String username){
        Log newLog = new Log();
        newLog.setLogEvent(event);
        newLog.setUsername(username);
        newLog.setDate(LocalDateTime.now());
        logService.saveLog(newLog);
    }
}

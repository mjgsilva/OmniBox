package shared;

import java.lang.Object;
import java.util.ArrayList;

/**
 * Created by OmniBox on 06/11/14.
 */
public class Command {
    private Constants.CMD cmd;
    private ArrayList<Object> argsList = new ArrayList<Object>();

    public Command(Constants.CMD cmd, ArrayList argsList){
        this.cmd = cmd;
        this.argsList = argsList;
    }
}

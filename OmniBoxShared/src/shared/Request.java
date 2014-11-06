package shared;

import java.lang.Object;
import java.util.ArrayList;

/**
 * Created by OmniBox on 06/11/14.
 */
public class Request {
    private Constants.CMD cmd;
    private ArrayList<Object> argsList = new ArrayList<Object>();

    public Request(Constants.CMD cmd, ArrayList argsList){
        this.cmd = cmd;
        this.argsList = argsList;
    }

    public Constants.CMD getCmd() {
        return cmd;
    }

    public ArrayList<Object> getArgsList() {
        return argsList;
    }

}

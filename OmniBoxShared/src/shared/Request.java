package shared;

import java.io.Serializable;
import java.lang.Object;
import java.util.ArrayList;

/**
 * Created by OmniBox on 06/11/14.
 *
 * Request class is the base of all communication in OmniBox application
 * One request contains one command to execute and a list of objects which is different depending on the flow of communication
 */
public class Request implements Serializable {
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

    @Override
    public String toString() {
        String str = "Request " + cmd + "\n";

        if (argsList.size() > 0)
            for (Object aux : argsList) {
                if (aux != null)
                    str += aux.toString() + " ";
                else
                    str += " null ";
            }

        return str;
    }
}

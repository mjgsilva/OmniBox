package shared;

import java.io.Serializable;
import java.lang.Object;
import java.util.ArrayList;

/**
 * Request class.
 * Is the base of all communication in OmniBox application
 * One request contains one command to execute and a list of objects which is different depending on the flow of communication
 *
 * Created by OmniBox on 06/11/14.
 */
public class Request implements Serializable {
    private Constants.CMD cmd;
    private ArrayList<Object> argsList = new ArrayList<Object>();

    /**
     * Request constructor.
     * Initialize given variables.
     *
     * @param cmd command
     * @param argsList arguments list
     */
    public Request(Constants.CMD cmd, ArrayList argsList){
        this.cmd = cmd;
        this.argsList = argsList;
    }

    /**
     * Returns command.
     */
    public Constants.CMD getCmd() {
        return cmd;
    }

    /**
     * Returns arguments list.
     */
    public ArrayList<Object> getArgsList() {
        return argsList;
    }

    @Override
    public String toString() {
        String str = "Request " + cmd + "\n";

        if (argsList.size() > 0) {
            for (Object aux : argsList) {
                if (aux != null)
                    str += aux.toString() + " ";
                else
                    str += " null ";
            }
        }

        return str;
    }
}

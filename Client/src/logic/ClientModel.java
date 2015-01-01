package logic;

import logic.state.StateInterface;
import shared.OmniFile;

import java.io.IOException;
import java.util.Observable;

/**
 * ClientModel.
 *
 * This class is one of the models of this MVC interface implementations.
 * Its the bridge between logic and interface.
 *
 * Created by OmniBox on 08-11-2014.
 */
public class ClientModel extends Observable implements ClientInterface {
    private Client client;
    private int selectedIndex = -1;
    private OmniFile selectedFile = null;

    public ClientModel(Client client) {
        this.client = client;
    }

    /**
     * Handles the notification of observers of this class.
     */
    public void sendNotification() {
        setChanged();
        notifyObservers();
    }

    public Client getClient() {
        return client;
    }

    @Override
    public void defineAuthentication(String username, String password) throws IOException, InterruptedException, ClassNotFoundException {
        client.defineAuthentication(username, password);
        sendNotification();
    }

    @Override
    public void defineGetRequest(OmniFile fileToGet) throws InterruptedException, IOException, ClassNotFoundException {
        client.defineGetRequest(fileToGet);
        sendNotification();
    }

    @Override
    public void defineSendRequest(OmniFile fileToSend) throws InterruptedException, IOException, ClassNotFoundException {
        client.defineSendRequest(fileToSend);
        sendNotification();
    }

    @Override
    public void defineRemoveRequest(OmniFile fileToRemove) throws IOException, InterruptedException {
        client.defineRemoveRequest(fileToRemove);
        ///////////////////sendNotification();
    }

    @Override
    public void defineReturnToRequest() {
        client.defineReturnToRequest();
        sendNotification();
    }

    @Override
    public void defineMulticastRequest() throws IOException {
        client.defineMulticastRequest();
        sendNotification();
    }

    /**
     * Sets file to upload for current client associated with this model instance.
     *
     * @param fileToUpload File to upload
     */
    public void setFileToUpload(OmniFile fileToUpload) {
        client.setFileToUpload(fileToUpload);
    }

    /**
     * Gets current state associated with this model instance.
     *
     * @return Current state on client instance associated with this model instance.
     */
    public StateInterface getCurrentState() {
        return client.getCurrentState();
    }

    /**
     * Closes server socket that is on client instance.
     *
     * @throws IOException
     */
    public void closeServerSocket() throws IOException {
        client.getServerSocket().close();
    }

    /**
     * @deprecated this method was intended for another way of manipulating JList on ListPanel.
     * There's no need to use this index anymore, its unsafe.<BR>
     *
     * Sets selected index, for JList purposes.
     * @param selectedIndex
     */
    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
    }

    /**
     * Returns index, use this to update FileInfoPanel information.
     *
     * @return selected index on JList from ListPanel
     */
    public int getSelectedIndex() {
        return selectedIndex;
    }

    /**
     * Returns index, use this to update FileInfoPanel information.
     *
     * @return selected OmniFile on JList from ListPanel
     */
    public OmniFile getSelectedFile() {
        return selectedFile;
    }

    /**
     * @deprecated this method was intended for another way of manipulating JList on ListPanel.
     * There's no need to use this index anymore, its unsafe.<BR>
     *
     * Sets selected index, for JList purposes.
     * @param selectedFile
     */
    public void setSelectedFile(OmniFile selectedFile) {
        this.selectedFile = selectedFile;
    }
}

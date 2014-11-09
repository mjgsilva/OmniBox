package ui.graphic;

import logic.ClientModel;

import javax.swing.*;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by OmniBox on 08-11-2014.
 */
public class ButtonsPanel extends JPanel implements Observer {
    private ClientModel cm;
    private Box horizontal;

    public ButtonsPanel(ClientModel cm) {
        this.cm = cm;
        
        buildLayout();
    }

    private void buildLayout() {
        horizontal = Box.createHorizontalBox();

        horizontal.add(new JButton("Send"));
        horizontal.add(new JButton("Get"));
        horizontal.add(new JButton("Remove"));

        horizontal.setAlignmentX(CENTER_ALIGNMENT);

        //setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        add(horizontal);
    }

    @Override
    public void update(Observable observable, Object o) {

    }
}

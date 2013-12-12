package lobby;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ConnectPanel extends JPanel {
	private static final String HOST_STR = "Hostname";
	private static final String PORT_STR = "Port";
	private static final String CONNECT_STR = "Connect";
	
	private static final String HOST_ERR = "Invalid hostname!";
	private static final String OTHER_ERR = "Unable to connect, please try again!";
	private JTextField hostField;
	private JTextField portField;

	public ConnectPanel() {
		hostField = new JTextField(HOST_STR, 30);
		portField = new JTextField(PORT_STR, 5);
		JButton connectButton = new JButton(CONNECT_STR);
		connectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				try {
					String hostname = hostField.getText();
					String port = portField.getText();
					GameLobbyClient client = new GameLobbyClient(hostname, Integer.parseInt(port));
				} catch (UnknownHostException e) {
					showErrorDialog(HOST_ERR);
				} catch (Exception e) {
					showErrorDialog(OTHER_ERR);
				}
			}
		});
		this.setLayout(new GridLayout(0, 3));
		this.add(hostField);
		this.add(portField);
		this.add(connectButton);
	}
	
	private void showErrorDialog(String msg) {
		JOptionPane.showMessageDialog(this, msg);
		hostField.setText(HOST_STR);
		portField.setText(PORT_STR);
	}
}

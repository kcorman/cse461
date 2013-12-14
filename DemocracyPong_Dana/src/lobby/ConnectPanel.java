package lobby;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ConnectPanel extends JPanel implements LobbyConnectionListener{
	private static final String HOST_STR = "Hostname";
	private static final String PORT_STR = "Port";
	private static final String DEFAULT_PORT = "34543";
	private static final String CONNECT_STR = "Connect";
	
	private static final String HOST_ERR = "Invalid hostname!";
	private static final String OTHER_ERR = "Unable to connect, please try again!";
	private JTextField hostField;
	private JTextField portField;
	private JTextField nameField = new JTextField("username");
	private final JButton connectButton;
	private JButton setNameButton;
	private ConnectionBean connectionBean;

	//For notifying other components when a connection is successful
	private final List<LobbyConnectionListener> connectionListeners;
	public ConnectPanel(final ConnectionBean connectionBean) {
		connectionListeners = new ArrayList<LobbyConnectionListener>();
		this.connectionBean = connectionBean;
		hostField = new JTextField(HOST_STR, 30);
		portField = new JTextField(DEFAULT_PORT, 5);
		connectButton = new JButton(CONNECT_STR);
		connectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				try {
					String hostname = hostField.getText();
					String port = portField.getText();
					//try to connect to the remote lobby
					connectionBean.connect(hostname, Integer.parseInt(port));
					//if no exceptions are thrown, notify listeners
					for(LobbyConnectionListener l : connectionListeners){
						l.onSuccessfulConnect();
					}
					connectButton.setEnabled(false);
				} catch (UnknownHostException e) {
					showErrorDialog(HOST_ERR);
					e.printStackTrace();
				} catch (Exception e) {
					showErrorDialog(OTHER_ERR);
					e.printStackTrace();
				}
			}
		});
		this.setLayout(new GridLayout(0, 3));
		this.add(hostField);
		this.add(portField);
		this.add(connectButton);
	}
	
	/**
	 * Adds the specified listener to this component
	 * Upon successful connection (via the user pushing the connect button),
	 * all listeners will be notified thru a call to their onSuccessfulConnect()
	 * method
	 * @param listener the listener to add
	 */
	public void addLobbyConnectionListener(LobbyConnectionListener listener){
		connectionListeners.add(listener);
	}
	
	private void showErrorDialog(String msg) {
		JOptionPane.showMessageDialog(this, msg);
		hostField.setText(HOST_STR);
		portField.setText(PORT_STR);
	}

	@Override
	public void onSuccessfulConnect() {
		
		this.remove(connectButton);
		this.remove(hostField);
		this.remove(portField);
		this.add(nameField);
		setNameButton = new JButton("Set Username");
		setNameButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				connectionBean.setUserName(nameField.getText());
			}
		});
		this.add(setNameButton);
	}
}

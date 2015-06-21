/*
 * DirectoryUIView.java
 */
package com.bensmann.directory.ui;

import com.bensmann.directory.LdapFacade;
import com.bensmann.directory.LdapFacadeException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.TaskEvent;
import org.jdesktop.application.TaskMonitor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.Timer;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import org.jdesktop.application.Task;
import org.jdesktop.application.TaskListener;

/**
 * The application's main frame.
 */
public class DirectoryUIView extends FrameView {

    /**
     *
     */
    private LdapFacade sourceFacade;

    /**
     * 
     */
    private LdapFacade destinationFacade;

    /**
     *
     */
    private SimpleDateFormat sdf;

    /**
     *
     * @param app
     */
    public DirectoryUIView(SingleFrameApplication app) {
        super(app);
        initComponents();
        // status bar initialization - message timeout, idle icon and busy animation, etc
        ResourceMap resourceMap = getResourceMap();
        int messageTimeout = resourceMap.getInteger("StatusBar.messageTimeout");
        messageTimer = new Timer(messageTimeout, new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                statusMessageLabel.setText("");
            }

        });
        messageTimer.setRepeats(false);
        int busyAnimationRate = resourceMap.getInteger("StatusBar.busyAnimationRate");
        for (int i = 0; i < busyIcons.length; i++) {
            busyIcons[i] = resourceMap.getIcon("StatusBar.busyIcons[" + i + "]");
        }
        busyIconTimer = new Timer(busyAnimationRate, new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
                statusAnimationLabel.setIcon(busyIcons[busyIconIndex]);
            }

        });
        idleIcon = resourceMap.getIcon("StatusBar.idleIcon");
        statusAnimationLabel.setIcon(idleIcon);
        progressBar.setVisible(false);
        // connecting action tasks to status bar via TaskMonitor
        TaskMonitor taskMonitor = new TaskMonitor(getApplication().getContext());
        taskMonitor.addPropertyChangeListener(new java.beans.PropertyChangeListener() {

            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                String propertyName = evt.getPropertyName();
                if ("started".equals(propertyName)) {
                    if (!busyIconTimer.isRunning()) {
                        statusAnimationLabel.setIcon(busyIcons[0]);
                        busyIconIndex = 0;
                        busyIconTimer.start();
                    }
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(true);
                } else if ("done".equals(propertyName)) {
                    busyIconTimer.stop();
                    statusAnimationLabel.setIcon(idleIcon);
                    progressBar.setVisible(false);
                    progressBar.setValue(0);
                } else if ("message".equals(propertyName)) {
                    String text = (String) (evt.getNewValue());
                    statusMessageLabel.setText((text == null) ? "" : text);
                    messageTimer.restart();
                } else if ("progress".equals(propertyName)) {
                    int value = (Integer) (evt.getNewValue());
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(value);
                }
            }

        });
        //
        sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    }

    /**
     *
     * @param text
     */
    protected void log(String text) {
        logTextArea.append(sdf.format(new Date()) + ": " + text + " \n");
    }

    /**
     *
     */
    @Action
    public void showAboutBox() {
        if (aboutBox == null) {
            JFrame mainFrame = DirectoryUIApp.getApplication().getMainFrame();
            aboutBox = new DirectoryUIAboutBox(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        DirectoryUIApp.getApplication().show(aboutBox);
    }

    /**
     *
     * @return
     */
    @Action
    public Task connectionSourceServerAction() {
        Map<String, String> params = new HashMap<String, String>();
        String server = sourceLdapServerTextField.getText();
        params.put("server", server);
        String port = sourceLdapPortTextField.getText();
        params.put("port", port);
        String user = sourceLdapUserTextField.getText();
        params.put("user", user);
        String pwd = String.valueOf(sourceLdapPasswordField.getPassword());
        params.put("pwd", pwd);
        String dn = sourceDnTextField.getText();
        params.put("dn", dn);
        //
        Task t = new ConnectLdapTask(getApplication(), params);
        t.addTaskListener(new TaskListener() {

            public void doInBackground(TaskEvent event) {
            }

            public void process(TaskEvent event) {
            }

            public void succeeded(TaskEvent event) {
                sourceFacade = (LdapFacade) event.getValue();
                try {
                    sourceFacade.connect();
                    log("source connected");
                } catch (LdapFacadeException e) {
                    log("ERROR: could not connect to source");
                }
            }

            public void failed(TaskEvent event) {
            }

            public void cancelled(TaskEvent event) {
            }

            public void interrupted(TaskEvent event) {
            }

            public void finished(TaskEvent event) {
            }

        });
        return t;
    }

    /**
     *
     * @return
     */
    @Action
    public Task connectionDestinationServerAction() {
        Map<String, String> params = new HashMap<String, String>();
        String server = destinationLdapServerTextField.getText();
        params.put("server", server);
        String port = destinationLdapPortTextField.getText();
        params.put("port", port);
        String user = destinationLdapUserTextField.getText();
        params.put("user", user);
        String pwd = String.valueOf(destinationLdapPasswordField.getPassword());
        params.put("pwd", pwd);
        String dn = destinationDnTextField.getText();
        params.put("dn", dn);
        //
        Task t = new ConnectLdapTask(getApplication(), params);
        t.addTaskListener(new TaskListener() {

            public void doInBackground(TaskEvent event) {
            }

            public void process(TaskEvent event) {
            }

            public void succeeded(TaskEvent event) {
                destinationFacade = (LdapFacade) event.getValue();
                try {
                    destinationFacade.connect();
                    log("destination connected");
                } catch (LdapFacadeException e) {
                    log("ERROR: could not connect to destination");
                }
            }

            public void failed(TaskEvent event) {
            }

            public void cancelled(TaskEvent event) {
            }

            public void interrupted(TaskEvent event) {
            }

            public void finished(TaskEvent event) {
            }

        });
        return t;
    }

    /**
     * 
     * @return
     */
    @Action
    public Task copyAction() {
        String sourceDn = sourceDnTextField.getText();
        String destinationDn = destinationDnTextField.getText();
        if (null != sourceDn && null != destinationDn) {
            try {
                sourceFacade.copy(sourceDn, destinationFacade.connect(), destinationDn);
            } catch (LdapFacadeException e) {
                log("ERROR: could not copy: " + e);
            }
        } else {
            log("No source or destination DN given!");
        }
        return null;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        sourceLdapServerPanel = new javax.swing.JPanel();
        sourceLdapServerTextLabel = new javax.swing.JLabel();
        sourceLdapServerTextField = new javax.swing.JTextField();
        sourceLdapPortLabel = new javax.swing.JLabel();
        sourceLdapPortTextField = new javax.swing.JTextField();
        sourceLdapUserLabel = new javax.swing.JLabel();
        sourceLdapUserTextField = new javax.swing.JTextField();
        sourceLdapConnectButton = new javax.swing.JButton();
        sourceLdapPasswordLabel = new javax.swing.JLabel();
        sourceLdapPasswordField = new javax.swing.JPasswordField();
        sourceDnLabel = new javax.swing.JLabel();
        sourceDnTextField = new javax.swing.JTextField();
        destinationLdapServerPanel = new javax.swing.JPanel();
        destinationLdapServerTextLabel = new javax.swing.JLabel();
        destinationLdapServerTextField = new javax.swing.JTextField();
        destinationLdapPortLabel = new javax.swing.JLabel();
        destinationLdapPortTextField = new javax.swing.JTextField();
        destinationLdapConnectButton = new javax.swing.JButton();
        destinationLdapUserLabel = new javax.swing.JLabel();
        destinationLdapUserTextField = new javax.swing.JTextField();
        destinationLdapPasswordLabel = new javax.swing.JLabel();
        destinationLdapPasswordField = new javax.swing.JPasswordField();
        destinationDnLabel = new javax.swing.JLabel();
        destinationDnTextField = new javax.swing.JTextField();
        logScrollPane = new javax.swing.JScrollPane();
        logTextArea = new javax.swing.JTextArea();
        copyButton = new javax.swing.JButton();
        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();
        statusPanel = new javax.swing.JPanel();
        javax.swing.JSeparator statusPanelSeparator = new javax.swing.JSeparator();
        statusMessageLabel = new javax.swing.JLabel();
        statusAnimationLabel = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();

        mainPanel.setName("mainPanel"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(com.bensmann.directory.ui.DirectoryUIApp.class).getContext().getResourceMap(DirectoryUIView.class);
        sourceLdapServerPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("sourceLdapServerPanel.border.title"))); // NOI18N
        sourceLdapServerPanel.setName("sourceLdapServerPanel"); // NOI18N

        sourceLdapServerTextLabel.setText(resourceMap.getString("sourceLdapServerTextLabel.text")); // NOI18N
        sourceLdapServerTextLabel.setName("sourceLdapServerTextLabel"); // NOI18N

        sourceLdapServerTextField.setText(resourceMap.getString("sourceLdapServerTextField.text")); // NOI18N
        sourceLdapServerTextField.setName("sourceLdapServerTextField"); // NOI18N
        sourceLdapServerTextField.setPreferredSize(new java.awt.Dimension(130, 20));

        sourceLdapPortLabel.setText(resourceMap.getString("sourceLdapPortLabel.text")); // NOI18N
        sourceLdapPortLabel.setName("sourceLdapPortLabel"); // NOI18N

        sourceLdapPortTextField.setText(resourceMap.getString("sourceLdapPortTextField.text")); // NOI18N
        sourceLdapPortTextField.setName("sourceLdapPortTextField"); // NOI18N

        sourceLdapUserLabel.setText(resourceMap.getString("sourceLdapUserLabel.text")); // NOI18N
        sourceLdapUserLabel.setName("sourceLdapUserLabel"); // NOI18N

        sourceLdapUserTextField.setText(resourceMap.getString("sourceLdapUserTextField.text")); // NOI18N
        sourceLdapUserTextField.setName("sourceLdapUserTextField"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(com.bensmann.directory.ui.DirectoryUIApp.class).getContext().getActionMap(DirectoryUIView.class, this);
        sourceLdapConnectButton.setAction(actionMap.get("connectionSourceServerAction")); // NOI18N
        sourceLdapConnectButton.setText(resourceMap.getString("sourceLdapConnectButton.text")); // NOI18N
        sourceLdapConnectButton.setName("sourceLdapConnectButton"); // NOI18N

        sourceLdapPasswordLabel.setText(resourceMap.getString("sourceLdapPasswordLabel.text")); // NOI18N
        sourceLdapPasswordLabel.setName("sourceLdapPasswordLabel"); // NOI18N

        sourceLdapPasswordField.setText(resourceMap.getString("sourceLdapPasswordField.text")); // NOI18N
        sourceLdapPasswordField.setName("sourceLdapPasswordField"); // NOI18N

        sourceDnLabel.setText(resourceMap.getString("sourceDnLabel.text")); // NOI18N
        sourceDnLabel.setName("sourceDnLabel"); // NOI18N

        sourceDnTextField.setText(resourceMap.getString("sourceDnTextField.text")); // NOI18N
        sourceDnTextField.setName("sourceDnTextField"); // NOI18N

        javax.swing.GroupLayout sourceLdapServerPanelLayout = new javax.swing.GroupLayout(sourceLdapServerPanel);
        sourceLdapServerPanel.setLayout(sourceLdapServerPanelLayout);
        sourceLdapServerPanelLayout.setHorizontalGroup(
            sourceLdapServerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sourceLdapServerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(sourceLdapServerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(sourceLdapServerTextLabel)
                    .addComponent(sourceLdapUserLabel)
                    .addComponent(sourceDnLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(sourceLdapServerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(sourceLdapServerPanelLayout.createSequentialGroup()
                        .addGroup(sourceLdapServerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(sourceLdapUserTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(sourceLdapServerTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(sourceLdapServerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(sourceLdapPasswordLabel)
                            .addComponent(sourceLdapPortLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(sourceLdapServerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(sourceLdapPortTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(sourceLdapPasswordField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(sourceDnTextField))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(sourceLdapConnectButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        sourceLdapServerPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {sourceLdapPasswordField, sourceLdapPortTextField, sourceLdapServerTextField, sourceLdapUserTextField});

        sourceLdapServerPanelLayout.setVerticalGroup(
            sourceLdapServerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sourceLdapServerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(sourceLdapServerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sourceLdapServerTextLabel)
                    .addComponent(sourceLdapServerTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sourceLdapPortLabel)
                    .addComponent(sourceLdapPortTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(sourceLdapServerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sourceLdapUserTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sourceLdapUserLabel)
                    .addComponent(sourceLdapPasswordLabel)
                    .addComponent(sourceLdapPasswordField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sourceLdapConnectButton))
                .addGroup(sourceLdapServerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(sourceLdapServerPanelLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(sourceDnLabel))
                    .addGroup(sourceLdapServerPanelLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(sourceDnTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        destinationLdapServerPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("destinationLdapServerPanel.border.title"))); // NOI18N
        destinationLdapServerPanel.setName("destinationLdapServerPanel"); // NOI18N

        destinationLdapServerTextLabel.setText(resourceMap.getString("destinationLdapServerTextLabel.text")); // NOI18N
        destinationLdapServerTextLabel.setName("destinationLdapServerTextLabel"); // NOI18N

        destinationLdapServerTextField.setText(resourceMap.getString("destinationLdapServerTextField.text")); // NOI18N
        destinationLdapServerTextField.setName("destinationLdapServerTextField"); // NOI18N
        destinationLdapServerTextField.setPreferredSize(new java.awt.Dimension(130, 20));

        destinationLdapPortLabel.setText(resourceMap.getString("destinationLdapPortLabel.text")); // NOI18N
        destinationLdapPortLabel.setName("destinationLdapPortLabel"); // NOI18N

        destinationLdapPortTextField.setText(resourceMap.getString("destinationLdapPortTextField.text")); // NOI18N
        destinationLdapPortTextField.setName("destinationLdapPortTextField"); // NOI18N

        destinationLdapConnectButton.setAction(actionMap.get("connectionDestinationServerAction")); // NOI18N
        destinationLdapConnectButton.setText(resourceMap.getString("destinationLdapConnectButton.text")); // NOI18N
        destinationLdapConnectButton.setName("destinationLdapConnectButton"); // NOI18N

        destinationLdapUserLabel.setText(resourceMap.getString("destinationLdapUserLabel.text")); // NOI18N
        destinationLdapUserLabel.setName("destinationLdapUserLabel"); // NOI18N

        destinationLdapUserTextField.setText(resourceMap.getString("destinationLdapUserTextField.text")); // NOI18N
        destinationLdapUserTextField.setName("destinationLdapUserTextField"); // NOI18N

        destinationLdapPasswordLabel.setText(resourceMap.getString("destinationLdapPasswordLabel.text")); // NOI18N
        destinationLdapPasswordLabel.setName("destinationLdapPasswordLabel"); // NOI18N

        destinationLdapPasswordField.setName("destinationLdapPasswordField"); // NOI18N

        destinationDnLabel.setText(resourceMap.getString("destinationDnLabel.text")); // NOI18N
        destinationDnLabel.setName("destinationDnLabel"); // NOI18N

        destinationDnTextField.setText(resourceMap.getString("destinationDnTextField.text")); // NOI18N
        destinationDnTextField.setName("destinationDnTextField"); // NOI18N

        javax.swing.GroupLayout destinationLdapServerPanelLayout = new javax.swing.GroupLayout(destinationLdapServerPanel);
        destinationLdapServerPanel.setLayout(destinationLdapServerPanelLayout);
        destinationLdapServerPanelLayout.setHorizontalGroup(
            destinationLdapServerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(destinationLdapServerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(destinationLdapServerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(destinationLdapServerTextLabel)
                    .addComponent(destinationLdapUserLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(destinationLdapServerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(destinationLdapUserTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
                    .addComponent(destinationLdapServerTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(destinationLdapServerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(destinationLdapPortLabel)
                    .addComponent(destinationLdapPasswordLabel))
                .addGap(4, 4, 4)
                .addGroup(destinationLdapServerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(destinationLdapServerPanelLayout.createSequentialGroup()
                        .addComponent(destinationLdapPasswordField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(destinationLdapConnectButton))
                    .addComponent(destinationLdapPortTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
            .addGroup(destinationLdapServerPanelLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(destinationDnLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(destinationDnTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 327, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(93, 93, 93))
        );

        destinationLdapServerPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {destinationLdapPasswordField, destinationLdapPortTextField, destinationLdapServerTextField, destinationLdapUserTextField});

        destinationLdapServerPanelLayout.setVerticalGroup(
            destinationLdapServerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(destinationLdapServerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(destinationLdapServerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(destinationLdapServerTextLabel)
                    .addComponent(destinationLdapServerTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(destinationLdapPortLabel)
                    .addComponent(destinationLdapPortTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(destinationLdapServerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(destinationLdapUserLabel)
                    .addComponent(destinationLdapUserTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(destinationLdapPasswordLabel)
                    .addComponent(destinationLdapPasswordField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(destinationLdapConnectButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(destinationLdapServerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(destinationDnLabel)
                    .addComponent(destinationDnTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        logScrollPane.setName("logScrollPane"); // NOI18N

        logTextArea.setColumns(20);
        logTextArea.setRows(5);
        logTextArea.setName("logTextArea"); // NOI18N
        logScrollPane.setViewportView(logTextArea);

        copyButton.setAction(actionMap.get("copyAction")); // NOI18N
        copyButton.setText(resourceMap.getString("copyButton.text")); // NOI18N
        copyButton.setName("copyButton"); // NOI18N

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(sourceLdapServerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(destinationLdapServerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(copyButton)
                    .addComponent(logScrollPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 505, Short.MAX_VALUE))
                .addContainerGap())
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(sourceLdapServerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(destinationLdapServerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(copyButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(logScrollPane)
                .addContainerGap())
        );

        menuBar.setName("menuBar"); // NOI18N

        fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
        fileMenu.setName("fileMenu"); // NOI18N

        exitMenuItem.setAction(actionMap.get("quit")); // NOI18N
        exitMenuItem.setName("exitMenuItem"); // NOI18N
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
        helpMenu.setName("helpMenu"); // NOI18N

        aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
        aboutMenuItem.setName("aboutMenuItem"); // NOI18N
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        statusPanel.setName("statusPanel"); // NOI18N

        statusPanelSeparator.setName("statusPanelSeparator"); // NOI18N

        statusMessageLabel.setName("statusMessageLabel"); // NOI18N

        statusAnimationLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        statusAnimationLabel.setName("statusAnimationLabel"); // NOI18N

        progressBar.setName("progressBar"); // NOI18N

        javax.swing.GroupLayout statusPanelLayout = new javax.swing.GroupLayout(statusPanel);
        statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(statusPanelSeparator, javax.swing.GroupLayout.DEFAULT_SIZE, 525, Short.MAX_VALUE)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(statusMessageLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 355, Short.MAX_VALUE)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(statusAnimationLabel)
                .addContainerGap())
        );
        statusPanelLayout.setVerticalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addComponent(statusPanelSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(statusMessageLabel)
                    .addComponent(statusAnimationLabel)
                    .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3))
        );

        setComponent(mainPanel);
        setMenuBar(menuBar);
        setStatusBar(statusPanel);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton copyButton;
    private javax.swing.JLabel destinationDnLabel;
    private javax.swing.JTextField destinationDnTextField;
    private javax.swing.JButton destinationLdapConnectButton;
    private javax.swing.JPasswordField destinationLdapPasswordField;
    private javax.swing.JLabel destinationLdapPasswordLabel;
    private javax.swing.JLabel destinationLdapPortLabel;
    private javax.swing.JTextField destinationLdapPortTextField;
    private javax.swing.JPanel destinationLdapServerPanel;
    private javax.swing.JTextField destinationLdapServerTextField;
    private javax.swing.JLabel destinationLdapServerTextLabel;
    private javax.swing.JLabel destinationLdapUserLabel;
    private javax.swing.JTextField destinationLdapUserTextField;
    private javax.swing.JScrollPane logScrollPane;
    private javax.swing.JTextArea logTextArea;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JLabel sourceDnLabel;
    private javax.swing.JTextField sourceDnTextField;
    private javax.swing.JButton sourceLdapConnectButton;
    private javax.swing.JPasswordField sourceLdapPasswordField;
    private javax.swing.JLabel sourceLdapPasswordLabel;
    private javax.swing.JLabel sourceLdapPortLabel;
    private javax.swing.JTextField sourceLdapPortTextField;
    private javax.swing.JPanel sourceLdapServerPanel;
    private javax.swing.JTextField sourceLdapServerTextField;
    private javax.swing.JLabel sourceLdapServerTextLabel;
    private javax.swing.JLabel sourceLdapUserLabel;
    private javax.swing.JTextField sourceLdapUserTextField;
    private javax.swing.JLabel statusAnimationLabel;
    private javax.swing.JLabel statusMessageLabel;
    private javax.swing.JPanel statusPanel;
    // End of variables declaration//GEN-END:variables

    private final Timer messageTimer;

    private final Timer busyIconTimer;

    private final Icon idleIcon;

    private final Icon[] busyIcons = new Icon[15];

    private int busyIconIndex = 0;

    private JDialog aboutBox;

}

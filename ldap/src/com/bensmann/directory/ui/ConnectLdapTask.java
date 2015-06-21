package com.bensmann.directory.ui;

import com.bensmann.directory.LdapFacade;
import java.util.Map;
import org.jdesktop.application.Application;
import org.jdesktop.application.Task;

/**
 *
 * @author rbe
 */
public class ConnectLdapTask extends Task<LdapFacade, LdapFacade> {

    /**
     *
     */
    private DirectoryUIView view;

    /**
     *
     */
    private Map<String, String> params;

    /**
     * 
     * @param application
     */
    public ConnectLdapTask(Application application, Map<String, String> params) {
        super(application);
        // Check arguments
        if (null == params) {
            throw new IllegalArgumentException("params==null");
        }
        //
        this.params = params;
        view = (DirectoryUIView) DirectoryUIApp.getApplication().getMainView();
    }

    /**
     *
     * @return
     * @throws java.lang.Exception
     */
    @Override
    protected LdapFacade doInBackground() throws Exception {
        //
        LdapFacade facade = null;
        if (null != params) {
            facade = LdapFacade.getInstance("ldap://" + params.get("server") + ":" + params.get("port"), params.get("user"), params.get("pwd"), params.get("dn"));
        } else {
            view.log("Could not connect: invalid server, port, credentials or DN");
        }
        return facade;
    }

}

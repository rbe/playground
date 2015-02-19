package com.bensmann.ferchau.singleton;

import java.util.ArrayList;
import java.util.List;

/**
 * A singleton is used to ensure there's only one instance of a class (per JVM).
 * An example would be to protect uses of a resource through a pool (e.g. database connections).
 * This class uses the initialization-on-demand-holder idiom which is compatible with any Java version and
 * memory model.
 */
public class ConnectionPoolSingleton {

    private static final ConnectionPoolSingleton INSTANCE = new ConnectionPoolSingleton();

    private final List<MyConnection> connections;

    public static ConnectionPoolSingleton getInstance() {
        return INSTANCE;
    }

    private ConnectionPoolSingleton() {
        int size = 10;
        connections = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            connections.add(new MyConnection());
        }
    }

    /**
     * Returns a connection.
     * Removes connection from internal list, so that no reference is hold to the connection object.
     * @return A connection.
     */
    public MyConnection getConnection() {
        return connections.remove(0);
    }

    public void putConnection(MyConnection connection) {
        connections.add(connection);
    }

}

package metadata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rest.MetaSettings;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ConnectionPool {

  private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionPool.class);

  private String username;
  private String password;
  private String url;

  final ArrayList<CPConnection> availableConn = new ArrayList<>();
  final ArrayList<CPConnection> busyConn = new ArrayList<>();

  ConnectionPool(String username, String password, String url, String driver) throws ClassNotFoundException {
    this.username = username;
    this.password = password;
    this.url = url;
    Class.forName(driver);
    Executors.newScheduledThreadPool(1).scheduleWithFixedDelay(this::removeOldConnections, 5, 5, TimeUnit.SECONDS);

    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      try {
        LOGGER.info("Close connections to " + url + " (count: " + (availableConn.size() + busyConn.size()) + ")");
        for (CPConnection conn : availableConn) {
          conn.removeConnection();
        }
        for (CPConnection conn : busyConn) {
          conn.removeConnection();
        }
      } catch (Exception e) {
        LOGGER.error("Failed to release connections", e);
      }
    }));
  }

  private void removeOldConnections() {
    if (availableConn.size() > MetaSettings.RES_POOL_MIN_CONNECTIONS) {
      synchronized (availableConn) {
        ArrayList<CPConnection> connToRemove = new ArrayList<>();
        for (CPConnection connection : availableConn) {
          if (connection.isOld()) {
            connToRemove.add(connection);
            if (availableConn.size() - connToRemove.size() <= MetaSettings.RES_POOL_MIN_CONNECTIONS) {
              break;
            }
          }
        }
        connToRemove.forEach(this::removeAndCloseConnection);
      }
    }
  }

  public synchronized CPConnection getConnection() {
    try {
      while (busyConn.size() >= MetaSettings.RES_POOL_MAX_CONNECTIONS) {
        this.wait();
      }
      if (availableConn.size() > 0) {
        CPConnection connection = availableConn.remove(availableConn.size() - 1);
        busyConn.add(connection);
        return connection;
      } else {
        return getNewConnection();
      }
    } catch (InterruptedException e) {
      LOGGER.error("getConnection error", e);
    }
    return null;
  }

  synchronized void releaseConnection(CPConnection connection) {
    busyConn.remove(connection);
    availableConn.add(connection);
    this.notify();
  }

  private CPConnection getNewConnection() {
    try {
      CPConnection connection =
              new CPConnection(DriverManager.getConnection(url, username, password), this);
      busyConn.add(connection);
      return connection;
    } catch (Exception e) {
      LOGGER.error("Can't open new connection", e);
      return null;
    }
  }

  private void removeAndCloseConnection(CPConnection connection) {
    synchronized (availableConn) {
      try {
        availableConn.remove(connection);
        connection.removeConnection();
      } catch (SQLException e) {
        LOGGER.error("Can't close the connection", e);
      }
    }
  }
}

class CPConnection implements AutoCloseable {
  private Connection connection;
  private ConnectionPool connectionPool;
  private long lastUsed;

  CPConnection(Connection connection, ConnectionPool connectionPool) {
    this.connection = connection;
    this.connectionPool = connectionPool;
    lastUsed = System.currentTimeMillis();
  }

  public Connection getConnection() {
    return connection;
  }

  @Override
  public void close() {
    lastUsed = System.currentTimeMillis();
    connectionPool.releaseConnection(this);
  }

  void removeConnection() throws SQLException {
    connection.close();
  }

  boolean isOld() {
    return (System.currentTimeMillis() - lastUsed) / 1000 > MetaSettings.RES_POOL_CONNECTION_TTL;
  }

  DatabaseMetaData getMetaData() throws SQLException {
    return connection.getMetaData();
  }
}

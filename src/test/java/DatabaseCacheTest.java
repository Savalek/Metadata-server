import metadata.DatabaseCache;
import metadata.element.Schema;
import org.junit.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DatabaseCacheTest {

  private static DatabaseCache dbCache;
  ConcurrentHashMap<String, Schema> allSchemas;

  private static String DATABASE_NAME = "Test_database";
  private static String URL = "jdbc:h2:file:./src/test/resources/test_db";
  private static String USERNAME = "test";
  private static String PASSWORD = "password";
  private static String DRIVER = "org.h2.Driver";

  private static Connection connection;

  private static void executeSQL(String sql) {
    try (Statement statement = connection.createStatement()) {
      statement.execute(sql);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @BeforeClass
  public static void start() throws Exception {
    dbCache = new DatabaseCache(DATABASE_NAME, URL, USERNAME, PASSWORD, DRIVER);
    dbCache.setFilter(Collections.singletonList("SCHEMA%"));
    connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
    executeSQL("DROP ALL OBJECTS ");
  }

  @AfterClass
  public static void finish() throws Exception {
    connection.close();
  }

  @Before
  public void beforeEach() {
    Arrays.asList(
            "CREATE SCHEMA SCHEMA_LIONS",
            "CREATE SCHEMA SCHEMA_DOGS",
            "CREATE SCHEMA SCHEMA_COWS",
            "CREATE TABLE SCHEMA_DOGS.TABLE_EMPTY()",
            "CREATE TABLE SCHEMA_DOGS.TABLE_WOLFS(COLUMN_ID integer, COLUMN_NAME varchar)",
            "CREATE TABLE SCHEMA_DOGS.TABLE_DOGS(COLUMN_ID integer, COLUMN_NAME varchar, COLUMN_UNUSEFUL timestamp)",
            "CREATE TABLE SCHEMA_LIONS.TABLE_LIONS(COLUMN_ID integer, COLUMN_NAME varchar)"
    ).forEach(DatabaseCacheTest::executeSQL);
    dbCache.updateDatabaseCache();
    allSchemas = dbCache.getAllSchemas();
  }

  @After
  public void afterEach() {
    executeSQL("DROP ALL OBJECTS");
  }

  @Test
  public void schemasLoadedTest() {
    assertEquals(3, allSchemas.size());
    assertNotNull(TestUtils.getSchema(allSchemas, "SCHEMA_LIONS"));
    assertNotNull(TestUtils.getSchema(allSchemas, "SCHEMA_DOGS"));
    assertNotNull(TestUtils.getSchema(allSchemas, "SCHEMA_COWS"));
  }

  @Test
  public void tablesLoadedTest() {
    assertNotNull(TestUtils.getTable(allSchemas, "SCHEMA_DOGS", "TABLE_EMPTY"));
    assertNotNull(TestUtils.getTable(allSchemas, "SCHEMA_DOGS", "TABLE_WOLFS"));
    assertNotNull(TestUtils.getTable(allSchemas, "SCHEMA_DOGS", "TABLE_DOGS"));

    assertNotNull(TestUtils.getTable(allSchemas, "SCHEMA_LIONS", "TABLE_LIONS"));
  }

  @Test
  public void columnsLoadedTest() {
    assertNull(TestUtils.getColumn(allSchemas, "SCHEMA_DOGS", "TABLE_EMPTY", "SOME_COLUMN"));

    assertNotNull(TestUtils.getColumn(allSchemas, "SCHEMA_DOGS", "TABLE_WOLFS", "COLUMN_ID"));
    assertNotNull(TestUtils.getColumn(allSchemas, "SCHEMA_DOGS", "TABLE_WOLFS", "COLUMN_NAME"));

    assertNotNull(TestUtils.getColumn(allSchemas, "SCHEMA_DOGS", "TABLE_DOGS", "COLUMN_ID"));
    assertNotNull(TestUtils.getColumn(allSchemas, "SCHEMA_DOGS", "TABLE_DOGS", "COLUMN_NAME"));
    assertNotNull(TestUtils.getColumn(allSchemas, "SCHEMA_DOGS", "TABLE_DOGS", "COLUMN_UNUSEFUL"));

    assertNotNull(TestUtils.getColumn(allSchemas, "SCHEMA_LIONS", "TABLE_LIONS", "COLUMN_ID"));
    assertNotNull(TestUtils.getColumn(allSchemas, "SCHEMA_LIONS", "TABLE_LIONS", "COLUMN_NAME"));
  }

  @Test
  public void columnUpdateTest() {
    executeSQL("ALTER TABLE SCHEMA_DOGS.TABLE_WOLFS DROP COLUMN COLUMN_ID");
    dbCache.updateDatabaseCache();
    assertNotNull(TestUtils.getTable(allSchemas, "SCHEMA_DOGS", "TABLE_WOLFS"));
    assertNull(TestUtils.getColumn(allSchemas, "SCHEMA_DOGS", "TABLE_WOLFS", "COLUMN_ID"));
    assertNotNull(TestUtils.getColumn(allSchemas, "SCHEMA_DOGS", "TABLE_WOLFS", "COLUMN_NAME"));

    executeSQL("DROP TABLE SCHEMA_DOGS.TABLE_WOLFS");
    executeSQL("CREATE TABLE SCHEMA_DOGS.TABLE_WOLFS");
    dbCache.updateDatabaseCache();
    assertNotNull(TestUtils.getTable(allSchemas, "SCHEMA_DOGS", "TABLE_WOLFS"));
    assertNull(TestUtils.getColumn(allSchemas, "SCHEMA_DOGS", "TABLE_WOLFS", "COLUMN_ID"));
    assertNull(TestUtils.getColumn(allSchemas, "SCHEMA_DOGS", "TABLE_WOLFS", "COLUMN_NAME"));
  }

  @Test
  public void tableUpdateTest() {
    executeSQL("DROP TABLE SCHEMA_DOGS.TABLE_WOLFS");
    dbCache.updateDatabaseCache();
    assertNull(TestUtils.getTable(allSchemas, "SCHEMA_DOGS", "TABLE_WOLFS"));
    assertNotNull(TestUtils.getSchema(allSchemas, "SCHEMA_DOGS"));
    executeSQL("CREATE TABLE SCHEMA_DOGS.TABLE_WOLFS");
    dbCache.updateDatabaseCache();
    assertNotNull(TestUtils.getTable(allSchemas, "SCHEMA_DOGS", "TABLE_WOLFS"));
  }

  @Test
  public void schemaUpdateTest() {
    executeSQL("DROP SCHEMA SCHEMA_DOGS CASCADE");
    dbCache.updateDatabaseCache();
    assertNull(TestUtils.getSchema(allSchemas, "SCHEMA_DOGS"));
  }

  @Test
  public void searchTest() throws Exception {
    HashSet<Integer> dbItems = dbCache.jstreeGetParentsOfElements("SCHEMA");
    // Because schemas already loaded
    assertEquals(0, dbItems.size());

    dbItems = dbCache.jstreeGetParentsOfElements("TABLE");
    // Need load two schemas: SCHEMA_LIONS, SCHEMA_DOGS
    assertEquals(2, dbItems.size());

    dbItems = dbCache.jstreeGetParentsOfElements("COLUMN");
    // Need load two schemas: SCHEMA_LIONS, SCHEMA_DOGS. And three table: TABLE_WOLFS, TABLE_DOGS, TABLE_LIONS
    assertEquals(5, dbItems.size());
  }

  @Test
  public void correctUpdateSearchAfterDeleteColumnTest() {
    HashSet<Integer> dbItems;
    executeSQL("ALTER TABLE SCHEMA_DOGS.TABLE_WOLFS DROP COLUMN COLUMN_ID");
    dbCache.updateDatabaseCache();
    dbItems = dbCache.jstreeGetParentsOfElements("COLUMN_ID");
    assertEquals(4, dbItems.size());

    executeSQL("ALTER TABLE SCHEMA_DOGS.TABLE_DOGS DROP COLUMN COLUMN_ID");
    dbCache.updateDatabaseCache();
    dbItems = dbCache.jstreeGetParentsOfElements("COLUMN_ID");
    assertEquals(2, dbItems.size());

    executeSQL("ALTER TABLE SCHEMA_LIONS.TABLE_LIONS DROP COLUMN COLUMN_ID");
    dbCache.updateDatabaseCache();
    dbItems = dbCache.jstreeGetParentsOfElements("COLUMN_ID");
    assertEquals(0, dbItems.size());
  }

  @Test
  public void correctUpdateSearchAfterDeleteTableTest() {
    HashSet<Integer> dbItems;
    executeSQL("DROP TABLE SCHEMA_DOGS.TABLE_WOLFS");
    dbCache.updateDatabaseCache();
    dbItems = dbCache.jstreeGetParentsOfElements("TABLE_WOLFS");
    assertEquals(0, dbItems.size());
    dbItems = dbCache.jstreeGetParentsOfElements("COLUMN");
    assertEquals(4, dbItems.size());

    executeSQL("DROP TABLE SCHEMA_DOGS.TABLE_DOGS");
    dbCache.updateDatabaseCache();
    dbItems = dbCache.jstreeGetParentsOfElements("COLUMN");
    assertEquals(2, dbItems.size());
  }

  @Test
  public void correctUpdateSearchAfterDeleteSchemaTest() {
    HashSet<Integer> dbItems;
    executeSQL("DROP SCHEMA SCHEMA_DOGS CASCADE");
    dbCache.updateDatabaseCache();
    dbItems = dbCache.jstreeGetParentsOfElements("TABLE_WOLFS");
    assertEquals(0, dbItems.size());
    dbItems = dbCache.jstreeGetParentsOfElements("TABLE_DOGS");
    assertEquals(0, dbItems.size());
    dbItems = dbCache.jstreeGetParentsOfElements("COLUMN");
    assertEquals(2, dbItems.size());

    executeSQL("DROP SCHEMA SCHEMA_LIONS CASCADE");
    dbCache.updateDatabaseCache();
    dbItems = dbCache.jstreeGetParentsOfElements("TABLE_LIONS");
    assertEquals(0, dbItems.size());
    dbItems = dbCache.jstreeGetParentsOfElements("COLUMN");
    assertEquals(0, dbItems.size());

    assertEquals(1, dbCache.getAllSchemas().size());
  }
}

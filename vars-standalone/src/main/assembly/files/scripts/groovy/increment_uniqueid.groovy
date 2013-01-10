import groovy.sql.Sql

def bundle = ResourceBundle.getBundle("annotation-jdbc", Locale.US)
def database = Sql.newInstance(bundle.getString("jdbc.url"),
        bundle.getString("jdbc.username"),
        bundle.getString("jdbc.password"),
        bundle.getString("jdbc.driver"))
        
def rows = database.rows('SELECT tablename, nextid FROM UniqueID')
rows.each { row ->
    database.execute """
        UPDATE UniqueID SET nextid = ${row.nextid + 100} WHERE tablename = ${row.tablename}
    """
}


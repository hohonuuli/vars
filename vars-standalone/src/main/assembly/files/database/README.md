# Createing the database from scratch

1. cd `bin`
2. `derbystart`
3. `derbyij`
    1. `CONNECT 'jdbc:derby://localhost:1527/VARS;user=varsuser;password=vars0sourceforge;create=true'`
    2. `call syscs_util.syscs_set_database_property('varsuser', 'vars0sourceforge')`
    3. `CREATE SCHEMA VARSUSER AUTHORIZATION varsuser`
    4. `SET SCHEMA VARSUSER`
    5. `run '/Users/brian/Desktop/army_corp/derby/scripts/sql/test-database-create.ddl';`
    6. `run '/Users/brian/Desktop/army_corp/derby/scripts/sql/vars-selectForAnnotationViewOnDerby.sql`
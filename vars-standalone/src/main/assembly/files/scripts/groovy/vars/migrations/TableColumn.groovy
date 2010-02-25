package vars.migrations

/**
 * Bean for holding a table name and a column. Used by @{link DestroyDuplicateFKFUnction}
 */
class TableColumn {
    def table
    def column

    def TableColumn(table, column) {
        this.table = table
        this.column = column
    }

}

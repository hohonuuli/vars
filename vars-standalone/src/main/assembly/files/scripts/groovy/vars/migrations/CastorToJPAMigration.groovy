package vars.migrations

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Feb 25, 2010
 * Time: 11:43:08 AM
 * To change this template use File | Settings | File Templates.
 */
class CastorToJPAMigration {

    def apply() {
        // Fix bogus keys
        def f = new FixFKFunction()
        f.apply()

        f = new DestroyDuplicateFKFunction()
        f.apply()

        f = new CombineDuplicatesFunction()
        f.apply()
    }
}

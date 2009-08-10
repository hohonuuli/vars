package vars.hibernate;

import java.io.Serializable;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Calendar;
import java.util.TimeZone;

import org.hibernate.HibernateException;
import org.hibernate.type.CustomType;
import org.hibernate.type.Type;
import org.hibernate.cfg.Environment;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.usertype.UserType;
import org.hibernate.usertype.UserVersionType;


/**
 * UserType for non-default TimeZone. Hibernate's built-in date, time and timestamp types assume
 * that dates in the database are in Java's default time zone, implicitly. If this assumption is
 * false (and you can't make it true by calling java.util.TimeZone.setDefault), you can configure
 * Hibernate to map to a UserType that does something else....
 *
 * This code is taken from {@link http://www.hibernate.org/100.html}. However, there are comments
 * there which apply to us, namely that it would be useful to treat the activeOn property as a
 * version field which is maintained by the database and it is not clear how to get that to work.
 * Also the thread {@link http://forum.hibernate.org/viewtopic.php?t=980279} suggests handling this
 * conversion in the POJO instead. However, at least one link (reference?) I found indicated a
 * problem when the front-end and the middle layer lived in different time zones. I'm not sure that
 * applies to use since we don't allow the front end to set timestamps however, if London starts
 * handling releases, there may be a problem displaying times in an easily understood form for the
 * users.
 */
public abstract class HibernateUTC implements UserVersionType, UserType {

    /** the SQL type this type manages */
    protected static int[] SQL_TYPES_UTC = { Types.TIMESTAMP };

    // Use to cooerce raw sql queries return columns to appropriate types
  public static final Type DATE = new CustomType(DateType.class, null);
  public static final Type TIME = new CustomType(TimeType.class, null);
  public static final Type TIMESTAMP = new CustomType(TimestampType.class, null);
  public static final Type CALENDAR = new CustomType(CalendarType.class, null);

    /**
     * @see org.hibernate.usertype.UserType#sqlTypes()
     */
    public int[] sqlTypes() {
        return SQL_TYPES_UTC;
    }

    /**
     * @see org.hibernate.usertype.UserType#equals(java.lang.Object, java.lang.Object)
     */
    public boolean equals(Object x, Object y) {
        return (x == null) ? (y == null) : x.equals(y);
    }

    /**
     * @see org.hibernate.usertype.UserType#isMutable()
     */
    public boolean isMutable() {
        return true;
    }

    /**
     * @see org.hibernate.usertype.UserType#returnedClass()
     */
    public Class<?> returnedClass() {
        return objectClass;
    }

    /**
     * The class of objects returned by <code>nullSafeGet</code>. Currently, returned objects are
     * derived from this class, not exactly this class.
     */
    protected Class<?> objectClass = Date.class;

    /**
     * Get a hashcode for the instance, consistent with persistence "equality"
     */
    public int hashCode(Object x) throws HibernateException {
        return x.hashCode();
    }

    /**
     * Transform the object into its cacheable representation. At the very least this method should
     * perform a deep copy if the type is mutable. That may not be enough for some implementations,
     * however; for example, associations must be cached as identifier values. (optional operation)
     *
     * @param value the object to be cached
     * @return a cachable representation of the object
     * @throws HibernateException
     */
    public Serializable disassemble(Object value) throws HibernateException {
        return (Serializable) deepCopy(value);
    }

    /**
     * Reconstruct an object from the cacheable representation. At the very least this method should
     * perform a deep copy if the type is mutable. (optional operation)
     *
     * @param cached the object to be cached
     * @param owner the owner of the cached object
     * @return a reconstructed object from the cachable representation
     * @throws HibernateException
     */
    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        return deepCopy(cached);
    }

    /**
     * During merge, replace the existing (target) value in the entity we are merging to with a new
     * (original) value from the detached entity we are merging. For immutable objects, or null
     * values, it is safe to simply return the first parameter. For mutable objects, it is safe to
     * return a copy of the first parameter. For objects with component values, it might make sense
     * to recursively replace component values.
     *
     * @param original the value from the detached entity being merged
     * @param target the value in the managed entity
     * @return the value to be merged
     */
    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return deepCopy(original);
    }

    public Object seed(SessionImplementor si) {
        return null;
    }

    public Object next(Object current, SessionImplementor si) {
        return null;
    }

    

    /**
     * Like a Hibernate date, but using the UTC TimeZone (not the default TimeZone).
     */
    public static class DateType extends HibernateUTC {
        protected static int[] SQL_TYPES_DATE = { Types.DATE };

        /**
         * @see org.hibernate.usertype.UserType#sqlTypes()
         */
        public int[] sqlTypes() {
            return SQL_TYPES_DATE;
        }

        /**
         * @see org.hibernate.usertype.UserType#deepCopy(java.lang.Object)
         */
        public Object deepCopy(Object value) {
            return (value == null) ? null : new java.sql.Date(((Date) value).getTime());

        }

        /**
         * @see org.hibernate.usertype.UserType#nullSafeGet(java.sql.ResultSet, java.lang.String[],
         *      java.lang.Object)
         */
        public Object nullSafeGet(ResultSet rs, String[] names, Object owner) throws SQLException {
            Calendar utcCalendar = (Calendar) UTC_CALENDAR.clone();
            return rs.getDate(names[0], utcCalendar);
        }

        /**
         * @see org.hibernate.usertype.UserType#nullSafeSet(java.sql.PreparedStatement, java.lang.Object,
         *      int)
         */
        public void nullSafeSet(PreparedStatement st, Object value, int index) throws SQLException {
            if (!(value instanceof java.sql.Date))
                value = deepCopy(value);
            Calendar utcCalendar = (Calendar) UTC_CALENDAR.clone();
            st.setDate(index, (java.sql.Date) value, utcCalendar);
        }

        public int compare(Object x, Object y) {
            if (x == null && y == null)
                return 0;
            else if (x == null)
                return 1;
            else if (y == null)
                return -1;
            else {
                java.sql.Date c1 = (java.sql.Date) x;
                java.sql.Date c2 = (java.sql.Date) y;
                return c1.compareTo(c2);
            }
        }

        /*
         * (non-Javadoc)
         * @see org.hibernate.usertype.UserType#hashCode(java.lang.Object)
         */
        public int hashCode(Object x) throws HibernateException {
            return ((java.sql.Date) x).hashCode();
        }

        public Object seed(SessionImplementor si) {
            return new java.sql.Date(System.currentTimeMillis());
        }

        public Object next(Object current, SessionImplementor si) {
            return new java.sql.Date(System.currentTimeMillis());
        }
    }

    /**
     * Like a Hibernate time, but using the UTC TimeZone (not the default TimeZone).
     */
    public static class TimeType extends HibernateUTC {

        protected static int[] SQL_TYPES_TIME = { Types.TIME };

        /**
         * @see org.hibernate.usertype.UserType#sqlTypes()
         */
        public int[] sqlTypes() {
            return SQL_TYPES_TIME;
        }

        /**
         * @see org.hibernate.usertype.UserType#deepCopy(java.lang.Object)
         */
        public Object deepCopy(Object value) {
            return (value == null) ? null : new java.sql.Time(((Date) value).getTime());
        }

        /**
         * @see org.hibernate.usertype.UserType#nullSafeGet(java.sql.ResultSet, java.lang.String[],
         *      java.lang.Object)
         */
        public Object nullSafeGet(ResultSet rs, String[] names, Object owner) throws SQLException {
            Calendar utcCalendar = (Calendar) UTC_CALENDAR.clone();
            return rs.getTime(names[0], utcCalendar);
        }

        /**
         * @see org.hibernate.usertype.UserType#nullSafeSet(java.sql.PreparedStatement, java.lang.Object,
         *      int)
         */
        public void nullSafeSet(PreparedStatement st, Object value, int index) throws SQLException {
            if (!(value instanceof java.sql.Time))
                value = deepCopy(value);
            Calendar utcCalendar = (Calendar) UTC_CALENDAR.clone();
            st.setTime(index, (java.sql.Time) value, utcCalendar);
        }

        public int compare(Object x, Object y) {
            if (x == null && y == null)
                return 0;
            else if (x == null)
                return 1;
            else if (y == null)
                return -1;
            else {
                java.sql.Time c1 = (java.sql.Time) x;
                java.sql.Time c2 = (java.sql.Time) y;
                return compare(c1, c2);
            }
        }

        /*
         * (non-Javadoc)
         * @see org.hibernate.usertype.UserType#hashCode(java.lang.Object)
         */
        public int hashCode(Object x) throws HibernateException {
            return ((java.sql.Time) x).hashCode();
        }

        public Object seed(SessionImplementor si) {
            return new java.sql.Time(System.currentTimeMillis());
        }

        public Object next(Object current, SessionImplementor si) {
            return new java.sql.Time(System.currentTimeMillis());
        }

    }

    /**
     * Like a Hibernate timestamp, but using the UTC TimeZone (not the default TimeZone).
     */
    public static class TimestampType extends HibernateUTC {

        /**
         * @see org.hibernate.usertype.UserType#deepCopy(java.lang.Object)
         */
        public Object deepCopy(Object value) {
            if (value == null)
                return null;
            java.sql.Timestamp ots = (java.sql.Timestamp) value;
            java.sql.Timestamp ts = new java.sql.Timestamp(ots.getTime());
            ts.setNanos(ots.getNanos());
            return ts;
        }

        /**
         * @see org.hibernate.usertype.UserType#nullSafeGet(java.sql.ResultSet, java.lang.String[],
         *      java.lang.Object)
         */
        public Object nullSafeGet(ResultSet rs, String[] names, Object owner) throws SQLException {
            Calendar utcCalendar = (Calendar) UTC_CALENDAR.clone();
            return rs.getTimestamp(names[0], utcCalendar);
        }

        /**
         * @see org.hibernate.usertype.UserType#nullSafeSet(java.sql.PreparedStatement, java.lang.Object,
         *      int)
         */

        public void nullSafeSet(PreparedStatement st, Object value, int index) throws SQLException {
            if (!(value instanceof java.sql.Timestamp))
                value = deepCopy(value);
            Calendar utcCalendar = (Calendar) UTC_CALENDAR.clone();
            st.setTimestamp(index, (java.sql.Timestamp) value, utcCalendar);
        }

        public int compare(Object x, Object y) {
            if (x == null && y == null)
                return 0;
            else if (x == null)
                return 1;
            else if (y == null)
                return -1;
            else {
                Timestamp c1 = (Timestamp) x;
                Timestamp c2 = (Timestamp) y;
                return c1.compareTo(c2);
            }
        }
        /*
         * (non-Javadoc)
         * @see org.hibernate.usertype.UserType#hashCode(java.lang.Object)
         */
        public int hashCode(Object x) throws HibernateException {
            return ((Timestamp) x).hashCode();
        }

        public Object seed(SessionImplementor si) {
            return new Timestamp(System.currentTimeMillis());
            // return new Timestamp(si.getTimestamp());
        }

        public Object next(Object current, SessionImplementor si) {
            return new Timestamp(System.currentTimeMillis());
            // return new Timestamp(si.getTimestamp());
        }

        public Class<?> getReturnedClass() {
          return java.sql.Timestamp.class;
        }
    }

    public static class CalendarType extends HibernateUTC {

        public Class<?> getReturnedClass() {
            return Calendar.class;
        }

        /**
         * @see org.hibernate.usertype.UserType#deepCopy(java.lang.Object)
         */
        public Object deepCopy(Object value) {
            if (value == null) {
                return null;
            }
            Calendar c = (Calendar) UTC_CALENDAR.clone();
            c.setTimeInMillis(((Calendar) value).getTimeInMillis());
            return c;
        }

        /**
         * @see org.hibernate.usertype.UserType#nullSafeGet(java.sql.ResultSet, java.lang.String[],
         *      java.lang.Object)
         */
        public Object nullSafeGet(ResultSet rs, String[] names, Object owner) throws SQLException {
            Calendar cal = (Calendar) UTC_CALENDAR.clone();
            Timestamp ts = rs.getTimestamp(names[0], cal);
            if (ts == null || rs.wasNull()) {
                return null;
            }
            if (Environment.jvmHasTimestampBug()) {
                cal.setTime(new Date(ts.getTime() + ts.getNanos() / 1000000));
            } else {
                cal.setTime(ts);
            }
            return cal;

        }

        /**
         * @see org.hibernate.usertype.UserType#nullSafeSet(java.sql.PreparedStatement, java.lang.Object,
         *      int)
         */

        public void nullSafeSet(PreparedStatement st, Object value, int index) throws SQLException {
            if (value == null) {
                st.setNull(index, Types.TIMESTAMP);
            } else {
                Timestamp t = new Timestamp(((Calendar) value).getTimeInMillis());
                Calendar utcCalendar = (Calendar) UTC_CALENDAR.clone();
                st.setTimestamp(index, t, utcCalendar);
            }
        }

        /*
         * (non-Javadoc)
         * @see org.hibernate.usertype.UserType#equals(java.lang.Object, java.lang.Object)
         */
        public boolean equals(Object x, Object y) {
            if (x == y)
                return true;
            if (x == null || y == null)
                return false;

            Calendar calendar1 = (Calendar) x;
            Calendar calendar2 = (Calendar) y;

            return calendar1.getTimeInMillis() == calendar2.getTimeInMillis();
        }

        public int compare(Object x, Object y) {
            if (x == null && y == null)
                return 0;
            else if (x == null)
                return 1;
            else if (y == null)
                return -1;
            else {
                Calendar c1 = (Calendar) x;
                Calendar c2 = (Calendar) y;
                return c1.compareTo(c2);
            }
        }

        /*
         * (non-Javadoc)
         * @see org.hibernate.usertype.UserType#hashCode(java.lang.Object)
         */
        public int hashCode(Object x) throws HibernateException {
            return ((Calendar) x).hashCode();
        }

        public Object seed(SessionImplementor si) {
            Calendar cal = (Calendar) UTC_CALENDAR.clone();
            cal.setTimeInMillis(System.currentTimeMillis());
            return cal;
        }

        public Object next(Object current, SessionImplementor si) {
            Calendar cal = (Calendar) UTC_CALENDAR.clone();
            cal.setTimeInMillis(System.currentTimeMillis());
            return cal;
        }

    }

    /**
     * Note 071107: passing the static sUTCCalendar instance to the setTimestamp(), getTimestamp()
     * calls above has concurrency issues, as some JDBC drivers do modify the supplied calendar
     * instance. Consequence, the calendar is cloned before use.
     */

    /** the Calendar to hold the UTC timezone */
    private static final TimeZone TZ_UTC;
    private static final Calendar UTC_CALENDAR;
    static {
        TZ_UTC = TimeZone.getTimeZone("UTC");
        UTC_CALENDAR = Calendar.getInstance(TZ_UTC);
    }

}
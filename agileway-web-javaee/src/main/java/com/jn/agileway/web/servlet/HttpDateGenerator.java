package com.jn.agileway.web.servlet;


import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;


public class HttpDateGenerator
{
    private static final TimeZone __GMT = TimeZone.getTimeZone("GMT");

    static
    {
        __GMT.setID("GMT");
    }

    static final String[] DAYS =
            {"Sat", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
    static final String[] MONTHS =
            {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec", "Jan"};

    private static final ThreadLocal<HttpDateGenerator> __dateGenerator = new ThreadLocal<HttpDateGenerator>()
    {
        @Override
        protected HttpDateGenerator initialValue()
        {
            return new HttpDateGenerator();
        }
    };

    public static final String __01Jan1970 = HttpDateGenerator.formatDate(0);

    /**
     * Format HTTP date "EEE, dd MMM yyyy HH:mm:ss 'GMT'"
     *
     * @param date the date in milliseconds
     * @return the formatted date
     */
    public static String formatDate(long date)
    {
        return __dateGenerator.get().doFormatDate(date);
    }

    /**
     * Format "EEE, dd-MMM-yyyy HH:mm:ss 'GMT'" for cookies
     *
     * @param buf the buffer to put the formatted date into
     * @param date the date in milliseconds
     */
    public static void formatCookieDate(StringBuilder buf, long date)
    {
        __dateGenerator.get().doFormatCookieDate(buf, date);
    }

    /**
     * Format "EEE, dd-MMM-yyyy HH:mm:ss 'GMT'" for cookies
     *
     * @param date the date in milliseconds
     * @return the formatted date
     */
    public static String formatCookieDate(long date)
    {
        StringBuilder buf = new StringBuilder(28);
        formatCookieDate(buf, date);
        return buf.toString();
    }

    private final StringBuilder buf = new StringBuilder(32);
    private final GregorianCalendar gc = new GregorianCalendar(__GMT);

    /**
     * Format HTTP date "EEE, dd MMM yyyy HH:mm:ss 'GMT'"
     *
     * @param date the date in milliseconds
     * @return the formatted date
     */
    public String doFormatDate(long date)
    {
        buf.setLength(0);
        gc.setTimeInMillis(date);

        int dayOfWeek = gc.get(Calendar.DAY_OF_WEEK);
        int dayOfMonth = gc.get(Calendar.DAY_OF_MONTH);
        int month = gc.get(Calendar.MONTH);
        int year = gc.get(Calendar.YEAR);
        int century = year / 100;
        year = year % 100;

        int hours = gc.get(Calendar.HOUR_OF_DAY);
        int minutes = gc.get(Calendar.MINUTE);
        int seconds = gc.get(Calendar.SECOND);

        buf.append(DAYS[dayOfWeek]);
        buf.append(',');
        buf.append(' ');
        append2digits(buf, dayOfMonth);

        buf.append(' ');
        buf.append(MONTHS[month]);
        buf.append(' ');
        append2digits(buf, century);
        append2digits(buf, year);

        buf.append(' ');
        append2digits(buf, hours);
        buf.append(':');
        append2digits(buf, minutes);
        buf.append(':');
        append2digits(buf, seconds);
        buf.append(" GMT");
        return buf.toString();
    }

    /**
     * Format "EEE, dd-MMM-yy HH:mm:ss 'GMT'" for cookies
     *
     * @param buf the buffer to format the date into
     * @param date the date in milliseconds
     */
    public void doFormatCookieDate(StringBuilder buf, long date)
    {
        gc.setTimeInMillis(date);

        int dayOfWeek = gc.get(Calendar.DAY_OF_WEEK);
        int dayOfMonth = gc.get(Calendar.DAY_OF_MONTH);
        int month = gc.get(Calendar.MONTH);
        int year = gc.get(Calendar.YEAR);
        year = year % 10000;

        int epoch = (int)((date / 1000) % (60 * 60 * 24));
        int seconds = epoch % 60;
        epoch = epoch / 60;
        int minutes = epoch % 60;
        int hours = epoch / 60;

        buf.append(DAYS[dayOfWeek]);
        buf.append(',');
        buf.append(' ');
        append2digits(buf, dayOfMonth);

        buf.append('-');
        buf.append(MONTHS[month]);
        buf.append('-');
        append2digits(buf, year / 100);
        append2digits(buf, year % 100);

        buf.append(' ');
        append2digits(buf, hours);
        buf.append(':');
        append2digits(buf, minutes);
        buf.append(':');
        append2digits(buf, seconds);
        buf.append(" GMT");
    }

    /**
     * Append 2 digits (zero padded) to the StringBuilder
     *
     * @param buf the buffer to append to
     * @param i the value to append
     */
    public static void append2digits(StringBuilder buf, int i)
    {
        if (i < 100)
        {
            buf.append((char)(i / 10 + '0'));
            buf.append((char)(i % 10 + '0'));
        }
    }

}

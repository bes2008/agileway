package com.jn.agileway.syslog.protocol;


/**
 * Defines the standard Syslog message facilities.
 *
 * @see <a href="https://tools.ietf.org/html/rfc5424#section-6.2.1">RFC-5424 section 6.2.1</a>
 */
public enum Facility {

    /**
     * Kernel messages.
     */
    KERN(0),
    /**
     * User-level messages.
     */
    USER(1),
    /**
     * Mail system.
     */
    MAIL(2),
    /**
     * System daemons.
     */
    DAEMON(3),
    /**
     * Security/authorization messages.
     */
    AUTH(4),
    /**
     * Messages generated internally by syslogd.
     */
    SYSLOG(5),
    /**
     * Line printer subsystem.
     */
    LPR(6),
    /**
     * Network news subsystem.
     */
    NEWS(7),
    /**
     * UUCP subsystem.
     */
    UUCP(8),
    /**
     * Clock daemon.
     */
    CRON(9),
    /**
     * Security/authorization messages.
     */
    AUTHPRIV(10),
    /**
     * FTP daemon.
     */
    FTP(11),
    /**
     * NTP subsystem.
     */
    NTP(12),
    /**
     * Log audit.
     */
    LOGAUDIT(13),
    /**
     * Log alert.
     */
    LOGALERT(14),
    /**
     * Clock daemon.
     */
    CLOCKD(15),
    /**
     * Local use 0 (local0).
     */
    LOCAL0(16),
    /**
     * Local use 1 (local1).
     */
    LOCAL1(17),
    /**
     * Local use 2 (local2).
     */
    LOCAL2(18),
    /**
     * Local use 3 (local3).
     */
    LOCAL3(19),
    /**
     * Local use 4 (local4).
     */
    LOCAL4(20),
    /**
     * Local use 5 (local5).
     */
    LOCAL5(21),
    /**
     * Local use 6 (local6).
     */
    LOCAL6(22),
    /**
     * Local use 7 (local7).
     */
    LOCAL7(23);

    private final int code;

    Facility(int code) {
        this.code = code;
    }

    /**
     * Get the syslog code for the facility.
     * @return The code.
     */
    public int getCode() {
        return code;
    }
}

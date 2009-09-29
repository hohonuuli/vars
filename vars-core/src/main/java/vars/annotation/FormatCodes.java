package vars.annotation;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 5, 2009
 * Time: 1:51:35 PM
 * To change this template use File | Settings | File Templates.
 */
public enum FormatCodes {

    /**
     * VideoArchiveSet represents Tapes annotated in Detailed mode only.
     */
    DETAILED('d'),

    /**
     * VideoArchiveSet represents Tapes annotated in Detailed mode and outline mode.
     * @deprecated: 20040907 brian: THis code is no longer used. At the request of the video lab.
     *
     */
    MIXED('m'),

    /**
     * VideoArchiveSet represents Tapes annotated in Outline mode only.
     */
    OUTLINE('o'),

    UNKNOWN('u');

    private char code;

    FormatCodes(char code) {
        this.code = code;
    }

    public char getCode() {
        return code;
    }

    @Override
    public String toString() {
        return String.valueOf(code);
    }
}

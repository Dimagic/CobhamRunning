package net.ddns.dimag.cobhamrunning.utils;

public class CobhamRunningException extends Exception {
    public CobhamRunningException(String message)
    {
        super(message);
    }
    public CobhamRunningException(Exception e) {super(e);}
}

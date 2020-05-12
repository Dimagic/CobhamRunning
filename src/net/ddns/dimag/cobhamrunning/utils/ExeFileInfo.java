package net.ddns.dimag.cobhamrunning.utils;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.VerRsrc.VS_FIXEDFILEINFO;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

public class ExeFileInfo {
    public static int MAJOR = 0;
    public static int MINOR = 1;
    public static int BUILD = 2;
    public static int REVISION = 3;

    public static int getMajorVersionOfProgram(String path) throws CobhamRunningException {
        return getVersionInfo(path)[MAJOR];
    }

    public static int getMinorVersionOfProgram(String path) throws CobhamRunningException {
        return getVersionInfo(path)[MINOR];
    }

    public static int getBuildOfProgram(String path) throws CobhamRunningException {
        return getVersionInfo(path)[BUILD];
    }

    public static int getRevisionOfProgram(String path) throws CobhamRunningException {
        return getVersionInfo(path)[REVISION];
    }

    public static String getStringVersion(String path) throws CobhamRunningException {
        return String.format("%s.%s.%s.%s", getMajorVersionOfProgram(path),
                getMinorVersionOfProgram(path), getBuildOfProgram(path), getRevisionOfProgram(path));
    }

    public static int[] getVersionInfo(String path) throws CobhamRunningException {
        try {
            IntByReference dwDummy = new IntByReference();
            dwDummy.setValue(0);

            int versionlength = com.sun.jna.platform.win32.Version.INSTANCE.GetFileVersionInfoSize(path, dwDummy);

            byte[] bufferarray = new byte[versionlength];
            Pointer lpData = new Memory(bufferarray.length);
            PointerByReference lplpBuffer = new PointerByReference();
            IntByReference puLen = new IntByReference();
            boolean fileInfoResult = com.sun.jna.platform.win32.Version.INSTANCE.GetFileVersionInfo(path, 0, versionlength, lpData);
            boolean verQueryVal = com.sun.jna.platform.win32.Version.INSTANCE.VerQueryValue(lpData, "\\", lplpBuffer, puLen);

            VS_FIXEDFILEINFO lplpBufStructure = new VS_FIXEDFILEINFO(lplpBuffer.getValue());
            lplpBufStructure.read();

            int v1 = (lplpBufStructure.dwFileVersionMS).intValue() >> 16;
            int v2 = (lplpBufStructure.dwFileVersionMS).intValue() & 0xffff;
            int v3 = (lplpBufStructure.dwFileVersionLS).intValue() >> 16;
            int v4 = (lplpBufStructure.dwFileVersionLS).intValue() & 0xffff;
            return new int[] { v1, v2, v3, v4 };
        } catch (Exception e) {
            throw new CobhamRunningException(e);
        }
    }
}
package net.sharksystem.fs;

import net.sharksystem.SharkException;
import net.sharksystem.asap.ASAPException;
import net.sharksystem.asap.utils.ASAPSerialization;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * An ExtraData implementation backed by the filesystem.
 */
public class ExtraDataFS implements ExtraData {
    public static final String DEFAULT_FILE_NAME = ".extraData";
    private final CharSequence fileName;
    private final CharSequence rootFolderName;

    private Map<CharSequence, byte[]> extraData = new HashMap<>();

    public ExtraDataFS(CharSequence rootFolderName) throws SharkException, IOException {
        this(rootFolderName, DEFAULT_FILE_NAME);
    }

    public ExtraDataFS(CharSequence rootFolder, CharSequence fileName) throws IOException, SharkException {
        this.rootFolderName = rootFolder;
        // hide file in any case
        if(fileName.charAt(0) != '.') {
            this.fileName = "." + fileName;
        }
        else {
            this.fileName = fileName;
        }
        File rootFolderFile = new File(rootFolder.toString());
        if(!rootFolderFile.exists()) {
            // create
            rootFolderFile.mkdirs();
        }
        this.restoreExtraData();
    }

    private File getExtraFile() {
        String extraFileName = this.rootFolderName + "/" + fileName;
        return new File(extraFileName);
    }

    /*
    Here is the catch... There can be - and in Android will - two instances share data over file system. The use
    same clock but run in different threads, most likely different processes. We have to synchronize both sides.

    There will be two processes A and B. Let's assume we are in process A.
    a) We never written something,

     */

    private long timeStampSyncExtraData = -1; // never saved anything

    private void extraDataSync() throws IOException, SharkException {
        InputStream is;
        try {
            is = new FileInputStream(this.getExtraFile());
        }
        catch(IOException ioEx) {
            // no such file - save it instead - there is no conflict
            this.saveExtraData();
            return;
        }

        // discard local changes and read from file
        this.restoreExtraData();
    }

    public void saveExtraData() throws IOException {
        OutputStream os = new FileOutputStream(this.getExtraFile());

        // write time stamp
        this.timeStampSyncExtraData = System.currentTimeMillis();
        ASAPSerialization.writeLongParameter(this.timeStampSyncExtraData, os);
        ASAPSerialization.writeNonNegativeIntegerParameter(this.extraData.size(), os);

        for(CharSequence key : this.extraData.keySet()) {
            // write key
            ASAPSerialization.writeCharSequenceParameter(key, os);
            // value
            byte[] v = this.extraData.get(key);
            ASAPSerialization.writeByteArray(v, os);
        }

        os.close();
    }

    /**
     * Ensures that this ExtraData holds the most recent data from the file system.
     * @throws IOException if an I/O error occurs
     * @throws SharkException if EOF is reached
     */
    public void restoreExtraData() throws IOException, SharkException {
        InputStream is;
        try {
            is = new FileInputStream(this.getExtraFile());
        }
        catch(IOException ioEx) {
            // no such file - nothing to do here
            return;
        }

        long timeStampSaved = ASAPSerialization.readLongParameter(is);
        // false if the data file contains more recent data than this ExtraData,
        // true if this ExtraData is up-to-date
        if(timeStampSaved == this.timeStampSyncExtraData) {
            is.close();
            return;
        }

        this.timeStampSyncExtraData = timeStampSaved;

        this.extraData = new HashMap<>();
        int counter = ASAPSerialization.readIntegerParameter(is);

        for (int i = 0; i < counter; i++) {
            // read key
            CharSequence key = ASAPSerialization.readCharSequenceParameter(is);
            // value
            byte[] value = ASAPSerialization.readByteArray(is);

            // save in memory
            this.extraData.put(key, value);
        }
        is.close();
    }

    @Override
    public void putExtra(CharSequence key, byte[] value) throws IOException, SharkException {
        this.extraDataSync();
        this.extraData.put(key, value);
        this.saveExtraData();
    }

    @Override
    public byte[] getExtra(CharSequence key) throws IOException, SharkException {
        this.restoreExtraData();
        byte[] value = this.extraData.get(key);
        if (value == null) {
            throw new SharkException("no value for key " + key);
        }
        return value;
    }

    @Override
    public void removeAll() {
        this.getExtraFile().delete();
    }
}

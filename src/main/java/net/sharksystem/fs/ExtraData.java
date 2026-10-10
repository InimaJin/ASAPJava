package net.sharksystem.fs;

import net.sharksystem.SharkException;

import java.io.IOException;
import java.util.Set;

/**
 * ExtraData provides utilities for writing to and reading from persistent storage using key-value pairs.
 */
public interface ExtraData {
    /**
     * Stores the given byte array and associates it with the given key.
     * @param key the key of the datum
     * @param value the byte array to store
     * @throws IOException if the datum cannot be written
     * @throws SharkException if a (de-)serialization error occurs, e.g. the underlying data is corrupted
     */
    void putExtra(CharSequence key, byte[] value) throws IOException, SharkException;

    /**
     * Stores the given integer value and associates it with the given key.
     * @param key the key of the datum
     * @param value the integer value to store
     * @throws IOException if the datum cannot be written
     * @throws SharkException if a (de-)serialization error occurs, e.g. the underlying data is corrupted
     */
    void putExtra(CharSequence key, Integer value) throws IOException, SharkException;

    /**
     * Stores the given char sequence and associates it with the given key.
     * @param key the key of the datum
     * @param value the char sequence to store
     * @throws IOException if the datum cannot be written
     * @throws SharkException if a (de-)serialization error occurs, e.g. the underlying data is corrupted
     */
    void putExtra(CharSequence key, CharSequence value) throws IOException, SharkException;

    /**
     * Stores the given set f char sequences and associates it with the given key.
     * @param key the key of the datum
     * @param value the set to store
     * @throws IOException if the datum cannot be written
     * @throws SharkException if a (de-)serialization error occurs, e.g. the underlying data is corrupted
     */
    void putExtra(CharSequence key, Set<CharSequence> value) throws IOException, SharkException;

    /**
     * Returns the byte array associated with the given key.
     * @param key the key whose value is to be retrieved
     * @return the value associated with the key. A value of null indicates that the key had been mapped onto null.
     * @throws IOException if an I/O error occurs
     * @throws SharkException if no such key exists
     */
    byte[] getExtra(CharSequence key) throws IOException, SharkException;

    /**
     * Returns the integer value associated with the given key.
     * @param key the key whose value is to be retrieved
     * @return the value associated with the key.
     * @throws IOException if an I/O error occurs
     * @throws SharkException if no such key exists
     */
    int getExtraInteger(CharSequence key) throws IOException, SharkException;

    /**
     * Returns the set of char sequences associated with the given key.
     * @param key the key whose value is to be retrieved
     * @return the value associated with the key.
     * @throws IOException if an I/O error occurs
     * @throws SharkException if no such key exists
     */
    Set<CharSequence> getExtraCharSequenceSetParameter(CharSequence key) throws IOException, SharkException;

    /**
     * Returns the string value associated with the given key.
     * @param key the key whose value is to be retrieved
     * @return the value associated with the key.
     * @throws IOException if an I/O error occurs
     * @throws SharkException if no such key exists
     */
    String getExtraString(CharSequence key) throws IOException, SharkException;

    /**
     * Deletes all data from storage.
     * @throws IOException if an I/O error occurs
     * @throws SharkException if a (de-)serialization error occurs, e.g. the underlying data is corrupted
     */
    void removeAll() throws IOException, SharkException;
}

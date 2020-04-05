/**
 * Created: 27.07.2016
 */
package de.freese.mediathek.cache;

import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Basis-Implementierung eines Caches.
 *
 * @author Thomas Freese
 */
public abstract class AbstractCache implements Cache
{
    /**
     * Erzeugt den MessageDigest für die Generierung des Keys.<br>
     * Beim Auftreten einer {@link NoSuchAlgorithmException} wird diese in eine {@link RuntimeException} konvertiert.
     *
     * @return {@link MessageDigest}
     *
     * @throws RuntimeException Falls was schief geht.
     */
    protected static MessageDigest createMessageDigest() throws RuntimeException
    {
        MessageDigest messageDigest = null;

        try
        {
            messageDigest = MessageDigest.getInstance("SHA-256");
        }
        catch (final NoSuchAlgorithmException ex)
        {
            try
            {
                messageDigest = MessageDigest.getInstance("MD5");
            }
            catch (final NoSuchAlgorithmException ex2)
            {
                throw new RuntimeException(ex2);
            }
        }

        return messageDigest;
    }

    /**
     *
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     *
     */
    private final MessageDigest messageDigest;

    /**
     * Erstellt ein neues {@link AbstractCache} Object.
     */
    public AbstractCache()
    {
        super();

        this.messageDigest = createMessageDigest();
    }

    /**
     * Erzeugt den Key auf dem Resource-Pfad.<br>
     * Die Bytes des MessageDigest werden dafür in einen Hex-String umgewandelt.
     *
     * @param path String
     *
     * @return String
     */
    protected String generateKey(final String path)
    {
        byte[] digest = getMessageDigest().digest(path.getBytes());
        String hex = Hex.encodeHexString(digest);

        return hex;
    }

    /**
     * @return {@link Logger}
     */
    protected Logger getLogger()
    {
        return this.logger;
    }

    /**
     * @return {@link MessageDigest}
     */
    protected MessageDigest getMessageDigest()
    {
        return this.messageDigest;
    }
}

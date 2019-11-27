package lib.util;

/**
 * Schnittstelle zum Vergleichen von Daten
 *
 * @param <ContentType> Datentyp der zu vergleichenden Daten
 * @author Stefan Christian Kohlmeier
 */
public interface ComparableContent<ContentType>
{

    /**
     * Prüft, ob dieses Element genauso groß wie <code>pContent</code> ist.
     *
     * @param pContent Element zum Vergleichen
     * @return wenn dieses Element genauso groß wie pContent ist <code>true</code>, ansonsten <code>false</code>
     */
    public boolean isEqual(ContentType pContent);

    /**
     * Prüft, ob dieses Element größer ist als <code>pContent</code>.
     *
     * @param pContent Element zum Vergleichen
     * @return wenn dieses Element größer ist als pContent <code>true</code>, ansonsten <code>false</code>
     */
    public boolean isGreater(ContentType pContent);

    /**
     * Prüft, ob dieses Element kleiner ist als <code>pContent</code>.
     *
     * @param pContent Element zum Vergleichen
     * @return wenn dieses Element kleiner ist als pContent <code>true</code>, anstonsten <code>false</code>
     */
    public boolean isLess(ContentType pContent);

}


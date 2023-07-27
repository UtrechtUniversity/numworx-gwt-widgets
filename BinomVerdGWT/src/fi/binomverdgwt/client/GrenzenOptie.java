package fi.binomverdgwt.client;

/**
 * Typesafe enum om aan te geven welke kans bekeken wordt
 * JDK 1.4 compatible
 * 
 * LINKS is alle gevallen kleiner dan de grens links (bij twee grenzen) of alle gevallen kleiner dan de grens (bij één grens)
 * RECHTS is alle gevallen groter dan de grens rechts (bij twee grenzen) of alle gevallen groter dan de grens (bij één grens)
 * GELIJK is alle gevallen >= grens links en <= grens rechts (bij twee grenzen) of het geval P(X=grens) (bij één grens)
 */

public class GrenzenOptie 
{
    private final String name;
    
    public static final GrenzenOptie LINKS = new GrenzenOptie("GrenzenOptie Links");
    public static final GrenzenOptie GELIJK = new GrenzenOptie("GrenzenOptie Gelijk");
    public static final GrenzenOptie RECHTS = new GrenzenOptie("GrenzenOpte Rechts");
    
    private GrenzenOptie(String name)
    {
        this.name = name;
    }
    
    public String toString()
    {
        return this.name;
    }
}
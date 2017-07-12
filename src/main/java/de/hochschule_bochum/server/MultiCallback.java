package de.hochschule_bochum.server;

/**
 * Created by nikla on 07.07.2017.
 */
public interface MultiCallback<Tyoe, Type> {
    public abstract void callback(Tyoe cb, Type cb2);
}

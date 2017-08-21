package de.hochschule_bochum.matrixtable.engine;

import de.hochschule_bochum.matrixtable.engine.game.Game;
import de.hochschule_bochum.matrixtable.ledmatrix.animations.Animation;
import de.hochschule_bochum.matrixtable.ledmatrix.objects.Display;
import de.hochschule_bochum.matrixtable.ledmatrix.objects.impl.APA102Impl;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Manager {

    private static ArrayList<Game> gamelist = new ArrayList<>();
    private static ArrayList<Animation> list = new ArrayList<>();

    public static ArrayList<Game> getGames() {
        if (gamelist.isEmpty()) {
            Reflections reflections = new Reflections("de.hochschule_bochum.matrixtable.game");
            Set<Class<? extends Game>> subTypes = reflections.getSubTypesOf(Game.class);

            subTypes.forEach(aClass -> {
                try {
                    Method methode = aClass.getDeclaredMethod("newInstance");
                    Object gameObj = methode.invoke(aClass.newInstance());
                    if (gameObj instanceof Game) {
                        Game game = (Game) gameObj;
                        gamelist.add(game);
                    }
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
                    Logger.getLogger(Manager.class.getName()).log(Level.SEVERE, null, e);
                }
            });
        }
        return gamelist;
    }

    public static ArrayList<Animation> getAnimations(Display d) {
        if (list.isEmpty()) {
            Reflections reflections = new Reflections("de.hochschule_bochum.matrixtable");
            Set<Class<? extends Animation>> classes = reflections.getSubTypesOf(Animation.class);

            classes.forEach(aClass -> {
                try {
                    Method methode = aClass.getDeclaredMethod("newInstance", Display.class);
                    Object obj = methode.invoke(aClass.getDeclaredConstructor(Display.class).newInstance(d), d);
                    if (obj instanceof Animation) {
                        Animation animation = (Animation) obj;
                        list.add(animation);
                    }
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
                    Logger.getLogger(Manager.class.getName()).log(Level.SEVERE, null, e);
                }
            });
        }
        return list;
    }

    public static void main(String[] s) {
        System.out.println(getAnimations(new APA102Impl(10, 20)));
    }
}

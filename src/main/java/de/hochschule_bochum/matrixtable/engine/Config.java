package de.hochschule_bochum.matrixtable.engine;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by nikla on 31.07.2017.
 */
public class Config extends JSONObject {

    File file;
    private boolean firstRun = false;

    public Config(File file) throws IOException {
        this.file = file;

        if (!file.exists()) {
            file.createNewFile();
            firstRun = true;
        }

        if (!file.isFile()) {
            throw new FileNotFoundException(file.getAbsolutePath() + " not found");
        }

        if (!file.canRead() || !file.canWrite()) {
            throw new AccessDeniedException(file.getAbsolutePath() + " is not accessable");
        }

        this.put("debug", true);
        this.put("api_address", "http://127.0.0.1:8081"); // To Sync with remote device
        this.put("api_port", 8081); // To Sync with remote device
        this.put("database", "database.sqlite3");
        this.put("led_type", "apa102");
    }

    public void save() {
        try {
            FileWriter fw = new FileWriter(file.getAbsolutePath());
            fw.write(this.toString(4));
            fw.close();
        } catch (IOException e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
        }
    }

    public void load() {
        try {
            String content = new String(Files.readAllBytes(file.toPath()), "UTF-8");
            if (content.isEmpty()) {
                save();
                return;
            }
            JSONTokener jt = new JSONTokener(content);
            if (jt.nextClean() != 123) {
                throw jt.syntaxError("A JSONObject text must begin with '{'");
            } else {
                while (jt.more()) {
                    char c = jt.nextClean();
                    switch (c) {
                        case '\u0000':
                            throw jt.syntaxError("A JSONObject text must end with '}'");
                        case '}':
                            return;
                        default:
                            jt.back();
                            String key = jt.nextValue().toString();
                            c = jt.nextClean();
                            if (c != 58) {
                                throw jt.syntaxError("Expected a ':' after a key");
                            }

                            this.remove(key);
                            this.putOnce(key, jt.nextValue());
                            switch (jt.nextClean()) {
                                case ',':
                                case ';':
                                    if (jt.nextClean() == 125) {
                                        return;
                                    }

                                    jt.back();
                                    break;
                                case '}':
                                    save();
                                    return;
                                default:
                                    throw jt.syntaxError("Expected a ',' or '}'");
                            }
                    }
                }
            }
        } catch (IOException e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
        }
    }

    public boolean debug() {
        return getBoolean("debug");
    }

    public boolean isFirstRun() {
        return firstRun;
    }

}

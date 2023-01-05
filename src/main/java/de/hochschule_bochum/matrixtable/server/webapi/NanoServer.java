package de.hochschule_bochum.matrixtable.server.webapi;

import de.hochschule_bochum.matrixtable.engine.Database;
import de.hochschule_bochum.matrixtable.engine.Manager;
import de.hochschule_bochum.matrixtable.engine.game.GameStatus;
import de.hochschule_bochum.matrixtable.ledmatrix.animations.Animation;
import de.hochschule_bochum.matrixtable.ledmatrix.animations.font.ScrollText;
import de.hochschule_bochum.matrixtable.ledmatrix.objects.Display;
import fi.iki.elonen.NanoHTTPD;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.io.*;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by nikla on 19.07.2017.
 */
public class NanoServer extends NanoHTTPD {
    private final GameStatus gamestatus;
    private final Display display;

    public NanoServer(int port, GameStatus gamestatus, Display display) throws IOException {
        super(port);
        this.gamestatus = gamestatus;
        this.display = display;
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
    }

    @Override
    public Response serve(IHTTPSession session) {
        Map<String, String> headers = session.getHeaders();
        String ip = headers.get("remote-addr");
        Logger.getLogger(getClass().getName()).log(Level.INFO, session.getMethod().name() + ": " + session.getUri() + " " + ip);

        if (Method.PUT.equals(session.getMethod()) || Method.POST.equals(session.getMethod())) {
            try {
                session.parseBody(new HashMap<>());
            } catch (IOException | ResponseException e) {
                return getError(Response.Status.INTERNAL_ERROR);
            }
        }

        JSONObject json = new JSONObject("{'error':'no records found'}");
        Response.Status status = Response.Status.BAD_REQUEST;

        final Map<String, String> parms = session.getParms();
        String search = parms.get("player");
        String game = parms.get("game");

        try {
            if (session.getUri().startsWith("/players")) {
                String playerWhere = "%";
                if (search != null)
                    playerWhere = "%" + search + "%";
                ResultSet result = Database.db.executeQuery("SELECT score,game,username FROM score s JOIN devices d ON s.user_id = d.user_id WHERE username LIKE ? GROUP BY d.mac ORDER BY s.score DESC;", playerWhere);
                if (result == null) return getError(Response.Status.INTERNAL_ERROR);
                json = new JSONObject();
                status = Response.Status.OK;

                while (result.next()) {
                    JSONObject player_best_score = new JSONObject();
                    player_best_score.put("game", result.getString("game"));
                    player_best_score.put("score", result.getString("score"));
                    json.put(result.getString("username"), player_best_score);
                }

            } else if (session.getUri().startsWith("/control")) {
                if (session.getUri().startsWith("/control/image")) {
                    if (parms.get("image") != null) {

                    } else {
                        return getError(Response.Status.BAD_REQUEST);
                    }
                } else if (session.getUri().startsWith("/control/set")) {
                    if (parms.get("brightness") != null) {
                        try {
                            double brightness = Double.valueOf(parms.get("brightness"));
                            if (brightness < 0D && brightness > 1D) return getError(Response.Status.INTERNAL_ERROR);
                            display.setGlobal_brightness(brightness);
                            return getError(Response.Status.OK);
                        } catch (NumberFormatException e) {
                            return getError(Response.Status.INTERNAL_ERROR);
                        }
                    }

                    if (!gamestatus.isPlaying()) {
                        if (parms.get("animation") != null) {
                            Animation selectedAnimation = Manager.getAnimations(display).stream().filter(x -> x.getName().equals(parms.get("animation"))).findFirst().orElse(null);
                            if (selectedAnimation == null) return getError(Response.Status.INTERNAL_ERROR);
                            if (gamestatus.getAnimation() != null) gamestatus.getAnimation().stop();
                            gamestatus.setAnimation(selectedAnimation);
                            new Thread(selectedAnimation::start).start();
                            return getError(Response.Status.OK);
                        }

                        if (parms.get("red") != null && parms.get("green") != null && parms.get("blue") != null) {
                            if (gamestatus.getAnimation() != null && !(gamestatus.getAnimation() instanceof ScrollText))
                                gamestatus.getAnimation().stop();
                            String redS = parms.getOrDefault("red", "0");
                            String greenS = parms.getOrDefault("green", "0");
                            String blueS = parms.getOrDefault("blue", "0");

                            try {
                                int red = Integer.valueOf(redS);
                                int green = Integer.valueOf(greenS);
                                int blue = Integer.valueOf(blueS);
                                Color newColor = new Color(red, green, blue);

                                if (gamestatus.getAnimation() != null && gamestatus.getAnimation() instanceof ScrollText) {
                                    ScrollText scrollText = (ScrollText) gamestatus.getAnimation();
                                    scrollText.setColor(newColor);
                                } else display.setAll(newColor);
                            } catch (NumberFormatException e) {
                                return getError(Response.Status.INTERNAL_ERROR);
                            }
                            return getError(Response.Status.OK);
                        }

                        if (parms.get("text") != null) {
                            if (gamestatus.getAnimation() == null) return getError(Response.Status.OK);
                            Animation animation = gamestatus.getAnimation();
                            if (!(animation instanceof ScrollText)) return getError(Response.Status.OK);
                            ScrollText scrollText = (ScrollText) animation;
                            scrollText.setText(parms.get("text"));
                            return getError(Response.Status.OK);
                        }

                        if (parms.get("text_speed") != null) {
                            try {
                                double text_speed = Double.valueOf(parms.get("text_speed"));
                                if (text_speed < 0D && text_speed > 1D) return getError(Response.Status.INTERNAL_ERROR);
                                if (gamestatus.getAnimation() == null) return getError(Response.Status.OK);
                                Animation animation = gamestatus.getAnimation();
                                if (!(animation instanceof ScrollText)) return getError(Response.Status.OK);
                                ScrollText scrollText = (ScrollText) animation;
                                scrollText.setSpeed(text_speed);
                                return getError(Response.Status.OK);
                            } catch (NumberFormatException e) {
                                return getError(Response.Status.INTERNAL_ERROR);
                            }
                        }
                    }
                } else {
                    json = new JSONObject();
                    status = Response.Status.OK;

                    json.put("brightness", display.getGlobalBrightness());
                    JSONArray animations = new JSONArray();
                    Manager.getAnimations(display).stream()
                            .map(Animation::getName)
                            .forEach(animations::put);
                    json.put("animations", animations);

                    JSONObject color = new JSONObject();
                    Color c = display.get(1, 1);
                    if (c == null) c = Color.BLACK;
                    color.put("red", c.getRed());
                    color.put("green", c.getGreen());
                    color.put("blue", c.getBlue());
                    json.put("color", color);
                }
            } else if (session.getUri().startsWith("/static")) {
                String[] allowedIndex = new String[]{"index.html"};
                String path = session.getUri().substring(1);
                if (path.endsWith("/") || !path.contains("."))
                    path = path + (path.endsWith("/") ? "" : File.separator) + allowedIndex[0];
                InputStream is = getResource(path);
                if (is == null) return getError(Response.Status.NOT_FOUND);
                try {
                    return newFixedLengthResponse(Response.Status.OK, URLConnection.guessContentTypeFromStream(is), readStream(is));
                } catch (IOException e) {
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
                    return getError(Response.Status.INTERNAL_ERROR);
                }
            } else if (session.getUri().startsWith("/dev")) {
                String[] allowedIndex = new String[]{"index.html"};
                String path = session.getUri().substring(1);
                if (path.endsWith("/") || !path.contains("."))
                    path = path + (path.endsWith("/") ? "" : File.separator) + allowedIndex[0];
                final File file = Paths.get(Paths.get("").toAbsolutePath().toString(), path).toFile();
                if (!file.exists() || !file.canRead()) return getError(Response.Status.NOT_FOUND);
                try {
                    InputStream is = Files.newInputStream(file.toPath());
                    return newFixedLengthResponse(Response.Status.OK, URLConnection.guessContentTypeFromStream(is), readStream(is));
                } catch (IOException e) {
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
                    return getError(Response.Status.INTERNAL_ERROR);
                }
            } else if (session.getUri().startsWith("/highscore")) {
                String playerWhere = "%";
                if (search != null)
                    playerWhere = "%" + search + "%";

                String gameWhere = "%";
                if (game != null)
                    gameWhere = "%" + game + "%";
                ResultSet result = Database.db.executeQuery("SELECT score,created,game,username FROM score s JOIN devices d ON s.user_id = d.user_id WHERE username LIKE ? AND game LIKE ? ORDER BY s.score DESC LIMIT 10;", playerWhere, gameWhere);
                if (result == null) return getError(Response.Status.INTERNAL_ERROR);

                json = new JSONObject();
                status = Response.Status.OK;

                while (result.next()) {
                    JSONObject userObj = new JSONObject();
                    userObj.put("user", result.getString("username"));
                    userObj.put("score", result.getInt("score"));
                    userObj.put("created", result.getInt("created"));
                    if (json.has(result.getString("game"))) {
                        json.getJSONArray(result.getString("game")).put(userObj);
                    } else {
                        JSONArray gameobj = new JSONArray();
                        gameobj.put(userObj);
                        json.put(result.getString("game"), gameobj);
                    }
                }
            } else {
                return newFixedLengthResponse(Response.Status.OK, "text/html", "<meta http-equiv='refresh' content='0; url=/static/' />");
            }
        } catch (SQLException e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
            return getError(Response.Status.INTERNAL_ERROR);
        }

        return newFixedLengthResponse(status, "application/json", json.toString());
    }

    public Response getError(Response.Status status, String error) {
        JSONObject json = new JSONObject("{'error':'unknown error'}");
        json.put("error", error);
        return newFixedLengthResponse(status, "application/json", json.toString());
    }

    public Response getError(Response.Status status) {
        switch (status) {
            case INTERNAL_ERROR:
                return getError(status, "internal server eror");
            case BAD_REQUEST:
                return getError(status, "bad request");
            case UNAUTHORIZED:
                return getError(status, "unauthorized");
            case OK:
                return getError(status, "no");
            case NOT_FOUND:
                return getError(status, "file not Found");
        }
        return getError(status, "unknown error");
    }

    public InputStream getResource(String path) {
        InputStream is = NanoServer.class.getClassLoader().getResourceAsStream(path);
        return is;
    }

    static String readStream(InputStream is) {
        BufferedReader in = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        String readLine;
        StringBuilder lines = new StringBuilder();
        try {
            while ((readLine = in.readLine()) != null) {
                lines.append(readLine).append("\n");
            }
        } catch (IOException e) {
            Logger.getLogger(NanoServer.class.getName()).log(Level.SEVERE, null, e);
        }
        return lines.toString();
    }

    static String readFile(File file) {
        StringBuilder result = new StringBuilder("");
        try (Scanner scanner = new Scanner(file)) {

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                result.append(line).append("\n");
            }

            scanner.close();

        } catch (IOException e) {
            Logger.getLogger(NanoServer.class.getName()).log(Level.SEVERE, null, e);
        }
        return result.toString();
    }
}

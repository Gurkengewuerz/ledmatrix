package de.hochschule_bochum.matrixtable.webapi;

import de.hochschule_bochum.matrixtable.engine.Database;
import de.hochschule_bochum.matrixtable.engine.SQLInjectionEscaper;
import fi.iki.elonen.NanoHTTPD;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by nikla on 19.07.2017.
 */
public class NanoServer extends NanoHTTPD {

    public NanoServer(int port) throws IOException {
        super(8081);
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
    }

    @Override
    public Response serve(IHTTPSession session) {
        Map<String, String> headers = session.getHeaders();
        String ip = headers.get("remote-addr");
        Logger.getLogger(getClass().getName()).log(Level.INFO, session.getMethod().name() + ": " + session.getUri() + " " + ip);
        Map<String, String> parms = session.getParms();

        try {
            if (session.getMethod().equals(Method.POST))
                session.parseBody(parms);
        } catch (IOException | ResponseException e) {
            return getError(Response.Status.INTERNAL_ERROR);
        }

        JSONObject json = new JSONObject("{'error':'no records found'}");
        Response.Status status = Response.Status.BAD_REQUEST;

        String search = parms.get("player");
        String game = parms.get("game");

        try {
            if (session.getUri().startsWith("/players")) {
                String whereclause = "";
                if (search != null)
                    whereclause = "WHERE username = '" + SQLInjectionEscaper.escapeString(search, false) + "'";
                ResultSet result = Database.db.executeQuery("SELECT score,game,username FROM score s JOIN devices d ON s.user_id = d.user_id " + whereclause + " GROUP BY d.mac ORDER BY s.score DESC;");
                if (result == null) return getError(Response.Status.INTERNAL_ERROR);
                json = new JSONObject();
                status = Response.Status.OK;

                while (result.next()) {
                    JSONObject player_best_score = new JSONObject();
                    player_best_score.put("game", result.getString("game"));
                    player_best_score.put("score", result.getString("score"));
                    json.put(result.getString("username"), player_best_score);
                }

            } else {
                String whereclause = "";
                if (search != null)
                    whereclause = "WHERE username = '" + SQLInjectionEscaper.escapeString(search, false) + "'";
                if (game != null && whereclause.equals(""))
                    whereclause = "WHERE game = '" + SQLInjectionEscaper.escapeString(game, false) + "'";
                if (game != null && !whereclause.equals(""))
                    whereclause += " AND game = '" + SQLInjectionEscaper.escapeString(game, false) + "'";
                ResultSet result = Database.db.executeQuery("SELECT score,created,game,username FROM score s JOIN devices d ON s.user_id = d.user_id " + whereclause + " ORDER BY s.score DESC;");
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
                return getError(status, "internal server rror");
            case BAD_REQUEST:
                return getError(status, "bad request");
            case UNAUTHORIZED:
                return getError(status, "unauthorized");
        }
        return getError(status, "unknown error");
    }
}

package rip.orbit.nebula.util.namemc;

import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class NameMCVerification {

    public static boolean hasLiked(Player player) {
        boolean voted = false;
        try {
            URL url = new URL("https://api.namemc.com/server/orbit.rip/likes?profile=" + player.getUniqueId().toString());
            URLConnection urlConnection = url.openConnection();
            InputStream inputStream = urlConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                voted = Boolean.parseBoolean(line);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return voted;
    }
}

package malphine.Main;

import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.security.auth.login.LoginException;
import java.io.IOException;

public class Main {
    public static JDA jda;
    public static String prefix = "~";

    public static void main(String[] args) throws LoginException {
        jda = JDABuilder.createDefault("OTAxOTEzODA2ODgyNjA3MTE0.YXWysA.5VN-6ZT1mRRrKs7sLSam0sQ5Gvk").build();
        jda.addEventListener(new commands());
    }
}

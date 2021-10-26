package malphine.Main;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.awt.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import static java.rmi.server.LogStream.log;

public class commands extends ListenerAdapter {


    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {

        String[] args = event.getMessage().getContentRaw().split(" ");

        if (args[0].equalsIgnoreCase(Main.prefix + "search") && args.length > 1) {
            String input = "";
            for (int i =0; i < args.length; i ++) {
                if (i != 0){
                    input += args[i];
                }
            }

            String[] words;


            words = input.split("\\s+");
            String word = "";
            if (words.length != 0) {
                for (String s : words) {
                    word += s + "%20";
                }
            }
            var embed = new EmbedBuilder();
            String anime = "";
            final Document document;
            try {
                document = Jsoup.connect("https://myanimelist.net/anime.php?q=" + word +"&cat=anime").userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/95.0.4638.54 Safari/537.36").get();

                for (Element row : document.select("table tr")) {
                    if (row.select("td.bgColor0.borderClass:nth-of-type(2)").text().equalsIgnoreCase("") ) {

                        continue;
                    } else  {
                        anime = row.select("a[href]").attr("href");
                        break;

                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }




            final Document page;

            String episode = "NA";
            String Genres = "NA";
            try {
                page = Jsoup.connect(anime).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/95.0.4638.54 Safari/537.36").get();
                for (int i =8; i <22; i++) {
                    String placeHolder ="td.borderClass div.spaceit_pad:nth-of-type(" + i + ")";

                    if ((page.select(placeHolder).text().charAt(0) == 'E')) {
                        episode = page.select(placeHolder).text();

                    }
                    if ((page.select(placeHolder).text().charAt(0) == 'G')) {
                        Genres= page.select(placeHolder).text();

                    }

                }

                Genres = clear(Genres);
                episode = clear(episode);

                String[] orgGenre = clearGenre(Genres);
                Genres = "";
                for (int i =0 ; i < orgGenre.length; i++) {
                    Genres += orgGenre[i] + " ";
                }



                embed.setDescription(page.select("table td > p").text());
                embed.setTitle(page.select("div.h1.edit-info div.h1-title").text());
                embed.setImage(page.select("tr td.borderClass img.lazyload").attr("abs:data-src"));
                embed.addField("Episodes", episode, true );
                embed.addField("Genres", Genres, true );
                embed.setFooter("SCORE: " +  page.select(getScore(page)).text());
                embed.setColor(Color.BLUE);
                event.getChannel().sendMessage(embed.build()).queue();


            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    public static String clear(String word) {
        StringBuilder sbWord = new StringBuilder(word);

        for (int i =0; i < word.length(); i++) {
            sbWord.deleteCharAt(0);
            if (word.charAt(i) == ' ') {
                break;
            }
        }


        return  sbWord.toString();
    }

    public static String[] clearGenre(String Genres) {
        String[] words =  Genres.split("\\s+");;

        boolean counter = false;

        int deleter = 0;


        for (int i = 0; i < words.length; i++) {
            StringBuilder sbGenre = new StringBuilder(words[i]);
            for (int ii = 0; ii < words[i].length(); ii++) {

                if (words[i].charAt(ii) == Character.toUpperCase(words[i].charAt(ii)) && ii != 0 && words[i].charAt(ii) != ',') {
                    counter = true;
                    deleter = ii;
                }
                if (counter) {
                    if (sbGenre.charAt(deleter) == ',') {
                        words[i] = sbGenre.toString();
                        break;
                    }
                    sbGenre.deleteCharAt(deleter);
                }
                if (ii == words[i].length() -1) {
                    words[i] = sbGenre.toString();
                }

            }
            deleter =0;
            counter = false;


        }


        return words;
    }
    public static String getScore(Element page) {
        String  placeHolder ="td div.score-label.score-8"  ;


        for (int i =6; i < 9; i++) {
        placeHolder ="td div.score-label.score-" + i;

            if (!page.select(placeHolder).text().equalsIgnoreCase("")) {
                break;
            }
        }


        return  placeHolder;
    }
}

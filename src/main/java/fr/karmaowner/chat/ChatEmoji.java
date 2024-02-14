package fr.karmaowner.chat;

import fr.karmaowner.common.CustomRunnable;
import fr.karmaowner.common.Main;
import fr.karmaowner.common.TaskCreator;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class ChatEmoji {
  public static HashMap<String, Character> emojis = new HashMap<>();
  
  public static List<String> disallowEmojis = Arrays.asList(":fu:", ":lips:", ":bikini:", ":tongue:");
  
  public static void loadEmojis() {
    char emojiChar = '가';
    try {
      InputStream listInput = ChatEmoji.class.getResourceAsStream("/list.txt");
      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(listInput));
      String line;
      while ((line = bufferedReader.readLine()) != null) {
        if (line.startsWith("#"))
          continue; 
        if (!disallowEmojis.contains(line))
          emojis.put(line, Character.valueOf(emojiChar)); 
        emojiChar = (char)(emojiChar + 1);
      } 
      bufferedReader.close();
      listInput.close();
    } catch (Exception e) {
      Main.Log("An error occured while loading emojis. More info below.");
      e.printStackTrace();
    } 
  }
  
  public static String toEmoji(String message) {
    for (String key : emojis.keySet())
      message = message.replace(key, ((Character)emojis.get(key)).toString()); 
    return message;
  }
  
  public static void loadEmojiRessources(final Player player) {
    new TaskCreator(new CustomRunnable() {
          public void customRun() {
            ((CraftPlayer)player).getHandle().setResourcePack("https://www.craftyourliferp.fr/EmojiChat.1.SD.ResourcePack.v1.8.zip","");
          }
        },  true, 0L);
  }
}

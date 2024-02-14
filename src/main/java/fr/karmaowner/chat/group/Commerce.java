package fr.karmaowner.chat.group;

import fr.karmaowner.chat.ChatFormatBuilder;
import fr.karmaowner.chat.ChatGroup;

public class Commerce extends ChatGroup {
  public Commerce() {
    super("Commerce", ChatFormatBuilder.build("%color_green %pre - %prp %color_white : %color_gray %msg").setPrefix("Commerce").create());
  }
}

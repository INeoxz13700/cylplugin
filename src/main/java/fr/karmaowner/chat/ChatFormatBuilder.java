package fr.karmaowner.chat;

public class ChatFormatBuilder {
  private ChatFormatting format;
  
  private ChatFormatBuilder(String format) {
    this.format = new ChatFormatting(format);
  }
  
  public static ChatFormatBuilder build(String format) {
    return new ChatFormatBuilder(format);
  }
  
  public ChatFormatBuilder setPrefix(String prefix) {
    this.format.setPrefix(prefix);
    return this;
  }
  
  public ChatFormatBuilder setSuffix(String suffix) {
    this.format.setSuffix(suffix);
    return this;
  }
  
  public ChatFormatBuilder define(String porcent, String replace) {
    this.format.definePorcent(porcent, replace);
    return this;
  }
  
  public ChatFormatting create() {
    return this.format;
  }
}

package com.example.addon.modules;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.example.addon.Addon;
import meteordevelopment.meteorclient.events.game.ReceiveMessageEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.text.Text;
import net.minecraft.text.HoverEvent;

public class ChatReaction
    extends Module {
    private final SettingGroup sgGeneral;
    private final Setting<Boolean> debugEnabled;
    private final Setting<Boolean> hover;
    private final Setting<String> chatRegExMatcher;
    private final Setting<String> hoverMessageRegExMatcher;
    private final Setting<String> hoverTextMessageRegExMatcher;
    private final Setting<Double> delay;
    private final Setting<Double> randomDelay;

    public ChatReaction() {
        super(Addon.CATEGORY, "Chat Reaction", "Auto type a word in chat");
        this.sgGeneral = this.settings.getDefaultGroup();
        this.debugEnabled = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)new BoolSetting.Builder().name("Debug Enabled")).description("Enables debug messages in logs")).defaultValue(false)).build());
        this.hover = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)new BoolSetting.Builder().name("Hover")).description("Whether or not the chat reaction is a hovered text.")).defaultValue(false)).build());
        this.chatRegExMatcher = this.sgGeneral.add(((StringSetting.Builder)((StringSetting.Builder)((StringSetting.Builder)((StringSetting.Builder)new StringSetting.Builder().name("RegEx")).description("What RegEx to match each message with. Capture group 1 will be typed in chat (https://regexr.com/)")).defaultValue("First to type '(.*)' will get")).visible(() -> (Boolean)this.hover.get() == false)).build());
        this.hoverMessageRegExMatcher = this.sgGeneral.add(((StringSetting.Builder)((StringSetting.Builder)((StringSetting.Builder)((StringSetting.Builder)new StringSetting.Builder().name("RegEx")).description("Message has to match this RegEx to trigger, and check for the hover message. (https://regexr.com/)")).defaultValue("(JustBox » Hover for the chat reaction! First to type it will get .*)")).visible(this.hover::get)).build());
        this.hoverTextMessageRegExMatcher = this.sgGeneral.add(((StringSetting.Builder)((StringSetting.Builder)((StringSetting.Builder)((StringSetting.Builder)new StringSetting.Builder().name("Hover Text RegEx")).description("RegEx to match the text in the hover message with. Capture group 1 is the text to send in chat. (https://regexr.com/)")).defaultValue("§e(.*)")).visible(this.hover::get)).build());
        this.delay = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)new DoubleSetting.Builder().name("Delay")).description("The delay between receiving and sending the message in ticks.")).defaultValue(0.3).min(0.0).sliderMax(100.0).build());
        this.randomDelay = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)new DoubleSetting.Builder().name("Randomized Delay")).description("How much random time to add/subtract to the delay in ticks.")).defaultValue(0.2).min(0.0).sliderMax(60.0).build());
    }

    @EventHandler
    private void onReceiveMessage(ReceiveMessageEvent event) {
        if (((Boolean)this.debugEnabled.get()).booleanValue()) {
            System.out.println("Received Message: " + event.getMessage().getString());
        }
        if (!((Boolean)this.hover.get()).booleanValue()) {
            Pattern pattern = Pattern.compile((String)this.chatRegExMatcher.get());
            Matcher matcher = pattern.matcher(event.getMessage().getString());
            if (matcher.find()) {
                if (((Boolean)this.debugEnabled.get()).booleanValue()) {
                    System.out.println("Matched RegEx: " + matcher.group(1));
                }
                long delayTime = (long)(((Double)this.delay.get() + (Double)this.randomDelay.get() * (Math.random() - 0.5)) * 50.0);
                if (((Boolean)this.debugEnabled.get()).booleanValue()) {
                    System.out.println("Delaying for " + delayTime + "ms");
                }
                new Thread(() -> {
                    try {
                        Thread.sleep(delayTime);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    this.mc.player.networkHandler.sendChatMessage(matcher.group(1));
                    if (((Boolean)this.debugEnabled.get()).booleanValue()) {
                        System.out.println("Sent Message: " + matcher.group(1));
                    }
                }).start();
            }
        } else {
            Pattern pattern = Pattern.compile((String)this.hoverMessageRegExMatcher.get());
            Matcher matcher = pattern.matcher(event.getMessage().getString());
            if (matcher.find()) {
                Pattern pattern2;
                Matcher matcher2;
                if (((Boolean)this.debugEnabled.get()).booleanValue()) {
                    System.out.println("Has Hover: " + event.getMessage().getStyle().getHoverEvent() != null);
                }
                if (event.getMessage().getStyle().getHoverEvent() == null) {
                    return;
                }
                if (((Boolean)this.debugEnabled.get()).booleanValue()) {
                    System.out.println("Has Hover Show Text: " + event.getMessage().getStyle().getHoverEvent().getValue(HoverEvent.Action.SHOW_TEXT) != null);
                }
                if (event.getMessage().getStyle().getHoverEvent().getValue(HoverEvent.Action.SHOW_TEXT) == null) {
                    return;
                }
                if (((Boolean)this.debugEnabled.get()).booleanValue()) {
                    System.out.println("Hover Message: " + ((Text)event.getMessage().getStyle().getHoverEvent().getValue(HoverEvent.Action.SHOW_TEXT)).getString());
                }
                if ((matcher2 = (pattern2 = Pattern.compile((String)this.hoverTextMessageRegExMatcher.get())).matcher(((Text)event.getMessage().getStyle().getHoverEvent().getValue(HoverEvent.Action.SHOW_TEXT)).getString())).find()) {
                    if (((Boolean)this.debugEnabled.get()).booleanValue()) {
                        System.out.println("Matched RegEx: " + matcher2.group(1));
                    }
                    long delayTime = (long)(((Double)this.delay.get() + (Double)this.randomDelay.get() * (Math.random() - 0.5)) * 50.0);
                    if (((Boolean)this.debugEnabled.get()).booleanValue()) {
                        System.out.println("Delaying for " + delayTime + "ms");
                    }
                    new Thread(() -> {
                        try {
                            Thread.sleep(delayTime);
                        }
                        catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        this.mc.player.networkHandler.sendChatMessage(matcher2.group(1));
                        if (((Boolean)this.debugEnabled.get()).booleanValue()) {
                            System.out.println("Sent Message: " + matcher2.group(1));
                        }
                    }).start();
                }
            }
        }
    }
}



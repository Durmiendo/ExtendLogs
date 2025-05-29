package org.durmiendo.extlogs;

import arc.Core;
import arc.Events;
import arc.files.Fi;
import arc.scene.ui.TextField;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.Log;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.gen.Icon;
import mindustry.gen.Tex;
import mindustry.graphics.Pal;
import mindustry.mod.Mod;
import mindustry.ui.Styles;


public class Main extends Mod {
    private static StringBuilder buf = new StringBuilder();
    private static String getCaller() {
        buf.setLength(0);
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        short i = 0;
        for (StackTraceElement caller : trace) {
            i++;
            if (i < 5) continue;
            if (Log.class.getName().equals(caller.getClassName())) continue;

            String[] classFullName = caller.getClassName().split("\\.");
            String className = classFullName[classFullName.length - 1];
            buf.append(callerFormat
                    .replace("%fullClassName%", caller.getClassName())
                    .replace("%className%", className)
                    .replace("%methodName%", caller.getMethodName())
                    .replace("%lineNumber%", Integer.toString(caller.getLineNumber()))
            );

            return buf.toString();
        }
        return "";
    }

    public static int var = 0;
    public static Seq<String> def = Seq.with(
            "[%className%.%methodName%:%lineNumber%] ",
            "[%fullClassName%.%methodName%:%lineNumber%] ",
            "[%className%.%methodName%] ",
            "[%className%.%methodName%:%lineNumber%] ",
            "[%methodName%:%lineNumber%] ",
            "[%methodName%] ",
            ""
    );
    public static TextField customFormat;
    public static String callerFormat;

    public static void change(int to) {
        var = to;
        if (var < 0) var = 0;
        else if (var >= def.size) callerFormat = customFormat.getText();
        else callerFormat = def.get(var);
        save();
    }

    public static void save() {
        Core.settings.put("extendedlog-loggerformat", var);
        Core.settings.put("extendedlog-customformat", customFormat.getText());
    }


    public void init() {
        Events.on(EventType.ClientLoadEvent.class, event -> {
            customFormat = new TextField();
            customFormat.setMessageText("Введите формат логгера");
            var = Core.settings.getInt("extendedlog-loggerformat", 0);
            customFormat.setText(Core.settings.getString("extendedlog-customformat", ""));

            if (var < 0) var = 0;
            else if (var >= def.size) callerFormat = customFormat.getText();
            else callerFormat = def.get(var);

            Vars.ui.settings.addCategory("Логгер", Icon.terminal, b -> {
                b.table(t -> {
                    t.table().width(Core.graphics.getWidth()).row();
                    t.add("Выбран:").left().padBottom(4f).row();

                    t.label(() -> callerFormat).left().padBottom(32f).row();

                    t.image(Tex.whiteui, Pal.gray).growX().height(3f).padBottom(6f).row();

                    t.table(l -> {
                        l.add(customFormat).height(48f).growX();
                        l.button(Icon.okSmall, Styles.flati, 48f, () -> {
                            change(def.size);
                        }).size(48f);
                    }).growX().row();
                    for (int i = 0; i < def.size; i++) {
                        int finalI = i;
                        t.button(def.get(i), Styles.flatt, () -> {
                            change(finalI);
                        }).growX().height(48f).left().row();
                    }
                }).growX().expandY().row();

                b.pane(p -> {
                    p.table().width(Core.graphics.getWidth()).row();
                    p.image(Tex.whiteui, Pal.gray).growX().height(3f).padBottom(6f).row();

                    p.button("Лог текущей сессии", Styles.flatt, () -> {
                        Core.app.setClipboardText(Vars.dataDirectory.child("last_log.txt").readString());
                    }).height(64f).pad(6f).growX().left().tooltip("Скопировать в буфер обмена").row();

                    p.image(Tex.whiteui, Pal.gray).growX().height(3f).padBottom(6f).row();

                    p.add("Краши").center().padBottom(4f).row();
                    p.image(Tex.whiteui, Pal.gray).growX().height(3f).padBottom(6f).row();

                    Seq<Fi> crashes = Vars.dataDirectory.child("crashes").seq();
                    for (Fi file : crashes) {
                        final Table[] ta = new Table[1];
                        ta[0] = p.table(k -> {
                            k.button(Icon.trashSmall, Styles.flati, 48f, () -> {
                                file.delete();
                                p.removeChild(ta[0]);
                            }).left().tooltip("Удалиьт крашлог");
                            k.button(file.nameWithoutExtension(), Styles.flatt,
                                    () -> Core.app.setClipboardText(file.readString())
                            ).height(48f).growX().left().tooltip("Скопировать в буфер обмена");
                        }).left().growX().get();
                        p.row();
                    }
                }).growX().expandY().row();
            });

            Log.LogHandler handler = Log.logger;
            Log.logger = (level, text) -> {
                handler.log(level, getCaller() + text);
            };
        });
    }
}

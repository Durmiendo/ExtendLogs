package com.github.template.core;

import arc.util.Log;
import mindustry.mod.Mod;

@SuppressWarnings("unused")
public class ModCore extends Mod {
    @Override
    public void loadContent() {
        var hello = "Hello!";
        Log.info(hello);
    }
}

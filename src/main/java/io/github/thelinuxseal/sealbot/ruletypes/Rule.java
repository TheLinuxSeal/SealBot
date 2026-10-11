package io.github.thelinuxseal.sealbot.ruletypes;

import io.github.thelinuxseal.sealbot.Permission;

import java.util.List;

public interface Rule {

    public List<String> check(String msg);

    Permission getPerm();
}

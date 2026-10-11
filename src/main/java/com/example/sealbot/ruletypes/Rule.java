package com.example.sealbot.ruletypes;

import com.example.sealbot.Permission;

import java.util.List;

public interface Rule {

    public List<String> check(String msg);

    Permission getPerm();
}

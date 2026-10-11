# SealBot (Fabric 1.21.1, client-side)

Build:   gradle build        (or ./gradlew build if you copy the wrapper from the Fabric example mod)
Output:  build/libs/sealbot-1.0.0.jar  -> put in .minecraft/mods (needs Fabric API)

Triggers:
  seal calc 1+1   -> 2
  seal list       -> "seal list" then "seal calc" (two messages; chat can't contain newlines)

Add rules in the RULES list in SealBot.java.

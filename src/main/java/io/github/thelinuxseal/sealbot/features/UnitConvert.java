package io.github.thelinuxseal.sealbot.features;

import io.github.thelinuxseal.sealbot.perms.Permission;
import io.github.thelinuxseal.sealbot.rule.RuleData;
import io.github.thelinuxseal.sealbot.rule.TriMap;
import io.github.thelinuxseal.sealbot.ruletypes.FunctionRule;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.github.thelinuxseal.sealbot.SealBot.*;
import static io.github.thelinuxseal.sealbot.SealBot.WHISPER;
import static io.github.thelinuxseal.sealbot.perms.Permission.NORMAL;

public class UnitConvert {
    public enum Dim { LENGTH, MASS, VOLUME, TIME, AREA, SPEED, DATA, ITEMS, NONE }

    public enum Unit {
        // length (base: meter)
        NM(Dim.LENGTH, 1e-9), UM(Dim.LENGTH, 1e-6), MM(Dim.LENGTH, 0.001), CM(Dim.LENGTH, 0.01),
        M(Dim.LENGTH, 1), KM(Dim.LENGTH, 1000),
        IN(Dim.LENGTH, 0.0254), FT(Dim.LENGTH, 0.3048), YD(Dim.LENGTH, 0.9144), MI(Dim.LENGTH, 1609.344),
        NMI(Dim.LENGTH, 1852),
        BLOCK(Dim.LENGTH, 1), CHUNK(Dim.LENGTH, 16),

        // mass (base: gram)
        NG(Dim.MASS, 1e-9), UG(Dim.MASS, 1e-6), MG(Dim.MASS, 0.001), G(Dim.MASS, 1), KG(Dim.MASS, 1000),
        T(Dim.MASS, 1_000_000),
        OZ(Dim.MASS, 28.349523125), LB(Dim.MASS, 453.59237), ST(Dim.MASS, 6350.29318),

        // volume (base: liter, US customary)
        NL(Dim.VOLUME, 1e-9), ML(Dim.VOLUME, 0.001), L(Dim.VOLUME, 1), KL(Dim.VOLUME, 1000),
        CM3(Dim.VOLUME, 0.001), M3(Dim.VOLUME, 1000),
        TSP(Dim.VOLUME, 0.00492892159375), TBSP(Dim.VOLUME, 0.01478676478125), FLOZ(Dim.VOLUME, 0.0295735295625),
        CUP(Dim.VOLUME, 0.2365882365), PINT(Dim.VOLUME, 0.473176473),
        QRT(Dim.VOLUME, 0.946352946), GAL(Dim.VOLUME, 3.785411784),

        // time (base: second)
        TICK(Dim.TIME, 0.05), S(Dim.TIME, 1), MIN(Dim.TIME, 60), H(Dim.TIME, 3600), D(Dim.TIME, 86400),
        MCDAY(Dim.TIME, 1200),   // one Minecraft day = 24000 ticks = 20 real minutes

        // area (base: square meter)
        CM2(Dim.AREA, 1e-4), M2(Dim.AREA, 1), KM2(Dim.AREA, 1e6), HA(Dim.AREA, 10000),
        IN2(Dim.AREA, 0.00064516), FT2(Dim.AREA, 0.09290304), YD2(Dim.AREA, 0.83612736),
        ACRE(Dim.AREA, 4046.8564224), MI2(Dim.AREA, 2589988.110336),

        // speed (base: meters per second)
        MPS(Dim.SPEED, 1), KMH(Dim.SPEED, 1 / 3.6), MPH(Dim.SPEED, 0.44704), KNOT(Dim.SPEED, 1852 / 3600.0),

        // data (base: byte). KB/MB/GB are decimal (1000), KIB/MIB/GIB are binary (1024).
        B(Dim.DATA, 1), KB(Dim.DATA, 1e3), MB(Dim.DATA, 1e6), GB(Dim.DATA, 1e9), TB(Dim.DATA, 1e12),
        KIB(Dim.DATA, 1024), MIB(Dim.DATA, 1048576), GIB(Dim.DATA, 1073741824), TIB(Dim.DATA, 1099511627776.0),

        // Minecraft items (base: one item)
        ITEM(Dim.ITEMS, 1), STACK(Dim.ITEMS, 64), SHULKER(Dim.ITEMS, 1728),

        NULL(Dim.NONE, 0);

        final Dim dim;
        final double toBase;
        Unit(Dim dim, double toBase) { this.dim = dim; this.toBase = toBase; }
    }

    private static final Map<String, Unit> ALIASES = new HashMap<>();

    static {
        for (Unit u : Unit.values()) if (u != Unit.NULL) ALIASES.put(u.name().toLowerCase(Locale.ROOT), u);

        // length
        alias(Unit.NM, "nanometer", "nanometers", "nanometre", "nanometres");
        alias(Unit.MM, "millimeter", "millimeters", "millimetre", "millimetres");
        alias(Unit.CM, "centimeter", "centimeters", "centimetre", "centimetres");
        alias(Unit.M,  "meter", "meters", "metre", "metres");
        alias(Unit.KM, "kilometer", "kilometers", "kilometre", "kilometres", "kms");
        alias(Unit.IN, "inch", "inches");
        alias(Unit.FT, "foot", "feet");
        alias(Unit.YD, "yard", "yards", "yds");
        alias(Unit.MI, "mile", "miles");

        // mass
        alias(Unit.NG, "nanogram", "nanograms");
        alias(Unit.MG, "milligram", "milligrams");
        alias(Unit.G,  "gram", "grams", "gramme", "grammes");
        alias(Unit.KG, "kilogram", "kilograms", "kilo", "kilos", "kgs");
        alias(Unit.LB, "lbs", "pound", "pounds");

        // volume
        alias(Unit.NL, "nanoliter", "nanoliters", "nanolitre", "nanolitres");
        alias(Unit.ML, "milliliter", "milliliters", "millilitre", "millilitres");
        alias(Unit.L,  "liter", "liters", "litre", "litres");
        alias(Unit.KL, "kiloliter", "kiloliters", "kilolitre", "kilolitres");
        alias(Unit.CUP, "cups");
        alias(Unit.QRT, "qt", "qts", "quart", "quarts");
        alias(Unit.PINT, "pt", "pts", "pints");
        alias(Unit.GAL, "gallon", "gallons", "gals");

        alias(Unit.UM, "micrometer", "micron", "microns");
        alias(Unit.UG, "microgram");
        alias(Unit.T, "tonne");
        alias(Unit.OZ, "ounce");
        alias(Unit.ST, "stone");
        alias(Unit.TSP, "teaspoon");
        alias(Unit.TBSP, "tablespoon");
        alias(Unit.CM3, "cc", "cm³");
        alias(Unit.M3, "m³");
        alias(Unit.S, "sec", "secs", "second");
        alias(Unit.MIN, "mins", "minute");
        alias(Unit.H, "hr", "hrs", "hour");
        alias(Unit.D, "day");
        alias(Unit.M2, "m²", "m2", "sqm");
        alias(Unit.KM2, "km²", "km2", "sqkm");
        alias(Unit.FT2, "ft²", "ft2", "sqft");
        alias(Unit.HA, "hectare");
        alias(Unit.MPS, "m/s");
        alias(Unit.KMH, "km/h", "kph", "kmph");
        alias(Unit.MPH, "mi/h");
        alias(Unit.KNOT, "kn");
        alias(Unit.B, "byte");
        alias(Unit.KB, "kilobyte");
        alias(Unit.MB, "megabyte");
        alias(Unit.GB, "gigabyte");
    }

    private static void alias(Unit u, String... names) {
        for (String n : names) ALIASES.put(n, u);
    }

    /** Looks up a unit by name; returns null if unknown. Falls back to stripping a plural "s". */
    public static Unit t2u(String t) {
        String k = t.toLowerCase(Locale.ROOT);
        Unit u = ALIASES.get(k);
        if (u == null && k.length() >= 4 && k.endsWith("s")) u = ALIASES.get(k.substring(0, k.length() - 1));
        return u;
    }

    // ---- temperature: needs offsets, so it lives outside the multiplier table -----------

    private enum Temp { C, F, K }

    private static final Map<String, Temp> TEMPS = Map.of(
        "c", Temp.C, "celsius", Temp.C, "°c", Temp.C, "degc", Temp.C,
        "f", Temp.F, "fahrenheit", Temp.F, "°f", Temp.F, "degf", Temp.F,
        "k", Temp.K, "kelvin", Temp.K
    );

    private static double toKelvin(Temp t, double v) {
        return switch (t) {
            case C -> v + 273.15;
            case F -> (v - 32) * 5 / 9 + 273.15;
            case K -> v;
        };
    }

    private static double fromKelvin(Temp t, double k) {
        return switch (t) {
            case C -> k - 273.15;
            case F -> (k - 273.15) * 9 / 5 + 32;
            case K -> k;
        };
    }

    private static String label(Temp t) {
        return switch (t) {
            case C -> "°C";
            case F -> "°F";
            case K -> "K";
        };
    }

    // ---- the rule ------------------------------------------------------------------------

    // "seal convert 5 km to mi", "seal convert -40 c to f", "seal convert 4 gib to gb"
    // Number: up to 12 digits plus optional decimals. Units: start with a letter or °, up to 12 chars.
    public static final FunctionRule CONVERT = new FunctionRule(
        new RuleData(
            TriMap.of(
                PUBLIC_CHAT, NORMAL, PUBLIC_CHAT.getCooldown(),
                DISCORD, NORMAL, DISCORD.getCooldown(),
                WHISPER, NORMAL, WHISPER.getCooldown()
            )
        ),
        Pattern.compile(
            "\\bseal\\s+convert\\s+(-?\\d{1,12}(?:\\.\\d{1,9})?)\\s*([a-z°][\\w°²³/]{0,11})\\s+(?:to|->)\\s+([a-z°][\\w°²³/]{0,11})\\s*$",
            Pattern.CASE_INSENSITIVE),
        (Matcher m) -> List.of(convert(m.group(1), m.group(2), m.group(3)))
    );

    static String convert(String num, String fromStr, String toStr) {
        double value = Double.parseDouble(num);   // safe: the regex only lets digits through
        String f = fromStr.toLowerCase(Locale.ROOT);
        String t = toStr.toLowerCase(Locale.ROOT);

        // temperature
        Temp ft = TEMPS.get(f);
        Temp tt = TEMPS.get(t);
        if (ft != null || tt != null) {
            if (ft == null || tt == null) return "Can't convert temperature to/from a non-temperature unit";
            double kelvin = toKelvin(ft, value);
            if (kelvin < 0) return "That's below absolute zero";
            return fmt(value) + " " + label(ft) + " = " + fmt(fromKelvin(tt, kelvin)) + " " + label(tt);
        }

        // everything else
        Unit from = t2u(f);
        Unit to = t2u(t);
        if (from == null) return "Unknown unit '" + fromStr + "'";
        if (to == null) return "Unknown unit '" + toStr + "'";
        if (from.dim != to.dim) {
            return "Can't convert " + from.dim.name().toLowerCase(Locale.ROOT)
                + " to " + to.dim.name().toLowerCase(Locale.ROOT);
        }

        double result = value * from.toBase / to.toBase;
        return fmt(value) + " " + from.name().toLowerCase(Locale.ROOT)
            + " = " + fmt(result) + " " + to.name().toLowerCase(Locale.ROOT);
    }

    // ---- number formatting: 10 significant digits, scientific notation at the extremes --------

    private static final MathContext SHOW = new MathContext(10, RoundingMode.HALF_UP);

    private static String fmt(double v) {
        if (v == 0) return "0";
        BigDecimal bd = new BigDecimal(v).round(SHOW).stripTrailingZeros();
        double abs = Math.abs(v);
        if (abs >= 1e15 || abs < 1e-4) {
            int exp = bd.precision() - bd.scale() - 1;
            return bd.movePointLeft(exp).stripTrailingZeros().toPlainString() + "e" + exp;
        }
        return bd.toPlainString();
    }
}
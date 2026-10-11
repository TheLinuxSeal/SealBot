package io.github.thelinuxseal.sealbot.featureclasses;

import io.github.thelinuxseal.sealbot.Feature;
import com.ezylang.evalex.Expression;
import com.ezylang.evalex.config.ExpressionConfiguration;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;

/** Arbitrary-range math via EvalEx (BigDecimal): handles things like 10^400 or 1e-500. */
@Deprecated
public final class DeprecatedCalc implements Feature {

    private static final MathContext CALC_MC = new MathContext(40, RoundingMode.HALF_UP); // internal precision
    private static final MathContext SHOW_MC = new MathContext(15, RoundingMode.HALF_UP); // digits shown in chat
    private static final long TIMEOUT_MS = 1000;

    private static final ExpressionConfiguration CONFIG =
            ExpressionConfiguration.builder().mathContext(CALC_MC).build();

    // Daemon threads so a runaway calculation can never keep the game from exiting.
    private static final ExecutorService EXEC = Executors.newCachedThreadPool(r -> {
        Thread t = new Thread(r, "sealbot-calc");
        t.setDaemon(true);
        return t;
    });

    @Override
    public List<String> trigger(Matcher m) {
        return List.of(safeEval(m.group(1)));
    }

    static String safeEval(String raw) {
        final String expr = raw.trim();
        if (expr.length() > 100) return "expression too long";

        Future<BigDecimal> future = EXEC.submit(() -> new Expression(expr, CONFIG).evaluate().getNumberValue());
        try {
            return format(future.get(TIMEOUT_MS, TimeUnit.MILLISECONDS));
        } catch (TimeoutException e) {
            future.cancel(true);
            return "took too long";
        } catch (ExecutionException e) {
            Throwable c = e.getCause();
            String msg = c != null && c.getMessage() != null ? c.getMessage().toLowerCase() : "";
            if (c instanceof ArithmeticException || msg.contains("zero")) return "math error";
            return "invalid expression";
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "interrupted";
        } catch (RuntimeException e) {
            return "invalid expression";
        }
    }

    private static String format(BigDecimal v) {
        if (v == null) return "invalid expression";
        if (v.signum() == 0) return "0";

        BigDecimal abs = v.abs();
        BigDecimal stripped = v.stripTrailingZeros();

        // exact integers below 1e15: print every digit
        if (stripped.scale() <= 0 && abs.compareTo(new BigDecimal("1e15")) < 0) {
            return stripped.toBigInteger().toString();
        }

        BigDecimal bd = v.round(SHOW_MC).stripTrailingZeros();

        // very large or very small: scientific notation
        if (abs.compareTo(new BigDecimal("1e15")) >= 0 || abs.compareTo(new BigDecimal("1e-4")) < 0) {
            int exp = bd.precision() - bd.scale() - 1;
            String mantissa = bd.movePointLeft(exp).stripTrailingZeros().toPlainString();
            return mantissa + "e" + exp;
        }
        return bd.toPlainString();
    }
}
package eu;

/**
 * @author Viktor Korovyanko <viktor.koro@gmail.com>
 */
public class ScriptRunner implements Runnable {
    private final String symbol;
    private final String perlPath;
    private final String scriptPath;

    public ScriptRunner(String symbol, String perlPath, String scriptPath) {
        this.symbol = symbol;
        this.perlPath = perlPath;
        this.scriptPath = scriptPath;
    }

    @Override
    public void run() {
        try {
            Process p = Runtime.getRuntime().exec(perlPath + " " + scriptPath + " " + symbol);
//            Process p = Runtime.getRuntime().exec("calc.exe");
            p.waitFor();
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}


package eu;

import java.util.List;

/**
 * @author Viktor Korovyanko <viktor.koro@gmail.com>
 */
public class CalcRunner implements Runnable {
    private final List<String> list;
    private final String programPath;

    public CalcRunner(List<String> list, String programPath) {
        this.list = list;
        this.programPath = programPath;
    }

    public static void main(final String[] args) {
        java.util.List<String> list = new java.util.ArrayList<String>();
        new Thread(new CalcRunner(list, args[0])).start();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(list.size() == 1){
            System.out.println(list.get(0));
        }

        if(list.size() > 1){
            System.out.println(list.get(1));
        }
    }


    @Override
    public void run() {
        try {
            list.add("hallo there");
            Process p = Runtime.getRuntime().exec(programPath);
//            p.waitFor();
        } catch (Exception ex) {
            list.add(ex.getMessage());
        }
    }
}


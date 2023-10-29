package Saborear.com.br.Utils;

public class Timer {

    private long startTime;

    public Timer() {
        startTime = System.currentTimeMillis();
    }
    public String stop() { return "["+(System.currentTimeMillis()-startTime)+"ms]"; }
    public Long getMiles() { return System.currentTimeMillis()-startTime; }
}

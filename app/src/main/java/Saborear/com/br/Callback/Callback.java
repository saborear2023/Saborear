package Saborear.com.br.Callback;

import java.util.ArrayList;
import java.util.Map;

public interface Callback {
    void onValuesReceived(ArrayList<Map<String, String>> data);
}

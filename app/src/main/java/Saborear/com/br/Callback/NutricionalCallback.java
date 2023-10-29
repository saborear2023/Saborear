package Saborear.com.br.Callback;

import java.util.ArrayList;

import Saborear.com.br.Classes.classeNutricional;

public interface NutricionalCallback {
    void onValuesReceived(ArrayList<classeNutricional> data);
}

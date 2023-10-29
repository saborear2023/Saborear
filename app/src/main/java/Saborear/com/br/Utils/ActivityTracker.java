package Saborear.com.br.Utils;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

import Saborear.com.br.Main;
import Saborear.com.br.Manage.ManageDatabase;

public class ActivityTracker {
    private static final List<Activity> activities = new ArrayList<>();

    public static void addActivity(Activity activity) { activities.add(activity); }

    public static void delActivity(Activity activity) {
        ManageDatabase.clear();
        activities.remove(activity);
    }
    public static Activity getMain() {
        for (Activity activity : activities)
            if (activity instanceof Main)
                return activity;
        return null;
    }

    public static void finishExceptMain(Activity currentActivity) {
        for (Activity activity : activities)
            if (activity != currentActivity && !(activity instanceof Main))
                activity.finish();
        activities.clear();
    }

    public static void finish(Activity currentActivity) {
        for (Activity activity : activities)
            if (activity != currentActivity)
                activity.finish();
        activities.clear();
    }

    public static void finishActivity(Activity currentActivity) {
        for (Activity activity : activities)
            if (activity == currentActivity)
                activity.finish();
    }
}
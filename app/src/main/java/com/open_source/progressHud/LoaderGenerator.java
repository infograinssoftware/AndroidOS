package com.open_source.progressHud;

/**
 * Created by Tuyen Nguyen on 2/13/17.
 */

public class LoaderGenerator {

    public static LoaderView generateLoaderView(int type) {
        return new TwinFishesSpinner();
    }

    public static LoaderView generateLoaderView(String type) {
        return new TwinFishesSpinner();
    }
}


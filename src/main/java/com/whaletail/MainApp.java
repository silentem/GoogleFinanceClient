package com.whaletail;
/*
Copyright 2014 Zoi Capital, LLC

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */


import com.whaletail.chart.BarData;
import com.whaletail.chart.CandleStickChart;
import com.whaletail.chart.DecimalAxisFormatter;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;


public class MainApp extends Application {

    CandleStickChart candleStickChart;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {

//        List<BarData> list = new ArrayList<>();
//        GregorianCalendar now = new GregorianCalendar();
//        list.add(new BarData((GregorianCalendar) now.clone(), 177.29, 177.49, 176.1, 176.23));
//        now.add(Calendar.MINUTE, 5);
//        list.add(new BarData((GregorianCalendar) now.clone(), 177.68, 178.28, 177.04, 177.31));
//        now.add(Calendar.MINUTE, 5);
//        list.add(new BarData((GregorianCalendar) now.clone(), 176.985, 177.74, 176.8, 177.68));
//        now.add(Calendar.MINUTE, 5);
//        list.add(new BarData((GregorianCalendar) now.clone(), 176.93, 177.303, 176.7, 176.99));
//        now.add(Calendar.MINUTE, 5);
//        list.add(new BarData((GregorianCalendar) now.clone(), 176.52, 176.99, 176.0301, 176.91));
//        now.add(Calendar.MINUTE, 5);
//        list.add(new BarData((GregorianCalendar) now.clone(), 176.96, 177.12, 176.2601, 176.4601));
//        now.add(Calendar.MINUTE, 5);
//        list.add(new BarData((GregorianCalendar) now.clone(), 177.42, 180.78, 176.61, 178.59));
//        now.add(Calendar.MINUTE, 5);
//        list.add(new BarData((GregorianCalendar) now.clone(), 175.415, 177.635, 175.16, 177.39));
//        now.add(Calendar.MINUTE, 5);
//        list.add(new BarData((GregorianCalendar) now.clone(), 174.8795, 175.7196, 174.73, 175.38));
//        now.add(Calendar.MINUTE, 5);
//
//
//        candleStickChart = new CandleStickChart("S&P 500 Index", list);

        Parent chartRoot = FXMLLoader.load(getClass().getResource("/main_chart.fxml"));


        Scene scene = new Scene(chartRoot, 1280, 840);
        scene.getStylesheets().add("/style.css");

        stage.setTitle("JavaFX and Maven");
        stage.setScene(scene);
        stage.show();
//        candleStickChart.setYAxisFormatter(new DecimalAxisFormatter("#000.00"));
//        Platform.runLater(new DaemonThread());
//        DaemonThread thread = new DaemonThread();
//        thread.start();

    }

    private class DaemonThread extends Thread {
        public DaemonThread() {
            this.setDaemon(true);
        }

        @Override
        public void run() {
            try {
                for (int i = 0; i < 5; i++) {
                    Thread.sleep(5000);
                    XYChart.Data<String, Number> data = new XYChart.Data<>(
                            "Hello" + i, 176.5, new BarData(new GregorianCalendar(), 174.8795, 175.7196, 174.73, 175.38));
                    candleStickChart.dataSeries.get(0).getData().add(data);
                    candleStickChart.dataSeries.get(0).getData().remove(0);
                    System.out.println("data was added");
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}

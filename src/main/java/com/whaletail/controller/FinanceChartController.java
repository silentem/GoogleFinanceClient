package com.whaletail.controller;

import com.whaletail.chart.BarData;
import com.whaletail.chart.CandleStickChart;
import com.whaletail.chart.DecimalAxisFormatter;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.*;

/**
 * Created by Whaletail on 14.02.2017.
 */
public class FinanceChartController implements Initializable{


    @FXML
    public VBox vBox;
    CandleStickChart candleStickChart;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        List<BarData> list = new ArrayList<>();
        GregorianCalendar now = new GregorianCalendar();
        list.add(new BarData((GregorianCalendar) now.clone(), 177.29, 177.49, 176.1, 176.23));
        now.add(Calendar.MINUTE, 5);
        list.add(new BarData((GregorianCalendar) now.clone(), 177.68, 178.28, 177.04, 177.31));
        now.add(Calendar.MINUTE, 5);
        list.add(new BarData((GregorianCalendar) now.clone(), 176.985, 177.74, 176.8, 177.68));
        now.add(Calendar.MINUTE, 5);
        list.add(new BarData((GregorianCalendar) now.clone(), 176.93, 177.303, 176.7, 176.99));
        now.add(Calendar.MINUTE, 5);
        list.add(new BarData((GregorianCalendar) now.clone(), 176.52, 176.99, 176.0301, 176.91));
        now.add(Calendar.MINUTE, 5);
        list.add(new BarData((GregorianCalendar) now.clone(), 176.96, 177.12, 176.2601, 176.4601));
        now.add(Calendar.MINUTE, 5);
        list.add(new BarData((GregorianCalendar) now.clone(), 177.42, 180.78, 176.61, 178.59));
        now.add(Calendar.MINUTE, 5);
        list.add(new BarData((GregorianCalendar) now.clone(), 175.415, 177.635, 175.16, 177.39));
        now.add(Calendar.MINUTE, 5);
        list.add(new BarData((GregorianCalendar) now.clone(), 174.8795, 175.7196, 174.73, 175.38));
        now.add(Calendar.MINUTE, 5);

        candleStickChart = new CandleStickChart("S&P 500 Index", list);

        candleStickChart.setYAxisFormatter(new DecimalAxisFormatter("#000.00"));

        vBox.getChildren().add(candleStickChart);
    }
}

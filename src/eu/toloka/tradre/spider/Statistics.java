package eu.toloka.tradre.spider;

import eu.toloka.tradre.analyzer.Bar;
import eu.toloka.tradre.persistence.entity.BarEntity;

//import java.util.ArrayList;
import java.util.List;

/**
 * @author Viktor Korovyanko <viktor.koro@gmail.com>
 */

public class Statistics {
    public static Float calculateSimpleMovingAverage(List<BarEntity> list, int period) {
        if (list.size() < period) {
            return null;
        }

        period = Math.min(period, list.size());

        float sum = 0;

        for (int i = 0; i < period; i++) {
            BarEntity entity = list.get(list.size() - i - 1);
            sum += entity.close;
        }

        Float f = sum / period;
        return f.isNaN() ? null: f;
    }

//    private static Float calculateExponentialMovingAverage(List<Float> list, Integer period) {
//        if (list.size() < period) {
//            return null;
//        }
//
//        float alpha = 2f / (period + 1);
//        float sum = 0;
//
//        for (int i = 0; i < period; i++) {
//            sum += Math.pow(1 - alpha, i) * list.get(i);
//        }
//
//        return alpha * sum;
//    }

//    public static Float calculateRelativeStrength(List<BarEntity> list, int period) {
//        if (list.size() < period) {
//            return null;
//        }
//
//        List<Float> ups = new ArrayList<Float>(period);
//        List<Float> downs = new ArrayList<Float>(period);
//
//        Float tomorrow = null;
//
//        for (int i = period - 1; i >= 0; i--) {
//            BarEntity entity = list.get(i);
//            Float today = entity.close;
//
//            if (tomorrow == null) {
//                tomorrow = today;
//                continue;
//            }
//
//            Float diff = tomorrow - today;
//            tomorrow = today;
//
//            if (diff > 0) {
//                ups.add(diff);
//                downs.add(0f);
//            } else {
//                ups.add(0f);
//                downs.add(-diff);
//            }
//        }
//
//        Float upEMA = calculateExponentialMovingAverage(ups, Math.min(period, ups.size()));
//        Float downEMA = calculateExponentialMovingAverage(downs, Math.min(period, downs.size()));
//
//        Float f = 100 - 100 / (1 + upEMA / downEMA);
//
//        return f.isNaN() ? null: f;
//    }

    public static Float deviation(List<BarEntity> list, Float moving, int period) {
        if (list.size() < period) {
            return null;
        }

        float sum = 0;

        for (int i = 0; i < period; i++) {
            BarEntity entity = list.get(list.size() - i - 1);
            sum += Math.pow(entity.close - moving, 2);
        }

        Float f = (float) Math.sqrt(sum / (float) period);
        return f.isNaN() ? null: f;
    }

    public static Float calculateCorrelation(List<Bar> djiList, List<Bar> list, int n) {
        if (list.size() < n || djiList.size() < n) {
            return null;
        }

        float xSum = 0, ySum = 0, xySum = 0, xxSum = 0, yySum = 0;

        for (int i = 0; i < n; i++) {
            Bar djiBar = djiList.get(djiList.size() - i - 1);
            Bar bar = list.get(list.size() - i - 1);

            xSum += djiBar.close();
            ySum += bar.close();
            xySum += djiBar.close() * bar.close();
            xxSum += djiBar.close() * djiBar.close();
            yySum += bar.close() * bar.close();
        }

        // Correlation(r) = (n?(XY) - (?X)(?Y)) / Sqrt((n?(X*X) - (?X)*(?X))(n?(Y*Y) - (?Y)*(?Y)))
        return (float) ((n * xySum - xSum * ySum) / Math.sqrt((n * xxSum - xSum * xSum) * (n * yySum - ySum * ySum)));
    }

}

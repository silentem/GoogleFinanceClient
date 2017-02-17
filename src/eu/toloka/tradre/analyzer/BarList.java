package eu.toloka.tradre.analyzer;

//import eu.toloka.tradre.analyzer.attribute.IAttributeProcessor;

import eu.toloka.tradre.analyzer.attribute.IAttributeProcessor;
import eu.toloka.tradre.persistence.entity.BarEntity;

import java.util.*;

/**
 * @author Viktor Korovyanko <viktor.koro@gmail.com>
 */
public class BarList extends ArrayList<Bar> {
  private Float high = null;
  private Float low = null;

  public BarList(int size) {
    super(size);
  }

  public Float getHigh() {
    return high;
  }

  public Float getLow() {
    return low;
  }

//    public Bar getBar(Long time){
//        if(this.size() == 0){
//            return null;
//        }
//
//        Bar lastBar = this.get(this.size() - 1);
//
//        return null;
//    }

//    public Float getMoving(Long time, Integer moving){
//
//
//        return 0f;
//    }

    public void addAttribute(IAttributeProcessor processor) {
        for (Bar bar : this) {
            processor.process(bar);
        }
    }

  public BarList lastBars(Integer n) {
    BarList barList = new BarList(n);
    n = Math.min(n, this.size());

    for (int i = this.size() - n; i < this.size(); i++) {
      barList.add(this.get(i));
    }

    return barList;
  }

  public List<BarEntity> getBarEntityList() {
    List<BarEntity> barEntityList = new ArrayList<BarEntity>(this.size());

    for (Bar bar : this) {
      barEntityList.add(bar.getEntity());
    }

    return barEntityList;
  }

  public Set<Long> getTimeSet() {
    Set<Long> set = new HashSet<Long>();

    for (Bar bar : this) {
      set.add(bar.time());
    }

    return set;
  }

  public Bar last(){
    return this.get(this.size() - 1);
  }

  public Bar beforeLast(){
    return this.get(this.size() - 2);
  }


  public Float getHighLowAbs() {
    return Math.abs(high - low);
  }

  public Float getHighLowAbsSum() {
    Float sum = 0f;

    for (Bar bar : this) {
      sum += Math.abs(bar.high() - bar.low());
    }

    return sum;
  }

  public Float getOpenCloseAbsSum() {
    Float sum = 0f;

    for (Bar bar : this) {
      sum += Math.abs(bar.open() - bar.close());
    }

    return sum;
  }

  public float getFlatBarsRelation() {
    float n = 0;

    for (Bar bar : this) {
      if (Math.abs(bar.open() - bar.close()) == 0) {
        n++;
      }
    }

    return n / this.size();
  }


  public Float getOCHLRelation() {
    return getOpenCloseAbsSum() / getHighLowAbsSum();
  }

//    public boolean contains(Long time) {
//        for (int i = this.size() - 1; i > 0; i--) {
//            if (this.get(i).time().equals(time)) {
//                return true;
//            }
//
//            if (this.get(i).time() < time) {
//                return false;
//            }
//        }
//
//        return false;
//    }

  public BarList getSubList(int from, int to) {
    BarList barList = new BarList(to - from);
    List<Bar> list = this.subList(from, to);

    for (Bar bar : list) {
      barList.add(bar);
    }

    return barList;
  }

    public BarList getSubListReversedIndex(int from) {
        if(this.size() < from){
            return new BarList(0);
        }

        BarList barList = new BarList(this.size() - from);
        List<Bar> list = this.subList(0, this.size() - from);

        for (Bar bar : list) {
            barList.add(bar);
        }

        return barList;
    }

    public BarList getSubListReversedIndex(int from, int to) {
        if(this.size() < to){
            return new BarList(0);
        }

        BarList barList = new BarList(to - from);
        List<Bar> list = this.subList(this.size() - to, this.size() - from);

        for (Bar bar : list) {
            barList.add(bar);
        }

        return barList;
    }

  public BarList getSubList(Long time) {
    BarList barList = new BarList(this.size());

    if (this.size() == 0) {
      return barList;
    }

    for (int i = this.size() - 1; i >= 0 && this.get(i).time() >= time; i--) {
      barList.add(this.get(i));
    }

    Collections.reverse(barList);

    return barList;
  }

//    public Float getPivot() {
//        Float high = null;
//        Float low = null;
//
//        for (Bar bar : this) {
//            high = high == null ? bar.getEntity().high : Math.max(high, bar.getEntity().high);
//            low = low == null ? bar.getEntity().low : Math.min(high, bar.getEntity().low);
//        }
//
//        if (this.size() == 0 || high == null || low == null) {
//            return null;
//        }
//
//        return (2 * this.get(0).getEntity().open + high + low) / 4f;
//    }

  @Override
  public boolean add(Bar bar) {
    high = high == null ? bar.high() : Math.max(high, bar.high());
    low = low == null ? bar.low() : Math.min(low, bar.low());

    return super.add(bar);
  }

  public Float getCoefficient(Integer height) {
    return (high - low) / (height - 20);
  }

}

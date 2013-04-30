package org.terracotta.marketing.analytics.chart;

import static org.junit.Assert.assertEquals;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.googlecode.charts4j.Data;

public class GAPlottableTest {

  @Test
  public void test() throws ParseException {
    List<Number> expectedData = new ArrayList<Number>();
    List<String> expectedDateStrings = new ArrayList<String>();
    NumberFormat fmt = NumberFormat.getInstance();
    String value;
    
    List<String> row1 = new ArrayList<String>();
    row1.add("2013");
    row1.add("01");
    expectedDateStrings.add("2013-01");
    value = "50";
    row1.add(value);
    expectedData.add(fmt.parse(value));
    
    List<String> row2 = new ArrayList<String>();
    row2.add("2013");
    row2.add("02");
    expectedDateStrings.add("2013-02");
    value = "200";
    row2.add(value);
    expectedData.add(fmt.parse(value));
    
    List<String> row3 = new ArrayList<String>();
    row3.add("2013");
    row3.add("03");
    expectedDateStrings.add("2013-03");
    value = "20";
    row3.add(value);
    expectedData.add(fmt.parse(value));
    
    List<List<String>> series = new ArrayList<List<String>>();
    series.add(row1);
    series.add(row2);
    series.add(row3);
    
    
    GAPlottable plt = new GAPlottable(series);
    List<? extends Number> data = plt.getData();
  
    // check to make sure it grabbed collated the data properly
    assertEquals(expectedData, data);
  
    // check to make sure it figured out the min and max properly.
    assertEquals(fmt.parse("200"), plt.getMax());
    assertEquals(fmt.parse("20"), plt.getMin());

    // check to make sure it scales the plottable data properly.
    Data plottable = plt.getPlottableData();
    double[] scaled = plottable.getData();
    double[] expectedScaled = new double[] { 25d, 100d, 10d };
    assertEquals(series.size(), scaled.length);
    
    for (int i = 0; i<expectedScaled.length; i++) {
      assertEquals((int)expectedScaled[i], (int)scaled[i]);
    }
  
    List<String> dateStrings = plt.getDateStrings();
    assertEquals(expectedDateStrings, dateStrings);

  }

}

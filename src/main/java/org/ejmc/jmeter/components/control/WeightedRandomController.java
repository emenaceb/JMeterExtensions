package org.ejmc.jmeter.components.control;

import java.io.Serializable;
import java.util.List;
import java.util.Random;
import org.apache.jmeter.control.GenericController;
import org.apache.jmeter.samplers.Sampler;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.property.JMeterProperty;
import org.apache.jmeter.testelement.property.NullProperty;
import org.apache.jmeter.testelement.property.ObjectProperty;

public class WeightedRandomController extends GenericController implements Serializable {

   private static final long serialVersionUID = -7000081603910944533L;
   static final String WEIGHTS = "WeightedRandomController.weights";
   private static final ThreadLocal RANDOM = new ThreadLocal() {
      protected Random initialValue() {
         return new Random(System.nanoTime());
      }
   };
   private transient int[] executionArray;
   private transient int[] weightsArray;


   private int[] getCacheddWeights() {
      if(this.weightsArray == null) {
         this.weightsArray = this.getWeights();
      }

      return this.weightsArray;
   }

   public int[] getWeights() {
      JMeterProperty p = this.getProperty("WeightedRandomController.weights");
      if(p != null && !(p instanceof NullProperty)) {
         ObjectProperty cp = (ObjectProperty)p;
         return (int[])cp.getObjectValue();
      } else {
         return new int[0];
      }
   }

   protected void incrementCurrent() {
      this.current = Integer.MAX_VALUE;
   }

   public Sampler next() {
      if(this.isFirst()) {
         this.current = this.selectExecution();
      }

      return super.next();
   }

   private int[] getCachedExecutionArray() {
      if(this.executionArray == null) {
         int[] weights = this.getCacheddWeights();
         List sc = this.getSubControllers();
         int length = sc.size();
         int wlen = weights.length;
         this.executionArray = new int[length];
         int sum = 0;

         for(int i = 0; i < length; ++i) {
            boolean enabled = ((TestElement)sc.get(i)).isEnabled();
            int w = 0;
            if(enabled) {
               w = i < wlen?weights[i]:1;
            }

            sum += w;
            this.executionArray[i] = sum;
         }
      }

      return this.executionArray;
   }

   private int selectExecution() {
      int[] ea = this.getCachedExecutionArray();
      int sum = ea[ea.length - 1];
      int rv = ((Random)RANDOM.get()).nextInt(sum);

      for(int i = 0; i < ea.length; ++i) {
         if(rv < ea[i]) {
            return i;
         }
      }

      System.out.println("----------------->" + sum + " ," + rv);
      return 0;
   }

   public void setWeights(int[] weights) {
      this.setProperty(new ObjectProperty("WeightedRandomController.weights", weights));
   }
}

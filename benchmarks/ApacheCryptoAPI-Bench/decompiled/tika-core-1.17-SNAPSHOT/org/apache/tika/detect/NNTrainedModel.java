package org.apache.tika.detect;

public class NNTrainedModel extends TrainedModel {
   private int numOfInputs;
   private int numOfHidden;
   private int numOfOutputs;
   private float[][] Theta1;
   private float[][] Theta2;

   public NNTrainedModel(int nInput, int nHidden, int nOutput, float[] nn_params) {
      this.numOfInputs = nInput;
      this.numOfHidden = nHidden;
      this.numOfOutputs = nOutput;
      this.Theta1 = new float[this.numOfHidden][this.numOfInputs + 1];
      this.Theta2 = new float[this.numOfOutputs][this.numOfHidden + 1];
      this.populateThetas(nn_params);
   }

   private void populateThetas(float[] nn_params) {
      int m = this.Theta1.length;
      int n = this.Theta1[0].length;
      int k = 0;

      for(int i = 0; i < n; ++i) {
         for(int j = 0; j < m; ++j) {
            this.Theta1[j][i] = nn_params[k];
            ++k;
         }
      }

      m = this.Theta2.length;
      n = this.Theta2[0].length;

      for(int var9 = 0; var9 < n; ++var9) {
         for(int j = 0; j < m; ++j) {
            this.Theta2[j][var9] = nn_params[k];
            ++k;
         }
      }

   }

   public double predict(double[] unseen) {
      return (double)0.0F;
   }

   public float predict(float[] unseen) {
      int m = this.Theta1.length;
      int n = this.Theta1[0].length;
      float[] hh = new float[m + 1];
      hh[0] = 1.0F;

      for(int i = 0; i < m; ++i) {
         double h = (double)0.0F;

         for(int j = 0; j < n; ++j) {
            h += (double)(this.Theta1[i][j] * unseen[j]);
         }

         h = (double)1.0F / ((double)1.0F + Math.exp(-h));
         hh[i + 1] = (float)h;
      }

      m = this.Theta2.length;
      n = this.Theta2[0].length;
      float[] oo = new float[m];

      for(int var10 = 0; var10 < m; ++var10) {
         double o = (double)0.0F;

         for(int j = 0; j < n; ++j) {
            o += (double)(this.Theta2[var10][j] * hh[j]);
         }

         o = (double)1.0F / ((double)1.0F + Math.exp(-o));
         oo[var10] = (float)o;
      }

      return oo[0];
   }
}

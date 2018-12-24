package SVDtrainer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.MatrixDimensionMismatchException;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

public class SVDtrainer {
	double[] b_user;
	double[] b_item;
	double u_global;
	double[][] q;
	double[][] p;
	double[][] y;
	double[][] implici; 
	double lamb;
	double yi;
	int[] N_implicit;
	int userNum;
	int itemNum;
	int F;
	public void init(int F,double lamb,List<rates> rateList,int userNum,int itemNum,double yi) {
		this.b_user=new double[userNum];
		this.b_item=new double[itemNum];
		this.lamb=lamb;
		this.F=F;
		this.q=new double[itemNum][F];
		this.p=new double[userNum][F];
		this.N_implicit=new int[userNum];
		this.yi=yi;
		this.implici=new double[userNum][itemNum];
		this.y=new double[itemNum][F];
		this.userNum=userNum;
		this.itemNum=itemNum;
		int sum=0;
		for(rates ra:rateList) {
			sum+=ra.getRate();
		}
		this.u_global=sum/rateList.size();
		for(int i=0;i<userNum;i++) {
			int N=0;
			for(rates ra:rateList) {
				if(ra.getUserid()==(i+1)) {
					implici[ra.getUserid()-1][ra.getMovieid()-1]=1;
					N++;
				}
			}
			this.N_implicit[i]=N;
			
		}
		double temp=(double) Math.sqrt(F);
		Random random=new Random();
		for(int i=0;i<userNum;i++) {
			for(int j=0;j<F;j++) {
				this.p[i][j]=random.nextDouble()/temp;
			}
		}
		for(int i=0;i<itemNum;i++) {
			for(int j=0;j<F;j++) {
				this.q[i][j]=random.nextDouble()/temp;
			}
		}
	}
	public double predict(int u,int i) {
		RealMatrix qi=new Array2DRowRealMatrix(q).getRowMatrix(i);
		RealMatrix pu=new Array2DRowRealMatrix(p).getRowMatrix(u).transpose();
		double constant=1/Math.sqrt(N_implicit[u]);
		double[] temp=new double[F];
		RealMatrix y_sum=new Array2DRowRealMatrix(temp);
		RealMatrix ytemp;
		RealMatrix t;
		ytemp=new Array2DRowRealMatrix(y);
		for(int j=0;j<itemNum;j++) {
			if(implici[u][j]==0) continue;
			y_sum.setColumnMatrix(0,y_sum.add(ytemp.transpose().getColumnMatrix(j)));
		}
		y_sum.setColumnVector(0, y_sum.getColumnVector(0).combine(constant, 0, y_sum.getColumnVector(0)));
		t=pu.add(y_sum);
		t=qi.multiply(t);
		double r_predict=u_global+b_user[u]+b_item[i]+t.getNorm();
		if(r_predict>5) return 5;
		else if(r_predict<0) return 0;
		return r_predict;
	}
	public void modify(double e,int i,int u) {
		b_user[u]=b_user[u]+this.yi*(e-this.lamb*b_user[u]);
		b_item[i]=b_item[i]+this.yi*(e-this.lamb*b_item[i]);
		double constant=1/Math.sqrt(N_implicit[u]);
		double[] temp=new double[F];
		RealMatrix Q=new Array2DRowRealMatrix(q);
		RealMatrix P=new Array2DRowRealMatrix(p);
		RealMatrix y_sum=new Array2DRowRealMatrix(temp);
		RealMatrix ytemp;
		RealVector t;
		ytemp=new Array2DRowRealMatrix(y);
		for(int j=0;j<itemNum;j++) {
			if(implici[u][j]==0) continue;
			y_sum.setColumnMatrix(0, y_sum.add(ytemp.transpose().getColumnMatrix(j)));
		}
		y_sum.setColumnVector(0, y_sum.getColumnVector(0).combine(constant, 0, y_sum.getColumnVector(0)));
		Q=Q.transpose();
		t=P.transpose().getColumnVector(u);
		t=t.add(y_sum.getColumnVector(0));
		t=t.combine(e,0,y_sum.getColumnVector(0));
		t=t.subtract(Q.getColumnVector(i).combine(this.lamb, 0, y_sum.getColumnVector(0)));
		t=t.combine(this.yi, 0, y_sum.getColumnVector(0));
		t=t.add(Q.getColumnVector(i));
		Q.setColumnVector(i, t);
		Q=Q.transpose();
		P=P.transpose();
		t=Q.transpose().getColumnVector(i);
		t=t.combine(e,0, y_sum.getColumnVector(0));
		t=t.subtract(P.getColumnVector(u).combine(this.lamb,0, y_sum.getColumnVector(0)));
		t=t.combine(this.yi,0, y_sum.getColumnVector(0));
		t=t.add(P.getColumnVector(u));
		P.setColumnVector(u, t);
		P=P.transpose();
		for(int j=0;j<this.itemNum;j++) {
			if(implici[u][j]==0) continue;
			double ee=e*constant;
			t=Q.transpose().getColumnVector(i);
			t=t.combine(ee, 0, y_sum.getColumnVector(0));
			t=t.subtract(ytemp.transpose().getColumnVector(j).combine(this.lamb, 0, y_sum.getColumnVector(0)));
			t=t.combine(this.yi, 0, y_sum.getColumnVector(0));
			t=t.add(ytemp.transpose().getColumnVector(j));
			ytemp=ytemp.transpose();
			ytemp.setColumnVector(j, t);
			ytemp=ytemp.transpose();
		}
		this.p=P.getData();
		this.q=Q.getData();
		this.y=ytemp.getData();
	}
	public void train(double lamb,int F,List<rates> rate,double yi,int n,int userNum,int itemNum,double loss) {
		init(F, lamb, rate, userNum, itemNum, yi);
		double bestrmse=2;
		double nowrmse;
		int times=0;
		for(int j=0;j<n&&times<3;j++) {
			//System.out.println(j);
			for(rates ra:rate) {
				int u=ra.getUserid()-1;
				int i=ra.getMovieid()-1;
				double r=ra.getRate();
				double r_pre=this.predict(u, i);
				double e=r-r_pre;
				this.modify(e, i, u);
//				System.out.println("after");
//				System.out.println("erro="+e+"pre="+r_pre);
//				System.out.println(new Array2DRowRealMatrix(this.p));
//				System.out.println(new Array2DRowRealMatrix(this.q));
//				System.out.println(new Array2DRowRealMatrix(y));
//				System.out.println(new Array2DRowRealMatrix(this.b_item));
//				System.out.println(new Array2DRowRealMatrix(this.b_user));
			}
			this.yi=loss*this.yi;
//			System.out.println(new Array2DRowRealMatrix(this.p));
//			System.out.println(new Array2DRowRealMatrix(this.q));
//			System.out.println(new Array2DRowRealMatrix(y));
//			System.out.println(new Array2DRowRealMatrix(this.b_item));
//			System.out.println(new Array2DRowRealMatrix(this.b_user));
			nowrmse=this.RMSE(rate);
			System.out.println(nowrmse);
			if(nowrmse<bestrmse) {
				bestrmse=nowrmse;
				times=0;
			}else {
				times++;
			}
		}
	}
	public double RMSE(List<rates> rating) {
		double rmse=0;
		int datasize=rating.size();
		for(rates ra:rating) {
			int userid=ra.getUserid()-1;
			int movieid=ra.getMovieid()-1;
			double rate=ra.getRate();
			double e=rate-this.predict(userid, movieid);
			rmse+=(e*e/datasize);
		}
		rmse=Math.sqrt(rmse);
		return rmse;
	}
	public static void main(String[] args) throws Exception{
		List<rates> rate=new ArrayList<>();
		BufferedReader reader=new BufferedReader(new FileReader("/Users/lijiahui/Downloads/ml-100k/ub.test"));
		String line;
		int i=0;
		reader.readLine();
		while((line=reader.readLine())!=null) {
			String[] data=line.split("\t");
			if(Integer.parseInt(data[1])>2000) continue;
			rate.add(new rates(Integer.parseInt(data[0]),Integer.parseInt(data[1]),Double.parseDouble(data[2])));
			//i++;
		}
		SVDtrainer train=new SVDtrainer();
		double lamb=0.98;
		double yi=0.15;
		int n=30;
		double loss=0.7;
		double rmse;
		int F=9;
		for(lamb=0.96;lamb<1.4;lamb+=0.02) {
			System.out.println(lamb);
			train.train(lamb, F, rate, yi, n, 943, 2000, loss);
			//rmse=train.RMSE(rate);
			//System.out.println(rmse);
		}
	}
}

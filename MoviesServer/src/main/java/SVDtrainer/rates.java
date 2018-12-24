package SVDtrainer;

public class rates {
	public int userid;
	public int movieid;
	public double rate;
	public rates(int userid,int movieid,double rate) {
		this.userid=userid;
		this.movieid=movieid;
		this.rate=rate;
	}
	public rates() {
		
	}
	public int getUserid() {
		return userid;
	}
	public void setUserid(int userid) {
		this.userid = userid;
	}
	public int getMovieid() {
		return movieid;
	}
	public void setMovieid(int movieid) {
		this.movieid = movieid;
	}
	public double getRate() {
		return rate;
	}
	public void setRate(double rate) {
		this.rate = rate;
	}
}

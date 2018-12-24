package SVDtrainer;

public class Y {
	public int userid;
	public int movieid;
	public double[] y;
	public Y(double[] y) {
		this.y=y;
	}
	public Y() {
		
	}
	public int getUserid() {
		return userid;
	}
	public void setUserid(int userid) {
		this.userid = userid;
	}
	public double[] getY() {
		return y;
	}
	public void setY(double[] y) {
		this.y = y;
	}
	public int getMovieid() {
		return movieid;
	}
	public void setMovieid(int movieid) {
		this.movieid = movieid;
	}
}

package DataManager;
// Generated 2018-12-18 10:02:08 by Hibernate Tools 5.2.10.Final

/**
 * Collect generated by hbm2java
 */
public class Collect implements java.io.Serializable {

	private Integer id;
	private int userid;
	private int movieid;

	public Collect() {
	}

	public Collect(int userid, int movieid) {
		this.userid = userid;
		this.movieid = movieid;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public int getUserid() {
		return this.userid;
	}

	public void setUserid(int userid) {
		this.userid = userid;
	}

	public int getMovieid() {
		return this.movieid;
	}

	public void setMovieid(int movieid) {
		this.movieid = movieid;
	}

}
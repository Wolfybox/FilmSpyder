package SpringModel;

import net.sf.json.JSONArray;

public class moviedetail {
	private Integer id;
	private String moviename;
	private String director;
	private String boxOffice;
	private String class_;
	private String time;
	private String introduce;
	private String thumburl;
	private Integer rate;
	private JSONArray actor;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getMoviename() {
		return moviename;
	}
	public void setMoviename(String moviename) {
		this.moviename = moviename;
	}
	public String getDirector() {
		return director;
	}
	public void setDirector(String director) {
		this.director = director;
	}
	public String getBoxOffice() {
		return boxOffice;
	}
	public void setBoxOffice(String boxOffice) {
		this.boxOffice = boxOffice;
	}
	public String getClass_() {
		return class_;
	}
	public void setClass_(String class_) {
		this.class_ = class_;
	}
	public String getIntroduce() {
		return introduce;
	}
	public void setIntroduce(String introduce) {
		this.introduce = introduce;
	}
	public String getThumburl() {
		return thumburl;
	}
	public void setThumburl(String thumburl) {
		this.thumburl = thumburl;
	}
	public Integer getRate() {
		return rate;
	}
	public void setRate(Integer rate) {
		this.rate = rate;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public JSONArray getActor() {
		return actor;
	}
	public void setActor(JSONArray actor) {
		this.actor = actor;
	}
}

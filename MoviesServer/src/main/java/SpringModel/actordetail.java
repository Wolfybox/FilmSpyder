package SpringModel;

import net.sf.json.JSONArray;

public class actordetail {
	private String actorname;
	private String imageurl;
	private String birth;
	private JSONArray play;
	private int count;
	public String getActorname() {
		return actorname;
	}
	public void setActorname(String actorname) {
		this.actorname = actorname;
	}
	public String getImageurl() {
		return imageurl;
	}
	public void setImageurl(String imageurl) {
		this.imageurl = imageurl;
	}
	public String getBirth() {
		return birth;
	}
	public void setBirth(String birth) {
		this.birth = birth;
	}
	public JSONArray getPlay() {
		return play;
	}
	public void setPlay(JSONArray play) {
		this.play = play;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
}

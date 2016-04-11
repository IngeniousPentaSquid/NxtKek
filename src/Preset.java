
public class Preset {
	
	private String name;
	private int thumbX;
	private int thumbY;
	private int storyX;
	private int storyY;
	private int windowX;
	private int windowY;
	
	
	public Preset(String name, int thumbX, int thumbY, int storyX, int storyY, int windowX, int windowY) {
		this.name = name;
		this.thumbX = thumbX;
		this.thumbY = thumbY;
		this.storyX = storyX;
		this.storyY = storyY;
		this.windowX = windowX;
		this.windowY = windowY;
	}
	
	public String toString() {
		return "Preset: " + name;
	}
	
	public String getName() {
		return name;
	}
	
	public int getThumbX() {
		return thumbX;
	}
	
	public int getThumbY() {
		return thumbY;
	}
	
	public int getStoryX() {
		return storyX;
	}
	
	public int getStoryY() {
		return storyY;
	}
	
	public int windowX() {
		return windowX;
	}
	
	public int windowY() {
		return windowY;
	}

}
